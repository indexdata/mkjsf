package com.indexdata.pz2utils4jsf.pazpar2.commands;

import com.indexdata.pz2utils4jsf.pazpar2.state.StateManager;

public class StatCommand extends Pazpar2Command {

  private static final long serialVersionUID = 3980630346114157336L;

  public StatCommand(StateManager stateMgr) {
    super("stat",stateMgr);
  }

}
