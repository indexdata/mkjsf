package com.indexdata.pz2utils4jsf.config;

import java.io.IOException;

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

@Named @SessionScoped @Alternative
public class Pz2ConfigureByMk2Config implements Pz2Configurator  {

  private static final long serialVersionUID = 8865086878660568870L;
  private static Logger logger = Logger.getLogger(Pz2ConfigureByMk2Config.class);
  private Pz2Config pz2config = null;

  public Pz2ConfigureByMk2Config () throws IOException {
    logger.debug("Instantiating Pazpar2 service configuration by MasterKey configuration scheme.");
    ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
    ServletContext servletContext = (ServletContext) externalContext.getContext();       
    MasterkeyConfiguration mkConfigContext =
        MasterkeyConfiguration.getInstance(servletContext,
        "pazpar-application-jsf", ((HttpServletRequest) externalContext.getRequest()).getServerName());
    ModuleConfiguration moduleConfig = mkConfigContext.getModuleConfiguration("pz2client");
    pz2config = new Pz2Config(moduleConfig);
  }
    
  @Override
  public Pz2Config getConfig() {
    return pz2config;
  }
  

}
