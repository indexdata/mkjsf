package com.indexdata.pz2utils4jsf.pazpar2.data;

import static com.indexdata.pz2utils4jsf.utils.Utils.nl;

import java.util.ArrayList;
import java.util.List;

import com.indexdata.pz2utils4jsf.pazpar2.ApplicationTroubleshooter;
import com.indexdata.utils.XmlUtils;

public class ApplicationError extends Pazpar2ResponseData {

  private static final long serialVersionUID = 8878776025779714122L;
  private ApplicationTroubleshooter errorHelper = null;
  
  
  public ApplicationError () {    
  }
  
  public String getCommandName() {
    return getOneElementValue("commandname");
  }
      
  public String getErrorMessage() {
    return getOneElementValue("errormessage");
  }
    
  public String getException () {
    return getOneElementValue("exception");
  }
    
  public List<String> getSuggestions() { 
    if (errorHelper!=null) {
      return errorHelper.getSuggestions(getCommandName(), getErrorMessage());
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
    errorXml.append("  <commandname>" + commandName + "</commandname>");
    errorXml.append("  <exception>" + XmlUtils.escape(exceptionName) + "</exception>"+nl);    
    errorXml.append("  <errormessage>" + XmlUtils.escape(errorMessage) + "</errormessage>"+nl);
    errorXml.append(" </applicationerror>"+nl);
    errorXml.append("</" + commandName + ">"+nl);
    return errorXml.toString(); 
  }
  
  public void setTroubleshooter (ApplicationTroubleshooter errorHelper) {
    this.errorHelper = errorHelper; 
  }

}
