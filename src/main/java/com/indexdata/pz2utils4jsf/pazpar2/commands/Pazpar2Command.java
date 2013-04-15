package com.indexdata.pz2utils4jsf.pazpar2.commands;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.indexdata.pz2utils4jsf.pazpar2.state.StateManager;

public class Pazpar2Command implements Serializable  {
  
  private static Logger logger = Logger.getLogger(Pazpar2Command.class);
  private static final long serialVersionUID = -6825491856480675917L;   
  private String name = "";
  protected Map<String,CommandParameter> parameters = new HashMap<String,CommandParameter>();
  
  StateManager stateMgr;
    
  public Pazpar2Command (String name, StateManager stateMgr) {
    this.name = name;
    if (stateMgr == null) {
      // Set one-off dummy state mgr
      this.stateMgr = new StateManager();
    } else {
      this.stateMgr = stateMgr;
    }
  }
      
  public Pazpar2Command copy () {
    Pazpar2Command newCommand = new Pazpar2Command(name,stateMgr);
    for (String parameterName : parameters.keySet()) {
      newCommand.setParameterInState(parameters.get(parameterName).copy());      
    }    
    return newCommand;
  }
  
  public String getName() {
    return name;
  }
    
  public void setParameter (CommandParameter parameter) {
    Pazpar2Command thisCommand = this.copy();
    logger.debug(name + " setting parameter " + parameter.getName() + "=" + parameter.getValueWithExpressions() + " to " + this.getName());
    thisCommand.parameters.put(parameter.getName(),parameter);
    stateMgr.checkIn(thisCommand);
  }
  
  public void setParameters (CommandParameter... params) {
    Pazpar2Command thisCommand = this.copy();
    for (CommandParameter param : params) {
      logger.debug(name + " setting parameter " + param.getName() + "=" + param.getValueWithExpressions() + " to " + this.getName());
      thisCommand.parameters.put(param.getName(),param);
    }
    stateMgr.checkIn(thisCommand);
  }
  
  public void setParametersInState (CommandParameter... params) {    
    for (CommandParameter param : params) {
      logger.debug(name + " setting parameter " + param.getName() + "=" + param.getValueWithExpressions() + " to " + this.getName());
      parameters.put(param.getName(),param);
    }    
  }
    
  public void setParameterInState (CommandParameter parameter) {
    logger.debug(name + " setting parameter silently " + parameter.getName() + "=" + parameter.getValueWithExpressions() + " to " + this.getName());
    parameters.put(parameter.getName(),parameter);    
  }
  
  
  public CommandParameter getParameter (String name) {
    return parameters.get(name);
  }
  
  public void removeParameter (String name) {
    Pazpar2Command thisCommand = this.copy();
    thisCommand.parameters.remove(name);
    stateMgr.checkIn(thisCommand);
  }
  
  public void removeParameters() {
    Pazpar2Command thisCommand = this.copy();
    thisCommand.parameters = new HashMap<String,CommandParameter>();
    stateMgr.checkIn(thisCommand);
  }
  
  public void removeParametersInState() {
    parameters = new HashMap<String,CommandParameter>();    
  }

  
  public boolean hasParameters () {
    return (parameters.keySet().size()>0);
  }
  
  public boolean hasParameterSet(String parameterName) {
    return (parameters.get(parameterName) != null);
  }
  
  public String getEncodedQueryString () {
    StringBuilder queryString = new StringBuilder("command="+name);
    for (CommandParameter parameter : parameters.values()) {
       queryString.append("&"+parameter.getEncodedQueryString());       
    }
    return queryString.toString();
  } 
    
  public String getValueWithExpressions() {    
    StringBuilder value = new StringBuilder("");
    for (CommandParameter parameter : parameters.values()) {
      value.append("&" + parameter.getName() + parameter.operator + parameter.getValueWithExpressions());       
   }
    return value.toString();
  }
  
  @Override
  public boolean equals (Object otherCommand) {
    return
        ((otherCommand instanceof Pazpar2Command)
         && this.getValueWithExpressions().equals(((Pazpar2Command) otherCommand).getValueWithExpressions()));
  }
  
  @Override
  public int hashCode () {
    return getValueWithExpressions().hashCode();
  }
  
  public String toString () {
    return parameters.toString();
  }

  public String getParameterValue(String parameterName) {
    return getParameter(parameterName).getValueWithExpressions();
    
  }

  public String getUrlEncodedParameterValue(String parameterName) {
    return getParameter(parameterName).getEncodedQueryString();
  }
  
  public void setSession (String sessionId) {
    setParameter(new CommandParameter("session","=",sessionId));
  }
  
  public String getSession() {
    return getParameterValue("session");
  }  
}
