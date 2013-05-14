package com.indexdata.mkjsf.pazpar2;

import org.apache.log4j.Logger;

import com.indexdata.mkjsf.pazpar2.commands.Pazpar2Command;

public class CommandThread extends Thread {

  private static Logger logger = Logger.getLogger(CommandThread.class);
  Pazpar2Command command;
  SearchClient client;
  HttpResponseWrapper commandResponse = null;      
  
  public CommandThread (Pazpar2Command command, SearchClient client) {
    this.command = command;
    this.client = client;
  }
  
  /**
   * Executes the specified command using the specified Pazpar2 client
   */
  public void run() {    
    logger.debug(command.getCommandName() + " executing asynchronously");
    commandResponse = client.executeCommand(command);
  }
  
  /**
   * 
   * @return Pazpar2 response as an XML string, possibly a generated error XML
   */
  public HttpResponseWrapper getCommandResponse () {
    return commandResponse;
  }
    
  public Pazpar2Command getCommand() {
    return command;
  }

}
