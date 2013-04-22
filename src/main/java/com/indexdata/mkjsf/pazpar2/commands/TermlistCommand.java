package com.indexdata.mkjsf.pazpar2.commands;

import com.indexdata.mkjsf.pazpar2.state.StateManager;

public class TermlistCommand extends Pazpar2Command {

  private static final long serialVersionUID = -7067878552863021727L;

  public TermlistCommand(StateManager stateMgr) {
    super("termlist",stateMgr);
  }

  public void setName(String names) {
    setParameter(new CommandParameter("name","name",names));
  }
  
  public String getName () {
    return getParameterValue("name");
  }
  
  public void setNum (String num) {
    setParameter(new CommandParameter("num","=",num));
  }
  
  public String getNum () {
    return getParameterValue("num");
  }
}
