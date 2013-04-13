package com.indexdata.pz2utils4jsf.pazpar2.commands;

/**
 * Interface to Pazpar2Command to be used when the given command
 * should not change. Meant to avoid redundant cloning of commands
 * for potential state changes. 
 * 
 * @author Niels Erik
 *
 */
public interface CommandReadOnly {

  public String getName();
  public String getValueWithExpressions();
  public String getEncodedQueryString();
  public boolean hasParameters();
  public boolean hasParameterSet(String parameterName);
  public String getParameterValue(String parameterName);
  public String getUrlEncodedParameterValue(String parameterName);
  
}
