package com.indexdata.pz2utils4jsf.pazpar2;

import static com.indexdata.pz2utils4jsf.utils.Utils.nl;

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
import com.indexdata.pz2utils4jsf.config.Configuration;
import com.indexdata.pz2utils4jsf.config.ConfigurationReader;
import com.indexdata.pz2utils4jsf.errors.ConfigurationException;
import com.indexdata.pz2utils4jsf.pazpar2.commands.Pazpar2Command;
import com.indexdata.pz2utils4jsf.utils.Utils;

public class Pz2Client implements SearchClient {

  private static final long serialVersionUID = 5414266730169982028L;
  private static Logger logger = Logger.getLogger(Pz2Client.class);
  private Pazpar2Client client = null;
  private Pazpar2ClientConfiguration cfg = null;
  public static final String MODULENAME = "pz2client";
  public static Map<String,String> DEFAULTS = new HashMap<String,String>();
  Configuration config = null;
  
  static {    
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
    ClientCommand clientCommand = new ClientCommand(command.getName(), command.getEncodedQueryString());
    client.setSearchCommand(clientCommand);    
  }

  @Override
  public CommandResponse executeCommand(Pazpar2Command command, ByteArrayOutputStream baos) 
       throws Pazpar2ErrorException, IOException {
    ClientCommand clientCommand = new ClientCommand(command.getName(), command.getEncodedQueryString());
    Pazpar2HttpResponse pz2HttpResponse = client.executeCommand(clientCommand, baos);
    return new Pz2CommandResponse(pz2HttpResponse,baos);
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

}
