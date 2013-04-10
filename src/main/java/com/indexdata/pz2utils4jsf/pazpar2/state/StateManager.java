package com.indexdata.pz2utils4jsf.pazpar2.state;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.indexdata.pz2utils4jsf.pazpar2.Pazpar2Command;

public class StateManager {
  
  Map<String, Pazpar2State> states = new HashMap<String, Pazpar2State>();
  String currentKey = "";
  Map<String,Boolean> pendingStateChanges = new HashMap<String,Boolean>();
  private static Logger logger = Logger.getLogger(StateManager.class);
  
  public StateManager () {
    Pazpar2State initialState = new Pazpar2State();
    states.put(initialState.getKey(), initialState);
    currentKey = initialState.getKey();
    for (String command : Pazpar2Command.allCommands) {
      pendingStateChanges.put(command, new Boolean(false));
    }

  }
  
  /**
   * Registers a Pazpar2 command for execution.
   * 
   * The state manager will update current state and flag that
   * a request change was made but that it was not yet carried 
   * out against Pazpar2.
   * 
   * Any command that is created or modified must be checked in
   * like this to come into effect.
   * 
   * @param command
   */
  public void checkIn(Pazpar2Command command) {
    if (getCurrentState().stateMutating(command)) {
      Pazpar2State state = new Pazpar2State(getCurrentState(),command);
      states.put(state.getKey(), state);
      currentKey = state.getKey();
      hasPendingStateChange(command.getName(),new Boolean(true));
    } else {
      logger.debug("Command " + command.getName() + " not found to change the state [" + command.getEncodedQueryString() + "]");
    }
  }
  
  /**
   * Gets a detached copy of a command. For the change manager
   * to become aware of any changes to the copy it must be 
   * checked back in with 'checkIn(Pazpar2Command)'
   * 
   * @param commandName
   * @return Copy this state's instance of the given command
   */
  public Pazpar2Command checkOut (String commandName) {
    return getCurrentState().getCommand(commandName).copy();
  }
  
  public Pazpar2State getCurrentState () {
    return states.get(currentKey);
  }
  
  /**
   * Changes the current state key. Invoked from the UI to have the state 
   * manager switch to another state than the current one. 
   * 
   * @param key
   */
  public void setCurrentStateKey(String key) {    
    if (currentKey.equals(key)) {
      logger.debug("setCurrentStateKey: no key change detected");
    } else {
      logger.debug("State key change. Was: [" + currentKey + "]. Will be ["+key+"]");
      if (states.get(key).getCommand("search").equals(states.get(currentKey).getCommand("search"))) {
        logger.debug("No search change detected");
      } else {
        hasPendingStateChange("search",true);
      }
      if (states.get(key).getCommand("record").equals(states.get(currentKey).getCommand("record"))) {
        logger.debug("No record change detected");
      } else {
        hasPendingStateChange("record",true);
      }
      currentKey = key;
    }
  }

  /**
   * Sets a pending-state-change flag for the given command. Used by
   * the beans to decide whether, say, a search should be executed before
   * doing the next show. 
   * 
   * It is up to the client to set and reset this flag since the state
   * manager is not otherwise informed about actual request activities 
   * (only about the definition of commands to be executed)
   * 
   * @param command
   * @param bool
   */
  public void hasPendingStateChange(String command, boolean bool) {
    pendingStateChanges.put(command, new Boolean(bool));
  }
  
  /**
   * 
   * @param command
   * @return true if there is a non-executed command change in this state
   */
  public boolean hasPendingStateChange (String command) {
    return pendingStateChanges.get(command).booleanValue();
  }

}
