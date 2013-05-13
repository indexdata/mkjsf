package com.indexdata.mkjsf.pazpar2;

import static com.indexdata.mkjsf.utils.Utils.nl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.indexdata.masterkey.config.MissingMandatoryParameterException;
import com.indexdata.masterkey.config.ModuleConfigurationGetter;
import com.indexdata.masterkey.pazpar2.client.ClientCommand;
import com.indexdata.masterkey.pazpar2.client.Pazpar2Client;
import com.indexdata.masterkey.pazpar2.client.Pazpar2ClientConfiguration;
import com.indexdata.masterkey.pazpar2.client.Pazpar2ClientGeneric;
import com.indexdata.masterkey.pazpar2.client.Pazpar2HttpResponse;
import com.indexdata.masterkey.pazpar2.client.exceptions.Pazpar2ErrorException;
import com.indexdata.masterkey.pazpar2.client.exceptions.ProxyErrorException;
import com.indexdata.mkjsf.config.Configuration;
import com.indexdata.mkjsf.config.ConfigurationReader;
import com.indexdata.mkjsf.errors.ConfigurationException;
import com.indexdata.mkjsf.pazpar2.commands.Pazpar2Command;
import com.indexdata.mkjsf.pazpar2.data.CommandError;
import com.indexdata.mkjsf.utils.Utils;

public class Pz2Client implements SearchClient {

  private static final long serialVersionUID = 5414266730169982028L;
  private static Logger logger = Logger.getLogger(Pz2Client.class);
  private transient Pazpar2Client client = null;
  private Pazpar2ClientConfiguration cfg = null;
  public static final String MODULENAME = "pz2client";
  public static Map<String,String> DEFAULTS = new HashMap<String,String>();
  Configuration config = null;  
  
  static {    
    DEFAULTS.put("PAZPAR2_URL", "");
    DEFAULTS.put("PROXY_MODE","1");
    DEFAULTS.put("SERIALIZE_REQUESTS", "false");
    DEFAULTS.put("STREAMBUFF_SIZE", "4096");
    DEFAULTS.put("PARSE_RESPONSES", "true");    
  }
  
  public Pz2Client() {}
  
  @Override
  public void configure(ConfigurationReader configReader) throws ConfigurationException {    
    logger.info(Utils.objectId(this) + " is configuring using the provided " + Utils.objectId(configReader));
    try {
      config = configReader.getConfiguration(this);      
      cfg = new Pazpar2ClientConfiguration(new ConfigurationGetter(config));
    } catch (ProxyErrorException pe) {
      logger.error("Could not configure Pazpar2 client: " + pe.getMessage());
      throw new ConfigurationException("Could not configure Pz2Client:  "+ pe.getMessage(),pe);
    } 
    if (cfg != null) {
      try {
        client = new Pazpar2ClientGeneric(cfg);  
      } catch (ProxyErrorException pe) {
        logger.error("Could not configure Pazpar2 client: " + pe.getMessage());
        throw new ConfigurationException("Could not configure Pz2Client:  "+ pe.getMessage(),pe);
      }
    } else {
      logger.error("There was a problem creating Pz2Client. Client is null after configuration.");
      throw new ConfigurationException("Pazpar2Client is null after configuration");
    } 
  }
  
  public boolean isAuthenticatingClient () {
    return false;
  }
  
  public boolean isAuthenticated() {
    return false;
  }
  
  public boolean authenticate() {
    throw new UnsupportedOperationException("No authentication mechanism for straight pazpar2 client");
  }
  
  @Override
  public void setSearchCommand(Pazpar2Command command) {
    ClientCommand clientCommand = new ClientCommand(command.getCommandName(), command.getEncodedQueryString());
    client.setSearchCommand(clientCommand);    
  }

  @Override
  public HttpResponseWrapper executeCommand(Pazpar2Command command) {
    ClientCommandResponse commandResponse = null;
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ClientCommand clientCommand = new ClientCommand(command.getCommandName(), command.getEncodedQueryString());
    Pazpar2HttpResponse pz2HttpResponse = null;
    long start = System.currentTimeMillis();
    try {
      pz2HttpResponse = client.executeCommand(clientCommand, baos);
      if (pz2HttpResponse.getStatusCode()==200 && pz2HttpResponse.getContentType().contains("xml")) {
        commandResponse = new ClientCommandResponse(pz2HttpResponse,baos);
      } else if (pz2HttpResponse.getStatusCode()==417) {
        logger.error("Pazpar2 status code 417: " + baos.toString("UTF-8"));
        commandResponse = new ClientCommandResponse(pz2HttpResponse.getStatusCode(),CommandError.insertErrorXml(command.getCommandName(), String.valueOf(pz2HttpResponse.getStatusCode()) ,"Pazpar2: Expectation failed (417)", baos.toString("UTF-8")),"text/xml");                       
      } else if (pz2HttpResponse.getContentType().contains("html")) {
        String resp = baos.toString("UTF-8");
        logger.error("HTML response where XML was expected. Status code was " + pz2HttpResponse.getStatusCode() + ": " + resp);
        String htmlStrippedOfTags = resp.replaceAll("\\<[^>]*>","");
        String errorXml = "";
        if (htmlStrippedOfTags.toLowerCase().contains("domain")) {
          errorXml = CommandError.createErrorXml(command.getCommandName(), String.valueOf(pz2HttpResponse.getStatusCode()), "Unexpected response type from Pazpar2", "Error: Expected XML response from Pazpar2 but got HTML. The HTML contains the word domain suggesting that the Pazpar2 address was not found.", htmlStrippedOfTags);
        } else {  
          errorXml = CommandError.createErrorXml(command.getCommandName(), String.valueOf(pz2HttpResponse.getStatusCode()), "Unexpected response type from Pazpar2: " + pz2HttpResponse.getContentType(),"Expected XML response from Pazpar2, got HTML", htmlStrippedOfTags);
        } 
        commandResponse = new ClientCommandResponse(pz2HttpResponse.getStatusCode(),errorXml,pz2HttpResponse.getContentType());        
      } else {
        String resp = baos.toString("UTF-8");
        logger.error("Pazpar2 status code was " + pz2HttpResponse.getStatusCode() + ": " + resp);
        commandResponse = new ClientCommandResponse(pz2HttpResponse.getStatusCode(),CommandError.insertErrorXml(command.getCommandName(), String.valueOf(pz2HttpResponse.getStatusCode()), "Pazpar2 error occurred", baos.toString("UTF-8")),"text/xml");        
      }       
    } catch (IOException e) {
      logger.error(e.getMessage());
      e.printStackTrace();
      commandResponse = new ClientCommandResponse(pz2HttpResponse.getStatusCode(),CommandError.createErrorXml(command.getCommandName(), String.valueOf(pz2HttpResponse.getStatusCode()), "IO exception", e.getMessage(), ""),"text/xml");      
    } catch (Pazpar2ErrorException e) {
      logger.error(e.getMessage());
      e.printStackTrace();
      logger.error("Creating error XML");
      commandResponse = new ClientCommandResponse(500,CommandError.createErrorXml(command.getCommandName(), "", "Pazpar2Error", e.getMessage(),""),"text/xml");
    }
    long end = System.currentTimeMillis();      
    logger.debug("Executed " + command.getCommandName() + " in " + (end-start) + " ms." );
    return commandResponse;
  }

  public Pz2Client cloneMe() {
    logger.debug("Cloning Pz2Client");
    Pz2Client clone = new Pz2Client();
    clone.client = this.client;
    clone.cfg = this.cfg;
    return clone;
  }

  @Override
  public Map<String, String> getDefaults() {
    return DEFAULTS;
  }

  @Override
  public String getModuleName() {
    return MODULENAME;
  }
  
  class ConfigurationGetter implements ModuleConfigurationGetter {
    Configuration config = null;
    ConfigurationGetter(Configuration configuration) {
      config = configuration;
    }
    @Override
    public String get(String value) {
      return config.get(value);
    }
    @Override
    public String get(String value, String defaultValue) {
      return config.get(value,defaultValue);
    }
    @Override
    public String getMandatory(String name)
        throws MissingMandatoryParameterException {
      return config.getMandatory(name);
    }
    @Override
    public String getConfigFilePath() {
      return config.getConfigFilePath();
    }
  }

  @Override
  public List<String> documentConfiguration() {
    List<String> doc = new ArrayList<String>();
    doc.add(nl+ MODULENAME + " was configured to access Pazpar2 at : " + cfg.PAZPAR2_URL);    
    return new ArrayList<String>();
  }
  
  public Configuration getConfiguration () {
    return config;
  }

  @Override
  public String getServiceUrl() {
    return cfg.PAZPAR2_URL;    
  }

  @Override
  public boolean hasServiceUrl() {
    return cfg.PAZPAR2_URL != null && cfg.PAZPAR2_URL.length()>0;
  }
  
  @Override 
  public void setServiceUrl (String serviceUrl) {    
    cfg.PAZPAR2_URL = serviceUrl;
    
  }

}
