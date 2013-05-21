package com.indexdata.mkjsf.pazpar2.commands.sp;

import com.indexdata.mkjsf.pazpar2.commands.Pazpar2Command;
import com.indexdata.mkjsf.pazpar2.state.StateManager;

public class CategoriesCommand extends Pazpar2Command implements ServiceProxyCommand {

  private static final long serialVersionUID = 5023993689780291641L;

  public CategoriesCommand(StateManager stateMgr) {
    super("categories", stateMgr);
  }

  @Override
  public Pazpar2Command copy() {
    CategoriesCommand newCommand = new CategoriesCommand(stateMgr);
    return newCommand;
  }

  @Override
  public ServiceProxyCommand getSp() {
    return this;
  }

  @Override
  public boolean spOnly() {
    return true;
  }

}
