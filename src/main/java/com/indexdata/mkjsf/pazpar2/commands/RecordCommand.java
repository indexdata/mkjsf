package com.indexdata.mkjsf.pazpar2.commands;

import org.apache.log4j.Logger;

import com.indexdata.mkjsf.pazpar2.ClientCommandResponse;
import com.indexdata.mkjsf.pazpar2.HttpResponseWrapper;
import com.indexdata.mkjsf.pazpar2.Pz2Service;
import com.indexdata.mkjsf.pazpar2.commands.sp.RecordCommandSp;
import com.indexdata.mkjsf.pazpar2.commands.sp.ServiceProxyCommand;
import com.indexdata.mkjsf.pazpar2.data.RecordResponse;
import com.indexdata.mkjsf.pazpar2.data.ResponseDataObject;
import com.indexdata.mkjsf.pazpar2.data.ResponseParser;

/**
 * Represents a Pazpar2 <code>record</code> command, can be accessed by <code>pzreq.record</code>
 * 
 * @author Niels Erik
 *
 */
public class RecordCommand extends Pazpar2Command implements ServiceProxyCommand {

  private static final long serialVersionUID = 2817539422114569506L;
  private static Logger logger = Logger.getLogger(RecordCommand.class);
  private RecordCommandSp spCommand = null;

  public RecordCommand() {
    super("record");
  }
  
  /**
   * Special handling of record responses since they come in three distinctly different ways
   * <ol>
   *  <li>As a regular &lt;record&gt; document</li>
   *  <li>In arbitrary XML format, in case of an offset request to get the native format</li>
   *  <li>In binary (non XML) format</li>
   * </ol> 
   */
  @Override
  public ResponseDataObject run() {
    ResponseDataObject responseObject = null;
    if (hasParameterValue("id")) {
      HttpResponseWrapper commandResponse = Pz2Service.get().getSearchClient().executeCommand(this);
      
      if (commandResponse.getContentType().contains("xml")) {
        responseObject = ResponseParser.getParser().getDataObject((ClientCommandResponse)commandResponse);
        if (ResponseParser.docTypes.contains(responseObject.getType())) {
          logger.debug("Storing " + responseObject.getType() + " in pzresp. ");
        } else {
          logger.debug("Command was 'record' but response not '<record>' - assuming raw record response.");
          ResponseDataObject recordResponse = new RecordResponse();
          recordResponse.setType("record");
          recordResponse.setXml(responseObject.getXml());
          recordResponse.setAttribute("activeclients", "0");
          responseObject = recordResponse;
        }
      } else if (commandResponse.isBinary()) {
        responseObject = new RecordResponse();
        responseObject.setType(getCommandName());
        logger.info("Binary response");
        responseObject.setAttribute("activeclients", "0");
        responseObject.setXml("<record>binary response</record>");
        responseObject.setBinary(commandResponse.getBytes());
      } else {
        logger.error("Response was not found to be XML or binary. The response was not handled.");
      }
      Pz2Service.get().getPzresp().put(getCommandName(), responseObject);
    } else {
      logger.debug("No record id parameter on this command. Ignoring request but clearing any previous record result.");
      Pz2Service.get().getPzresp().put(getCommandName(), new RecordResponse());
    }
    return responseObject;
  }
  
  /**
   * Sets the <code>id</code> parameter. See Pazpar2 documentation for details.
   * 
   * @param recId record ID
   */
  public void setId(String recId) {
    setParameter(new CommandParameter("id","=",recId));
  }

  /** 
   * Returns the <code>id</code> parameter value.
   */
  public String getId () {
    return getParameterValue("id");
  }

  /**
   * Sets the <code>offset</code> parameter. See Pazpar2 documentation for details.
   */  
  public void setOffset (String offset) {
    setParameter(new CommandParameter("offset","=",offset));
  }

  /** 
   * Returns the <code>offset</code> parameter value.
   */
  public String getOffset () {
    return getParameterValue("offset");
  }
  
  /**
   * Sets the <code>checksum</code> parameter. See Pazpar2 documentation for details.
   */  
  public void setChecksum (String checksum) {
    setParameter(new CommandParameter("checksum","=",checksum));
  }
  
  /** 
   * Returns the <code>checksum</code> parameter value.
   */
  public String getChecksum () {
    return getParameterValue("checksum");
  }
  
  /**
   * Sets the <code>nativesyntax</code> parameter. See Pazpar2 documentation for details.
   */    
  public void setNativesyntax (String nativesyntax) {
    setParameterInState(new CommandParameter("nativesyntax","=",nativesyntax));
  }
  
  /** 
   * Returns the <code>nativesyntax</code> parameter value.
   */
  public String getNativesyntax () {
    return getParameterValue("nativesyntax");
  }
  
  /**
   * Sets the <code>syntax</code> parameter. See Pazpar2 documentation for details.
   */  
  public void setSyntax (String syntax) {
    setParameterInState(new CommandParameter("syntax","=",syntax));    
  }
  
  /** 
   * Returns the <code>syntax</code> parameter value.
   */
  public String getSyntax () {
    return getParameterValue("syntax");
  }
  
  /**
   * Sets the <code>esn</code> parameter. See Pazpar2 documentation for details.
   */  
  public void setEsn (String esn) {
    setParameter(new CommandParameter("esn","=",esn));
  }
  
  /** 
   * Returns the <code>esn</code> parameter value.
   */
  public String getEsn () {
    return getParameterValue("esn");
  }
  
  /**
   * Sets the <code>binary</code> parameter. See Pazpar2 documentation for details.
   */  
  public void setBinary (String binary) {
    setParameter(new CommandParameter("binary","=",binary));
  }
  
  /** 
   * Returns the <code>binary</code> parameter value.
   */
  public String getBinary () {
    return getParameterValue("binary");
  }

  @Override
  public RecordCommand copy () {
    RecordCommand newCommand = new RecordCommand();
    for (String parameterName : parameters.keySet()) {
      newCommand.setParameterInState(parameters.get(parameterName).copy());      
    }    
    newCommand.spCommand = this.spCommand;
    return newCommand;
  }
  
  
  /**
   * Returns a record command object with Service Proxy extension parameters 
   * 
   */
  public RecordCommandSp getSp () {
    if (spCommand==null) {
      spCommand = new RecordCommandSp(this);
    } 
    return spCommand;
  }

  @Override
  public boolean spOnly() {    
    return false;
  }
}
