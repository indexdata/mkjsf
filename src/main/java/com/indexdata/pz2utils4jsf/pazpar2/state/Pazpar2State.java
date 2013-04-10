package com.indexdata.pz2utils4jsf.pazpar2.state;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.indexdata.pz2utils4jsf.pazpar2.Pazpar2Command;


public class Pazpar2State {

  String key = null;
  Map<String,Pazpar2Command> commands = new HashMap<String,Pazpar2Command>();;

  public Pazpar2State () {    
    for (String command : Arrays.asList("init","ping","settings","search","stat","show","record","termlist","bytarget")) {
      commands.put(command, new Pazpar2Command(command));
    }
    key = "#initial";
  }
  
  public Pazpar2State (Pazpar2State previousState, Pazpar2Command newCommand) {
    for (String commandName : previousState.commands.keySet()) {
      this.commands.put(commandName, previousState.commands.get(commandName).copy());
    }
    this.commands.put(newCommand.getName(),newCommand);
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
      StringBuilder querystatebuilder = new StringBuilder("#");
      for (Pazpar2Command command : commands.values()) {
        if (command.hasParameters()) {
          querystatebuilder.append("||"+command.getName()+"::");
          querystatebuilder.append(command.getValueWithExpressions());
        }      
      }            
      key = querystatebuilder.toString();
      return key;
    } else {      
      return key;
    }
  }
  
  /**
   * Checks if the provided command represents a state change
   * 
   * @param command
   * @return true if the command causes a change of state
   */
  public boolean stateMutating (Pazpar2Command command) {
    if (command == null) {
      return true;
    } else if (commands.get(command.getName()) == null) {
      return true;
    } else if ((command.equals(commands.get(command.getName())))) {
      return false;      
    } else {
      return true;
    }
  } 
  
  public Pazpar2Command getCommand(String name) {
    return commands.get(name);
  }
}
