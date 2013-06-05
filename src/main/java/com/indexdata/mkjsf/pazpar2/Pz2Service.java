package com.indexdata.mkjsf.pazpar2;

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Qualifier;

import org.apache.log4j.Logger;

import com.indexdata.mkjsf.config.Configurable;
import com.indexdata.mkjsf.config.Configuration;
import com.indexdata.mkjsf.config.ConfigurationReader;
import com.indexdata.mkjsf.controls.ResultsPager;
import com.indexdata.mkjsf.errors.ConfigurationError;
import com.indexdata.mkjsf.errors.ConfigurationException;
import com.indexdata.mkjsf.errors.ErrorCentral;
import com.indexdata.mkjsf.errors.ErrorHelper;
import com.indexdata.mkjsf.errors.MissingConfigurationContextException;
import com.indexdata.mkjsf.pazpar2.commands.Pazpar2Commands;
import com.indexdata.mkjsf.pazpar2.data.RecordResponse;
import com.indexdata.mkjsf.pazpar2.data.Responses;
import com.indexdata.mkjsf.pazpar2.state.StateListener;
import com.indexdata.mkjsf.pazpar2.state.StateManager;
import com.indexdata.mkjsf.utils.Utils;

@Named("pz2") @SessionScoped
public class Pz2Service implements StateListener, Configurable, Serializable {

  private static final String MODULE_NAME = "service";
  private static String SERVICE_TYPE_TBD = "TBD", SERVICE_TYPE_PZ2 = "PZ2", SERVICE_TYPE_SP = "SP";
  private static final List<String> serviceTypes = 
                Arrays.asList(SERVICE_TYPE_PZ2,SERVICE_TYPE_SP,SERVICE_TYPE_TBD);
  private String serviceType = SERVICE_TYPE_TBD;
  private List<String> serviceProxyUrls = new ArrayList<String>();
  public static final String SERVICE_PROXY_URL_LIST = "SERVICE_PROXY_URL_LIST";
  private List<String> pazpar2Urls = new ArrayList<String>();
  public static final String PAZPAR2_URL_LIST = "PAZPAR2_URL_LIST";

  private static final long serialVersionUID = 3440277287081557861L;
  private static Logger logger = Logger.getLogger(Pz2Service.class);  
  protected Pz2Client pz2Client = null;
  protected ServiceProxyClient spClient = null;
  protected SearchClient searchClient = null;  
    
  @Inject ConfigurationReader configurator;
  
  private StateManager stateMgr = null;
  private Pazpar2Commands pzreq = null;
  private Responses pzresp = null;
  private ErrorCentral errors = null;  
  
  protected ResultsPager pager = null; 
  
  protected ErrorHelper errorHelper = null;
              
  public Pz2Service () {
    logger.info("Instantiating pz2 bean [" + Utils.objectId(this) + "]");    
  }
  
  public static Pz2Service get() {
    FacesContext context = FacesContext.getCurrentInstance();
    return (Pz2Service) context.getApplication().evaluateExpressionGet(context, "#{pz2}", Object.class); 
  }
    
  @PostConstruct
  public void postConstruct() throws MissingConfigurationContextException {    
    logger.info("Pz2Service PostConstruct of " + this);
    stateMgr = new StateManager();
    pzreq = new Pazpar2Commands();
    pzresp = new Responses();    
    errors = new ErrorCentral(); 
    pzresp.setErrorHelper(errors.getHelper());
    
    logger.debug("Pz2Service PostConstruct: Configurator is " + configurator);
    logger.debug(Utils.objectId(this) + " will instantiate a Pz2Client next.");    
    pz2Client = new Pz2Client();
    spClient = new ServiceProxyClient();
    stateMgr.addStateListener(this);
    try {
      configureClient(pz2Client,configurator);      
      configureClient(spClient,configurator);
      this.configure(configurator);
    } catch (MissingConfigurationContextException mcc) {
      logger.info("No configuration context available at this point");
      logger.debug("Configuration invoked from a Servlet filter before application start?");
      throw mcc;
    } catch (ConfigurationException e) {
      logger.warn("There was a problem configuring the Pz2Service and/or clients (\"pz2\")");
      e.printStackTrace();
    }     
  }
  
  @Qualifier
  @Target({java.lang.annotation.ElementType.TYPE,
           java.lang.annotation.ElementType.METHOD,
           java.lang.annotation.ElementType.PARAMETER,
           java.lang.annotation.ElementType.FIELD})
  @Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
  public  @interface Preferred{}
  
  @Produces @Preferred @SessionScoped @Named("pzresp") public Responses getPzresp () {
    logger.trace("Producing pzresp");
    return pzresp;
  }
  
  @Produces @Preferred @SessionScoped @Named("pzreq") public Pazpar2Commands getPzreq () {
    logger.trace("Producing pzreq");
    return pzreq;
  }
  
  @Produces @Preferred @SessionScoped @Named("errors") public ErrorCentral getErrors() {
    logger.trace("Producing errors");
    return errors;
  }
  
  @Produces @Preferred @SessionScoped @Named("stateMgr") public StateManager getStateMgr() {
    logger.trace("Producing stateMgr");
    return stateMgr;
  }
  
  public void configureClient(SearchClient client, ConfigurationReader configReader)  throws MissingConfigurationContextException {
    logger.debug(Utils.objectId(this) + " will configure search client for the session");
    try {
      client.configure(configReader);
    } catch (MissingConfigurationContextException mcce) {
      logger.info("No Faces context is available to the configurator at this time of invocation");
      throw mcce;
    } catch (ConfigurationException e) {
      logger.debug("Pz2Service adding configuration error");
      errors.addConfigurationError(new ConfigurationError("Search Client","Configuration",e.getMessage()));                
    }
    logger.info(configReader.document());
    pzresp.getSp().resetAuthAndBeyond(true);    
  }
  
  public void resetSearchAndRecordCommands () {
    pzreq.getRecord().removeParametersInState();
    pzreq.getSearch().removeParametersInState();   
  }
     
  
  /**
   * Updates display data objects by issuing the following pazpar2 commands: 
   * 'show', 'stat', 'termlist' and 'bytarget'.
   * 
   * If there is an outstanding change to the search command, a search
   * will be issued before the updates are performed. 
   *  
   * Returns a count of the remaining active clients from the most recent search.
   * 
   * After refreshing the data from pazpar2 the UI components displaying those 
   * data should be re-rendered.
   * 
   * @return count of activeclients 
   */  
  public String update () {
    logger.debug("Updating show,stat,termlist,bytarget from pazpar2");
    if (errors.hasConfigurationErrors()) {
      logger.error("Ignoring show,stat,termlist,bytarget commands due to configuration errors.");
      return "";
    } else if (pzresp.getSearch().hasApplicationError()) {
      logger.error("Ignoring show,stat,termlist,bytarget commands due to problem with most recent search.");
      return "";
    } else if (!hasQuery()) {
      logger.debug("Ignoring show,stat,termlist,bytarget commands because there is not yet a query.");
      return "";
    } else {
      return update("show,stat,termlist,bytarget");
    }
  }
     
  /**
   * Refreshes the data objects listed in 'commands' from pazpar2
   * 
   * @param commands
   * @return Number of activeclients at the time of the 'show' command,
   *         or 'new' if search was just initiated.
   */
  public String update (String commands) {
    logger.debug("Request to update: " + commands);
    try {
      if (commands.equals("search")) {
        pzreq.getSearch().run();
        pzresp.getSearch().setIsNew(false);
        return "new";
      } else if (commands.equals("record")) {
        pzreq.getRecord().run();
        return pzresp.getRecord().getActiveClients();
      } else if (pzresp.getSearch().isNew()) {
        // For returning notification of 'search started' quickly to UI
        logger.info("New search. Marking it old, then returning 'new' to trigger another round-trip.");
        pzresp.getSearch().setIsNew(false);
        return "new";
      } else {
        handleQueryStateChanges(commands);
        if (pzresp.getSearch().hasApplicationError()) {
          logger.error("The command(s) " + commands + " cancelled because the latest search command had an error.");
          return "0";
        } else if (errors.hasConfigurationErrors()) {
          logger.error("The command(s) " + commands + " cancelled due to configuration errors.");
          return "0";
        } else {
          logger.debug("Processing request for " + commands); 
          List<CommandThread> threadList = new ArrayList<CommandThread>();
          StringTokenizer tokens = new StringTokenizer(commands,",");
          while (tokens.hasMoreElements()) {          
            threadList.add(new CommandThread(pzreq.getCommand(tokens.nextToken()),searchClient,Pz2Service.get().getPzresp()));            
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
          return pzresp.getActiveClients();
        }
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
  
  /**
   * This methods main purpose is to support browser history.
   *  
   * When the browsers back or forward buttons are pressed, a  
   * re-search /might/ be required - namely if the query changes.
   * So, as the UI requests updates of the page (show,facets,
   * etc) this method checks if a search must be executed
   * before those updates are performed.
   *  
   * @see {@link com.indexdata.mkjsf.pazpar2.state.StateManager#setCurrentStateKey} 
   * @param commands
   */
  protected void handleQueryStateChanges (String commands) {
    if (stateMgr.hasPendingStateChange("search") && hasQuery()) { 
      logger.info("Triggered search: Found pending search change [" + pzreq.getCommand("search").toString() + "], doing search before updating " + commands);      
      pzreq.getSearch().run();
      pzresp.getSearch().setIsNew(false);
    } 
    if (stateMgr.hasPendingStateChange("record") && ! commands.equals("record")) {        
      logger.debug("Found pending record ID change. Doing record before updating " + commands);
      stateMgr.hasPendingStateChange("record",false);
      pzreq.getRecord().run();
    }
  }
      
  @Override
  public void stateUpdated(String commandName) {
    logger.debug("State change reported for [" + commandName + "]");
    if (commandName.equals("show")) {
      logger.debug("Updating show");
      update(commandName);
    } 
  }

      
  /**
   * Will retrieve -- or alternatively remove -- the record with the given 
   * recid from memory.
   * 
   * A pazpar2 'record' command will then be issued. The part of the UI 
   * showing record data should thus be re-rendered.
   *  
   * @param recid
   * @return
   */
  public String toggleRecord (String recId) {
    if (hasRecord(recId)) {
      pzreq.getRecord().removeParameters();  
      pzresp.put("record", new RecordResponse());
      return "";
    } else {
      pzreq.getRecord().setId(recId);
      pzreq.getRecord().run();
      // doCommand("record");
      return pzresp.getRecord().getActiveClients();
    }
  }
  
  /**
   * Resolves whether the backend has a record with the given recid in memory 
   * 
   * @return true if the bean currently holds the record with recid
   */  
  public boolean hasRecord (String recId) {
    return pzreq.getCommand("record").hasParameters() && pzresp.getRecord().getRecId().equals(recId);
  }
        
  /**
   * Returns the current hash key, used for internal session state tracking
   * and potentially for browser history entries
   * 
   * A UI author would not normally be concerned with retrieving this. It's used by the
   * framework internally
   *  
   * @return string that can be used for browsers window.location.hash
   */
  public String getCurrentStateKey () {    
    return stateMgr.getCurrentState().getKey();
  }
      
  /**
   * Sets the current state key, i.e. when user clicks back or forward in browser history.
   * Would normally be automatically handled by the frameworks components.
   *  
   * @param key corresponding to browsers hash string
   */
  public void setCurrentStateKey(String key) {       
    stateMgr.setCurrentStateKey(key);
  }
      
  protected boolean hasQuery() {        
    return pzreq.getCommand("search").hasParameterValue("query"); 
  }
    
  /**
   * Returns a component for drawing a pager to navigate by.
   * @return ResultsPager pager component
   */
  public ResultsPager getPager () {
    if (pager == null) {
      pager = new ResultsPager(pzresp);      
    } 
    return pager;      
  }
  
 /**
  * Initiates a pager object, a component holding the data to draw a sequence
  * of page numbers to navigate by and mechanisms to navigate with
  * 
  * @param pageRange number of pages to display in the pager
  * @return ResultsPager the initiated pager component
  */
  public ResultsPager setPager (int pageRange) {
    pager =  new ResultsPager(pzresp,pageRange,pzreq);
    return pager;
  }
     
  public void setServiceProxyUrl(String url) {
    searchClient = spClient;
    setServiceType(SERVICE_TYPE_SP);
    setServiceUrl(url);
  }
  
  public String getServiceProxyUrl () {
    if (isServiceProxyService()) {
      return spClient.getServiceUrl();
    } else {
      return "";
    }
  }
  
  public void setPazpar2Url(String url) {
    searchClient = pz2Client;
    setServiceType(SERVICE_TYPE_PZ2);
    setServiceUrl(url);
  }
  
  public String getPazpar2Url() {
    if (isPazpar2Service()) {
      return pz2Client.getServiceUrl();
    } else {
      return "";
    }
  }

  public void setServiceUrl(String url) {
    if (url!=null && searchClient != null && !url.equals(searchClient.getServiceUrl())) {
      pzreq.getRecord().removeParametersInState();
      pzreq.getSearch().removeParametersInState();
      pzresp.getSp().resetAuthAndBeyond(true);      
      searchClient.setServiceUrl(url);
    }    
  }
  
  public String getServiceUrl() {
    return (searchClient!=null ? searchClient.getServiceUrl() : "");
  }
  
  public void setServiceId () {
    pzreq.getRecord().removeParametersInState();
    pzreq.getSearch().removeParametersInState();
    pzresp.resetSearchAndBeyond();
    pz2Client.setServiceId(pzreq.getInit().getService());
  }
  
  public String getServiceId () {
    return pzreq.getInit().getService();
  }
  
  public boolean getServiceUrlIsDefined() {
    return (searchClient != null && searchClient.hasServiceUrl());
  }
  
  public List<String> getServiceProxyUrls() {
    List<String> urls = new ArrayList<String>();
    urls.add("");
    urls.addAll(serviceProxyUrls);
    return urls;
  }
  
  public List<String> getPazpar2Urls () {
    List<String> urls = new ArrayList<String>();
    urls.add("");
    urls.addAll(pazpar2Urls);
    return urls;
  }
  
  public String getServiceType () {
    return serviceType;
  }
  
  public boolean isPazpar2Service () {
    return serviceType.equals(SERVICE_TYPE_PZ2);
  }
  
  public boolean isServiceProxyService() {
    return serviceType.equals(SERVICE_TYPE_SP);
  }
  
  public boolean serviceIsToBeDecided () {
    return serviceType.equals(SERVICE_TYPE_TBD);
  }
  
  public ServiceProxyClient getSpClient () {
    return spClient;
  }  
  
  public boolean getAuthenticationRequired () {
    return spClient.isAuthenticatingClient();
  }

  public String getCheckHistory () {
    return ":pz2watch:stateForm:windowlocationhash";
  }
    
  public String getWatchActiveclients () {
    return ":pz2watch:activeclientsForm:activeclientsField";
  }
  
  public String getWatchActiveclientsRecord () {
    return ":pz2watch:activeclientsForm:activeclientsFieldRecord";
  }

  @Override
  public void configure(ConfigurationReader reader)
      throws ConfigurationException {
    Configuration config = reader.getConfiguration(this);
    if (config == null) {
      serviceType = SERVICE_TYPE_TBD;
    } else {
      String service = config.get("TYPE");
      if (service == null || service.length()==0) {
        serviceType = SERVICE_TYPE_TBD;
      } else if (serviceTypes.contains(service.toUpperCase())) {        
        setServiceType(service.toUpperCase());
      } else {
        logger.error("Unknown serviceType type in configuration [" + service + "], can be one of " + serviceTypes);
        serviceType = SERVICE_TYPE_TBD;
      }
      serviceProxyUrls = config.getMultiProperty(SERVICE_PROXY_URL_LIST,",");
      pazpar2Urls = config.getMultiProperty(PAZPAR2_URL_LIST, ",");
    }
    logger.info(reader.document());
    logger.info("Service Type is configured to " + serviceType);
    
  }

  @Override
  public Map<String, String> getDefaults() {
    return new HashMap<String,String>();
  }

  @Override
  public String getModuleName() {
    return MODULE_NAME;
  }

  @Override
  public List<String> documentConfiguration() {
    return new ArrayList<String>();
  }

  public void setServiceTypePZ2() {
    setServiceType(SERVICE_TYPE_PZ2);    
  }

  public void setServiceTypeSP() {
    setServiceType(SERVICE_TYPE_SP);        
  }

  public void setServiceTypeTBD() {
    setServiceType(SERVICE_TYPE_TBD);    
  }
  
  private void setServiceType(String type) {
    if (!serviceType.equals(type)  &&
        !serviceType.equals(SERVICE_TYPE_TBD)) {
      resetSearchAndRecordCommands();
      pzresp.getSp().resetAuthAndBeyond(true);
    }
    serviceType = type;
    if (serviceType.equals(SERVICE_TYPE_PZ2)) {
      searchClient = pz2Client;
      logger.info("Setting a Pazpar2 client to serve requests.");
    } else if (serviceType.equals(SERVICE_TYPE_SP)) {
      searchClient = spClient;
      logger.info("Setting a Service Proxy client to serve requests.");
    } else {
      logger.info("Clearing search client. No client defined to serve requests at this point.");
      searchClient = null;
    }
  }
  
  public SearchClient getSearchClient() {
    return searchClient;
  }
  
}
