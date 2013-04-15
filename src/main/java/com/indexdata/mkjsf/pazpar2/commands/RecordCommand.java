package com.indexdata.mkjsf.pazpar2.commands;

import com.indexdata.mkjsf.pazpar2.state.StateManager;

public class RecordCommand extends Pazpar2Command {

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

  @Override
  public RecordCommand copy () {
    RecordCommand newCommand = new RecordCommand(stateMgr);
    for (String parameterName : parameters.keySet()) {
      newCommand.setParameterInState(parameters.get(parameterName).copy());      
    }    
    return newCommand;
  }
}
