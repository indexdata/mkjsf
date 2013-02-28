package com.indexdata.pz2utils4jsf.config;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import com.indexdata.pz2utils4jsf.config.JsfdemoModuleConfiguration;

@Named("pz2clientConfig")
@SessionScoped
public class JsfdemoPazpar2ClientConfiguration extends JsfdemoModuleConfiguration {

  private static final long serialVersionUID = 8865086878660568870L;
  
  @Override
  public String getModuleName() {
    return "pz2client";
  }

}
