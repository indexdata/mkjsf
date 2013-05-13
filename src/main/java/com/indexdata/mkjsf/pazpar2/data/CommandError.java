package com.indexdata.mkjsf.pazpar2.data;

import static com.indexdata.mkjsf.utils.Utils.nl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.indexdata.mkjsf.errors.ErrorHelper;
import com.indexdata.mkjsf.errors.ErrorInterface;
import com.indexdata.mkjsf.errors.ErrorHelper.ErrorCode;
import com.indexdata.utils.XmlUtils;

/**
 * Holds an error encountered during the execution of a command.
 * 
 * An error can be received by a command thread as an exception message 
 * or as an error XML. In both cases the error (string or xml) will be embedded
 * in a new 'applicationerror' element which in turn will be embedded in a
 * command XML (i.e. a 'search' or a 'show' response XML)  
 * 
 * The command response XML is subsequently parsed by ResponseParser, 
 * which will then create the CommandError object.
 * 
 * @author Niels Erik
 *
 */
public class CommandError extends ResponseDataObject implements ErrorInterface {

  private static final long serialVersionUID = 8878776025779714122L;
  private static Pattern xmlDeclaration = Pattern.compile("<\\?xml.*\\?>");
  private ErrorCode applicationErrorCode;
  private ErrorHelper errorHelper = null;
  
  
  public CommandError () {    
  }
  
  public String getLabel() {
    return getOneElementValue("commandname");
  }
      
  public String getMessage() {
    if (hasPazpar2Error()) {      
      return getPazpar2Error().getMsg();
    } else {      
      return getOneElementValue("errormessage");
    }
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
   * This is the XML that ResponseParser will turn into a CommandError object. 
   * @param commandName
   * @param exception
   * @param errorMessage
   * @return
   */
  public static String createErrorXml (String commandName, String statusCode, String exception, String errorMessage, String response) {
    StringBuilder errorXml = new StringBuilder("");
    errorXml.append("<" + commandName + ">"+nl);
    errorXml.append(" <applicationerror>"+nl);
    errorXml.append("  <commandname>" + commandName + "</commandname>"+nl);
    errorXml.append("  <statuscode>" + statusCode + "</statuscode>"+nl);
    errorXml.append("  <exception>" + (exception != null ? XmlUtils.escape(exception) : "") + "</exception>"+nl);    
    errorXml.append("  <errormessage>" + (errorMessage != null  ? XmlUtils.escape(errorMessage) : "") + "</errormessage>"+nl);
    errorXml.append("  <response>" + response + "</response>" + nl);
    errorXml.append(" </applicationerror>"+nl);
    errorXml.append("</" + commandName + ">"+nl);
    return errorXml.toString(); 
  }
  
  /**
   * Embeds a Pazpar2 (or Pazpar2 client) error response document as a child element of
   * a command response document (like 'search' or 'show').
   * This is the XML that ResponseParser will turn into a CommandError object.
   * 
   * 
   * @param commandName The name of the command during which's execution the error was encountered
   * @param exception The (possibly loosely defined) name of the exception that was thrown
   * @param pazpar2ErrorXml The error document as created by Pazpar2, or the Service Proxy or 
   *                        by the Pazpar2 client itself. 
   * @return
   */
  public static String insertErrorXml (String commandName, String statusCode, String exception, String pazpar2ErrorXml) {
    StringBuilder errorXml = new StringBuilder("");
    errorXml.append("<" + commandName + ">"+nl);
    errorXml.append(" <applicationerror>"+nl);
    errorXml.append("  <commandname>" + commandName + "</commandname>"+nl);
    errorXml.append("  <statuscode>" + statusCode + "</statuscode>"+nl);
    errorXml.append("  <exception>" + XmlUtils.escape(exception) + "</exception>"+nl);    
    errorXml.append(xmlDeclaration.matcher(pazpar2ErrorXml).replaceAll("")+nl);    
    errorXml.append(" </applicationerror>"+nl);
    errorXml.append("</" + commandName + ">"+nl);
    return errorXml.toString(); 
    
  }
   
  /**
   * Sets the object that should be used to analyze the error
   *  
   */
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
  
  public boolean hasPazpar2Error () {
    return ( getOneElement("error") != null);            
  }
  
  public Pazpar2Error getPazpar2Error() {
    return (Pazpar2Error) getOneElement("error");
  }


}
