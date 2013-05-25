package com.indexdata.mkjsf.pazpar2.commands.sp;

import org.apache.log4j.Logger;

import com.indexdata.mkjsf.pazpar2.Pz2Bean;
import com.indexdata.mkjsf.pazpar2.commands.Pazpar2Command;
import com.indexdata.mkjsf.pazpar2.data.sp.CategoriesResponse;

public class CategoriesCommand extends Pazpar2Command implements ServiceProxyCommand {

  private static final long serialVersionUID = 5023993689780291641L;
  private static Logger logger = Logger.getLogger(CategoriesCommand.class);

  public CategoriesCommand() {
    super("categories");
  }
  
  @Override
  public CategoriesResponse run () {
    if (Pz2Bean.get().getPzresp().getSp().getCategories().unsupportedCommand()) {
      logger.info("Skipping seemingly unsupported categories command");  
      return new CategoriesResponse();
    } else {
      if (Pz2Bean.get().isServiceProxyService()) {
        try {
          CategoriesResponse response = (CategoriesResponse) super.run();
          if (response.unsupportedCommand()) {
            logger.warn("Command 'categories' not supported by this Service Proxy");          
          }
          return response;
        } catch (Exception e) {
          e.printStackTrace();
          return new CategoriesResponse();
        }
      } else {
        return new CategoriesResponse();
      }
    }
  }

  @Override
  public Pazpar2Command copy() {
    CategoriesCommand newCommand = new CategoriesCommand();
    return newCommand;
  }

  @Override
  public ServiceProxyCommand getSp() {
    return this;
  }

  @Override
  public boolean spOnly() {
    return true;
  }

}
