package com.indexdata.mkjsf.pazpar2.commands;

import com.indexdata.mkjsf.pazpar2.state.StateManager;

public class StatCommand extends Pazpar2Command {

  private static final long serialVersionUID = 3980630346114157336L;

  public StatCommand(StateManager stateMgr) {
    super("stat",stateMgr);
  }

}
