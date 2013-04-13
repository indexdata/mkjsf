package com.indexdata.pz2utils4jsf.pazpar2.commands;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.indexdata.pz2utils4jsf.pazpar2.state.StateManager;

public class Pazpar2Command implements CommandReadOnly, Serializable  {
  
  private static Logger logger = Logger.getLogger(Pazpar2Command.class);
  private static final long serialVersionUID = -6825491856480675917L;   
  private String name = "";
  protected Map<String,CommandParameter> parameters = new HashMap<String,CommandParameter>();
  
  StateManager stateMgr;
    
  public Pazpar2Command (String name, StateManager stateMgr) {
    this.name = name;
    this.stateMgr = stateMgr;
  }
      
  public Pazpar2Command copy () {
    Pazpar2Command newCommand = new Pazpar2Command(name,stateMgr);
    for (String parameterName : parameters.keySet()) {
      newCommand.setParameterSilently(parameters.get(parameterName).copy());      
    }    
    return newCommand;
  }
  
  public String getName() {
    return name;
  }
    
  public void setParameter (CommandParameter parameter) {
    logger.debug("Setting parameter " + parameter.getName() + "=" + parameter.getValueWithExpressions() + " to " + this.getName());
    parameters.put(parameter.getName(),parameter);
    stateMgr.checkIn(this);
  }
  
  public void setParameters (CommandParameter... params) {
    for (CommandParameter param : params) {
      logger.debug("Setting parameter " + param.getName() + "=" + param.getValueWithExpressions() + " to " + this.getName());
      parameters.put(param.getName(),param);
    }
    stateMgr.checkIn(this);
  }
  
  
  public void setParameterSilently (CommandParameter parameter) {
    logger.debug("Setting parameter silently " + parameter.getName() + "=" + parameter.getValueWithExpressions() + " to " + this.getName());
    parameters.put(parameter.getName(),parameter);    
  }
  
  
  public CommandParameter getParameter (String name) {
    return parameters.get(name);
  }
  
  public void removeParameter (String name) {
    parameters.remove(name);
    stateMgr.checkIn(this);
  }
  
  public void removeParameters() {
    parameters = new HashMap<String,CommandParameter>();
    stateMgr.checkIn(this);
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

  @Override
  public String getParameterValue(String parameterName) {
    return getParameter(parameterName).getValueWithExpressions();
    
  }

  @Override
  public String getUrlEncodedParameterValue(String parameterName) {
    return getParameter(parameterName).getEncodedQueryString();
  }
  
  
  
}
