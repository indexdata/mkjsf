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
import com.indexdata.pz2utils4jsf.pazpar2.state.StateManager;
import com.indexdata.pz2utils4jsf.utils.Utils;

@Named @SessionScoped  
public class Pz2Session implements Pz2Interface {
    
  private static final long serialVersionUID = 3947514708343320514L;
  private static Logger logger = Logger.getLogger(Pz2Session.class);
  
  protected Map<String,Pazpar2ResponseData> dataObjects = new ConcurrentHashMap<String,Pazpar2ResponseData>();
  protected StateManager stateManager = new StateManager();
  protected ErrorHelper errorHelper = null;
  
  protected List<ErrorInterface> configurationErrors = null;
  protected SearchClient searchClient = null;   
  protected SingleTargetFilter singleTargetFilter = null;  
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
    stateManager.hasPendingStateChange("search",false);
    resetDataObjects();
    removeCommand("record");
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
        if (commands.equals("record")) {
          logger.debug("Record: Active clients: "+getRecord().getActiveClients());
          return getRecord().getActiveClients();
        } else {
          return getActiveClients();
        }  
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
      Pazpar2Command command = getCommand("search");
      command.getParameter("query").addExpression(new Expression(facetKey,"=",term));
      stateManager.checkIn(command);
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
    Pazpar2Command command = getCommand("search");
    command.getParameter("query").removeExpression(new Expression(facetKey,"=",term));
    stateManager.checkIn(command);
    doSearch();
  }
  
  public void setSingleTargetFilter (String targetId, String targetName) {    
    if (hasSingleTargetFilter(new SingleTargetFilter(targetId,targetName))) {
      logger.debug("Already using target filter " + this.singleTargetFilter.getFilterExpression());
    } else {      
      this.singleTargetFilter = new SingleTargetFilter(targetId,targetName);
      setCommandParameter("search",new CommandParameter("filter","=",this.singleTargetFilter.getFilterExpression()));      
      doSearch();
    }    
  }

  public SingleTargetFilter getSingleTargetFilter () {
    return singleTargetFilter;
  }
    
  public void removeSingleTargetFilter () {
    logger.debug("Removing target filter " + singleTargetFilter.getFilterExpression());
    this.singleTargetFilter = null;
    removeCommandParameter("search","filter");         
    doSearch();
  }
  
  public boolean hasSingleTargetFilter() {
    return singleTargetFilter != null;    
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
    return stateManager.getCurrentState().getKey();
  }
      
  public void setCurrentStateKey(String key) {       
    stateManager.setCurrentStateKey(key);
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

    
  protected boolean hasSingleTargetFilter(SingleTargetFilter targetFilter) {
    return hasSingleTargetFilter() && targetFilter.equals(this.singleTargetFilter);
  }
  
  protected boolean hasQuery() {
    return getCommand("search").getParameter("query") != null && getCommand("search").getParameter("query").getValueWithExpressions().length()>0;
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
    if (stateManager.hasPendingStateChange("search") && hasQuery()) { 
      logger.debug("Found pending search change. Doing search before updating " + commands);      
      doSearch();
    } 
    if (stateManager.hasPendingStateChange("record") && ! commands.equals("record")) {        
      logger.debug("Found pending record ID change. Doing record before updating " + commands);
      stateManager.hasPendingStateChange("record",false);
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

  /**
   * Returns a Pazpar2 command 'detached' from the current Pazpar2 state.
   * 
   * 'Detached' is meant to imply that this is a copy of a command in the 
   * current state, detached so as to NOT change the current state if 
   * modified. It can be viewed and executed, however. 
   * 
   * In order to modify the command with effect for subsequent searches,
   * it must be checked back into the StateManager, which will
   * then create a new current Pazpar2 state as needed.
   *  
   * @param name
   * @return
   */
  protected Pazpar2Command getCommand(String name) {
    return stateManager.checkOut(name);
  }
  
  protected void setCommandParameter(String commandName, CommandParameter parameter) {
    logger.debug("Setting parameter for " + commandName + ": " + parameter);
    Pazpar2Command command = getCommand(commandName);
    command.setParameter(parameter);
    stateManager.checkIn(command);    
  }
  
  
  protected void removeCommandParameter(String commandName, String parameterName) {
    Pazpar2Command command = getCommand(commandName);
    command.removeParameter(parameterName);
    stateManager.checkIn(command);    
  }
  
  protected void removeCommand (String commandName) {
    Pazpar2Command command = getCommand(commandName);
    command.removeParameters();
    stateManager.checkIn(command);
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
  
  @Override
  public void setFilter(String filterExpression) {
    logger.debug("Setting filter to " + filterExpression);
    setCommandParameter("search",new CommandParameter("filter","=",filterExpression));    
  }
  
  public String getFilter() {
    return getCommandParameterValueSimple("search", "filter", "");
  }
  
  public boolean hasFilter () {
    return getFilter().length()>0;
  }
  
}
