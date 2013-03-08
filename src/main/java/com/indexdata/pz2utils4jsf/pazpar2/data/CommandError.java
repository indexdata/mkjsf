package com.indexdata.pz2utils4jsf.pazpar2.data;

import static com.indexdata.pz2utils4jsf.utils.Utils.nl;

import java.util.ArrayList;
import java.util.List;

import com.indexdata.pz2utils4jsf.errors.ApplicationError;
import com.indexdata.pz2utils4jsf.errors.ErrorHelper;
import com.indexdata.pz2utils4jsf.errors.ErrorHelper.ErrorCode;
import com.indexdata.utils.XmlUtils;

/**
 * Captures errors encountered during the execution of a command. 
 * Is parsed by Pazpar2ResponseParser, piggybacked in a (seemingly)
 * regular command respond.
 * 
 * @author Niels Erik
 *
 */
public class CommandError extends Pazpar2ResponseData implements ApplicationError {

  private static final long serialVersionUID = 8878776025779714122L;
  private ErrorCode applicationErrorCode;
  private ErrorHelper errorHelper = null;
  
  
  public CommandError () {    
  }
  
  public String getLabel() {
    return getOneElementValue("commandname");
  }
      
  public String getMessage() {
    return getOneElementValue("errormessage");
  }
    
  public String getException () {
    return getOneElementValue("exception");
  }
    
  public List<String> getSuggestions() { 
    if (errorHelper!=null) {
      return errorHelper.getSuggestions(this);
    } else {
      List<String> nohelper = new ArrayList<String>();
      nohelper.add("Tips: could not generate tips due to a programming error, error helper was not set");
      return nohelper;
    }
  }
  
  /**
   * Creates an XML string error message, embedded in an XML string document named by the command
   * @param commandName
   * @param exceptionName
   * @param errorMessage
   * @return
   */
  public static String createErrorXml (String commandName, String exceptionName, String errorMessage) {
    StringBuilder errorXml = new StringBuilder("");
    errorXml.append("<" + commandName + ">"+nl);
    errorXml.append(" <applicationerror>"+nl);
    errorXml.append("  <commandname>" + commandName + "</commandname>"+nl);
    errorXml.append("  <exception>" + XmlUtils.escape(exceptionName) + "</exception>"+nl);    
    errorXml.append("  <errormessage>" + XmlUtils.escape(errorMessage) + "</errormessage>"+nl);    
    errorXml.append(" </applicationerror>"+nl);
    errorXml.append("</" + commandName + ">"+nl);
    return errorXml.toString(); 
  }
  
  public void setErrorHelper (ErrorHelper errorHelper) {
    this.errorHelper = errorHelper; 
  }

  @Override
  public void setApplicationErrorCode(ErrorCode code) {
    this.applicationErrorCode = code;    
  }

  @Override
  public ErrorCode getApplicationErrorCode() {
    return applicationErrorCode;    
  }

}
