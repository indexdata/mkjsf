package com.indexdata.mkjsf.pazpar2.commands;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import com.indexdata.mkjsf.pazpar2.ClientCommandResponse;
import com.indexdata.mkjsf.pazpar2.HttpResponseWrapper;
import com.indexdata.mkjsf.pazpar2.Pz2Bean;
import com.indexdata.mkjsf.pazpar2.SearchClient;
import com.indexdata.mkjsf.pazpar2.commands.sp.ServiceProxyCommand;
import com.indexdata.mkjsf.pazpar2.data.ResponseDataObject;
import com.indexdata.mkjsf.pazpar2.data.ResponseParser;
import com.indexdata.mkjsf.pazpar2.data.Responses;

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
      
  public abstract Pazpar2Command copy ();
          
  public String getCommandName() {
    return name;
  }
  
  public ResponseDataObject run() {    
    return run(Pz2Bean.get().getSearchClient(),
               Pz2Bean.get().getPzresp());
  }
  
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
   * For running the command in a thread. Client and Responses must be 
   * provided because at this point the CDI bean cannot be retrieved 
   * from within a thread.
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
  
    
  public void setParameter (CommandParameter parameter) {
    Pazpar2Command copy = this.copy();
    logger.trace(name + " command: setting parameter [" + parameter.getName() + "=" + parameter.getValueWithExpressions() + "]");
    copy.parameters.put(parameter.getName(),parameter);
    checkInState(copy);
  }
  
  public void setParameters (CommandParameter... params) {
    Pazpar2Command copy = this.copy();
    for (CommandParameter param : params) {
      logger.trace(name + " command: setting parameter [" + param.getName() + "=" + param.getValueWithExpressions() + "]");
      copy.parameters.put(param.getName(),param);
    }
    checkInState(copy);
  }
  
  public void setParametersInState (CommandParameter... params) {    
    for (CommandParameter param : params) {
      logger.trace(name + " command: setting parameter [" + param.getName() + "=" + param.getValueWithExpressions() + "] silently");
      parameters.put(param.getName(),param);
    }    
  }
    
  public void setParameterInState (CommandParameter parameter) {
    logger.trace(name + " command: setting parameter [" + parameter.getName() + "=" + parameter.getValueWithExpressions() + "] silently");
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
    Pz2Bean.get().getStateMgr().checkIn(command);
  }
  
  public String navigateTo (String target) {
    return target;
  }
  
  public abstract ServiceProxyCommand getSp();
   
  public abstract boolean spOnly();  
}
