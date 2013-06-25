package com.indexdata.mkjsf.pazpar2.commands.sp;

import java.io.Serializable;

import com.indexdata.mkjsf.pazpar2.commands.CommandParameter;
import com.indexdata.mkjsf.pazpar2.commands.TermlistCommand;

public class TermlistCommandSp implements Serializable, ServiceProxyCommand {

  private TermlistCommand command = null;
  private static final long serialVersionUID = -7453670169089123800L;

  public TermlistCommandSp(TermlistCommand command) {
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
