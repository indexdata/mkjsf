package com.indexdata.pz2utils4jsf.pazpar2.commands;

import com.indexdata.pz2utils4jsf.pazpar2.state.StateManager;

public class RecordCommand extends Pazpar2Command {

  private static final long serialVersionUID = 2817539422114569506L;

  public RecordCommand(StateManager stateMgr) {
    super("record",stateMgr);
  }
  
  public void setRecordId(String recId) {
    setParameter(new CommandParameter("id","=",recId));
  }
  
  public String getRecordId () {
    return getParameterValue("id");
  }

  @Override
  public RecordCommand copy () {
    RecordCommand newCommand = new RecordCommand(stateMgr);
    for (String parameterName : parameters.keySet()) {
      newCommand.setParameterSilently(parameters.get(parameterName).copy());      
    }    
    return newCommand;
  }

  

}
