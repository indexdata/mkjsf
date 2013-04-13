package com.indexdata.pz2utils4jsf.pazpar2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.log4j.Logger;

import com.indexdata.pz2utils4jsf.config.ConfigurationReader;
import com.indexdata.pz2utils4jsf.controls.ResultsPager;
import com.indexdata.pz2utils4jsf.errors.ConfigurationError;
import com.indexdata.pz2utils4jsf.errors.ConfigurationException;
import com.indexdata.pz2utils4jsf.errors.ErrorHelper;
import com.indexdata.pz2utils4jsf.errors.ErrorInterface;
import com.indexdata.pz2utils4jsf.pazpar2.commands.CommandParameter;
import com.indexdata.pz2utils4jsf.pazpar2.commands.CommandReadOnly;
import com.indexdata.pz2utils4jsf.pazpar2.commands.Pazpar2Command;
import com.indexdata.pz2utils4jsf.pazpar2.commands.Pazpar2Commands;
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
import com.indexdata.pz2utils4jsf.pazpar2.state.StateListener;
import com.indexdata.pz2utils4jsf.pazpar2.state.StateManager;
import com.indexdata.pz2utils4jsf.utils.Utils;

@ForStraightPz2
public class Pz2Session implements Pz2Interface, StateListener {
    
  private static final long serialVersionUID = 3947514708343320514L;
  private static Logger logger = Logger.getLogger(Pz2Session.class);
  
  protected Map<String,Pazpar2ResponseData> dataObjects = new ConcurrentHashMap<String,Pazpar2ResponseData>();
  
  @Inject StateManager stateMgr;
  @Inject Pazpar2Commands req;
  
  protected ErrorHelper errorHelper = null;
  
  protected List<ErrorInterface> configurationErrors = null;
  protected SearchClient searchClient = null;       
  protected ResultsPager pager = null; 
    
  public Pz2Session () {
    logger.info("Instantiating pz2 session object [" + Utils.objectId(this) + "]");        
  }
  
  @PostConstruct
  public void listenToStateManager() {
    logger.debug("in post-construct of Pz2Session stateMgr is " + stateMgr);
    logger.debug("in post-construct req is " + req);
    stateMgr.addStateListener(this);
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
    stateMgr.hasPendingStateChange("search",false);
    resetDataObjects();
    removeCommand("record");
    setCommandParameter("show",new CommandParameter("start","=",0));    
    logger.debug(Utils.objectId(this) + " is searching using "+req.getCommandReadOnly("search").getUrlEncodedParameterValue("query"));
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
          threadList.add(new CommandThread(req.getCommandReadOnly(tokens.nextToken()),searchClient));            
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
            
                    
  public String toggleRecord (String recId) {
    if (hasRecord(recId)) {
      removeCommand("record");  
      dataObjects.put("record", new RecordResponse());
      return "";
    } else {
      req.getRecord().setRecordId(recId);
      return doCommand("record");
    }
  }
  
  @Override
  public boolean hasRecord (String recId) {
    return req.getCommandReadOnly("record").hasParameters() && getRecord().getRecId().equals(recId);
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
    return stateMgr.getCurrentState().getKey();
  }
      
  public void setCurrentStateKey(String key) {       
    stateMgr.setCurrentStateKey(key);
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
  
  protected boolean hasQuery() {    
    return req.getSearch().getParameter("query") != null && req.getSearch().getParameter("query").getValueWithExpressions().length()>0;
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
    pager =  new ResultsPager(this,pageRange,req);
    return pager;
  }
  
  protected ErrorHelper getTroubleshooter() {
    return errorHelper;
  }
  
  protected void handleQueryStateChanges (String commands) {
    if (stateMgr.hasPendingStateChange("search") && hasQuery()) { 
      logger.debug("Found pending search change. Doing search before updating " + commands);      
      doSearch();
    } 
    if (stateMgr.hasPendingStateChange("record") && ! commands.equals("record")) {        
      logger.debug("Found pending record ID change. Doing record before updating " + commands);
      stateMgr.hasPendingStateChange("record",false);
      if (req.getCommandReadOnly("record").hasParameters()) {
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
    return req.getCommand(name);
  }
  
  /** 
   * Returns an interface to a Pazpar2Command with only String getters.
   * 
   * Since the command cannot be modified (unless it is cast) we can avoid 
   * cloning it before returning it from the current state. 
   * It can be used for log statements, checks and for performing the 
   * actual pazpar2 request. 
   * 
   * @param name
   * @return
   */
  protected CommandReadOnly getCommandReadOnly(String name) {
    return req.getCommandReadOnly(name);
  }

  
  protected void setCommandParameter(String commandName, CommandParameter parameter) {
    logger.debug("Setting parameter for " + commandName + ": " + parameter);
    Pazpar2Command command = req.getCommand(commandName);
    command.setParameter(parameter);
    stateMgr.checkIn(command);    
  }
  
  
  protected void removeCommandParameter(String commandName, String parameterName) {
    Pazpar2Command command = req.getCommand(commandName);
    command.removeParameter(parameterName);
    stateMgr.checkIn(command);    
  }
  
  protected void removeCommand (String commandName) {
    Pazpar2Command command = req.getCommand(commandName);
    command.removeParameters();
    stateMgr.checkIn(command);
  }
    
  protected String getCommandParameterValue (String commandName, String parameterName, String defaultValue) {    
    CommandReadOnly command = req.getCommandReadOnly(commandName);
    if (command != null) {
      String parameter = command.getParameterValue(parameterName);
      if (parameter != null) {
        return parameter;
      }
    }
    return defaultValue;    
  }
    
  protected int getCommandParameterValue (String commandName, String parameterName, int defaultValue) {
    CommandReadOnly command = req.getCommandReadOnly(commandName);
    if (command != null) {
      String parameter = command.getParameterValue(parameterName);
      if (parameter != null) {
        return Integer.parseInt(parameter);
      }
    }
    return defaultValue;    
  }

  protected String doCommand(String commandName) {             
    logger.debug(req.getCommandReadOnly(commandName).getEncodedQueryString() + ": Results for "+ req.getCommandReadOnly("search").getEncodedQueryString());
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
  public void stateUpdated(String commandName) {
    logger.debug("State change reported for [" + commandName + "]");
    if (commandName.equals("show")) {
      logger.debug("Updating show");
      update(commandName);
    } 
  }
  
}
