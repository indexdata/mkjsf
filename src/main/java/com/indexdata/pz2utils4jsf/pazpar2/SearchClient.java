package com.indexdata.pz2utils4jsf.pazpar2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

import com.indexdata.masterkey.pazpar2.client.exceptions.Pazpar2ErrorException;
import com.indexdata.pz2utils4jsf.config.Configurable;
import com.indexdata.pz2utils4jsf.config.Configuration;
import com.indexdata.pz2utils4jsf.pazpar2.commands.CommandReadOnly;

public interface SearchClient extends Configurable, Serializable {
  
  public void setSearchCommand(CommandReadOnly command);
  public CommandResponse executeCommand(CommandReadOnly command, ByteArrayOutputStream baos) throws Pazpar2ErrorException, IOException;
  public SearchClient cloneMe();
  public boolean isAuthenticatingClient();  
  public Configuration getConfiguration();
}
