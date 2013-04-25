package com.indexdata.mkjsf.pazpar2.commands.sp;

import java.io.Serializable;

import com.indexdata.mkjsf.pazpar2.commands.CommandParameter;
import com.indexdata.mkjsf.pazpar2.commands.RecordCommand;

public class RecordCommandSp  implements Serializable, ServiceProxyCommand {

  private static final long serialVersionUID = -3901864271733337221L;
  private RecordCommand command = null;
  
  public RecordCommandSp(RecordCommand command) {    
    this.command = command;
  }
  
  public void setRecordquery (String recordQuery) {
    command.setParameter(new CommandParameter("recordquery","=",recordQuery));
  }
  
  public String getRecordquery() {
    return command.getParameterValue("recordquery");
  }
  
  public void setAcefilter (String aceFilter) {
    command.setParameter(new CommandParameter("acefilter","=",aceFilter));
  }
  
  public String getAcefilter () {
    return command.getParameterValue("acefilter");
  }


}
