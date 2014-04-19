package com.indexdata.mkjsf.pazpar2.commands;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import org.apache.log4j.Logger;

import com.indexdata.mkjsf.pazpar2.Pz2Service;
import com.indexdata.mkjsf.pazpar2.commands.sp.ServiceProxyCommands;
import com.indexdata.mkjsf.utils.Utils;

/**
 * Pazpar2Commands holds references to all Pazpar2 commands. 
 * <p>
 * The Pazpar2Commands object itself is exposed to the UI as <code>pzreq</code>. 
 * </p>
 * <p>
 * When the UI request a command it will be retrieved from the current state
 * through the state manager, so that the command can trigger a mutation of 
 * the state if the user/UI modifies its parameters. 
 * </p>
 * <p>Examples:</p>
 * <ul>
 *  <li><code>pzreq.show</code>    - will retrieve the show command for editing or execution
 *  <li><code>pzreq.sp.auth</code> - will retrieve the Service Proxy extension command 'auth'
 * </ul>
 * 
 * @author Niels Erik
 *
 */
@SessionScoped @Named
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
  public static final String INFO =     "info";
  private ServiceProxyCommands sp = null;    
  
  public Pazpar2Commands() {
    logger.info("Initializing Pazpar2Commands [" + Utils.objectId(this) + "]");   
  }
  
  /**
   * init command - referenced from UI as <code>pzreq.init</code>
   * 
   * @return init command from current state
   */
  public InitCommand getInit() {
    return (InitCommand) (Pz2Service.get().getStateMgr().getCommand(INIT));    
  }
    
  /** 
   * ping command - referenced from UI as <code>pzreq.ping</code>
   * 
   * @return ping command from current state 
   */
  public PingCommand getPing() {
    return (PingCommand) (Pz2Service.get().getStateMgr().getCommand(PING));
  }
  
  /**
   * settings command - referenced from UI as <code>pzreq.settings</code>
   * 
   * @return settings command from current state
   */
  public SettingsCommand getSettings() {
    return (SettingsCommand) (Pz2Service.get().getStateMgr().getCommand(SETTINGS));
  }

  /**
   * 
   * @return search command from current state
   */
  public SearchCommand getSearch() {
    return (SearchCommand) (Pz2Service.get().getStateMgr().getCommand(SEARCH));
  }
  
  /**
   * 
   * @return stat command from current state
   */
  public StatCommand getStat() {
    return (StatCommand) (Pz2Service.get().getStateMgr().getCommand(STAT));
  }
  
  /**
   * 
   * @return show command from current state
   */
  public ShowCommand getShow() {
    return (ShowCommand) (Pz2Service.get().getStateMgr().getCommand(SHOW));
  }
    
  /**
   * 
   * @return record command from current state
   */
  public RecordCommand getRecord() {
    return (RecordCommand) (Pz2Service.get().getStateMgr().getCommand(RECORD));
  }

  /**
   * 
   * @return termlist command from current state
   */
  public TermlistCommand getTermlist() {
    return (TermlistCommand) (Pz2Service.get().getStateMgr().getCommand(TERMLIST));
  }
  
  /**
   * 
   * @return bytarget command from current state
   */
  public BytargetCommand getBytarget() {
    return (BytargetCommand) (Pz2Service.get().getStateMgr().getCommand(BYTARGET));
  }
  
  /**
   *
   * @return info command from current state
   */
  public InfoCommand getInfo () {
    return (InfoCommand) (Pz2Service.get().getStateMgr().getCommand(INFO));
  }

  /**
   * Generically retrieves any command
   * 
   * @param name name of command to retrieve
   * @return command of the given type
   */
  public Pazpar2Command getCommand(String name) {
    return Pz2Service.get().getStateMgr().getCommand(name);
  }
  
  /**
   * Gets the object holding references to Service Proxy-only commands.
   * @return
   */
  public ServiceProxyCommands getSp() {
    if (sp == null) {
      sp = new ServiceProxyCommands(Pz2Service.get().getStateMgr());
    }
    return sp;
  }
      
}
