package com.indexdata.mkjsf.pazpar2.commands;

import org.apache.log4j.Logger;

import com.indexdata.mkjsf.pazpar2.commands.sp.InitCommandSp;
import com.indexdata.mkjsf.pazpar2.commands.sp.ServiceProxyCommand;

/**
 * Represents a Pazpar2 <code>init</code> command, can be accessed by <code>pzreq.init</code>
 * 
 * @author Niels Erik
 *
 */
public class InitCommand extends Pazpar2Command implements ServiceProxyCommand {

  private static final long serialVersionUID = -4915976465898889987L;
  private static Logger logger = Logger.getLogger(InitCommand.class);
  private InitCommandSp spCommand = null;
  
  public InitCommand() {
    super("init");
  }
  
  /**
   * Sets the <code>clear</code> parameter. See Pazpar2 documentation for details.
   * 
   * @param clear
   */
  public void setClear(String clear) { 
    setParameterInState(new CommandParameter("clear","=",clear));
  }

  /**
   * Returns the <code>clear</code> parameter value.
   */
  public String getClear() {
    return getParameterValue("clear");
  }
  
  /**
   * Sets the <code>service</code> parameter. See Pazpar2 documentation for details.
   * @param serviceId
   */
  public void setService(String serviceId) {    
    setParameterInState(new CommandParameter("service","=",serviceId));
  }
  
  /**
   * Returns the <code>service</code> parameter value.
   */  
  public String getService() {
    return getParameterValue("service");
  }
  
  /**
   * Disabled, not supported for <code>init</code>
   */
  @Override
  public void setSession (String sessionId) {
    throw new UnsupportedOperationException("Cannot set session id on init command");
  }

  /**
   * Disabled, not supported for <code>init</code>
   */
  @Override
  public String getSession () {
    throw new UnsupportedOperationException("Cannot set or get session id on init command");
  }  
  
  public InitCommand copy () {
    logger.info("Copying init command");
    InitCommand newCommand = new InitCommand();
    for (String parameterName : parameters.keySet()) {
      newCommand.setParameterInState(parameters.get(parameterName).copy());      
    }
    newCommand.spCommand = new InitCommandSp(this);
    newCommand.spCommand.setUploadedInitDoc(spCommand.getUploadedInitDoc());
    return newCommand;
  }
  
  public ServiceProxyCommand getSp() {
    if (spCommand==null) {
      spCommand = new InitCommandSp(this);      
    } 
    return spCommand;
  }

  @Override
  public boolean spOnly() {
    return false;
  }


}
