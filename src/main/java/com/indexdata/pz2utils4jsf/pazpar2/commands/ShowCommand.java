package com.indexdata.pz2utils4jsf.pazpar2.commands;

import com.indexdata.pz2utils4jsf.pazpar2.state.StateManager;

public class ShowCommand extends Pazpar2Command {

  private static final long serialVersionUID = -8242768313266051307L;

  public ShowCommand(StateManager stateMgr) {
    super("show",stateMgr);
  }

  public void setSort (String sort) {
    setParameter(new CommandParameter("sort","=",sort));
  }
  
  public String getSort () {
    return getParameter("sort") != null ? getParameter("sort").value : "relevance";
  }
  
  public void setPageSize (String perPageOption) {    
    setParameters(new CommandParameter("num","=",perPageOption),
                  new CommandParameter("start","=",0));
  }
  
  public String getPageSize () {
    return getParameter("num") != null ? getParameter("num").value : "20";
  }
  
  public ShowCommand copy () {
    ShowCommand newCommand = new ShowCommand(stateMgr);
    for (String parameterName : parameters.keySet()) {
      newCommand.setParameterSilently(parameters.get(parameterName).copy());      
    }    
    return newCommand;
  }

}
