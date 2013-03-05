package com.indexdata.pz2utils4jsf.config;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Alternative;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.ServletContext;

import org.apache.log4j.Logger;


@Named @SessionScoped @Alternative
public class Pz2ConfigureByWebXml implements Pz2Configurator {

  private static final long serialVersionUID = 144390224959311772L;
  private static Logger logger = Logger.getLogger(Pz2ConfigureByWebXml.class);
  private Pz2Config config = null;
  
  public Pz2ConfigureByWebXml () {
    logger.debug("Instantiating Pazpar2 service configuration by web.xml parameters");
    ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
    ServletContext servletContext = (ServletContext) externalContext.getContext();
    Map<String,String> parameters = new HashMap<String,String>();
    parameters.put("PAZPAR2_URL", servletContext.getInitParameter("PAZPAR2_URL"));
    parameters.put("PAZPAR2_SERVICE_ID", servletContext.getInitParameter("PAZPAR2_SERVICE_ID"));
    config = new Pz2Config(parameters);
  }
  
  @Override
  public Pz2Config getConfig() {
    return config;
  }
}
