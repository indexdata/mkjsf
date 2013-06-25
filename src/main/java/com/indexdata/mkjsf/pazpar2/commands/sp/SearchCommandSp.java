package com.indexdata.mkjsf.pazpar2.commands.sp;

import java.io.Serializable;

import com.indexdata.mkjsf.pazpar2.commands.CommandParameter;
import com.indexdata.mkjsf.pazpar2.commands.SearchCommand;

public class SearchCommandSp implements Serializable, ServiceProxyCommand {

  private SearchCommand command = null;
  private static final long serialVersionUID = -8945477254342198735L;

  public SearchCommandSp(SearchCommand command) {
    this.command=command;
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
