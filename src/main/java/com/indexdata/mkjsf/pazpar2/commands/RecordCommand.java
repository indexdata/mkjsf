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

public class RecordCommand extends Pazpar2Command implements ServiceProxyCommand {

  private static final long serialVersionUID = 2817539422114569506L;
  private static Logger logger = Logger.getLogger(RecordCommand.class);

  public RecordCommand() {
    super("record");
  }
  
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
  
  public void setId(String recId) {
    setParameter(new CommandParameter("id","=",recId));
  }
  
  public String getId () {
    return getParameterValue("id");
  }
  
  public void setOffset (String offset) {
    setParameter(new CommandParameter("offset","=",offset));
  }
  
  public String getOffset () {
    return getParameterValue("offset");
  }
  
  public void setChecksum (String checksum) {
    setParameter(new CommandParameter("checksum","=",checksum));
  }
  
  public String getChecksum () {
    return getParameterValue("checksum");
  }
  
  public void setNativesyntax (String nativesyntax) {
    setParameterInState(new CommandParameter("nativesyntax","=",nativesyntax));
  }
  
  public String getNativesyntax () {
    return getParameterValue("nativesyntax");
  }
  
  public void setSyntax (String syntax) {
    setParameterInState(new CommandParameter("syntax","=",syntax));    
  }
  
  public String getSyntax () {
    return getParameterValue("syntax");
  }
  
  public void setEsn (String esn) {
    setParameter(new CommandParameter("esn","=",esn));
  }
  
  public String getEsn () {
    return getParameterValue("esn");
  }
  
  public void setBinary (String binary) {
    setParameter(new CommandParameter("binary","=",binary));
  }
  
  public String getBinary () {
    return getParameterValue("binary");
  }

  @Override
  public RecordCommand copy () {
    RecordCommand newCommand = new RecordCommand();
    for (String parameterName : parameters.keySet()) {
      newCommand.setParameterInState(parameters.get(parameterName).copy());      
    }    
    return newCommand;
  }
  
  
  /**
   * Returns a record command object with Service Proxy extension parameters 
   * 
   */
  public RecordCommandSp getSp () {
    return new RecordCommandSp(this);
  }

  @Override
  public boolean spOnly() {    
    return false;
  }
}
