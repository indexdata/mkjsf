package com.indexdata.pz2utils4jsf.errors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.indexdata.pz2utils4jsf.config.Pz2Configurator;
import com.indexdata.pz2utils4jsf.utils.Utils;
import static com.indexdata.pz2utils4jsf.utils.Utils.nl;

public class ErrorHelper implements Serializable {

  public enum ErrorCode {PAZPAR2_404, 
                         PAZPAR2_UNEXPECTED_RESPONSE,
                         LOCAL_SERVICE_DEF_FILE_NOT_FOUND,
                         REMOTE_SERVICE_DEF_NOT_FOUND,
                         LOCAL_SETTINGS_FILE_NOT_FOUND,
                         NOT_RESOLVED};

  private static final long serialVersionUID = 2860804561068279131L;
  private static Pattern httpResponsePattern = Pattern.compile("Unexpected HTTP response code \\(([0-9]*)\\).*");
  private static Pattern missingLocalServiceDefFile = Pattern.compile(".*Error reading service definition XML.*");
  private static Logger logger = Logger.getLogger(ErrorHelper.class);
  
  private Pz2Configurator configurator = null;
  
  public ErrorHelper(Pz2Configurator configurator) {
    this.configurator = configurator;
  }
  
  public ErrorHelper.ErrorCode getErrorCode(ApplicationError error) {    
    if (error.getMessage().startsWith("Unexpected HTTP response")) {
      Matcher m = httpResponsePattern.matcher(error.getMessage());
      if (m.matches()) {
        String errorCode = m.group(1);
        if (errorCode.equals("404")) {
          return ErrorCode.PAZPAR2_404;
        } else {
          return ErrorCode.PAZPAR2_UNEXPECTED_RESPONSE;
        }
      }       
    } else if (error.getMessage().contains("Error reading service definition XML")) {
      return ErrorCode.LOCAL_SERVICE_DEF_FILE_NOT_FOUND;
    }
    return ErrorCode.NOT_RESOLVED;
  }
    
  public ArrayList<String> getSuggestions(ApplicationError error) {
    ArrayList<String> suggestions = new ArrayList<String>();
    ErrorCode code = getErrorCode(error);
    switch (code) {
    case PAZPAR2_404:
      suggestions.add("Pazpar2 service not found (404). ");
      suggestions.add("Please check the PAZPAR2_URL configuration and verify "
          + "that a pazpar2 service is running at the given address.");
      suggestions.add("The application was configured using " + Utils.baseObjectName(configurator));
      suggestions.add("The configurator reports following configuration was used: ");
      suggestions.addAll(configurator.document());
      break;
    case PAZPAR2_UNEXPECTED_RESPONSE:
      suggestions.add("Unexpected response code from Pazpar2. " + nl
          + "Please check the PAZPAR2_URL configuration and verify "
          + "that a pazpar2 service is running at the given address." + nl);
      break;
    case LOCAL_SERVICE_DEF_FILE_NOT_FOUND:
      suggestions.add("The service definition file could not be loaded.");
      suggestions.add("Please check the configuration and verify that the file exists");
      suggestions.add("The configurator reports following configuration was used: ");
      suggestions.addAll(configurator.document());    
      break;
    case REMOTE_SERVICE_DEF_NOT_FOUND:
      break;
    case LOCAL_SETTINGS_FILE_NOT_FOUND:
      break;
    case NOT_RESOLVED:
      break;
    }
    return suggestions;
  }
}
