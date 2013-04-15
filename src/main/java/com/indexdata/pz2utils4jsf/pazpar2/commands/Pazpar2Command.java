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
      // Sets throw-away state
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
    Pazpar2Command copy = this.copy();
    logger.debug(name + " command: setting parameter [" + parameter.getName() + "=" + parameter.getValueWithExpressions() + "]");
    copy.parameters.put(parameter.getName(),parameter);
    stateMgr.checkIn(copy);
  }
  
  public void setParameters (CommandParameter... params) {
    Pazpar2Command copy = this.copy();
    for (CommandParameter param : params) {
      logger.debug(name + " command: setting parameter [" + param.getName() + "=" + param.getValueWithExpressions() + "]");
      copy.parameters.put(param.getName(),param);
    }
    stateMgr.checkIn(copy);
  }
  
  public void setParametersInState (CommandParameter... params) {    
    for (CommandParameter param : params) {
      logger.debug(name + " command: setting parameter [" + param.getName() + "=" + param.getValueWithExpressions() + "] silently");
      parameters.put(param.getName(),param);
    }    
  }
    
  public void setParameterInState (CommandParameter parameter) {
    logger.debug(name + " command: setting parameter [" + parameter.getName() + "=" + parameter.getValueWithExpressions() + "] silently");
    parameters.put(parameter.getName(),parameter);    
  }
  
  
  public CommandParameter getParameter (String name) {
    return parameters.get(name);
  }
  
  public void removeParameter (String name) {
    Pazpar2Command copy = this.copy();
    copy.parameters.remove(name);
    stateMgr.checkIn(copy);
  }
  
  public void removeParameters() {
    Pazpar2Command copy = this.copy();
    copy.parameters = new HashMap<String,CommandParameter>();
    stateMgr.checkIn(copy);
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
