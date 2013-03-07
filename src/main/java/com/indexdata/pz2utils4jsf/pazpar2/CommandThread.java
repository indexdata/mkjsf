package com.indexdata.pz2utils4jsf.pazpar2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.indexdata.pz2utils4jsf.pazpar2.CommandThread;
import com.indexdata.pz2utils4jsf.pazpar2.Pazpar2Command;
import com.indexdata.utils.XmlUtils;
import com.indexdata.masterkey.pazpar2.client.ClientCommand;
import com.indexdata.masterkey.pazpar2.client.Pazpar2Client;
import com.indexdata.masterkey.pazpar2.client.exceptions.Pazpar2ErrorException;

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
      logger.error("Message: " + e.getMessage());      
      response = new StringBuilder("<"+command.getName()+"><error exception=\"io\">"+XmlUtils.escape(e.getMessage())+"</error></"+command.getName()+">");
      logger.error(response.toString());
    } catch (Pazpar2ErrorException e) {
      logger.error(e.getMessage());
      response = new StringBuilder("<"+command.getName()+"><error exception=\"pazpar2error\">"+XmlUtils.escape(e.getMessage())+"</error></"+command.getName()+">");      
      logger.error(response.toString());
    }
  }
  
  public String getResponse () {
    return response.toString();
  }
  
  public Pazpar2Command getCommand() {
    return command;
  }

}
