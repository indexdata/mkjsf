package com.indexdata.mkjsf.pazpar2.commands.sp;

import java.io.Serializable;

import com.indexdata.mkjsf.pazpar2.commands.CommandParameter;
import com.indexdata.mkjsf.pazpar2.commands.RecordCommand;

/**
 * Service Proxy extensions to the Papzar2 <code>record</code> command, 
 * these parameters being accessible by <code>pzreq.record.sp.[parameter]</code>
 * 
 * @author Niels Erik
 *
 */
public class RecordCommandSp  implements Serializable, ServiceProxyCommand {

  private static final long serialVersionUID = -3901864271733337221L;
  private RecordCommand command = null;
  
  public RecordCommandSp(RecordCommand command) {    
    this.command = command;
  }
  
  /**
   * Sets Service Proxy command parameter <code>recordquery</code>. See Service Proxy documentation for details. 
   */
  public void setRecordquery (String recordQuery) {
    command.setParameter(new CommandParameter("recordquery","=",recordQuery));
  }
  
  /**
   * Gets parameter value for <code>recordquery</cod>
   */
  public String getRecordquery() {
    return command.getParameterValue("recordquery");
  }
  
  /**
   * Sets Service Proxy command parameter <code>acefilter</code>. See Service Proxy documentation for details.
   */
  public void setAcefilter (String aceFilter) {
    command.setParameter(new CommandParameter("acefilter","=",aceFilter));
  }
  
  /**
   * Gets parameter value for <code>acefilter</cod>
   */
  public String getAcefilter () {
    return command.getParameterValue("acefilter");
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
    return true;
  }

  

}
