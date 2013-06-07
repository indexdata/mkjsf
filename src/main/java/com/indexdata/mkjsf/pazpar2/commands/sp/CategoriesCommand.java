package com.indexdata.mkjsf.pazpar2.commands.sp;

import org.apache.log4j.Logger;

import com.indexdata.mkjsf.pazpar2.Pz2Service;
import com.indexdata.mkjsf.pazpar2.commands.Pazpar2Command;
import com.indexdata.mkjsf.pazpar2.data.sp.CategoriesResponse;

/**
 * Represents a Service Proxy <code>categories</code> command, can be accessed by <code>pzreq.sp.categories</code>
 * 
 * <p>Retrieves target categories available to the current Service Proxy user.</p> 
 * <p>Target categories can be used to limit a search to resources tagged with the given 
 * category. The following request in a Faces page would obtain categories for display in, say, a select list:
 * <p>
 *  <code>pzreq.sp.categories.run().targetCategories</code>   
 * <p>
 * This would get the Service Proxy extension commands, pick the categories command, execute it, and retrieve
 *  a list of TargetCategory objects from the returned response data object.  
 * </p>
 * @author Niels Erik
 *
 */
public class CategoriesCommand extends Pazpar2Command implements ServiceProxyCommand {

  private static final long serialVersionUID = 5023993689780291641L;
  private static Logger logger = Logger.getLogger(CategoriesCommand.class);

  public CategoriesCommand() {
    super("categories");
  }
  
  @Override
  public CategoriesResponse run () {
    if (Pz2Service.get().getPzresp().getSp().getCategories().unsupportedCommand()) {
      logger.info("Skipping seemingly unsupported categories command");  
      return new CategoriesResponse();
    } else {
      if (Pz2Service.get().isServiceProxyService()) {
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
