package com.indexdata.pz2utils4jsf.pazpar2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Named;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.indexdata.masterkey.pazpar2.client.exceptions.Pazpar2ErrorException;
import com.indexdata.pz2utils4jsf.config.Pz2Configurator;
import com.indexdata.pz2utils4jsf.utils.Utils;

@Named @SessionScoped @Alternative
public class ProxyPz2Client implements SearchClient {

  private static final long serialVersionUID = -4031644009579840277L;
  private static Logger logger = Logger.getLogger(ProxyPz2Client.class);
  
  ProxyPz2ResponseHandler handler = new ProxyPz2ResponseHandler();
  HttpClient client = new DefaultHttpClient();  

  public ProxyPz2Client(HttpClient client) {
  }
  
  @Override
  public void configure (Pz2Configurator configurator) {
    logger.info(Utils.objectId(this) + " is configuring itself using the provided " + Utils.objectId(configurator));

  }
  
  /**
   * Makes the request
   * @param request
   * @return HTTP response as a String
   * @throws ClientProtocolException
   * @throws IOException
   */
  private String send(Pazpar2Command command) throws ClientProtocolException, IOException {
    String url = command.getEncodedQueryString(); 
    logger.debug("Sending request "+url);    
    HttpGet httpget = new HttpGet(url);     
    byte[] response = client.execute(httpget, handler);    
    return new String(response);
  }

  
  public class ProxyPz2ResponseHandler implements ResponseHandler<byte[]> {
    private StatusLine statusLine = null;
    public byte[] handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
      HttpEntity entity = response.getEntity();      
      statusLine = response.getStatusLine();
      if (entity != null) {        
        return EntityUtils.toByteArray(entity);
      } else {
        return null;
      }
    }
    public int getStatusCode() {
      return statusLine.getStatusCode();
    }    
    public String getReasonPhrase() {
      return statusLine.getReasonPhrase();
    }
  }

  public int getStatusCode () {
    return handler.getStatusCode();
  }
  
  public String getReasonPhrase() {
    return handler.getReasonPhrase();
  }

  @Override
  public void setSearchCommand(Pazpar2Command command) {
    // Do nothing, Service Proxy is handling this    
  }

  @Override
  public CommandResponse executeCommand(Pazpar2Command command,
      ByteArrayOutputStream baos) throws Pazpar2ErrorException, IOException {
    String response = send(command);
    return new ProxyPz2ClientCommandResponse(getStatusCode(), response);    
  }

  public ProxyPz2Client cloneMe() {
    return this;
  }

}
