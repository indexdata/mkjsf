package com.indexdata.mkjsf.pazpar2.commands;

import com.indexdata.mkjsf.pazpar2.commands.sp.ServiceProxyCommand;
import com.indexdata.mkjsf.pazpar2.state.StateManager;

public class SettingsCommand extends Pazpar2Command implements ServiceProxyCommand {

  private static final long serialVersionUID = 2291179325470387102L;

  public SettingsCommand(StateManager stateMgr) {
    super("settings",stateMgr);
    // TODO Auto-generated constructor stub
  }
  
  public SettingsCommand copy () {
    SettingsCommand newCommand = new SettingsCommand(stateMgr);
    for (String parameterName : parameters.keySet()) {
      newCommand.setParameterInState(parameters.get(parameterName).copy());      
    }    
    return newCommand;
  }

  @Override
  public ServiceProxyCommand getSp() {
    return this;
  }

}
