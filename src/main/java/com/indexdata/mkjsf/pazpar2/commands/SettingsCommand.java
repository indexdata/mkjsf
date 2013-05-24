package com.indexdata.mkjsf.pazpar2.commands;

import com.indexdata.mkjsf.pazpar2.commands.sp.ServiceProxyCommand;

public class SettingsCommand extends Pazpar2Command implements ServiceProxyCommand {

  private static final long serialVersionUID = 2291179325470387102L;

  public SettingsCommand() {
    super("settings");
    // TODO Auto-generated constructor stub
  }
  
  public SettingsCommand copy () {
    SettingsCommand newCommand = new SettingsCommand();
    for (String parameterName : parameters.keySet()) {
      newCommand.setParameterInState(parameters.get(parameterName).copy());      
    }    
    return newCommand;
  }

  @Override
  public ServiceProxyCommand getSp() {
    return this;
  }

  @Override
  public boolean spOnly() {
    return false;
  }

}
