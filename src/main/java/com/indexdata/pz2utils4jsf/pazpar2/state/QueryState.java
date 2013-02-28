package com.indexdata.pz2utils4jsf.pazpar2.state;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.indexdata.pz2utils4jsf.pazpar2.CommandParameter;
import com.indexdata.pz2utils4jsf.pazpar2.Expression;
import com.indexdata.pz2utils4jsf.pazpar2.Pazpar2Command;
import com.indexdata.pz2utils4jsf.pazpar2.state.QueryState;
import com.indexdata.pz2utils4jsf.pazpar2.state.QueryStates;

public class QueryState implements Serializable {
  
  private static final long serialVersionUID = -1465676392610954168L;
  private static Logger logger = Logger.getLogger(QueryState.class);
  private Map<String,Pazpar2Command> pz2commands = new HashMap<String,Pazpar2Command>();
  private String key = null;  
  
  public QueryState () {
    for (String command : Arrays.asList("init","ping","settings","search","stat","show","record","termlist","bytarget")) {
      pz2commands.put(command, new Pazpar2Command(command));
    }
  }    
  
  private QueryState copy() {    
    QueryState newState = new QueryState();
    for (String commandName : pz2commands.keySet()) {
      newState.setCommand(pz2commands.get(commandName).copy());
    }
    return newState;
  }
    
  public void setCommandParameter(String commandName, CommandParameter parameter, QueryStates queryStates) {
    CommandParameter current = getCommand(commandName).getParameter(parameter.getName());
    if (current != null && current.equals(parameter)) {
      logger.debug("Recieved parameter but already have " + parameter.getValueWithExpressions() + " in this state. No state change.");
    } else {
      logger.debug("New command parameter received: " + parameter.getValueWithExpressions() + ". Initiating new state.");
      QueryState newState = this.copy();
      newState._setCommandParameter(commandName,parameter);
      logger.debug("Old state: " + this);
      logger.debug("New state: " + newState);        
      queryStates.setCurrentState(newState);
    }
  }
  
  public void removeCommandParameter(String commandName, String parameterName, QueryStates queryStates) {
    QueryState newState = this.copy();
    newState._removeCommandParameter(commandName, parameterName);
    queryStates.setCurrentState(newState);
  }
  
  public void setCommandParameterExpression (String commandName, String parameterName, Expression expression, QueryStates queryStates) {
    QueryState newState = this.copy();
    newState._setCommandParameterExpression(commandName, parameterName, expression);
    queryStates.setCurrentState(newState);
  }
  
  public void _setCommandParameterExpression (String commandName, String parameterName, Expression expression) {
    getCommand(commandName).getParameter(parameterName).addExpression(expression);
  }
  
  public void removeCommandParameterExpression (String commandName, String parameterName, Expression expression, QueryStates queryStates) {
    QueryState newState = this.copy();
    newState._removeCommandParameterExpression(commandName, parameterName, expression);
    queryStates.setCurrentState(newState);
  }
  
  public void _removeCommandParameterExpression (String commandName, String parameterName, Expression expression) {
    getCommand(commandName).getParameter(parameterName).removeExpression(expression);
    
  }

  public void _setCommandParameter(String commandName, CommandParameter parameter) {
    getCommand(commandName).setParameter(parameter);
  }
  
  public void _removeCommandParameter(String commandName, String parameterName) {
    getCommand(commandName).removeParameter(parameterName);
  }
  
  public void _removeCommand(String commandName) {
    getCommand(commandName).removeParameters();    
  }

  
  public void removeCommand (String commandName, QueryStates queryStates) {
    QueryState newState = this.copy();
    newState._removeCommand(commandName);
    queryStates.setCurrentState(newState);
    
  }

  public Pazpar2Command getCommand(String name) {
    return pz2commands.get(name);
  }
  
  private void setCommand(Pazpar2Command command) {
    pz2commands.put(command.getName(), command);
  }
  
  public String getKey() {
    if (key == null) {
      StringBuilder querystatebuilder = new StringBuilder("#");
      for (Pazpar2Command command : pz2commands.values()) {
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
  
  public void setKey(String key) {
    logger.debug("Setting key on demand to: " + key);
    this.key = key;
  }
  
  public String toString () {
    return pz2commands.toString();
  }
  
  public boolean searchEquals(Object otherQueryState) {
    if (otherQueryState instanceof QueryState) {
      return getCommand("search").equals(((QueryState) otherQueryState).getCommand("search"));
    } else {
      return false;
    }
  }
  
  public boolean equals (Object otherQueryState) {
    if (otherQueryState instanceof QueryState) {
      return this.toString().equals(otherQueryState.toString());              
    } else {
      return false;
    }
  }

}
