package com.indexdata.pz2utils4jsf.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import com.indexdata.pz2utils4jsf.errors.ConfigurationException;
import com.indexdata.pz2utils4jsf.utils.Utils;
import static com.indexdata.pz2utils4jsf.utils.Utils.nl;

@Named @SessionScoped @Alternative
public class Pz2ConfigureByMk2Config implements Pz2Configurator  {

  private static final long serialVersionUID = 8865086878660568870L;
  private static Logger logger = Logger.getLogger(Pz2ConfigureByMk2Config.class);
  private Pz2Config pz2config = null;
  private String configFilePathAndName = "none";

  public Pz2ConfigureByMk2Config () throws IOException {
    logger.info(Utils.objectId(this) + " is instantiating Pazpar2 service configuration by MasterKey configuration scheme.");
  }
    
  @Override
  public Pz2Config getConfig() throws ConfigurationException {
    if (pz2config == null) {
      createConfig();
    }
    return pz2config;
  }
  
  private void createConfig () throws ConfigurationException {
    ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
    ServletContext servletContext = (ServletContext) externalContext.getContext();  
    MasterkeyConfiguration mkConfigContext;
    try {
      mkConfigContext = MasterkeyConfiguration.getInstance(servletContext,
      "pazpar-application-jsf", ((HttpServletRequest) externalContext.getRequest()).getServerName());
    } catch (IOException e) {
      throw new ConfigurationException("Pz2ConfigureByMk2Config could not configure Pazpar2 client using MasterKey configuration scheme: "+e.getMessage(),e);
    }
    configFilePathAndName = mkConfigContext.getConfigFileLocation().getConfigFilePath();    
    try {
      ModuleConfiguration moduleConfig = mkConfigContext.getModuleConfiguration("pz2client");
      pz2config = new Pz2Config(moduleConfig);
      logger.info(document());
    } catch (IOException e) {
      throw new ConfigurationException("Pz2ConfigureByMk2Config could not get configuration for module 'pz2client': "+e.getMessage(),e);
    }        
  }
  

  public List<String> document() {
    List<String> doc = new ArrayList<String>();
    doc.add(":"+nl+"Attempted to configure service using the file " + configFilePathAndName);
    doc.add(nl+"-- Configured to access Pazpar2 at: " +pz2config.get("PAZPAR2_URL"));
    if (pz2config.get("PAZPAR2_SERVICE_XML") != null) {
      doc.add(nl+"-- Configured to use the service definition contained in " + pz2config.getConfigFilePath() + "/" + pz2config.get("PAZPAR2_SERVICE_XML"));
      if (pz2config.get("PAZPAR2_SETTINGS_XML") != null) {
        doc.add(nl+"-- Configured to use the target settings contained in " + pz2config.getConfigFilePath() + "/" + pz2config.get("PAZPAR2_SETTINGS_XML"));
      } else {
        doc.add(nl+"-- Configured to use the server side target settings as defined in the service definition.");
      }
    } else if (pz2config.get("PAZPAR2_SERVICE_ID") != null) {
      doc.add(nl+"-- Configured to use the server side service definition identified by service id \""+pz2config.get("PAZPAR2_SERVICE_ID") + "\"");
    } else {
      doc.add(nl+"Error: Did not find service ID nor service definition XML file for setting up a pazpar2 service.");
    }
    return doc;
  }
    

}
