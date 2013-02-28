package com.indexdata.pz2utils4jsf.config;

import com.indexdata.pz2utils4jsf.config.FacesModuleConfiguration;

public abstract class JsfdemoModuleConfiguration extends FacesModuleConfiguration {

  private static final long serialVersionUID = -2936339728016978922L;

  @Override
  public abstract String getModuleName();

  @Override
  public String getComponentName() {
    return "jsfdemo";
  }

}
