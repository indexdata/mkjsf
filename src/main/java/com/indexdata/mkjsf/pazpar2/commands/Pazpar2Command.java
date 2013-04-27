package com.indexdata.mkjsf.pazpar2.commands;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.indexdata.mkjsf.pazpar2.commands.sp.ServiceProxyCommand;
import com.indexdata.mkjsf.pazpar2.state.StateManager;

public abstract class Pazpar2Command implements Serializable  {
  
  private static Logger logger = Logger.getLogger(Pazpar2Command.class);
  private static final long serialVersionUID = -6825491856480675917L;   
  protected String name = "";
  protected Map<String,CommandParameter> parameters = new HashMap<String,CommandParameter>();
  
  protected StateManager stateMgr;
    
  public Pazpar2Command (String name, StateManager stateMgr) {
    this.name = name;
    this.stateMgr = stateMgr;
  }
      
  public abstract Pazpar2Command copy ();
  
  public String getName() {
    return name;
  }
    
  public void setParameter (CommandParameter parameter) {
    Pazpar2Command copy = this.copy();
    logger.debug(name + " command: setting parameter [" + parameter.getName() + "=" + parameter.getValueWithExpressions() + "]");
    copy.parameters.put(parameter.getName(),parameter);
    checkInState(copy);
  }
  
  public void setParameters (CommandParameter... params) {
    Pazpar2Command copy = this.copy();
    for (CommandParameter param : params) {
      logger.debug(name + " command: setting parameter [" + param.getName() + "=" + param.getValueWithExpressions() + "]");
      copy.parameters.put(param.getName(),param);
    }
    checkInState(copy);
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
    checkInState(copy);
  }
  
  public void removeParameters() {
    Pazpar2Command copy = this.copy();
    copy.parameters = new HashMap<String,CommandParameter>();
    checkInState(copy);
  }
  
  public void removeParametersInState() {
    parameters = new HashMap<String,CommandParameter>();    
  }

  
  public boolean hasParameters () {
    return (parameters.keySet().size()>0);
  }
  
  public boolean hasParameterValue(String parameterName) {
    return (parameters.get(parameterName) != null && parameters.get(parameterName).hasValue());
  }
  
  public String getEncodedQueryString () {
    StringBuilder queryString = new StringBuilder("command="+name);
    for (CommandParameter parameter : parameters.values()) {
      if (parameter.hasValue()) {
        queryString.append("&"+parameter.getEncodedQueryString());
      }
    }
    return queryString.toString();
  } 
    
  public String getValueWithExpressions() {    
    StringBuilder value = new StringBuilder("");
    for (CommandParameter parameter : parameters.values()) {
      if (parameter.hasValue()) {
        value.append("&" + parameter.getName() + parameter.operator + parameter.getValueWithExpressions());
      }
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
    return getParameter(parameterName)==null ? "" : getParameter(parameterName).getValueWithExpressions();
    
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
  
  private void checkInState(Pazpar2Command command) {
    if (stateMgr != null) {
      stateMgr.checkIn(command);
    } else {
      logger.info("Command '" + command.getName() + "' not affecting state (history) as no state manager was defined for this command.");
    }
  }
  
  public abstract ServiceProxyCommand getSp();
  
}
