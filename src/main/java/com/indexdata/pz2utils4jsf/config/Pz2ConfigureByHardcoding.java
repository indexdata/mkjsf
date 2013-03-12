/**
 * 
 */
package com.indexdata.pz2utils4jsf.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Named;

@Named @SessionScoped @Alternative
public class Pz2ConfigureByHardcoding implements Pz2Configurator {

  /**
   * 
   */
  private static final long serialVersionUID = -3833516705975419652L;
  private Pz2Config pz2config = null;

  
  @Override
  public Pz2Config getConfig() throws IOException {
    if (pz2config == null) {
      createConfig();
    }
    return pz2config;
  }
  
  private void createConfig () throws IOException {
    Map<String,String> parameters = new HashMap<String,String>();
    parameters.put("PAZPAR2_URL", "http://mk2-test.indexdata.com/test-pazpar2/");
    parameters.put("PAZPAR2_SERVICE_ID", "jsfdemo");
    pz2config = new Pz2Config(parameters);    
  }


  /* (non-Javadoc)
   * @see com.indexdata.pz2utils4jsf.config.Pz2Configurator#document()
   */
  @Override
  public List<String> document() {
    List<String> docs = new ArrayList<String>();
    docs.add("No docs");
    return docs;
  }

}
