package com.indexdata.mkjsf.pazpar2.commands;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import org.apache.log4j.Logger;

import com.indexdata.mkjsf.pazpar2.Pz2Bean;
import com.indexdata.mkjsf.pazpar2.commands.sp.ServiceProxyCommands;
import com.indexdata.mkjsf.utils.Utils;

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
  private ServiceProxyCommands sp = null;    
  
  public Pazpar2Commands() {
    logger.info("Initializing Pazpar2Commands [" + Utils.objectId(this) + "]");   
  }
      
  // public void setService(Pz2Bean service) {
  //   this.pz2 = service;
  //  logger.info("Post construct Pazpar2Command: Service is " + pz2);    
  // }
      
  public InitCommand getInit() {
    return (InitCommand) (Pz2Bean.get().getStateMgr().getCommand(INIT));    
  }
    
  public PingCommand getPing() {
    return (PingCommand) (Pz2Bean.get().getStateMgr().getCommand(PING));
  }
  
  public SettingsCommand getSettings() {
    return (SettingsCommand) (Pz2Bean.get().getStateMgr().getCommand(SETTINGS));
  }

  public SearchCommand getSearch() {
    return (SearchCommand) (Pz2Bean.get().getStateMgr().getCommand(SEARCH));
  }
  
  public StatCommand getStat() {
    return (StatCommand) (Pz2Bean.get().getStateMgr().getCommand(STAT));
  }
  
  public ShowCommand getShow() {
    return (ShowCommand) (Pz2Bean.get().getStateMgr().getCommand(SHOW));
  }
    
  public RecordCommand getRecord() {
    return (RecordCommand) (Pz2Bean.get().getStateMgr().getCommand(RECORD));
  }

  public TermlistCommand getTermlist() {
    return (TermlistCommand) (Pz2Bean.get().getStateMgr().getCommand(TERMLIST));
  }
  
  public BytargetCommand getBytarget() {
    return (BytargetCommand) (Pz2Bean.get().getStateMgr().getCommand(BYTARGET));
  }
  
  public Pazpar2Command getCommand(String name) {    
    return Pz2Bean.get().getStateMgr().getCommand(name);
  }
  
  public ServiceProxyCommands getSp() {
    if (sp == null) {
      sp = new ServiceProxyCommands(Pz2Bean.get().getStateMgr());
    }
    return sp;
  }
      
}
