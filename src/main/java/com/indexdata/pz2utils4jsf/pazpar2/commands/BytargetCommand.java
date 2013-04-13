package com.indexdata.pz2utils4jsf.pazpar2.commands;

import com.indexdata.pz2utils4jsf.pazpar2.state.StateManager;

public class BytargetCommand extends Pazpar2Command {

  private static final long serialVersionUID = 9070458716105294392L;

  public BytargetCommand(StateManager stateMgr) {
    super("bytarget",stateMgr);
  }

}
