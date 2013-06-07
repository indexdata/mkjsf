package com.indexdata.mkjsf.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Alternative;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.indexdata.masterkey.config.MasterkeyConfiguration;
import com.indexdata.masterkey.config.ModuleConfiguration;
import com.indexdata.mkjsf.errors.ConfigurationException;
import com.indexdata.mkjsf.errors.MissingConfigurationContextException;
import com.indexdata.mkjsf.utils.Utils;

import static com.indexdata.mkjsf.utils.Utils.nl;

/**
 * Reads configuration from a MasterKey configuration scheme
 * 
 * 
 * @author Niels Erik
 *
 */
@Named @SessionScoped @Alternative
public class Mk2ConfigReader implements ConfigurationReader  {

  private static final long serialVersionUID = 8865086878660568870L;
  private static Logger logger = Logger.getLogger(Mk2ConfigReader.class);
  private Map<String,Configuration> configs = new HashMap<String,Configuration>(); 
  private Map<String,Configurable> configurables = new HashMap<String,Configurable>();

  public Mk2ConfigReader () throws IOException {
    logger.info(Utils.objectId(this) + " is instantiating Pazpar2 service configuration by MasterKey configuration scheme.");
  }

  @Override
  public Configuration getConfiguration(Configurable configurable) throws ConfigurationException {    
    if (configs.get(configurable.getModuleName()) == null) {
      Configuration config = readConfig(configurable);
      configs.put(configurable.getModuleName(), config); 
      configurables.put(configurable.getModuleName(), configurable);
    }
    return configs.get(configurable.getModuleName());    
  }
    
  private Configuration readConfig (Configurable configurable) throws ConfigurationException {
    Configuration config = new Configuration();
    MasterkeyConfiguration mkConfigContext = null;
    ExternalContext externalContext = null;
    try {
      externalContext = FacesContext.getCurrentInstance().getExternalContext();      
    } catch (NullPointerException npe){
      throw new MissingConfigurationContextException("No FacesContext available to get configuration context from: " + npe.getMessage());
    }
    ServletContext servletContext = (ServletContext) externalContext.getContext();
    try {
      mkConfigContext = MasterkeyConfiguration.getInstance(servletContext,
      "mkjsf", ((HttpServletRequest) externalContext.getRequest()).getServerName());
    } catch (IOException e) {
      throw new ConfigurationException(Mk2ConfigReader.class + " could not read configuration for '" + configurable.getModuleName() + "' using MasterKey configuration scheme: "+e.getMessage(),e);
    }        
    try {
      ModuleConfiguration moduleConfig = mkConfigContext.getModuleConfiguration(configurable.getModuleName());      
      config.addAll(configurable.getDefaults(),moduleConfig.getConfigMap());
      config.set("configpath", moduleConfig.getConfigFilePath());            
    } catch (IOException e) {
      throw new ConfigurationException(Mk2ConfigReader.class + " could not read configuration for '"+ configurable.getModuleName() + "': "+e.getMessage(),e);
    }        
    return config;
  }
        
  public List<String> document() {
    List<String> doc = new ArrayList<String>();
    doc.add("Application properties as read by " + this.getClass());
    for (String moduleName : configs.keySet()) {
      doc.add(nl+"Module: " + moduleName);
      Configurable module = configurables.get(moduleName);
      Map<String,String> map = configs.get(moduleName).getConfigMap();
      for (String key :  map.keySet()) {
        doc.add(nl+key+": "+ map.get(key) +
            (module.getDefaults().containsKey(key) ? 
                (module.getDefaults().get(key).equals(map.get(key)) ? " [default]" : " [override]")
                : "")); 
      }
    }
    return doc;
  }
    

}
