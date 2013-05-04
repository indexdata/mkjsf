package com.indexdata.mkjsf.pazpar2;

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

import com.indexdata.mkjsf.config.ConfigurationReader;
import com.indexdata.mkjsf.controls.ResultsPager;
import com.indexdata.mkjsf.errors.ConfigurationError;
import com.indexdata.mkjsf.errors.ConfigurationException;
import com.indexdata.mkjsf.errors.ErrorCentral;
import com.indexdata.mkjsf.errors.ErrorHelper;
import com.indexdata.mkjsf.pazpar2.commands.CommandParameter;
import com.indexdata.mkjsf.pazpar2.commands.Pazpar2Commands;
import com.indexdata.mkjsf.pazpar2.data.Pazpar2ResponseData;
import com.indexdata.mkjsf.pazpar2.data.Pazpar2ResponseParser;
import com.indexdata.mkjsf.pazpar2.data.Pazpar2Responses;
import com.indexdata.mkjsf.pazpar2.data.RecordResponse;
import com.indexdata.mkjsf.pazpar2.state.StateListener;
import com.indexdata.mkjsf.pazpar2.state.StateManager;
import com.indexdata.mkjsf.utils.Utils;

@Named("pz2") @SessionScoped @Alternative
public class Pz2Bean implements Pz2Interface, StateListener, Serializable {

  private static final long serialVersionUID = 3440277287081557861L;
  private static Logger logger = Logger.getLogger(Pz2Bean.class);
  private static Logger responseLogger = Logger.getLogger("com.indexdata.mkjsf.pazpar2.responses");
  
  protected SearchClient searchClient = null;
  
  @Inject ConfigurationReader configurator;
  @Inject StateManager stateMgr;
  @Inject Pazpar2Commands pzreq;
  @Inject Pazpar2Responses pzresp;
  @Inject ErrorCentral errors;
  
  protected ResultsPager pager = null; 

  
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
    logger.debug(Utils.objectId(this) + " will configure search client for the session");
    try {
      searchClient.configure(configReader);            
    } catch (ConfigurationException e) {
      logger.debug("Pz2Bean adding configuration error");
      errors.addConfigurationError(new ConfigurationError("Search Client","Configuration",e.getMessage()));                
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
    pzreq.getRecord().removeParametersInState();        
    pzreq.getShow().setParameterInState(new CommandParameter("start","=",0));    
    logger.debug(Utils.objectId(this) + " is searching using "+pzreq.getCommand("search").getUrlEncodedParameterValue("query"));
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
    try {
    if (! errors.hasConfigurationErrors()) {
      if (commandsAreValid(commands)) {
        if (hasQuery() || (commands.equals("record") && pzreq.getCommand("record").hasParameterValue("recordquery"))) {
          handleQueryStateChanges(commands);
          logger.debug("Processing request for " + commands); 
          List<CommandThread> threadList = new ArrayList<CommandThread>();
          StringTokenizer tokens = new StringTokenizer(commands,",");
          while (tokens.hasMoreElements()) {          
            threadList.add(new CommandThread(pzreq.getCommand(tokens.nextToken()),searchClient));            
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
             String commandName = thread.getCommand().getCommandName();
             CommandResponse response = thread.getCommandResponse();
             responseLogger.debug("Response was: " + response.getResponseString());
             Pazpar2ResponseData responseObject = Pazpar2ResponseParser.getParser().getDataObject(response.getResponseString());
             if (Pazpar2ResponseParser.docTypes.contains(responseObject.getType())) {
               pzresp.put(commandName, responseObject);
             } else {
               if (commandName.equals("record") && 
                   (pzreq.getRecord().hasParameterValue("offset") ||
                    pzreq.getRecord().hasParameterValue("checksum"))) {
                 RecordResponse recordResponse = new RecordResponse();
                 recordResponse.setType("record");
                 recordResponse.setXml(responseObject.getXml());
                 recordResponse.setAttribute("activeclients", "0");
                 pzresp.put(commandName, recordResponse);
               }
             }
          }
          if (commands.equals("record")) {
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
    } catch (ClassCastException cce) {
      cce.printStackTrace();    
      return "";
    } catch (NullPointerException npe) {
      npe.printStackTrace();
      return "";
    } catch (Exception e) {
      e.printStackTrace();
      return "";
    }
    
  }
  
  public boolean commandsAreValid(String commands) {
    if (commands.equals("record")) {
      if (!pzreq.getCommand("record").hasParameterValue("id")) {
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
    return pzreq.getCommand("record").hasParameters() && pzresp.getRecord().getRecId().equals(recId);
  }
        
  public String getCurrentStateKey () {    
    return stateMgr.getCurrentState().getKey();
  }
      
  public void setCurrentStateKey(String key) {       
    stateMgr.setCurrentStateKey(key);
  }
      
  protected boolean hasQuery() {        
    return pzreq.getCommand("search").hasParameterValue("query"); 
  }
    
    
  @Override
  public ResultsPager getPager () {
    if (pager == null) {
      pager = new ResultsPager(pzresp);      
    } 
    return pager;      
  }
  
  @Override
  public ResultsPager setPager (int pageRange) {
    pager =  new ResultsPager(pzresp,pageRange,pzreq);
    return pager;
  }
    
  protected void handleQueryStateChanges (String commands) {
    if (stateMgr.hasPendingStateChange("search") && hasQuery()) { 
      logger.debug("Found pending search change. Doing search before updating " + commands);      
      doSearch();
    } 
    if (stateMgr.hasPendingStateChange("record") && ! commands.equals("record")) {        
      logger.debug("Found pending record ID change. Doing record before updating " + commands);
      stateMgr.hasPendingStateChange("record",false);
      if (pzreq.getCommand("record").hasParameterValue("id")) {
        update("record");
      } else {         
        pzresp.put("record", new RecordResponse());
      }
    }
  }
  
  protected String doCommand(String commandName) {             
    logger.debug(pzreq.getCommand(commandName).getEncodedQueryString() + ": Results for "+ pzreq.getCommand("search").getEncodedQueryString());
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
  
  @Override
  public boolean getAuthenticationRequired () {
    return searchClient.isAuthenticatingClient();
  }

  @Override
  public String getCheckHistory () {
    return ":pz2watch:stateForm:windowlocationhash";
  }
    
  @Override
  public String getWatchActiveclients () {
    return ":pz2watch:activeclientsForm:activeclientsField";
  }
  
  @Override
  public String getWatchActiveclientsRecord () {
    return ":pz2watch:activeclientsForm:activeclientsFieldRecord";
  }
  
}
