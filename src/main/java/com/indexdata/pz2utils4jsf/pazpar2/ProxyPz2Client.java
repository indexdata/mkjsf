package com.indexdata.pz2utils4jsf.pazpar2;

import static com.indexdata.pz2utils4jsf.utils.Utils.nl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.indexdata.masterkey.config.MissingMandatoryParameterException;
import com.indexdata.masterkey.pazpar2.client.exceptions.Pazpar2ErrorException;
import com.indexdata.pz2utils4jsf.config.Configuration;
import com.indexdata.pz2utils4jsf.config.ConfigurationReader;
import com.indexdata.pz2utils4jsf.errors.ConfigurationException;
import com.indexdata.pz2utils4jsf.utils.Utils;

@Named @SessionScoped @Alternative
public class ProxyPz2Client implements SearchClient {

  private static final long serialVersionUID = -4031644009579840277L;
  private static Logger logger = Logger.getLogger(ProxyPz2Client.class);
  public static final String MODULENAME = "proxyclient";
  private String serviceUrl = "undefined";
  
  ProxyPz2ResponseHandler handler = new ProxyPz2ResponseHandler();
  private HttpClient client;

  public ProxyPz2Client () {
    SchemeRegistry schemeRegistry = new SchemeRegistry();
    schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
    ClientConnectionManager cm = new PoolingClientConnectionManager(schemeRegistry);
    client = new DefaultHttpClient(cm);
  }
    
  @Override
  public void configure (ConfigurationReader configReader) {
    logger.info(Utils.objectId(this) + " is configuring using the provided " + Utils.objectId(configReader));
    try {
      Configuration config = configReader.getConfiguration(this);      
      serviceUrl = config.getMandatory("SERVICE_PROXY_URL");
      authenticate();
    } catch (ConfigurationException c) {
      // TODO Auto-generated catch block
      c.printStackTrace();
    } catch (MissingMandatoryParameterException mmp) {
      mmp.printStackTrace();
    }
  }
  
  public void authenticate () {
    try {
      Pazpar2Command auth = new Pazpar2Command("auth");
      auth.setParameter(new CommandParameter("action","=","login"));
      auth.setParameter(new CommandParameter("username","=","demo"));
      auth.setParameter(new CommandParameter("password","=","demo"));
      send(auth);
    } catch (ClientProtocolException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
  }
  
  /**
   * Makes the request
   * @param request
   * @return HTTP response as a String
   * @throws ClientProtocolException
   * @throws IOException
   */
  private String send(Pazpar2Command command) throws ClientProtocolException, IOException {
    String url = serviceUrl + "?" + command.getEncodedQueryString(); 
    logger.info("Sending request "+url);    
    HttpGet httpget = new HttpGet(url);     
    byte[] response = client.execute(httpget, handler);    
    return new String(response);
  }

  
  public class ProxyPz2ResponseHandler implements ResponseHandler<byte[]> {
    private StatusLine statusLine = null;
    public byte[] handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
      byte[] resp = null;
      HttpEntity entity = response.getEntity();      
      statusLine = response.getStatusLine();
      if (entity != null) {        
        resp = EntityUtils.toByteArray(entity);        
      } 
      EntityUtils.consume(entity);
      return resp;
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
    logger.debug("Cloning StraightPz2Client");
    ProxyPz2Client clone = new ProxyPz2Client();
    clone.client = this.client;
    clone.serviceUrl = this.serviceUrl;
    return clone;
  }

  @Override
  public Map<String, String> getDefaults() {    
    return new HashMap<String,String>();
  }

  @Override
  public String getModuleName() {
    return MODULENAME;
  }
  
  @Override
  public List<String> documentConfiguration () {
    List<String> doc = new ArrayList<String>();
    doc.add(nl+ MODULENAME + " was configured to access the Pazpar2 service proxy at: " + serviceUrl);
    return null;
  }

}
