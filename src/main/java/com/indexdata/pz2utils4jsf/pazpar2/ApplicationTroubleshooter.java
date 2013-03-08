package com.indexdata.pz2utils4jsf.pazpar2;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.indexdata.pz2utils4jsf.config.Pz2Configurator;
import com.indexdata.pz2utils4jsf.utils.Utils;
import static com.indexdata.pz2utils4jsf.utils.Utils.nl;

public class ApplicationTroubleshooter {

  private static Pattern httpResponsePattern = Pattern.compile("Unexpected HTTP response code \\(([0-9]*)\\).*");
  private static Logger logger = Logger.getLogger(ApplicationTroubleshooter.class);
  
  private Pz2Configurator configurator = null;
  
  public ApplicationTroubleshooter(Pz2Configurator configurator) {
    this.configurator = configurator;
  }
    
  public ArrayList<String> getSuggestions(String commandName, String errorMessage) {
    ArrayList<String> suggestions = new ArrayList<String>();
    if (errorMessage.startsWith("Unexpected HTTP response")) {
      Matcher m = httpResponsePattern.matcher(errorMessage);
      if (m.matches()) {
        String errorCode = m.group(1);
        if (errorCode.equals("404")) {
          suggestions.add("Pazpar2 service not found (response code 404). ");
          suggestions.add("Please check the PAZPAR2_URL configuration and verify " +
          		"that a pazpar2 service is running at the given address."); 
          suggestions.add("The application was configured using " + Utils.baseObjectName(configurator));
          suggestions.add("The configurator reports following configuration was used: ");
          suggestions.addAll(configurator.document());          
        } else {
          suggestions.add("Response code was " + errorCode + ". " + nl +
              "Please check the PAZPAR2_URL configuration and verify " + 
              "that a pazpar2 service is running at the given address." + nl);           
        }        
      } else {
        logger.warn("Found message but no pattern match");        
      }      
    }
    if (errorMessage == null || errorMessage.length()==0) {
      logger.debug("No error message found, no suggestions made.");
    } else {
      logger.info("No suggestions yet for message " + errorMessage);
    }
    return suggestions;
  }
}
