package com.indexdata.mkjsf.pazpar2.commands;

import com.indexdata.mkjsf.pazpar2.commands.sp.ServiceProxyCommand;

public class TermlistCommand extends Pazpar2Command implements ServiceProxyCommand {

  private static final long serialVersionUID = -7067878552863021727L;

  public TermlistCommand() {
    super("termlist");
  }

  public void setName(String names) {
    setParameter(new CommandParameter("name","name",names));
  }
  
  public String getName () {
    return getParameterValue("name");
  }
  
  public void setNum (String num) {
    setParameter(new CommandParameter("num","=",num));
  }
  
  public String getNum () {
    return getParameterValue("num");
  }
  
  public TermlistCommand copy () {
    TermlistCommand newCommand = new TermlistCommand();
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
