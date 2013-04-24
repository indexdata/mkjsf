package com.indexdata.mkjsf.pazpar2.commands.sp;

import com.indexdata.mkjsf.pazpar2.commands.Pazpar2Command;
import com.indexdata.mkjsf.pazpar2.state.StateManager;

public class AuthCommand extends Pazpar2Command implements ServiceProxyCommand {

  private static final long serialVersionUID = 5487611235664162578L;

  public AuthCommand(StateManager stateMgr) {
    super("auth", stateMgr);
    // TODO Auto-generated constructor stub
  }
  
  public AuthCommand copy () {
    AuthCommand newCommand = new AuthCommand(stateMgr);
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
