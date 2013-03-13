package com.indexdata.pz2utils4jsf.errors;

import static com.indexdata.pz2utils4jsf.utils.Utils.nl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.indexdata.pz2utils4jsf.config.Pz2Configurator;
import com.indexdata.pz2utils4jsf.pazpar2.data.Pazpar2Error;
import com.indexdata.pz2utils4jsf.utils.Utils;

public class ErrorHelper implements Serializable {

  public enum ErrorCode {PAZPAR2_404, 
                         PAZPAR2_UNEXPECTED_RESPONSE,
                         PAZPAR2_12,
                         PAZPAR2_ERRORS,
                         LOCAL_SERVICE_DEF_FILE_NOT_FOUND,
                         REMOTE_SERVICE_DEF_NOT_FOUND,
                         LOCAL_SETTINGS_FILE_NOT_FOUND,
                         MASTERKEY_CONFIG_FILE_NOT_FOUND,
                         MISSING_MANDATORY_PARAMETER,
                         MISSING_MK2_CONFIG_INIT_PARAMETER,
                         NOT_RESOLVED,
                         SKIP_SUGGESTIONS};

  private static final long serialVersionUID = 2860804561068279131L;
  private static Pattern httpResponsePattern = Pattern.compile("Unexpected HTTP response code \\(([0-9]*)\\).*");
  
  private static Logger logger = Logger.getLogger(ErrorHelper.class);
  
  private Pz2Configurator configurator = null;
  
  public ErrorHelper(Pz2Configurator configurator) {
    this.configurator = configurator;
  }
  
  public ErrorHelper.ErrorCode getErrorCode(ErrorInterface appError) {
    if (appError.hasPazpar2Error()) {
      Pazpar2Error pz2err = appError.getPazpar2Error();
      String pz2errcode = pz2err.getCode();
      switch (pz2errcode) {
      case "12": 
        return ErrorCode.PAZPAR2_12;
      case "0":    
        if (pz2err.getMsg().contains("target settings from file")) {
          return ErrorCode.LOCAL_SETTINGS_FILE_NOT_FOUND;
        } else {
          return ErrorCode.PAZPAR2_ERRORS;
        }
      default: 
        return ErrorCode.PAZPAR2_ERRORS;
      }
    } else if (appError.getMessage().startsWith("Unexpected HTTP response")) {
      Matcher m = httpResponsePattern.matcher(appError.getMessage());
      if (m.matches()) {
        String errorCode = m.group(1);
        if (errorCode.equals("404")) {
          return ErrorCode.PAZPAR2_404;
        } else {
          return ErrorCode.PAZPAR2_UNEXPECTED_RESPONSE;
        }
      }       
    } else if (appError.getMessage().contains("Configuration file") & appError.getMessage().contains("properties")) {
      return ErrorCode.MASTERKEY_CONFIG_FILE_NOT_FOUND; 
    } else if (appError.getMessage().contains("Error reading service definition XML")) {
      return ErrorCode.LOCAL_SERVICE_DEF_FILE_NOT_FOUND;    
    } else if (appError.getMessage().contains("Cannot query Pazpar2 while there are configuration errors")) {
      return ErrorCode.SKIP_SUGGESTIONS;
    } else if (appError.getMessage().contains("Missing mandatory parameter")) {
      return ErrorCode.MISSING_MANDATORY_PARAMETER;
    } else if (appError.getMessage().contains("Init parameter")
               && appError.getMessage().contains("MASTERKEY")
               && appError.getMessage().contains("missing in deployment descriptor")) {
      return ErrorCode.MISSING_MK2_CONFIG_INIT_PARAMETER;
    }
    return ErrorCode.NOT_RESOLVED;
  }
    
  public ArrayList<String> getSuggestions(ErrorInterface error) {
    ArrayList<String> suggestions = new ArrayList<String>();
    ErrorCode code = getErrorCode(error);
    switch (code) {
    case PAZPAR2_404:
      suggestions.add("Pazpar2 service not found (404). ");
      suggestions.add("Please check the PAZPAR2_URL configuration and verify "
          + "that a pazpar2 service is running at the given address.");
      addConfigurationDocumentation(suggestions);      
      break;
    case PAZPAR2_UNEXPECTED_RESPONSE:
      suggestions.add("Unexpected response code from Pazpar2. " + nl
          + "Please check the PAZPAR2_URL configuration and verify "
          + "that a pazpar2 service is running at the given address." + nl);
      break;     
    case MASTERKEY_CONFIG_FILE_NOT_FOUND: 
      suggestions.add("The main configuration file that is looked up using parameters" +
      		" in web.xml (MASTERKEY_ROOT_CONFIG_DIR,MASTERKEY_COMPONENT_CONFIG_DIR,MASTERKEY_CONFIG_FILE_NAME)" +
      		" could not be found. Please check the web.xml parameters and the expected file system location. ");      
      break;
    case LOCAL_SERVICE_DEF_FILE_NOT_FOUND:
      suggestions.add("The service definition file could not be loaded.");
      suggestions.add("Please check the configuration and verify that the file exists");
      addConfigurationDocumentation(suggestions);     
      break;
    case REMOTE_SERVICE_DEF_NOT_FOUND:
      break;
    case LOCAL_SETTINGS_FILE_NOT_FOUND:
      suggestions.add("A configuration using local target settings file was found, but " +
      		" the file itself could not be found. Please check the configuration.");
      addConfigurationDocumentation(suggestions);
      break;
    case MISSING_MANDATORY_PARAMETER:
      suggestions.add("A mandatory configuration parameter was not found in the MK2 config properties" +
      		" file used. Please check the property file for the parameter given in the error message ");
      addConfigurationDocumentation(suggestions);
      break;
    case MISSING_MK2_CONFIG_INIT_PARAMETER:
      suggestions.add("A mandatory init parameter was not found in the deployment descriptor (web.xml)." +
      		" Following init parameters must be present in web.xml when using the Masterkey (MK2) configuration scheme:" +
      		" MASTERKEY_ROOT_CONFIG_DIR (i.e. '/etc/masterkey'), MASTERKEY_COMPONENT_CONFIG_DIR (i.e. '/myapp'), " +
      		"MASTERKEY_CONFIG_FILE_NAME (i.e. 'myapp.properties'");      
      break;
    case NOT_RESOLVED:
      suggestions.add("Unforeseen error situation. No suggestions prepared.");
      break;
    case SKIP_SUGGESTIONS:
      break;
    case PAZPAR2_12: 
      suggestions.add("The Pazpar2 service does not have a service definition with the requested ID ");
      suggestions.add("Please check the service ID set in the configuration and compare it with the " +
      		" pazpar2 (server side) configuration.");
      addConfigurationDocumentation(suggestions);    
      break;
    case PAZPAR2_ERRORS:
      if (error.hasPazpar2Error()) {
        if (error.getPazpar2Error().getCode().equals("0")) {
          
        }
        suggestions.add("Encountered Pazpar2 error: " + error.getPazpar2Error().getMsg() + " ("+error.getPazpar2Error().getCode()+")");
      } else {
        logger.error("Programming problem. An application error was categorized as a Papzar2 error yet does not have Pazpar2 error information as expected.");
      }
      break;
    }
    return suggestions;
  }
  
  private void addConfigurationDocumentation (ArrayList<String> suggestions) {
    suggestions.add("The application was configured using the configurator " + Utils.baseObjectName(configurator));
    suggestions.add("This configurator reports that following configuration was used: ");
    suggestions.addAll(configurator.document());
  }
}
