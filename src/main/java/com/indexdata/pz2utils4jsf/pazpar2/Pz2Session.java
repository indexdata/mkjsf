package com.indexdata.pz2utils4jsf.pazpar2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import org.apache.log4j.Logger;

import com.indexdata.masterkey.pazpar2.client.Pazpar2Client;
import com.indexdata.masterkey.pazpar2.client.Pazpar2ClientConfiguration;
import com.indexdata.masterkey.pazpar2.client.Pazpar2ClientGeneric;
import com.indexdata.masterkey.pazpar2.client.exceptions.ProxyErrorException;
import com.indexdata.pz2utils4jsf.config.Pz2Configurator;
import com.indexdata.pz2utils4jsf.controls.ResultsPager;
import com.indexdata.pz2utils4jsf.errors.ApplicationError;
import com.indexdata.pz2utils4jsf.errors.ErrorHelper;
import com.indexdata.pz2utils4jsf.errors.ConfigurationError;
import com.indexdata.pz2utils4jsf.pazpar2.data.CommandError;
import com.indexdata.pz2utils4jsf.pazpar2.data.ByTarget;
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
  
  private static Logger logger = Logger.getLogger(Pz2Session.class);
  
  private Map<String,Pazpar2ResponseData> dataObjects = new ConcurrentHashMap<String,Pazpar2ResponseData>();
  private QueryStates queryStates = new QueryStates();
  
  private static final long serialVersionUID = 3947514708343320514L;  
  private Pazpar2ClientConfiguration cfg = null;
  private Pazpar2Client client = null;   
  private TargetFilter targetFilter = null;  
  private ResultsPager pager = null; 
  private ErrorHelper errorHelper = null;
  private List<ApplicationError> configurationErrors = null;
  
  public Pz2Session () {
    logger.info("Instantiating pz2 session object [" + Utils.objectId(this) + "]");      
  }
    
  public void init(Pz2Configurator pz2conf) {
    if (client==null) {
      configurationErrors = new ArrayList<ApplicationError>();
      errorHelper = new ErrorHelper(pz2conf);
      logger.info(Utils.objectId(this) + " is configuring itself using the provided " + Utils.objectId(pz2conf));
      try {
        cfg = new Pazpar2ClientConfiguration(pz2conf.getConfig());
      } catch (ProxyErrorException pe) {
        logger.error("Could not configure Pazpar2 client: " + pe.getMessage());
        configurationErrors.add(new ConfigurationError("Pz2Client Config","ProxyError","Could not configure Pazpar2 client: " + pe.getMessage(),errorHelper));
      } catch (IOException io) {
        logger.error("Could not configure Pazpar2 client: " + io.getMessage());
        configurationErrors.add(new ConfigurationError("Pz2Client Config","ProxyError","Could not configure Pazpar2 client: " + io.getMessage(),errorHelper));
      }
      try {
        client = new Pazpar2ClientGeneric(cfg);     
      } catch (ProxyErrorException pe) {
        logger.error("Could not instantiate Pazpar2 client: " + pe.getMessage());
        configurationErrors.add(new ConfigurationError("Pz2Client","ProxyError","Could not create Pazpar2 client: " +pe.getMessage(),errorHelper));                
      } 
      logger.info("Got " + configurationErrors.size() + " configuration errors");
      resetDataObjects();
    } else {
      logger.warn("Attempt to configure session but it already has a configured client");
    }
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
          threadList.add(new CommandThread(getCommand(tokens.nextToken()),client));            
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
           Pazpar2ResponseData responseObject = Pazpar2ResponseParser.getParser().getDataObject(thread.getResponse());
           dataObjects.put(commandName, responseObject);        
        }
        return getActiveClients();
      } else {
        logger.debug("Skipped requests for " + commands + " as there's not yet a query."); 
        resetDataObjects();
        return "0";
      }
    } else {
      configurationErrors.add(
          new ConfigurationError("Querying while errors",
                                 "App halted",
                                 "Cannot query Pazpar2 while there are configuration errors.",
                                 errorHelper));
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
      return updateRecord(recId);
    }
  }
  
  private String updateRecord(String recId) {    
    setCommandParameter("record",new CommandParameter("id","=",recId));    
    return doCommand("record");    
  }
  
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
    logger.debug("************** request to set state key to: [" + key + "]");    
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

  public List<ApplicationError> getConfigurationErrors() {
    logger.info("Returning " + configurationErrors.size() + " configuration errors");
    return configurationErrors;
  }
  
  /**
   * Returns a search command error, if any, otherwise the first
   * error found for an arbitrary command, if any, otherwise
   * an empty dummy error. 
   */    
  public ApplicationError getCommandError() {
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

    
  private boolean hasTargetFilter(TargetFilter targetFilter) {
    return hasTargetFilter() && targetFilter.equals(this.targetFilter);
  }
  
  private boolean hasQuery() {
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
  
  private void handleQueryStateChanges (String commands) {
    if (queryStates.hasPendingStateChange("search")) { 
      logger.debug("Found pending search change. Doing search before updating " + commands);
      doSearch();
    } 
    if (queryStates.hasPendingStateChange("record") && ! commands.equals("record")) {        
      logger.debug("Found pending record ID change. Doing record before updating " + commands);
      queryStates.hasPendingStateChange("record",false);
      if (getCommand("record").hasParameters()) {
        updateRecord(getCommand("record").getParameter("id").getSimpleValue());
      } else {
        removeCommand("record");  
        dataObjects.put("record", new RecordResponse());
      }
    }    
  }

  private String getActiveClients() {    
    if (getShow()!=null) {
      logger.debug("Active clients: "+getShow().getActiveClients());
      return getShow().getActiveClients();
    } else {
      return "";
    }
  }

  private Pazpar2Command getCommand(String name) {
    return queryStates.getCurrentState().getCommand(name);
  }
  
  private void setCommandParameter(String commandName, CommandParameter parameter) {
    logger.debug("Setting parameter for " + commandName + ": " + parameter);
    queryStates.getCurrentState().setCommandParameter(commandName, parameter, queryStates);    
  }
  
  
  private void removeCommandParameter(String commandName, String parameterName) {
    queryStates.getCurrentState().removeCommandParameter(commandName,parameterName,queryStates);    
  }
  
  private void removeCommand (String commandName) {
    queryStates.getCurrentState().removeCommand(commandName, queryStates);
  }
    
  private String getCommandParameterValue (String commandName, String parameterName, String defaultValue) {    
    Pazpar2Command command = getCommand(commandName);
    if (command != null) {
      CommandParameter parameter = command.getParameter(parameterName);
      if (parameter != null) {
        return parameter.getValueWithExpressions();
      }
    }
    return defaultValue;    
  }
  
  private String getCommandParameterValueSimple (String commandName, String parameterName, String defaultValue) {    
    Pazpar2Command command = getCommand(commandName);
    if (command != null) {
      CommandParameter parameter = command.getParameter(parameterName);
      if (parameter != null) {
        return parameter.getSimpleValue();
      }
    }
    return defaultValue;    
  }

  
  private int getCommandParameterValue (String commandName, String parameterName, int defaultValue) {
    Pazpar2Command command = getCommand(commandName);
    if (command != null) {
      CommandParameter parameter = command.getParameter(parameterName);
      if (parameter != null) {
        return Integer.parseInt(parameter.getSimpleValue());
      }
    }
    return defaultValue;    
  }

  private String doCommand(String commandName) {      
    Pazpar2Command command = getCommand(commandName);    
    logger.debug(command.getEncodedQueryString() + ": Results for "+ getCommand("search").getEncodedQueryString());
    return update(commandName);      
  }
  
  private void resetDataObjects() {
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
