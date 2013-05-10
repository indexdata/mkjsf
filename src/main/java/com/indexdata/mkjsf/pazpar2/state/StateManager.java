package com.indexdata.mkjsf.pazpar2.state;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.SessionScoped;

import org.apache.log4j.Logger;

import com.indexdata.mkjsf.pazpar2.commands.Pazpar2Command;
import com.indexdata.mkjsf.utils.Utils;

@SessionScoped
public class StateManager implements Serializable {
  
  private static final long serialVersionUID = 8152558351351730035L;

  Map<String, Pazpar2State> states = new HashMap<String, Pazpar2State>();
  String currentKey = "";
  private static List<String> allCommands = new ArrayList<String>(Arrays.asList("init","ping","settings","search","stat","show","record","termlist","bytarget","auth"));
  Map<String,Boolean> pendingStateChanges = new HashMap<String,Boolean>();
  private static Logger logger = Logger.getLogger(StateManager.class);
  private List<StateListener> listeners = new ArrayList<StateListener>();
  
  public StateManager () {
    logger.info("Initializing a Pazpar2 state manager [" + Utils.objectId(this) + "]");
    Pazpar2State initialState = new Pazpar2State(this);
    states.put(initialState.getKey(), initialState);
    currentKey = initialState.getKey();
    for (String command : allCommands) {
      pendingStateChanges.put(command, new Boolean(false));
    }
  }
  
  public void addStateListener(StateListener listener) {
    listeners.add(listener);
  }
  
  public void removeStateListener (StateListener listener) {
    listeners.remove(listener);
  }
  
  private void updateListeners (String command) {
    for (StateListener lsnr : listeners) {
      lsnr.stateUpdated(command);
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
      logger.debug("State changed by: " + command.getCommandName());
      Pazpar2State state = new Pazpar2State(getCurrentState(),command);
      states.put(state.getKey(), state);
      currentKey = state.getKey();
      hasPendingStateChange(command.getCommandName(),new Boolean(true));      
      logger.debug("Updating " + listeners.size() + " listener(s) with state change from " + command);
      updateListeners(command.getCommandName());      
    } else {
      logger.debug("Command " + command.getCommandName() + " not found to change the state [" + command.getEncodedQueryString() + "]");
    }
  }
      
  public Pazpar2Command getCommand (String commandName) {
    return getCurrentState().getCommand(commandName);
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
      if (states.get(key)==null) {
        logger.error("The back-end received an unknow state key: ["+ key +"].");        
      } else {
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
  }

  /**
   * Sets a pending-state-change flag for the given command and notifies
   * registered listeners. 
   * 
   * It is up to the listener to reset the flag as needed.
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
