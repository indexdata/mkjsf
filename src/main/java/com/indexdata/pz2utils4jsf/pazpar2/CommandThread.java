package com.indexdata.pz2utils4jsf.pazpar2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.indexdata.masterkey.pazpar2.client.ClientCommand;
import com.indexdata.masterkey.pazpar2.client.Pazpar2Client;
import com.indexdata.masterkey.pazpar2.client.exceptions.Pazpar2ErrorException;
import com.indexdata.pz2utils4jsf.pazpar2.data.ApplicationError;

public class CommandThread extends Thread {

  private static Logger logger = Logger.getLogger(CommandThread.class);
  Pazpar2Command command;
  Pazpar2Client client;
  private ByteArrayOutputStream baos = new ByteArrayOutputStream();
  private StringBuilder response = new StringBuilder("");
  
  public CommandThread (Pazpar2Command command, Pazpar2Client client) {
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
    ClientCommand clientCommand = new ClientCommand(command.getName(), command.getEncodedQueryString());
    if (command.getName().equals("search")) {
      client.setSearchCommand(clientCommand);
    }
    try {
      long start = System.currentTimeMillis();
      client.executeCommand(clientCommand, baos);
      response.append(baos.toString("UTF-8"));
      long end = System.currentTimeMillis();      
      logger.debug("Executed " + command.getName() + " in " + (end-start) + " ms." );
    } catch (IOException e) {
      response.append(ApplicationError.createErrorXml(command.getName(), "io", e.getMessage())); 
      logger.error(response.toString());
    } catch (Pazpar2ErrorException e) {
      response.append(ApplicationError.createErrorXml(command.getName(), "pazpar2error", e.getMessage())); 
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
