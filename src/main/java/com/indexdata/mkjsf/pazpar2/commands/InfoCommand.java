/**
 * 
 */
package com.indexdata.mkjsf.pazpar2.commands;

import com.indexdata.mkjsf.pazpar2.commands.sp.ServiceProxyCommand;

/**
 * @author Niels Erik
 *
 */
public class InfoCommand extends Pazpar2Command implements ServiceProxyCommand {

  /**
   * 
   */
  private static final long serialVersionUID = -180974027395677475L;

  public InfoCommand () {
    super("info");
  }
  
  /* (non-Javadoc)
   * @see com.indexdata.mkjsf.pazpar2.commands.Pazpar2Command#copy()
   */
  @Override
  public Pazpar2Command copy() {
    InfoCommand newCommand = new InfoCommand();
    for (String parameterName : parameters.keySet()) {
      newCommand.setParameterInState(parameters.get(parameterName).copy());      
    }    
    return newCommand;
  }

  /* (non-Javadoc)
   * @see com.indexdata.mkjsf.pazpar2.commands.Pazpar2Command#getSp()
   */
  @Override
  public ServiceProxyCommand getSp() {
    return this;
  }

  /* (non-Javadoc)
   * @see com.indexdata.mkjsf.pazpar2.commands.Pazpar2Command#spOnly()
   */
  @Override
  public boolean spOnly() {
    return false;
  }

}
