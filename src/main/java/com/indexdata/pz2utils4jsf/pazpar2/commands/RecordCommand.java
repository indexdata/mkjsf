package com.indexdata.pz2utils4jsf.pazpar2.commands;

import com.indexdata.pz2utils4jsf.pazpar2.state.StateManager;

public class RecordCommand extends Pazpar2Command {

  private static final long serialVersionUID = 2817539422114569506L;

  public RecordCommand(StateManager stateMgr) {
    super("record",stateMgr);
  }

}
