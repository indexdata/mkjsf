package com.indexdata.mkjsf.pazpar2.commands;

import com.indexdata.mkjsf.pazpar2.commands.sp.RecordCommandSp;
import com.indexdata.mkjsf.pazpar2.commands.sp.ServiceProxyCommand;
import com.indexdata.mkjsf.pazpar2.state.StateManager;

public class RecordCommand extends Pazpar2Command implements ServiceProxyCommand {

  private static final long serialVersionUID = 2817539422114569506L;

  public RecordCommand(StateManager stateMgr) {
    super("record",stateMgr);
  }
  
  public void setId(String recId) {
    setParameter(new CommandParameter("id","=",recId));
  }
  
  public String getId () {
    return getParameterValue("id");
  }
  
  public void setOffset (String offset) {
    setParameter(new CommandParameter("offset","=",offset));
  }
  
  public String getOffset () {
    return getParameterValue("offset");
  }
  
  public void setChecksum (String checksum) {
    setParameter(new CommandParameter("checksum","=",checksum));
  }
  
  public String getChecksum () {
    return getParameterValue("checksum");
  }
  
  public void setNativesyntax (String nativesyntax) {
    setParameterInState(new CommandParameter("nativesyntax","=",nativesyntax));
  }
  
  public String getNativesyntax () {
    return getParameterValue("nativesyntax");
  }
  
  public void setSyntax (String syntax) {
    setParameterInState(new CommandParameter("syntax","=",syntax));    
  }
  
  public String getSyntax () {
    return getParameterValue("syntax");
  }
  
  public void setEsn (String esn) {
    setParameter(new CommandParameter("esn","=",esn));
  }
  
  public String getEsn () {
    return getParameterValue("esn");
  }
  
  public void setBinary (String binary) {
    setParameter(new CommandParameter("binary","=",binary));
  }
  
  public String getBinary () {
    return getParameterValue("binary");
  }

  @Override
  public RecordCommand copy () {
    RecordCommand newCommand = new RecordCommand(stateMgr);
    for (String parameterName : parameters.keySet()) {
      newCommand.setParameterInState(parameters.get(parameterName).copy());      
    }    
    return newCommand;
  }
  
  
  /**
   * Returns a record command object with Service Proxy extension parameters 
   * 
   */
  public RecordCommandSp getSp () {
    return new RecordCommandSp(this);
  }
}
