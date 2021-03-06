package com.indexdata.mkjsf.pazpar2.commands;

import com.indexdata.mkjsf.pazpar2.commands.sp.ServiceProxyCommand;

/**
 * Represents a Pazpar2 <code>ping</code> command, , can be accessed by <code>pzreq.ping</code>
 * 
 * @author Niels Erik
 *
 */
public class PingCommand extends Pazpar2Command implements ServiceProxyCommand {

  private static final long serialVersionUID = 8876721711326535847L;

  public PingCommand() {
    super("ping");    
  }
  
  public PingCommand copy () {
    PingCommand newCommand = new PingCommand();
    for (String parameterName : parameters.keySet()) {
      newCommand.setParameterInState(parameters.get(parameterName).copy());      
    }    
    return newCommand;
  }

  public ServiceProxyCommand getSp() {
    return this;
  }

  @Override
  public boolean spOnly() {
    return false;
  }
}
