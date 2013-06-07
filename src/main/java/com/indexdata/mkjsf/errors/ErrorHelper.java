package com.indexdata.mkjsf.errors;

import static com.indexdata.mkjsf.utils.Utils.nl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.indexdata.mkjsf.config.ConfigurationReader;
import com.indexdata.mkjsf.utils.Utils;

public class ErrorHelper implements Serializable {

  public enum ErrorCode {PAZPAR2_404, 
                         PAZPAR2_UNEXPECTED_RESPONSE,
                         PAZPAR2_ERRORS,
                         LOCAL_SERVICE_DEF_FILE_NOT_FOUND,
                         REMOTE_SERVICE_DEF_NOT_FOUND,
                         LOCAL_SETTINGS_FILE_NOT_FOUND,
                         MASTERKEY_CONFIG_FILE_NOT_FOUND,
                         MISSING_MANDATORY_PROPERTY,
                         MISSING_MK2_CONFIG_INIT_PARAMETER,
                         MISSING_CONTEXT_PARAMETER,
                         NOT_RESOLVED,
                         SKIP_SUGGESTIONS};

  private static final long serialVersionUID = 2860804561068279131L;
  private static Pattern httpResponsePattern = Pattern.compile("Unexpected HTTP response code \\(([0-9]*)\\).*");
  
  private static Logger logger = Logger.getLogger(ErrorHelper.class);
  
  private ConfigurationReader configurator = null;
  
  public ErrorHelper(ConfigurationReader configurator) {
    this.configurator = configurator;
  }
  
  public ErrorHelper.ErrorCode getErrorCode(ErrorInterface appError) {
    String errmsg = appError.getMessage();
    if (appError.isServiceError()) {
      if (appError.getServiceError().getMsg().contains("target settings from file")) {
        return ErrorCode.LOCAL_SETTINGS_FILE_NOT_FOUND;
      } else {
        return ErrorCode.PAZPAR2_ERRORS;
      }
    } else if (errmsg.startsWith("Unexpected HTTP response")) {
      Matcher m = httpResponsePattern.matcher(appError.getMessage());
      if (m.matches()) {
        String errorCode = m.group(1);
        if (errorCode.equals("404")) {
          return ErrorCode.PAZPAR2_404;
        } else {
          return ErrorCode.PAZPAR2_UNEXPECTED_RESPONSE;
        }
      }       
    } else if (errmsg.contains("Configuration file") & appError.getMessage().contains("properties")) {
      return ErrorCode.MASTERKEY_CONFIG_FILE_NOT_FOUND; 
    } else if (errmsg.contains("Error reading service definition XML")) {
      return ErrorCode.LOCAL_SERVICE_DEF_FILE_NOT_FOUND;    
    } else if (errmsg.contains("Cannot query Pazpar2 while there are configuration errors")) {
      return ErrorCode.SKIP_SUGGESTIONS;
    } else if (errmsg.contains("Missing mandatory parameter")) {
      return ErrorCode.MISSING_MANDATORY_PROPERTY;
    } else if (errmsg.contains("ConfigureByMk2Config") && errmsg.contains("Init parameter") && (errmsg.contains("missing"))) {                   
      return ErrorCode.MISSING_MK2_CONFIG_INIT_PARAMETER;
    } else if (appError.getMessage().contains("WebXmlConfigReader could not find mandatory context-param")) {
      return ErrorCode.MISSING_CONTEXT_PARAMETER;
    }
    return ErrorCode.NOT_RESOLVED;
  }
    
  public ArrayList<String> getSuggestions(ErrorInterface error) {
    ArrayList<String> suggestions = new ArrayList<String>();
    ErrorCode code = getErrorCode(error);
    switch (code) {
      case MISSING_MK2_CONFIG_INIT_PARAMETER:
        suggestions.add("A mandatory init parameter (context-param) was not found in the deployment descriptor (web.xml)." +
          " Following init parameters must be present when using the MasterKey configuration scheme (ConfigureByMk2Config):" +
          " MASTERKEY_ROOT_CONFIG_DIR (i.e. '/etc/masterkey'), MASTERKEY_COMPONENT_CONFIG_DIR (i.e. '/myapp'), " +
          " MASTERKEY_CONFIG_FILE_NAME (i.e. 'myapp.properties'");      
        break;
      case MISSING_CONTEXT_PARAMETER:
        suggestions.add("A mandatory init parameter (context-param) was not found in the deployment descriptor (web.xml)." +
        " Following init parameters must be present when using WebXmlConfigReader:" +
        " PAZPAR2_URL, PAZPAR2_SERVICE_ID");      
        break;
      case MISSING_MANDATORY_PROPERTY:
        suggestions.add("A mandatory configuration parameter was not found in the MK2 config properties" +
            " file used. Please check the property file for the parameter given in the error message ");
        addConfigurationDocumentation(suggestions);
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
      case PAZPAR2_ERRORS:
        if (error.isServiceError()) {          
          int pz2code = Integer.parseInt(error.getServiceError().getCode());
          switch (pz2code) {
            case 3:
              suggestions.add("The search experienced a problem with the query terms.");
              break;
            case 12:
              suggestions.add("The Pazpar2 server does not have a service defined by the requested ID ");
              suggestions.add("Please check the service ID set in the configuration and compare it with the " +
                  " configuration on the Pazpar2 server-side.");
              addConfigurationDocumentation(suggestions);    
              break;
            case 100:
              suggestions.add("Pazpar2 Service Proxy error");
              suggestions.add("A request was made to the Pazpar2 Service Proxy, but the Service Proxy reports ");
              suggestions.add(" that authentication is lacking. Could be no successful authentication request was made or");
              suggestions.add(" that the Service Proxy session timed out.");
              break;
            default:
              suggestions.add("Pazpar2 error: " + error.getServiceError().getMsg() + " (Pazpar2 # "+error.getServiceError().getCode()+")");
          }
          break;
        } else {
          logger.error("Programming problem. An application error was categorized as a Papzar2 error yet does not have Pazpar2 error information as expected.");
        }
        break;
      case SKIP_SUGGESTIONS:
        break;       
      case NOT_RESOLVED:
        suggestions.add("Sorry, no troubleshooting suggestions were written for this error scenario just yet.");
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
