package com.indexdata.pz2utils4jsf.pazpar2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.indexdata.pz2utils4jsf.pazpar2.CommandParameter;
import com.indexdata.pz2utils4jsf.pazpar2.Pazpar2Command;

public class Pazpar2Command implements Serializable  {

  private static Logger logger = Logger.getLogger(Pazpar2Command.class);
  private static final long serialVersionUID = -6825491856480675917L;
  public static List<String> allCommands = new ArrayList<String>(Arrays.asList("init","ping","settings","search","stat","show","record","termlist","bytarget"));

  private String name = "";
  private Map<String,CommandParameter> parameters = new HashMap<String,CommandParameter>();
  
  public Pazpar2Command (String name) {    
    this.name = name;
  }
  
  public Pazpar2Command copy () {
    Pazpar2Command newCommand = new Pazpar2Command(name);
    for (String parameterName : parameters.keySet()) {
      newCommand.setParameter(parameters.get(parameterName).copy());      
    }
    return newCommand;
  }
  
  public String getName() {
    return name;
  }
    
  public void setParameter (CommandParameter parameter) {
    logger.debug("Setting parameter " + parameter.getName() + "=" + parameter.getValueWithExpressions() + " to " + this.getName());
    parameters.put(parameter.getName(),parameter);
  }
  
  public CommandParameter getParameter (String name) {
    return parameters.get(name);
  }
  
  public void removeParameter (String name) {
    parameters.remove(name);    
  }
  
  public void removeParameters() {
    parameters = new HashMap<String,CommandParameter>();
  }
  
  public boolean hasParameters () {
    return (parameters.keySet().size()>0);
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
  
}
