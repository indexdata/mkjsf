package com.indexdata.pz2utils4jsf.config;

import static com.indexdata.pz2utils4jsf.utils.Utils.nl;

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

import org.apache.log4j.Logger;

import com.indexdata.pz2utils4jsf.errors.ConfigurationException;


@Named @SessionScoped @Alternative
public class Pz2ConfigureByWebXml implements Pz2Configurator {

  private static final long serialVersionUID = 144390224959311772L;
  private static Logger logger = Logger.getLogger(Pz2ConfigureByWebXml.class);
  private Pz2Config pz2config = null;
  private Map<String,String> parameters = new HashMap<String,String>(); 
  
  public Pz2ConfigureByWebXml () {
    logger.info("Instantiating Pazpar2 service configuration by web.xml parameters");
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
    parameters.put("PAZPAR2_URL", servletContext.getInitParameter("PAZPAR2_URL"));
    if (parameters.get("PAZPAR2_URL")==null || parameters.get("PAZPAR2_URL").length()==0) {      
      throw new ConfigurationException("Pz2ConfigureByWebXml could not find mandatory context-param 'PAZPAR2_URL'");
    }
    parameters.put("PAZPAR2_SERVICE_ID", servletContext.getInitParameter("PAZPAR2_SERVICE_ID"));
    if (parameters.get("PAZPAR2_SERVICE_ID")==null || parameters.get("PAZPAR2_SERVICE_ID").length()==0) {      
      throw new ConfigurationException("Pz2ConfigureByWebXml could not find mandatory context-param 'PAZPAR2_SERVICE_ID'");
    }
    pz2config = new Pz2Config(parameters);
  }
  
  public List<String> document() {
    List<String> doc = new ArrayList<String>();    
    doc.add("Attempted to configure service using web.xml context-parameters ");
    doc.add(nl+"-- Configured to access Pazpar2 at [" +parameters.get("PAZPAR2_URL") + "]");
    doc.add(nl+"-- Configured to use the server side service definition identified by service id [" +parameters.get("PAZPAR2_SERVICE_ID") + "]");
    return doc;    
  }
}
