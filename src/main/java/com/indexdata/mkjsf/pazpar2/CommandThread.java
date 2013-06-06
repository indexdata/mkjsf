package com.indexdata.mkjsf.pazpar2;

import org.apache.log4j.Logger;

import com.indexdata.mkjsf.pazpar2.commands.Pazpar2Command;
import com.indexdata.mkjsf.pazpar2.data.Responses;

/**
 * Helper class for running multiple concurrent Pazpar2 commands. Basically 
 * used for updating display data (show,stat,bytarget,termlist) together.
 * 
 * @author Niels Erik
 *
 */
public class CommandThread extends Thread {

  private static Logger logger = Logger.getLogger(CommandThread.class);
  Pazpar2Command command;
  SearchClient client;
  Responses pzresp;
  HttpResponseWrapper commandResponse = null;      
  
  public CommandThread (Pazpar2Command command, SearchClient client, Responses pzresp) {
    this.command = command;
    this.client = client;
    this.pzresp = pzresp;
  }
  
  /**
   * Executes the specified command using the specified Pazpar2 client
   */
  public void run() {    
    logger.debug(command.getCommandName() + " executing asynchronously");
    command.run(client,pzresp);
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
