package com.indexdata.pz2utils4jsf.pazpar2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import org.apache.log4j.Logger;

import com.indexdata.pz2utils4jsf.config.ConfigurationReader;
import com.indexdata.pz2utils4jsf.controls.ResultsPager;
import com.indexdata.pz2utils4jsf.errors.ConfigurationError;
import com.indexdata.pz2utils4jsf.errors.ConfigurationException;
import com.indexdata.pz2utils4jsf.errors.ErrorHelper;
import com.indexdata.pz2utils4jsf.errors.ErrorInterface;
import com.indexdata.pz2utils4jsf.pazpar2.data.ByTarget;
import com.indexdata.pz2utils4jsf.pazpar2.data.CommandError;
import com.indexdata.pz2utils4jsf.pazpar2.data.Pazpar2ResponseData;
import com.indexdata.pz2utils4jsf.pazpar2.data.Pazpar2ResponseParser;
import com.indexdata.pz2utils4jsf.pazpar2.data.RecordResponse;
import com.indexdata.pz2utils4jsf.pazpar2.data.SearchResponse;
import com.indexdata.pz2utils4jsf.pazpar2.data.ShowResponse;
import com.indexdata.pz2utils4jsf.pazpar2.data.StatResponse;
import com.indexdata.pz2utils4jsf.pazpar2.data.TermListsResponse;
import com.indexdata.pz2utils4jsf.pazpar2.data.TermResponse;
import com.indexdata.pz2utils4jsf.pazpar2.state.QueryStates;
import com.indexdata.pz2utils4jsf.utils.Utils;

@Named @SessionScoped  
public class Pz2Session implements Pz2Interface {
    
  private static final long serialVersionUID = 3947514708343320514L;
  private static Logger logger = Logger.getLogger(Pz2Session.class);
  
  protected Map<String,Pazpar2ResponseData> dataObjects = new ConcurrentHashMap<String,Pazpar2ResponseData>();
  protected QueryStates queryStates = new QueryStates();
  protected ErrorHelper errorHelper = null;
  
  protected List<ErrorInterface> configurationErrors = null;
  protected SearchClient searchClient = null;   
  protected TargetFilter targetFilter = null;  
  protected ResultsPager pager = null; 
    
  public Pz2Session () {
    logger.info("Instantiating pz2 session object [" + Utils.objectId(this) + "]");      
  }
    
  public void configureClient(SearchClient searchClient, ConfigurationReader configReader) {
    configurationErrors = new ArrayList<ErrorInterface>();
    errorHelper = new ErrorHelper(configReader);    
    logger.debug(Utils.objectId(this) + " will configure search client for the session");
    try {
      searchClient.configure(configReader);            
      // At the time of writing this search client is injected using Weld. 
      // However, the client is used for asynchronously sending off requests
      // to the server AND propagation of context to threads is currently 
      // not supported. Trying to do so throws a WELD-001303 error. 
      // To avoid that, a context free client is cloned from the context 
      // dependent one. 
      // If propagation to threads gets supported, the cloning can go. 
      this.searchClient = searchClient.cloneMe();         
    } catch (ConfigurationException e) {
      configurationErrors.add(new ConfigurationError("Search Client","Configuration",e.getMessage(),new ErrorHelper(configReader)));          
    } 
    logger.info(configReader.document());
    resetDataObjects();
  }
      
  public void doSearch(String query) {
    setCommandParameter("search",new CommandParameter("query","=",query));     
    doSearch();
  }

  public void doSearch() { 
    queryStates.hasPendingStateChange("search",false);
    resetDataObjects();
    setCommandParameter("show",new CommandParameter("start","=",0));    
    logger.debug(Utils.objectId(this) + " is searching using "+getCommand("search").getParameter("query").getEncodedQueryString());
    doCommand("search");    
  }
      
  /**
   * Refreshes 'show', 'stat', 'termlist', and 'bytarget' data object from pazpar2
   * 
   * @return Number of activeclients at the time of the 'show' command.
   */
  public String update () {
    logger.debug("Updating show,stat,termlist,bytarget from pazpar2");
    return update("show,stat,termlist,bytarget");
  }
   
  /**
   * Refreshes the data objects listed in 'commands' from pazpar2
   * 
   * @param commands
   * @return Number of activeclients at the time of the 'show' command
   */
  public String update (String commands) {
    if (! hasConfigurationErrors()) {
      if (hasQuery()) {
        handleQueryStateChanges(commands);
        logger.debug("Processing request for " + commands); 
        List<CommandThread> threadList = new ArrayList<CommandThread>();
        StringTokenizer tokens = new StringTokenizer(commands,",");
        while (tokens.hasMoreElements()) {
          threadList.add(new CommandThread(getCommand(tokens.nextToken()),searchClient));            
        }
        for (CommandThread thread : threadList) {
          thread.start();
        }
        for (CommandThread thread : threadList) {
          try {
            thread.join();
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
        for (CommandThread thread : threadList) {
           String commandName = thread.getCommand().getName();
           String response = thread.getResponse();
           logger.debug("Response was: " + response);
           Pazpar2ResponseData responseObject = Pazpar2ResponseParser.getParser().getDataObject(response);
           dataObjects.put(commandName, responseObject);        
        }
        return getActiveClients();
      } else {
        logger.debug("Skipped requests for " + commands + " as there's not yet a query."); 
        resetDataObjects();
        return "0";
      }
    } else {
      logger.error("Did not attempt to execute query since there are configuration errors.");
      return "0";
    }
    
  }
        
  public void setQuery (String query) {
    logger.debug("Creating new command parameter for " + query);
    setCommandParameter("search",new CommandParameter("query","=",query));
  }
  
  public String getQuery () {
    return getCommandParameterValueSimple("search","query",null);
  }
  
  public void setFacet (String facetKey, String term) {           
    if (term != null && term.length()>0) {
      queryStates.getCurrentState().setCommandParameterExpression("search","query",new Expression(facetKey,"=",term),queryStates);
      doSearch();
    }            
  }
  
  public void setFacetOnQuery (String facetKey, String term) {
    String facetExpression = facetKey + "=" + term;    
    if (term != null && term.length()>0) {
      setCommandParameter("search",new CommandParameter("query","=", getQuery() + " and " + facetExpression));
      doSearch();        
    }            
  }
      
  public void removeFacet(String facetKey, String term) {
    queryStates.getCurrentState().removeCommandParameterExpression("search","query",new Expression(facetKey,"=",term),queryStates);
    doSearch();
  }
  
  public void setTargetFilter (String targetId, String targetName) {    
    if (hasTargetFilter(new TargetFilter(targetId,targetName))) {
      logger.debug("Already using target filter " + this.targetFilter.getFilterExpression());
    } else {      
      this.targetFilter = new TargetFilter(targetId,targetName);
      setCommandParameter("search",new CommandParameter("filter","=",this.targetFilter.getFilterExpression()));      
      doSearch();
    }    
  }

  public TargetFilter getTargetFilter () {
    return targetFilter;
  }
    
  public void removeTargetFilter () {
    logger.debug("Removing target filter " + targetFilter.getFilterExpression());
    this.targetFilter = null;
    removeCommandParameter("search","filter");         
    doSearch();
  }
  
  public boolean hasTargetFilter() {
    return targetFilter != null;    
  }
        
  public void setSort (String sortOption) {
    logger.debug("Setting sort option: " + sortOption);
    setCommandParameter("show",new CommandParameter("sort","=",sortOption));
    update("show");
  }
  
  public String getSort () {
    return getCommandParameterValue("show","sort","relevance");
  }
    
  public void setPageSize (int perPageOption) {
    if (getPageSize()!=perPageOption) {
     logger.debug("Setting perpage option to " + perPageOption + " and resetting start page.");
     setCommandParameter("show",new CommandParameter("num","=",perPageOption));
     setCommandParameter("show",new CommandParameter("start","=",0));
     update("show");
    } else {
      logger.debug("Not updating page size, already is " + perPageOption);
    }
  }
  
  public int getPageSize () {
    return getCommandParameterValue("show","num",20);
  }
  
  public void setStart (int start) {
    logger.debug("Setting start num to " + start);
    setCommandParameter("show", new CommandParameter("start","=",start));  
    update("show");
  }
  
  public int getStart() {
    return getCommandParameterValue("show","start",0);
  }
          
  public String toggleRecord (String recId) {
    if (hasRecord(recId)) {
      removeCommand("record");  
      dataObjects.put("record", new RecordResponse());
      return "";
    } else {
      setRecordId(recId);
      return doCommand("record");
    }
  }
  
  @Override
  public void setRecordId(String recId) {
    setCommandParameter("record",new CommandParameter("id","=",recId));
  }
  
  @Override
  public String getRecordId () {
    return getCommandParameterValue("record","recid","");
  }
  
  @Override
  public boolean hasRecord (String recId) {
    return getCommand("record").hasParameters() && getRecord().getRecId().equals(recId);
  }
      
  public ShowResponse getShow () {
    return ((ShowResponse) dataObjects.get("show"));
  }
  
  public StatResponse getStat () {
    return ((StatResponse) dataObjects.get("stat"));
  }
  
  public RecordResponse getRecord() {
    return ((RecordResponse) dataObjects.get("record"));
  }
  
  public TermListsResponse getTermLists () {
    return ((TermListsResponse) dataObjects.get("termlist"));
  }
  
  public List<TermResponse> getFacetTerms (String facet, int count) {
    return (getTermLists().getTermList(facet).getTerms(count));
  }
    
  public List<TermResponse> getFacetTerms (String facet) {
    return (getTermLists().getTermList(facet).getTerms());
  }
  
  public ByTarget getByTarget() {
    return ((ByTarget) dataObjects.get("bytarget"));
  }
  
  
  public String getCurrentStateKey () {    
    return queryStates.getCurrentStateKey();
  }
      
  public void setCurrentStateKey(String key) {       
    queryStates.setCurrentStateKey(key);
  }
  
  public boolean hasConfigurationErrors () {
      return (configurationErrors.size()>0);      
  }
  
  public boolean hasCommandErrors () {
    if (dataObjects.get("search").hasApplicationError()) {
      logger.info("Error detected in search");
      return true;
    }
    for (String name : dataObjects.keySet()) {
      if (dataObjects.get(name).hasApplicationError()) {
        logger.info("Error detected in " + name);
        return true;
      }
    }    
    return false;    
  }
  
  /**
   * Returns true if application error found in any response data objects 
   */
  public boolean hasErrors () {
    return hasConfigurationErrors() || hasCommandErrors();
  }

  public List<ErrorInterface> getConfigurationErrors() {    
    return configurationErrors;
  }
  
  /**
   * Returns a search command error, if any, otherwise the first
   * error found for an arbitrary command, if any, otherwise
   * an empty dummy error. 
   */    
  public ErrorInterface getCommandError() {
    CommandError error = new CommandError();    
    if (dataObjects.get("search").hasApplicationError()) {
      error = dataObjects.get("search").getApplicationError();                        
    } else {
      for (String name : dataObjects.keySet()) {     
        if (dataObjects.get(name).hasApplicationError()) {     
          error = dataObjects.get(name).getApplicationError(); 
          break;
        } 
      }
    }
    error.setErrorHelper(errorHelper);
    return error;         
  }

    
  protected boolean hasTargetFilter(TargetFilter targetFilter) {
    return hasTargetFilter() && targetFilter.equals(this.targetFilter);
  }
  
  protected boolean hasQuery() {
    return !(getCommand("search").getParameter("query") == null);
  }
    
  public boolean hasRecords () {
    return getStat().getRecords() > 0            
           && getShow().getHits() != null 
           && getShow().getHits().size()>0;
  }
    
  public ResultsPager getPager () {
    if (pager == null) {
      pager = new ResultsPager(this);      
    } 
    return pager;      
  }
  
  public ResultsPager setPager (int pageRange) {
    pager =  new ResultsPager(this,pageRange);
    return pager;
  }
  
  protected ErrorHelper getTroubleshooter() {
    return errorHelper;
  }
  
  protected void handleQueryStateChanges (String commands) {
    if (queryStates.hasPendingStateChange("search")) { 
      logger.debug("Found pending search change. Doing search before updating " + commands);
      doSearch();
    } 
    if (queryStates.hasPendingStateChange("record") && ! commands.equals("record")) {        
      logger.debug("Found pending record ID change. Doing record before updating " + commands);
      queryStates.hasPendingStateChange("record",false);
      if (getCommand("record").hasParameters()) {
        update("record");
      } else {
        removeCommand("record");  
        dataObjects.put("record", new RecordResponse());
      }
    }    
  }

  protected String getActiveClients() {    
    if (getShow()!=null) {
      logger.debug("Active clients: "+getShow().getActiveClients());
      return getShow().getActiveClients();
    } else {
      return "";
    }
  }

  protected Pazpar2Command getCommand(String name) {
    return queryStates.getCurrentState().getCommand(name);
  }
  
  protected void setCommandParameter(String commandName, CommandParameter parameter) {
    logger.debug("Setting parameter for " + commandName + ": " + parameter);
    queryStates.getCurrentState().setCommandParameter(commandName, parameter, queryStates);    
  }
  
  
  protected void removeCommandParameter(String commandName, String parameterName) {
    queryStates.getCurrentState().removeCommandParameter(commandName,parameterName,queryStates);    
  }
  
  protected void removeCommand (String commandName) {
    queryStates.getCurrentState().removeCommand(commandName, queryStates);
  }
    
  protected String getCommandParameterValue (String commandName, String parameterName, String defaultValue) {    
    Pazpar2Command command = getCommand(commandName);
    if (command != null) {
      CommandParameter parameter = command.getParameter(parameterName);
      if (parameter != null) {
        return parameter.getValueWithExpressions();
      }
    }
    return defaultValue;    
  }
  
  protected String getCommandParameterValueSimple (String commandName, String parameterName, String defaultValue) {    
    Pazpar2Command command = getCommand(commandName);
    if (command != null) {
      CommandParameter parameter = command.getParameter(parameterName);
      if (parameter != null) {
        return parameter.getSimpleValue();
      }
    }
    return defaultValue;    
  }

  
  protected int getCommandParameterValue (String commandName, String parameterName, int defaultValue) {
    Pazpar2Command command = getCommand(commandName);
    if (command != null) {
      CommandParameter parameter = command.getParameter(parameterName);
      if (parameter != null) {
        return Integer.parseInt(parameter.getSimpleValue());
      }
    }
    return defaultValue;    
  }

  protected String doCommand(String commandName) {     
    Pazpar2Command command = getCommand(commandName);    
    logger.debug(command.getEncodedQueryString() + ": Results for "+ getCommand("search").getEncodedQueryString());
    return update(commandName);
  }
  
  protected void resetDataObjects() {
    logger.debug("Resetting show,stat,termlist,bytarget,search response objects.");
    dataObjects = new ConcurrentHashMap<String,Pazpar2ResponseData>();
    dataObjects.put("show", new ShowResponse());
    dataObjects.put("stat", new StatResponse());
    dataObjects.put("termlist", new TermListsResponse());
    dataObjects.put("bytarget", new ByTarget());
    dataObjects.put("record", new RecordResponse());
    dataObjects.put("search", new SearchResponse());
  }
  
}
