package com.indexdata.pz2utils4jsf.pazpar2.state;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.indexdata.pz2utils4jsf.pazpar2.Pazpar2Command;
import com.indexdata.pz2utils4jsf.pazpar2.state.QueryState;
import com.indexdata.pz2utils4jsf.pazpar2.state.QueryStates;

public class QueryStates implements Serializable {
  
  private static final long serialVersionUID = 6131720167974584659L;
  private static Logger logger = Logger.getLogger(QueryStates.class);
    
  Map<String, QueryState> queryStates = new HashMap<String, QueryState>();
  String currentStateKey = "";
  Map<String,Boolean> pendingStateChanges = new HashMap<String,Boolean>();  

  public QueryStates () {
    queryStates.put("#initial", new QueryState());
    currentStateKey = "#initial";
    for (String command : Pazpar2Command.allCommands) {
      pendingStateChanges.put(command, new Boolean(false));
    }
  }
  
  public String getCurrentStateKey() {
    return currentStateKey;
  }

  public void setCurrentStateKey(String key) {
    
    if (currentStateKey.equals(key)) {
      logger.debug("setCurrentStateKey: no key change detected");
    } else {
      logger.debug("State key change. Was: [" + currentStateKey + "]. Will be ["+key+"]");
      if (queryStates.get(key).getCommand("search").equals(getCurrentState().getCommand("search"))) {
        logger.debug("No search change detected");
      } else {
        hasPendingStateChange("search",true);
      }
      if (queryStates.get(key).getCommand("record").equals(getCurrentState().getCommand("record"))) {
        logger.debug("No record change detected");
      } else {
        hasPendingStateChange("record",true);
      }
      currentStateKey = key;
    }
  }
    
  public QueryState getCurrentState() {
    if (queryStates.get(currentStateKey) == null) {      
      return new QueryState();
    } else {            
      return queryStates.get(currentStateKey);
    }
  }
  
  public void setCurrentState(QueryState queryState) {
    logger.debug("Setting current state: " + queryState.getKey());
    queryStates.put(queryState.getKey(), queryState);
    setCurrentStateKey(queryState.getKey());
  }
    
  public void hasPendingStateChange(String command, boolean bool) {
    pendingStateChanges.put(command, new Boolean(bool));
  }
  
  public boolean hasPendingStateChange (String command) {
    return pendingStateChanges.get(command).booleanValue();
  }
  
}
