package com.indexdata.mkjsf.pazpar2.commands;

import com.indexdata.mkjsf.pazpar2.state.StateManager;

public class PingCommand extends Pazpar2Command {

  private static final long serialVersionUID = 8876721711326535847L;

  public PingCommand(StateManager stateMgr) {
    super("ping",stateMgr);    
  }
  
}
