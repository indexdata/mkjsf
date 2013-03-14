package com.indexdata.pz2utils4jsf.pazpar2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.indexdata.masterkey.pazpar2.client.exceptions.Pazpar2ErrorException;
import com.indexdata.pz2utils4jsf.pazpar2.data.CommandError;

public class CommandThread extends Thread {

  private static Logger logger = Logger.getLogger(CommandThread.class);
  Pazpar2Command command;
  SearchClient client;
  private ByteArrayOutputStream baos = new ByteArrayOutputStream();
  private StringBuilder response = new StringBuilder("");  
  
  public CommandThread (Pazpar2Command command, SearchClient client) {
    this.command = command;
    this.client = client;
  }
  
  /**
   * Runs the specified command using the specified Pazpar2 client
   * Sets the Pazpar2 response as an XML response string to be retrieved by
   * getResponse().
   * 
   * In case of an exception, an error response is generated, the document
   * element being the same as it would have been if successful (named after
   * the command, that is).  
   *  
   */
  public void run() {
    
    if (command.getName().equals("search")) {
      client.setSearchCommand(command);
    }
    try {
      long start = System.currentTimeMillis();
      CommandResponse commandResponse = client.executeCommand(command, baos);
      if (commandResponse.getStatusCode()==200) {
        response.append(commandResponse.getResponseString());  
      } else if (commandResponse.getStatusCode()==417) {
        logger.error("Pazpar2 status code 417: " + baos.toString("UTF-8"));
        response.append(CommandError.insertPazpar2ErrorXml(command.getName(), "Expectation failed (417)", baos.toString("UTF-8")));        
      } else {
        String resp = baos.toString("UTF-8");
        logger.error("Pazpar2 status code was " + commandResponse.getStatusCode() + ": " + resp);
        throw new Pazpar2ErrorException(resp,commandResponse.getStatusCode(),resp,null);
      }       
      long end = System.currentTimeMillis();      
      logger.debug("Executed " + command.getName() + " in " + (end-start) + " ms." );
    } catch (IOException e) {
      response.append(CommandError.createErrorXml(command.getName(), "io", e.getMessage())); 
      logger.error(response.toString());
    } catch (Pazpar2ErrorException e) {
      response.append(CommandError.createErrorXml(command.getName(), "pazpar2error", e.getMessage())); 
      logger.error(response.toString());
    } catch (Exception e) {
      response.append(CommandError.createErrorXml(command.getName(), "general", e.getMessage())); 
      logger.error(response.toString());      
    }
  }
  
  /**
   * 
   * @return Pazpar2 response as an XML string, possibly a generated error XML
   */
  public String getResponse () {
    return response.toString();
  }
    
  public Pazpar2Command getCommand() {
    return command;
  }

}
