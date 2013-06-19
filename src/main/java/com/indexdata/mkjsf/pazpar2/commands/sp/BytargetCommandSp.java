package com.indexdata.mkjsf.pazpar2.commands.sp;

import java.io.Serializable;

import com.indexdata.mkjsf.pazpar2.commands.BytargetCommand;
import com.indexdata.mkjsf.pazpar2.commands.CommandParameter;

public class BytargetCommandSp implements Serializable, ServiceProxyCommand {

  private BytargetCommand command = null;
  private static final long serialVersionUID = -1742198227615699037L;

  public BytargetCommandSp(BytargetCommand command) {
    this.command = command;
  }

  /**
   * Sets the <code>windowid</code> parameter. See Service Proxy documentation for details.
   */  
  public void setWindowid (String windowid) {
    command.setParameterInState(new CommandParameter("windowid","=",windowid));
  }
  
  /** 
   * Returns the <code>windowid</code> parameter value.
   */
  public String getWindowid () {
    return command.getParameterValue("windowid");
  }

  @Override
  public boolean spOnly() {
    // TODO Auto-generated method stub
    return false;
  }

}
