package com.indexdata.pz2utils4jsf.pazpar2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;

import com.indexdata.pz2utils4jsf.config.ConfigurationReader;
import com.indexdata.pz2utils4jsf.controls.ResultsPager;
import com.indexdata.pz2utils4jsf.errors.ConfigurationError;
import com.indexdata.pz2utils4jsf.errors.ConfigurationException;
import com.indexdata.pz2utils4jsf.errors.ErrorHelper;
import com.indexdata.pz2utils4jsf.errors.ErrorInterface;
import com.indexdata.pz2utils4jsf.pazpar2.commands.CommandParameter;
import com.indexdata.pz2utils4jsf.pazpar2.commands.Pazpar2Commands;
import com.indexdata.pz2utils4jsf.pazpar2.data.Pazpar2ResponseData;
import com.indexdata.pz2utils4jsf.pazpar2.data.Pazpar2ResponseParser;
import com.indexdata.pz2utils4jsf.pazpar2.data.Pazpar2Responses;
import com.indexdata.pz2utils4jsf.pazpar2.data.RecordResponse;
import com.indexdata.pz2utils4jsf.pazpar2.state.StateListener;
import com.indexdata.pz2utils4jsf.pazpar2.state.StateManager;
import com.indexdata.pz2utils4jsf.utils.Utils;

@Named("pz2") @SessionScoped @Alternative
public class Pz2Bean implements Pz2Interface, StateListener, Serializable {

  private static final long serialVersionUID = 3440277287081557861L;
  private static Logger logger = Logger.getLogger(Pz2Bean.class);
  private static Logger responseLogger = Logger.getLogger("com.indexdata.pz2utils4jsf.pazpar2.responses");
  
  protected SearchClient searchClient = null;
  
  @Inject ConfigurationReader configurator;
  @Inject StateManager stateMgr;
  @Inject Pazpar2Commands pzreq;
  @Inject Pazpar2Responses pzresp;
  
  protected ResultsPager pager = null; 

  protected List<ErrorInterface> configurationErrors = null;
  protected ErrorHelper errorHelper = null;
              
  public Pz2Bean () {
    logger.info("Instantiating pz2 bean [" + Utils.objectId(this) + "]");    
  }
  
  @PostConstruct
  public void postConstruct() {    
    logger.debug("in start of Pz2Bean post-construct configurator is " + configurator);
    logger.debug(Utils.objectId(this) + " will instantiate a Pz2Client next.");
    searchClient = new Pz2Client();
    logger.info("Using [" + Utils.objectId(searchClient) + "] configured by [" 
                          + Utils.objectId(configurator) + "]" );    
    configureClient(searchClient,configurator);
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
      //
      // Commented as I'm trying with regular instantiation instead
      // this.searchClient = searchClient.cloneMe();         
    } catch (ConfigurationException e) {
      configurationErrors.add(new ConfigurationError("Search Client","Configuration",e.getMessage(),new ErrorHelper(configReader)));          
    } 
    logger.info(configReader.document());
    pzresp.reset();    
  }

    
  public void doSearch(String query) {
    pzreq.getSearch().setParameter(new CommandParameter("query","=",query));     
    doSearch();
  }

  public void doSearch() { 
    stateMgr.hasPendingStateChange("search",false);
    pzresp.reset();
    // resets some record and show command parameters without 
    // changing state or creating state change feedback
    pzreq.getRecordInState().removeParametersSilently();        
    pzreq.getShowInState().setParameterSilently(new CommandParameter("start","=",0));    
    logger.debug(Utils.objectId(this) + " is searching using "+pzreq.getCommandReadOnly("search").getUrlEncodedParameterValue("query"));
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
      if (commandsAreValid(commands)) {
        if (hasQuery()) {
          handleQueryStateChanges(commands);
          logger.debug("Processing request for " + commands); 
          List<CommandThread> threadList = new ArrayList<CommandThread>();
          StringTokenizer tokens = new StringTokenizer(commands,",");
          while (tokens.hasMoreElements()) {          
            threadList.add(new CommandThread(pzreq.getCommandReadOnly(tokens.nextToken()),searchClient));            
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
             responseLogger.debug("Response was: " + response);
             Pazpar2ResponseData responseObject = Pazpar2ResponseParser.getParser().getDataObject(response);
             pzresp.put(commandName, responseObject);        
          }
          if (commands.equals("record")) {
            logger.debug("Record: Active clients: "+pzresp.getRecord().getActiveClients());
            return pzresp.getRecord().getActiveClients();
          } else {
            return pzresp.getActiveClients();
          }  
        } else {
          logger.debug("Skipped requests for " + commands + " as there's not yet a query."); 
          pzresp.reset();
          return "0";
        }
      } else {
        logger.error("Did not attemt to run command(s) due to a validation error.");
        return "0";
      }
    } else {      
      logger.error("Did not attempt to execute query since there are configuration errors.");
      return "0";
    }
    
  }
  
  public boolean commandsAreValid(String commands) {
    if (commands.equals("record")) {
      if (!pzreq.getCommandReadOnly("record").hasParameterSet("id")) {
        logger.error("Attempt to send record command without the id parameter");
        return false;
      }
    }
    return true;
  }
                                
  public String toggleRecord (String recId) {
    if (hasRecord(recId)) {
      pzreq.getRecord().removeParameters();  
      pzresp.put("record", new RecordResponse());
      return "";
    } else {
      pzreq.getRecord().setId(recId);
      return doCommand("record");
    }
  }
  
  @Override
  public boolean hasRecord (String recId) {
    return pzreq.getCommandReadOnly("record").hasParameters() && pzresp.getRecord().getRecId().equals(recId);
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
    return pzresp.hasApplicationError();
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
  
  
  protected boolean hasQuery() {        
    return pzreq.getCommandReadOnly("search").hasParameterSet("query"); 
  }
    
    
  public ResultsPager getPager () {
    if (pager == null) {
      pager = new ResultsPager(pzresp);      
    } 
    return pager;      
  }
  
  public ResultsPager setPager (int pageRange) {
    pager =  new ResultsPager(pzresp,pageRange,pzreq);
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
      if (pzreq.getCommandReadOnly("record").hasParameterSet("id")) {
        update("record");
      } else {         
        pzresp.put("record", new RecordResponse());
      }
    }
  }
  
  protected String doCommand(String commandName) {             
    logger.debug(pzreq.getCommandReadOnly(commandName).getEncodedQueryString() + ": Results for "+ pzreq.getCommandReadOnly("search").getEncodedQueryString());
    return update(commandName);
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
