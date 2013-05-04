package com.indexdata.mkjsf.pazpar2;

import org.apache.log4j.Logger;

import com.indexdata.mkjsf.pazpar2.commands.Pazpar2Command;

public class CommandThread extends Thread {

  private static Logger logger = Logger.getLogger(CommandThread.class);
  Pazpar2Command command;
  SearchClient client;
  CommandResponse commandResponse = null;      
  
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
    
    if (command.getCommandName().equals("search")) {
      client.setSearchCommand(command);
    }
    long start = System.currentTimeMillis();
    commandResponse = client.executeCommand(command);
    long end = System.currentTimeMillis();
    logger.debug("Executed " + command.getCommandName() + " in " + (end-start) + " ms." );
  }
  
  /**
   * 
   * @return Pazpar2 response as an XML string, possibly a generated error XML
   */
  public CommandResponse getCommandResponse () {
    return commandResponse;
  }
    
  public Pazpar2Command getCommand() {
    return command;
  }

}
