package com.indexdata.mkjsf.pazpar2.commands;

import com.indexdata.mkjsf.pazpar2.commands.sp.ServiceProxyCommand;
import com.indexdata.mkjsf.pazpar2.state.StateManager;

public class BytargetCommand extends Pazpar2Command implements ServiceProxyCommand {

  private static final long serialVersionUID = 9070458716105294392L;

  public BytargetCommand(StateManager stateMgr) {
    super("bytarget",stateMgr);
  }

  public BytargetCommand copy () {
    BytargetCommand newCommand = new BytargetCommand(stateMgr);
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
