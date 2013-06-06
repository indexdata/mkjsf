package com.indexdata.mkjsf.pazpar2.commands;

import com.indexdata.mkjsf.pazpar2.commands.sp.ServiceProxyCommand;

/**
 * Represents a Pazpar2 <code>termlist</code> command. 
 * 
 * @author Niels Erik
 *
 */
public class TermlistCommand extends Pazpar2Command implements ServiceProxyCommand {

  private static final long serialVersionUID = -7067878552863021727L;

  public TermlistCommand() {
    super("termlist");
  }

  /**
   * Sets Pazpar2 parameter <code>name</code>. See Pazpar2 documentation for details. 
   */
  public void setName(String names) {
    setParameter(new CommandParameter("name","=",names));
  }
  
  /**
   * Gets parameter value for <code>name</cod>
   */
  public String getName () {
    return getParameterValue("name");
  }
    
  /**
   * Sets Pazpar2 parameter <code>num</code>. See Pazpar2 documentation for details. 
   */
  public void setNum (String num) {
    setParameter(new CommandParameter("num","=",num));
  }
  
  /**
   * Gets parameter value for <code>num</cod>
   */
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
