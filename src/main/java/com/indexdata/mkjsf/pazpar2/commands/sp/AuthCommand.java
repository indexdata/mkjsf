package com.indexdata.mkjsf.pazpar2.commands.sp;

import org.apache.log4j.Logger;

import com.indexdata.mkjsf.pazpar2.ClientCommandResponse;
import com.indexdata.mkjsf.pazpar2.Pz2Service;
import com.indexdata.mkjsf.pazpar2.commands.CommandParameter;
import com.indexdata.mkjsf.pazpar2.commands.Pazpar2Command;
import com.indexdata.mkjsf.pazpar2.data.ResponseParser;
import com.indexdata.mkjsf.pazpar2.data.sp.SpResponseDataObject;

/**
 * Represents a Service Proxy <code>auth</code> command, can be accessed by <code>pzreq.sp.auth</code> 
 * 
 * <p>Authenticates a user against a Pazpar2 Service Proxy</p>
 * 
 * @author Niels Erik
 *
 */
public class AuthCommand extends Pazpar2Command implements ServiceProxyCommand {

  private static final long serialVersionUID = 5487611235664162578L;
  private static Logger logger = Logger.getLogger(AuthCommand.class);

  public AuthCommand() {
    super("auth");
  }
  
  public SpResponseDataObject run() {
    Pz2Service.get().resetSearchAndRecordCommands();
    Pz2Service.get().getPzresp().getSp().resetAuthAndBeyond(true);
    ClientCommandResponse response = (ClientCommandResponse) Pz2Service.get().getSearchClient().executeCommand(this);      
    String renamedResponse = renameResponseElement(response.getResponseString(), "auth");    
    response.setResponseToParse(renamedResponse);
    SpResponseDataObject responseObject = (SpResponseDataObject) ResponseParser.getParser().getDataObject(response);    
    if (ResponseParser.docTypes.contains(responseObject.getType())) {
      Pz2Service.get().getPzresp().put(getCommandName(), responseObject);
    }
    if (responseObject.unsupportedCommand()) {
      logger.error("auth command does not seem to be supported by this Service Proxy");
    }
    return responseObject;
  }
  
  /**
   * Normalizes the response XML for the benefit of the SAX parser that creates data objects. 
   * <p>The parser expects responses to have document element names corresponding to the names of
   * the commands that created the responses.</p>
   * 
   * @param responseString
   * @param newName
   * @return
   */
  private String renameResponseElement(String responseString, String newName) {
    responseString = responseString.replace("<response>", "<" + newName + ">");
    responseString = responseString.replace("</response>", "</" + newName + ">");
    return responseString;
  }

  /**
   * Sets Service Proxy command parameter <code>action</code>. See Service Proxy documentation for details. 
   */
  public void setAction (String action) {
    setParameterInState(new CommandParameter("action","=",action));
  }  
  
  /**
   * Gets parameter value for <code>action</cod>
   */
  public String getAction () {
    return getParameterValue("action");
  }
  
  /**
   * Sets Service Proxy command parameter <code>username</code>. See Service Proxy documentation for details. 
   */
  public void setUsername(String username) {
    setParameterInState(new CommandParameter("username","=",username));
  }
  
  /**
   * Gets parameter value for <code>username</cod>
   */
  public String getUsername () {
    return getParameterValue("username");
  }
    
  /**
   * Sets Service Proxy command parameter <code>password</code>. See Service Proxy documentation for details. 
   */
  public void setPassword (String password) {
    setParameterInState(new CommandParameter("password","=",password));
  }
  
  /**
   * Gets parameter value for <code>password</cod>
   */
  public String getPassword () {
    return getParameterValue("password");
  }
    
  public AuthCommand copy () {
    AuthCommand newCommand = new AuthCommand();
    for (String parameterName : parameters.keySet()) {
      newCommand.setParameterInState(parameters.get(parameterName).copy());      
    }    
    return newCommand;
  }

  @Override
  public ServiceProxyCommand getSp() {
    return this;
  }

  @Override
  public boolean spOnly() {
    return true;
  }
}
