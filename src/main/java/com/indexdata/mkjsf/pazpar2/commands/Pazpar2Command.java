package com.indexdata.mkjsf.pazpar2.commands;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import com.indexdata.mkjsf.pazpar2.ClientCommandResponse;
import com.indexdata.mkjsf.pazpar2.HttpResponseWrapper;
import com.indexdata.mkjsf.pazpar2.Pz2Service;
import com.indexdata.mkjsf.pazpar2.SearchClient;
import com.indexdata.mkjsf.pazpar2.commands.sp.ServiceProxyCommand;
import com.indexdata.mkjsf.pazpar2.data.ResponseDataObject;
import com.indexdata.mkjsf.pazpar2.data.ResponseParser;
import com.indexdata.mkjsf.pazpar2.data.Responses;

/**
 * Represents a generic Pazpar2 or Service Proxy command with all its current parameters, and has
 * methods for executing the command against the currently selected Pazpar2 service</p>
 * <p>Being an abstract class it only has generic methods for getting and setting parameters. 
 * Implementing classes are supposed to create named getters and setters for convenient access
 * to parameters from the UI.</p> 
 * <p>Parameters can be set with or without notifying the state manager.<p>
 * 
 * <p><i>Note: Internally the application has to be able to set parameters without state changes 
 * - for instance to avoid eternal feedback when copying parameter from one state to the next. A 
 * setting from the UI should spawn a new search state however.</i></p>   
 * 
 * @author Niels Erik
 *
 */
public abstract class Pazpar2Command implements Serializable  {
  
  private static Logger logger = Logger.getLogger(Pazpar2Command.class);
  private static final long serialVersionUID = -6825491856480675917L;   
  protected String name = "";
  protected Map<String,CommandParameter> parameters = new HashMap<String,CommandParameter>();  
  
  public Pazpar2Command () {    
  }
  
  public void setCommandName(String name) {
    this.name = name;
  }
          
  public Pazpar2Command (String name) {
    this.name = name;    
  }
      
  /**
   * Commands must implement this method to provide a completely detached, deep clone of 
   * themselves.
   * 
   * The clone is needed by the state manager to transfer commands with current setting 
   * from one state to the next.
   * 
   * Whenever a non-standard attribute is added to a command class, the copy method must 
   * be updated to ensure that the new attribute is brought over as well. 
   *   
   * @return a Pazpar2 command of the given type
   */
  public abstract Pazpar2Command copy ();
          
  public String getCommandName() {
    return name;
  }
  
  /**
   * Executes the command with the currently selected parameters against 
   * the currently selected Pazpar2 service
   * 
   * @return Response data object based on the Pazpar2 service response. 
   */
  public ResponseDataObject run() {    
    return run(Pz2Service.get().getSearchClient(),
               Pz2Service.get().getPzresp());
  }
  
  /**
   * Executes the commands with the currently selected parameters, while adding
   * the parameters provided in the vararg
   * @param parameters A list of parameters on the form [key=value]
   * 
   * @return Response data object based on the Pazpar2 service response
   */
  public ResponseDataObject runWith(String... parameters) {
    for (String parameter : parameters) {
      StringTokenizer tokenizer = new StringTokenizer(parameter,"=");
      String name = (String) tokenizer.nextElement();
      String value = (String) tokenizer.nextElement();
      CommandParameter commandParameter = new CommandParameter(name,"=",value);
      setParameterInState(commandParameter);
    }
    return run();
  }

  /**
   * Executes the commands with the currently selected parameters, while adding
   * the parameters provided in the 'delimiter'-separated String.
   * 
   * Note: This is for Glassfish/JBoss support. With Tomcat7 the method 
   *       runWith(String... parameters) can be used directly from EL 
   *       with a vararg 
   *  
   * @param parameters A list of parameters separated by 'delimiter'
   * @param delimiter The separator character of the String 'parameters' 
   * 
   * @return Response data object based on the Pazpar2 service response
   */
  public ResponseDataObject runWith2(String parameters, String delimiter) {    
    StringTokenizer params = new StringTokenizer(parameters,delimiter);
    String[] vararg = new String[params.countTokens()];
    int i=0;
    while (params.hasMoreTokens()) {
      vararg[i++] = params.nextToken();
    }
    return runWith(vararg);
  }
    
  /**
   * Executes the command in a thread.  
   * 
   * Note: Client and Responses must be provided because at this point 
   * CDI beans cannot be retrieved from within a thread.
   * 
   * @param client
   * @param pzresp
   * @return
   */
  public ResponseDataObject run(SearchClient client,Responses pzresp) {
    logger.debug("Running " + getCommandName() + " using " + client);    
    HttpResponseWrapper httpResponse = client.executeCommand(this);
    logger.debug("Parsing response for " + getCommandName());
    ResponseDataObject responseObject = ResponseParser.getParser().getDataObject((ClientCommandResponse) httpResponse);
    logger.trace("Storing response for " + getCommandName());
    pzresp.put(getCommandName(), responseObject);
    return responseObject;    
  }
  
   
  /**
   * Sets a parameter on this command and notifies the state manager
   * about the change
   * 
   * @param parameter 
   */
  public void setParameter (CommandParameter parameter) {
    Pazpar2Command copy = this.copy();
    logger.trace(name + " command: setting parameter [" + parameter.getName() + "=" + parameter.getValueWithExpressions() + "]");
    copy.parameters.put(parameter.getName(),parameter);
    checkInState(copy);
  }
  
  /**
   * Sets multiple parameters on the command and notifies the state
   * manager -- once -- about the change
   * 
   * @param params 
   */
  public void setParameters (CommandParameter... params) {
    Pazpar2Command copy = this.copy();
    for (CommandParameter param : params) {
      logger.trace(name + " command: setting parameter [" + param.getName() + "=" + param.getValueWithExpressions() + "]");
      copy.parameters.put(param.getName(),param);
    }
    checkInState(copy);
  }
  
  /**
   * Sets multiple parameters on this command without notifying the state manager. 
   * Typically used when one parameter setting should automatically trigger
   * other parameters to be reset to defaults etc. Intended to avoid 
   * useless proliferation of states  
   * 
   * @param params
   */
  public void setParametersInState (CommandParameter... params) {    
    for (CommandParameter param : params) {
      logger.trace(name + " command: setting parameter [" + param.getName() + "=" + param.getValueWithExpressions() + "] silently");
      parameters.put(param.getName(),param);
    }    
  }
  
  /**
   * Sets a parameter on this command without notifying the state manager. 
   * Typically used when one parameter setting should automatically trigger
   * other parameters to be reset to defaults etc. Intended to avoid 
   * useless proliferation of states  
   * 
   * @param parameter
   */    
  public void setParameterInState (CommandParameter parameter) {
    logger.trace(name + " command: setting parameter [" + parameter.getName() + "=" + parameter.getValueWithExpressions() + "] silently");
    parameters.put(parameter.getName(),parameter);    
  }
  
  
  /**
   * Retrieves a command parameter by parameter name
   * 
   * @param name of the parameter
   * @return CommandParameter
   */
  public CommandParameter getParameter (String name) {
    return parameters.get(name);
  }
  
  /**
   * Removes a parameter completely and notifies the state manager
   * about the change
   * 
   * @param name of the parameter to remove
   */
  public void removeParameter (String name) {
    Pazpar2Command copy = this.copy();
    copy.parameters.remove(name);
    checkInState(copy);
  }
  
  /**
   * Removes multiple parameters completely and notifies the state manager
   * -- once -- about the change
   * 
   * @param name of the parameter to remove
   */  
  public void removeParameters() {
    Pazpar2Command copy = this.copy();
    copy.parameters = new HashMap<String,CommandParameter>();
    checkInState(copy);
  }
  
  
  /**
   * Removes all parameters without notifying the state manager. For instance
   * used in case of change of Pazpar2 service or renewed login to a service.
   *  
   */
  public void removeParametersInState() {
    parameters = new HashMap<String,CommandParameter>();    
  }
  
  /**
   * Adds an expression to an ordered list of expressions on a given parameter
   * and notifies the state manager of the change
   * 
   * @param parameterName name of the parameter to add the expression to
   * @param expression
   */
  public void addExpression(String parameterName, Expression expression) {
    Pazpar2Command copy = this.copy();
    copy.getParameter(parameterName).addExpression(expression);
    checkInState(copy);
  }
  
  public void removeExpression(String parameterName, Expression expression) {
    Pazpar2Command copy = this.copy();
    copy.getParameter(parameterName).removeExpression(expression);
    checkInState(copy);    
  }
  
  public void removeExpressionsAfter(String parameterName, Expression expression,String... expressionFields) {
    Pazpar2Command copy = this.copy();
    copy.getParameter(parameterName).removeExpressionsAfter(expression,expressionFields);
    checkInState(copy);    
  }
  
  public void removeExpressions(String parameterName, String... expressionFields) {
    Pazpar2Command copy = this.copy();    
    copy.getParameter(parameterName).removeExpressions(expressionFields);    
    if (!getParameter(parameterName).hasValue() && !getParameter(parameterName).hasExpressions()) {
      copy.parameters.remove(parameterName);
    }
    checkInState(copy);    
  }
  
  public boolean hasParameters () {
    return (parameters.keySet().size()>0);
  }
  
  public boolean hasParameterValue(String parameterName) {
    return (parameters.get(parameterName) != null && (parameters.get(parameterName).hasValue()));
  }
    
  public String getEncodedQueryString () {
    StringBuilder queryString = new StringBuilder("command="+name);
    for (CommandParameter parameter : parameters.values()) {
      if (parameter.hasValue() || parameter.hasExpressions()) {
        queryString.append("&"+parameter.getEncodedQueryString());
      }
    }
    return queryString.toString();
  } 
    
  public String getValueWithExpressions() {    
    StringBuilder value = new StringBuilder("");
    for (CommandParameter parameter : parameters.values()) {
      if (parameter.hasValue() || parameter.hasExpressions()) {
        value.append("&" + parameter.getName() + parameter.operator + parameter.getValueWithExpressions());
      }
   }
    return value.toString();
  }
  
  @Override
  public boolean equals (Object otherCommand) {
    logger.trace("Comparing commands ["+this.toString()+"] and ["+otherCommand.toString() +"]");
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

  /*
  public String getUrlEncodedParameterValue(String parameterName) {
    return getParameter(parameterName).getEncodedQueryString();
  }
  */
  
  public void setSession (String sessionId) {
    setParameter(new CommandParameter("session","=",sessionId));
  }
  
  public String getSession() {
    return getParameterValue("session");
  } 
  
  /**
   * Notifies the state manager that this command changed a parameter
   * 
   * @param command
   */
  protected void checkInState(Pazpar2Command command) {
    Pz2Service.get().getStateMgr().checkIn(command);
  }
    
  /**
   * Implementing classes must provide their Service Proxy 
   * extension command if any extension parameters exists, 
   * or -- just to be polite -- 'this' if there is no
   * Service Proxy extension to the given command.
   * @return
   */
  public abstract ServiceProxyCommand getSp();
     
  /**
   * Here implementing commands publish whether they only 
   * apply to the Service Proxy or can be executed 
   * against straight Pazpar2 as well. This is convenient for a 
   * UI that switches between service types either 
   * deployment time or run time.
   *   
   * @return false if the command applies to straight Pazpar2
   */
  public abstract boolean spOnly();  
}
