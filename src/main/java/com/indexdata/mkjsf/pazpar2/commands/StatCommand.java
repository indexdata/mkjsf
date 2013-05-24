package com.indexdata.mkjsf.pazpar2.commands;

import com.indexdata.mkjsf.pazpar2.commands.sp.ServiceProxyCommand;

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
    return this;
  }

  @Override
  public boolean spOnly() {    
    return false;
  }


}
