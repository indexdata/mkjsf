package com.indexdata.pz2utils4jsf.pazpar2.commands;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;

import com.indexdata.pz2utils4jsf.pazpar2.state.StateManager;
import com.indexdata.pz2utils4jsf.utils.Utils;

@Named("pzreq") @SessionScoped
public class Pazpar2Commands implements Serializable {

  private static final long serialVersionUID = -5172466320351302413L;
  private static Logger logger = Logger.getLogger(Pazpar2Commands.class);

  public static final String INIT =     "init";
  public static final String PING =     "ping";
  public static final String SETTINGS = "settings";
  public static final String SEARCH =   "search";
  public static final String STAT =     "stat";
  public static final String SHOW =     "show";
  public static final String RECORD =   "record";
  public static final String TERMLIST = "termlist";
  public static final String BYTARGET = "bytarget";
    
  @Inject StateManager stateMgr; 
  
  public Pazpar2Commands() {
    logger.info("Initializing Pazpar2Commands [" + Utils.objectId(this) + "]");
  }
  
  @PostConstruct
  public void postConstruct() {
    logger.info("in post-construct stateMgr is " + stateMgr);
  }
    
  public InitCommand getInit() {
    return (InitCommand) (stateMgr.checkOut(INIT));
  }
  
  public PingCommand getPing() {
    return (PingCommand) (stateMgr.checkOut(PING));
  }
  
  public SettingsCommand getSettings() {
    return (SettingsCommand) (stateMgr.checkOut(SETTINGS));
  }

  public SearchCommand getSearch() {
    return (SearchCommand) (stateMgr.checkOut(SEARCH));
  }
  
  /**
   * Gets a mutable SearchCommand from current state (no checkout)
   * Can be used for updating Search parameters without spawning new state.
   * @return
   */
  public SearchCommand getSearchInState() {
    return (SearchCommand) (stateMgr.getCurrentState().getCommand(SEARCH));
  }

  public StatCommand getStat() {
    return (StatCommand) (stateMgr.checkOut(STAT));
  }
  
  public ShowCommand getShow() {
    return (ShowCommand) (stateMgr.checkOut(SHOW));
  }
  
  /**
   * Gets a mutable ShowCommand from current state (no checkout)
   * Can be used for updating show parameters without spawning new state.
   * @return
   */
  public ShowCommand getShowInState () {
    return (ShowCommand) (stateMgr.getCurrentState().getCommand(SHOW));
  }
  
  /**
   * Gets a detached (copied) record command from the current state
   * 
   * @return
   */
  public RecordCommand getRecord() {
    return (RecordCommand) (stateMgr.checkOut(RECORD));
  }

  /**
   * Gets a mutable RecordCommand from current state (no checkout)
   * Can be used for updating record parameters without spawning new state.
   * @return
   */  
  public RecordCommand getRecordInState() {
    return (RecordCommand)stateMgr.getCurrentState().getCommand(RECORD);
  }
  
  /**
   * Gets a detached (copied) termlist command from the current state
   * 
   * @return Mutable termlist command
   */
  public TermlistCommand getTermlist() {
    return (TermlistCommand) (stateMgr.checkOut(TERMLIST));
  }
  
  /**
   * 
   * @return
   */
  public BytargetCommand getBytarget() {
    return (BytargetCommand) (stateMgr.checkOut(BYTARGET));
  }
  
  public Pazpar2Command getCommand(String name) {
    return stateMgr.checkOut(name);
  }
  
  public CommandReadOnly getCommandReadOnly(String name) {
    return stateMgr.getCommand(name);
  }
    
}
