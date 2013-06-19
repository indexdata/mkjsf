package com.indexdata.mkjsf.pazpar2.commands.sp;

import java.io.Serializable;

import com.indexdata.mkjsf.pazpar2.commands.CommandParameter;
import com.indexdata.mkjsf.pazpar2.commands.ShowCommand;

public class ShowCommandSp implements Serializable, ServiceProxyCommand{

  ShowCommand command = null;
  /**
   * 
   */
  private static final long serialVersionUID = -4563427833820559878L;

  public ShowCommandSp(ShowCommand showCommand) {
      this.command=showCommand;
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
    return false;
  }

}
