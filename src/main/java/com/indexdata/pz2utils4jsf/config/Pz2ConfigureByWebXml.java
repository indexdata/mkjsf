package com.indexdata.pz2utils4jsf.config;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.ServletContext;

import org.apache.log4j.Logger;

@Named("pz2configwebxml")
@SessionScoped
public class Pz2ConfigureByWebXml implements Pz2Configurator {

  private static final long serialVersionUID = 144390224959311772L;
  private static Logger logger = Logger.getLogger(Pz2ConfigureByWebXml.class);
  private Pz2Config config = null;
  
  public Pz2ConfigureByWebXml () {
    logger.debug("Instantiating Pazpar2 service configuration by web.xml parameters");
    ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
    ServletContext servletContext = (ServletContext) externalContext.getContext();
    String pazpar2Url = servletContext.getInitParameter("PAZPAR2_URL");
    String pazpar2ServiceId = servletContext.getInitParameter("PAZPAR2_SERVICE_id");
    config = new Pz2Config(pazpar2Url,pazpar2ServiceId);
  }
  
  @Override
  public Pz2Config getConfig() {
    return config;
  }
}
