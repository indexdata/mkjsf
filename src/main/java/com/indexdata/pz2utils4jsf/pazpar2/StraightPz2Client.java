package com.indexdata.pz2utils4jsf.pazpar2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Named;

import org.apache.log4j.Logger;

import com.indexdata.masterkey.pazpar2.client.ClientCommand;
import com.indexdata.masterkey.pazpar2.client.Pazpar2Client;
import com.indexdata.masterkey.pazpar2.client.Pazpar2ClientConfiguration;
import com.indexdata.masterkey.pazpar2.client.Pazpar2ClientGeneric;
import com.indexdata.masterkey.pazpar2.client.Pazpar2HttpResponse;
import com.indexdata.masterkey.pazpar2.client.exceptions.Pazpar2ErrorException;
import com.indexdata.masterkey.pazpar2.client.exceptions.ProxyErrorException;
import com.indexdata.pz2utils4jsf.config.Pz2Configurator;
import com.indexdata.pz2utils4jsf.errors.ConfigurationException;
import com.indexdata.pz2utils4jsf.utils.Utils;

@Named @SessionScoped @Alternative
public class StraightPz2Client implements SearchClient {

  private static final long serialVersionUID = 5414266730169982028L;
  private static Logger logger = Logger.getLogger(StraightPz2Client.class);
  private Pazpar2Client client = null;
  private Pazpar2ClientConfiguration cfg = null;  
  
  public StraightPz2Client() {}
  
  public void configure(Pz2Configurator configurator) throws ConfigurationException {
    logger.info(Utils.objectId(this) + " is configuring itself using the provided " + Utils.objectId(configurator));
    try {
      cfg = new Pazpar2ClientConfiguration(configurator.getConfig());
    } catch (ProxyErrorException pe) {
      logger.error("Could not configure Pazpar2 client: " + pe.getMessage());
      throw new ConfigurationException("Could not configure StraightPz2Client:  "+ pe.getMessage(),pe);
    } 
    if (cfg != null) {
      try {
        client = new Pazpar2ClientGeneric(cfg);  
      } catch (ProxyErrorException pe) {
        logger.error("Could not configure Pazpar2 client: " + pe.getMessage());
        throw new ConfigurationException("Could not configure StraightPz2Client:  "+ pe.getMessage(),pe);
      }
    } else {
      logger.error("There was a problem creating StraightPz2Client. Client is null after configuration.");
      throw new ConfigurationException("Pazpar2Client is null after configuration");
    } 
  }

  @Override
  public void setSearchCommand(Pazpar2Command command) {
    ClientCommand clientCommand = new ClientCommand(command.getName(), command.getEncodedQueryString());
    client.setSearchCommand(clientCommand);
    
  }

  @Override
  public CommandResponse executeCommand(Pazpar2Command command, ByteArrayOutputStream baos) 
       throws Pazpar2ErrorException, IOException {
    ClientCommand clientCommand = new ClientCommand(command.getName(), command.getEncodedQueryString());
    Pazpar2HttpResponse pz2HttpResponse = client.executeCommand(clientCommand, baos);
    return new StraightPz2CommandResponse(pz2HttpResponse,baos);
  }

  public StraightPz2Client cloneMe() {
    logger.debug("Cloning StraightPz2Client");
    StraightPz2Client clone = new StraightPz2Client();
    clone.client = this.client;
    clone.cfg = this.cfg;
    return clone;
  }
}
