package com.indexdata.mkjsf.pazpar2.commands.sp;

import java.io.Serializable;

import com.indexdata.mkjsf.pazpar2.commands.CommandParameter;
import com.indexdata.mkjsf.pazpar2.commands.StatCommand;

public class StatCommandSp implements Serializable, ServiceProxyCommand {

  private StatCommand command = null;
  private static final long serialVersionUID = -469324132819092701L;

  public StatCommandSp(StatCommand command) {
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

  public boolean spOnly() {
    return false;
  }

}
