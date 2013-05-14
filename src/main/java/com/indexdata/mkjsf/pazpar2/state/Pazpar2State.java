package com.indexdata.mkjsf.pazpar2.state;

import java.util.HashMap;
import java.util.Map;

import com.indexdata.mkjsf.pazpar2.commands.BytargetCommand;
import com.indexdata.mkjsf.pazpar2.commands.InitCommand;
import com.indexdata.mkjsf.pazpar2.commands.Pazpar2Command;
import com.indexdata.mkjsf.pazpar2.commands.Pazpar2Commands;
import com.indexdata.mkjsf.pazpar2.commands.PingCommand;
import com.indexdata.mkjsf.pazpar2.commands.RecordCommand;
import com.indexdata.mkjsf.pazpar2.commands.SearchCommand;
import com.indexdata.mkjsf.pazpar2.commands.SettingsCommand;
import com.indexdata.mkjsf.pazpar2.commands.ShowCommand;
import com.indexdata.mkjsf.pazpar2.commands.StatCommand;
import com.indexdata.mkjsf.pazpar2.commands.TermlistCommand;
import com.indexdata.mkjsf.pazpar2.commands.sp.AuthCommand;
import com.indexdata.mkjsf.pazpar2.commands.sp.CategoriesCommand;
import com.indexdata.mkjsf.pazpar2.commands.sp.ServiceProxyCommands;

/**
 * Holds a 'pazpar2 state', understood as a full set of pazpar2 commands and 
 * all their parameter settings at a given point in time.
 *  
 * @author Niels Erik
 *
 */
public class Pazpar2State {

  String key = null;
  Map<String,Pazpar2Command> commands = new HashMap<String,Pazpar2Command>();;

  public Pazpar2State (StateManager mgr) {
    commands.put(Pazpar2Commands.INIT,     new InitCommand(mgr));
    commands.put(Pazpar2Commands.PING,     new PingCommand(mgr));
    commands.put(Pazpar2Commands.SETTINGS, new SettingsCommand(mgr));
    commands.put(Pazpar2Commands.SEARCH,   new SearchCommand(mgr));
    commands.put(Pazpar2Commands.STAT,     new StatCommand(mgr));
    commands.put(Pazpar2Commands.SHOW,     new ShowCommand(mgr));
    commands.put(Pazpar2Commands.RECORD,   new RecordCommand(mgr));
    commands.put(Pazpar2Commands.TERMLIST, new TermlistCommand(mgr));
    commands.put(Pazpar2Commands.BYTARGET, new BytargetCommand(mgr));  

    commands.put(ServiceProxyCommands.AUTH, new AuthCommand(mgr));
    commands.put(ServiceProxyCommands.CATEGORIES, new CategoriesCommand(mgr));
    key = "#1";
  }
    
  /**
   * Creates new state by cloning all commands of the provided state and 
   * then overriding one of them with the provided state changing command.
   * 
   * @param previousState
   * @param newCommand
   */
  public Pazpar2State (Pazpar2State previousState, Pazpar2Command newCommand) {
    for (String commandName : previousState.commands.keySet()) {
      this.commands.put(commandName, previousState.commands.get(commandName).copy());
    }
    this.commands.put(newCommand.getCommandName(),newCommand);
    this.key = getKey();           
  }
    
  /**
   * Generates a state key that can be used by the browser to pick
   * up this state again at a later point in time.
   * 
   * @return
   */
  public String getKey() {
    if (key == null) {
      StringBuilder querystatebuilder = new StringBuilder("");
      for (Pazpar2Command command : commands.values()) {
        if (! (command instanceof AuthCommand )) {
          if (command.hasParameters()) {
            querystatebuilder.append("||"+command.getCommandName()+"::");
            querystatebuilder.append(command.getValueWithExpressions());
          }
        }
      }            
      key = "#"+querystatebuilder.toString();
      return key;
    } else {      
      return key;
    }
  }
  
  /**
   * Checks if a command represents a change of this state
   * 
   * @param command
   * @return true if the command causes a change of state
   */
  public boolean stateMutating (Pazpar2Command command) {
    if (command == null) {
      return true;
    } else if (commands.get(command.getCommandName()) == null) {
      return true;
    } else if ((command.equals(commands.get(command.getCommandName())))) {
      return false;      
    } else {
      return true;
    }
  } 
  
  /**
   * Returns a command from this state
   * 
   * @param name
   * @return
   */  
  public Pazpar2Command getCommand(String name) {
    return commands.get(name);
  }
}
