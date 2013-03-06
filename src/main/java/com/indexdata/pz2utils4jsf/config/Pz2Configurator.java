package com.indexdata.pz2utils4jsf.config;

import java.io.IOException;
import java.io.Serializable;

public interface Pz2Configurator extends Serializable {
  public Pz2Config getConfig() throws IOException;
}
