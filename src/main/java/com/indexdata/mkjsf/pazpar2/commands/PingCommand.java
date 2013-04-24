package com.indexdata.mkjsf.pazpar2.commands;

import com.indexdata.mkjsf.pazpar2.commands.sp.ServiceProxyCommand;
import com.indexdata.mkjsf.pazpar2.state.StateManager;

public class PingCommand extends Pazpar2Command implements ServiceProxyCommand {

  private static final long serialVersionUID = 8876721711326535847L;

  public PingCommand(StateManager stateMgr) {
    super("ping",stateMgr);    
  }
  
  public PingCommand copy () {
    PingCommand newCommand = new PingCommand(stateMgr);
    for (String parameterName : parameters.keySet()) {
      newCommand.setParameterInState(parameters.get(parameterName).copy());      
    }    
    return newCommand;
  }

  public ServiceProxyCommand getSp() {
    return this;
  }
}
