package com.indexdata.pz2utils4jsf.config;

import java.io.IOException;
import java.io.Serializable;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import com.indexdata.masterkey.config.MasterkeyConfiguration;
import com.indexdata.masterkey.config.ModuleConfiguration;
import com.indexdata.masterkey.config.ModuleConfigurationGetter;

public abstract class FacesModuleConfiguration implements ModuleConfigurationGetter, Serializable {

  private static final long serialVersionUID = -7225977088088592928L;
  private ModuleConfiguration config;

  public FacesModuleConfiguration() {
    if (FacesContext.getCurrentInstance() != null) {
      ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
      HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
      ServletContext servletContext = (ServletContext) externalContext.getContext();
      String host = request.getServerName();
      try {
        config = MasterkeyConfiguration.getModuleConfiguration(servletContext, host, getComponentName(), getModuleName());
      } catch (IOException ioe) {
        ioe.printStackTrace();
      }
    }
  }

  public String get(String name) {
    return config.get(name);
  }

  public String get(String name, String defaultValue) {
    return config.get(name, defaultValue);
  }
  
  public ModuleConfiguration getModuleConfiguration () {
    return config;
  }
  
  public abstract String getModuleName();
  
  public abstract String getComponentName();

}
