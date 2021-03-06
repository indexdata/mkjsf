package com.indexdata.mkjsf.pazpar2.commands;

import com.indexdata.mkjsf.pazpar2.commands.sp.ServiceProxyCommand;
import com.indexdata.mkjsf.pazpar2.commands.sp.StatCommandSp;

/**
 * <b><code>stat</code></b> Pazpar2 command, referenced as: <code>pzreq.stat</code>
 * 
 * @author Niels Erik
 *
 */
public class StatCommand extends Pazpar2Command implements ServiceProxyCommand {

  private static final long serialVersionUID = 3980630346114157336L;

  public StatCommand() {
    super("stat");
  }
  
  public StatCommand copy () {
    StatCommand newCommand = new StatCommand();
    for (String parameterName : parameters.keySet()) {
      newCommand.setParameterInState(parameters.get(parameterName).copy());      
    }    
    return newCommand;
  }

  @Override
  public ServiceProxyCommand getSp() {
    return new StatCommandSp(this);
  }

  @Override
  public boolean spOnly() {    
    return false;
  }


}
