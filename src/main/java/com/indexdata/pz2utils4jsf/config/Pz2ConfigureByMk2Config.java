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
import com.indexdata.pz2utils4jsf.utils.Utils;

@Named @SessionScoped @Alternative
public class Pz2ConfigureByMk2Config implements Pz2Configurator  {

  private static final long serialVersionUID = 8865086878660568870L;
  private static Logger logger = Logger.getLogger(Pz2ConfigureByMk2Config.class);
  private Pz2Config pz2config = null;

  public Pz2ConfigureByMk2Config () throws IOException {
    logger.info(Utils.objectId(this) + " is instantiating Pazpar2 service configuration by MasterKey configuration scheme.");
  }
    
  @Override
  public Pz2Config getConfig() throws IOException {
    if (pz2config == null) {
      createConfig();
    }
    return pz2config;
  }
  
  private void createConfig () throws IOException {
    ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
    ServletContext servletContext = (ServletContext) externalContext.getContext();       
    MasterkeyConfiguration mkConfigContext =
        MasterkeyConfiguration.getInstance(servletContext,
        "pazpar-application-jsf", ((HttpServletRequest) externalContext.getRequest()).getServerName());
    ModuleConfiguration moduleConfig = mkConfigContext.getModuleConfiguration("pz2client");
    pz2config = new Pz2Config(moduleConfig);
    logger.info(document());
  }
  

  public List<String> document() {
    List<String> doc = new ArrayList<String>();
    
    doc.add("-- Set to access Pazpar2 at: " +pz2config.get("PAZPAR2_URL"));
    if (pz2config.get("PAZPAR2_SERVICE_XML") != null) {
      doc.add("-- App set to use the service definition contained in " + pz2config.getConfigFilePath() + "/" + pz2config.get("PAZPAR2_SERVICE_XML"));
      if (pz2config.get("PAZPAR2_SETTINGS_XML") != null) {
        doc.add("-- App set to use the target settings contained in " + pz2config.getConfigFilePath() + "/" + pz2config.get("PAZPAR2_SETTINGS_XML"));
      } else {
        doc.add("-- App set to use the server side target settings as defined in the service definition.");
      }
    } else if (pz2config.get("PAZPAR2_SERVICE_ID") != null) {
      doc.add("-- App set to use the server side service definition identified by service id \""+pz2config.get("PAZPAR2_SERVICE_ID") + "\"");
    } else {
      doc.add("Error: Did not find service ID nor service definition XML file to set up pazpar2 service.");
    }
    return doc;
  }
    

}
