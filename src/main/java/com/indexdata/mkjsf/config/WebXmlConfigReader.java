package com.indexdata.mkjsf.config;

import static com.indexdata.mkjsf.utils.Utils.nl;

import java.util.ArrayList;
import java.util.Enumeration;
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

import com.indexdata.mkjsf.errors.ConfigurationException;

/**
 * Reads a configuration from the context parameters of the deployment descriptor (web.xml)
 * 
 * @author Niels Erik
 *
 */
@Named @SessionScoped @Alternative
public class WebXmlConfigReader implements ConfigurationReader {

  private static final long serialVersionUID = 144390224959311772L;
  private static Logger logger = Logger.getLogger(WebXmlConfigReader.class);
  private Configuration config = null;
  private Map<String,String> parameters = new HashMap<String,String>(); 
  
  public WebXmlConfigReader () {
    logger.info("Instantiating Pazpar2 service configuration by web.xml parameters");
  }
  
  public Configuration getConfiguration(Configurable configurable) throws ConfigurationException {
    if (config == null) {
      parameters.putAll(configurable.getDefaults());
      parameters.putAll(readConfig());
      config = new Configuration(parameters);
    }
    return config;
  }
      
  private Map<String,String> readConfig () throws ConfigurationException {
    Map<String,String> map = new HashMap<String,String>();
    ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
    ServletContext servletContext = (ServletContext) externalContext.getContext();        
    Enumeration<String> enumer = servletContext.getInitParameterNames();
    while (enumer.hasMoreElements()) {
      String name = enumer.nextElement();
      map.put(name,servletContext.getInitParameter(name));
    }
    return map;    
  }
  
  public List<String> document() {
    List<String> doc = new ArrayList<String>();
    doc.add("Application properties as read by " + this.getClass());
    for (String key :  parameters.keySet()) {
      doc.add(nl+key+": "+ parameters.get(key));
    }
    return doc;
  }
}
