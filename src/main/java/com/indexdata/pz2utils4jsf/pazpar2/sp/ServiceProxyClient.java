package com.indexdata.pz2utils4jsf.pazpar2.sp;

import static com.indexdata.pz2utils4jsf.utils.Utils.nl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.indexdata.masterkey.config.MissingMandatoryParameterException;
import com.indexdata.masterkey.pazpar2.client.exceptions.Pazpar2ErrorException;
import com.indexdata.pz2utils4jsf.config.Configuration;
import com.indexdata.pz2utils4jsf.config.ConfigurationReader;
import com.indexdata.pz2utils4jsf.errors.ConfigurationException;
import com.indexdata.pz2utils4jsf.pazpar2.CommandParameter;
import com.indexdata.pz2utils4jsf.pazpar2.CommandResponse;
import com.indexdata.pz2utils4jsf.pazpar2.Pazpar2Command;
import com.indexdata.pz2utils4jsf.pazpar2.SearchClient;
import com.indexdata.pz2utils4jsf.pazpar2.sp.auth.AuthenticationEntity;
import com.indexdata.pz2utils4jsf.pazpar2.sp.auth.ServiceProxyUser;
import com.indexdata.pz2utils4jsf.utils.Utils;

@Named @SessionScoped 
public class ServiceProxyClient implements SearchClient {
    
  private static final long serialVersionUID = -4031644009579840277L;
  private static Logger logger = Logger.getLogger(ServiceProxyClient.class);
  public static final String MODULENAME = "proxyclient";
  public static final String SERVICE_PROXY_URL = "SERVICE_PROXY_URL";
  public static final String SP_INIT_DOC_PATHS = "SP_INIT_DOC_PATHS";
  private String serviceUrl = "undefined";
  private String[] initDocPaths = null;
  
  ProxyPz2ResponseHandler handler = new ProxyPz2ResponseHandler();
  private HttpClient client;
  private ServiceProxyUser user;

  public ServiceProxyClient () {
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
      serviceUrl = config.getMandatory(SERVICE_PROXY_URL);  
      this.initDocPaths = getMultiProperty(config.get(SP_INIT_DOC_PATHS));            
    } catch (ConfigurationException c) {
      c.printStackTrace();
    } catch (MissingMandatoryParameterException mmp) {
      mmp.printStackTrace();
    }    
  }
  
  private String[] getMultiProperty(String prop) {    
    if (prop != null) {
      return prop.split(",");
    } else {
      return null;
    }
  }
  
  public boolean authenticate (AuthenticationEntity user) {
    try {      
      logger.info("Authenticating [" + user.getProperty("name") + "]");
      this.user = (ServiceProxyUser) user;
      Pazpar2Command auth = new Pazpar2Command("auth");
      auth.setParameter(new CommandParameter("action","=","login"));
      auth.setParameter(new CommandParameter("username","=",user.getProperty("name")));
      auth.setParameter(new CommandParameter("password","=",user.getProperty("password")));
      byte[] response = send(auth);
      String responseStr = new String(response,"UTF-8");
      logger.info(responseStr);      
      if (responseStr.contains("FAIL")) {
        return false;
      } else {
        return true;
      }      
    } catch (ClientProtocolException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return false;
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return false;
    }        
  }
  
  public boolean checkAuthentication () {
    try {
      Pazpar2Command check = new Pazpar2Command("auth");
      check.setParameter(new CommandParameter("action","=","check"));
      byte[] response = send(check);
      logger.info(new String(response,"UTF-8"));
    } catch (ClientProtocolException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return false;
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return false;
    }    
    return true;
    
  }
  
  public boolean isAuthenticatingClient () {
    return true;
  }
  
  public boolean isAuthenticated () {
    if (user.getProperty("name") != null && user.getProperty("password") != null) {
      return checkAuthentication();
    } else {
      return false;
    }
  }
  
  /**
   * Makes the request
   * @param request
   * @return HTTP response as a String
   * @throws ClientProtocolException
   * @throws IOException
   */
  private byte[] send(Pazpar2Command command) throws ClientProtocolException, IOException {
    String url = serviceUrl + "?" + command.getEncodedQueryString(); 
    logger.info("Sending request "+url);    
    HttpGet httpget = new HttpGet(url);     
    byte[] response = client.execute(httpget, handler);    
    return response;
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
    byte[] response = send(command);
    baos.write(response);
    return new ServiceProxyClientCommandResponse(getStatusCode(), new String(response,"UTF-8"));    
  }

  public ServiceProxyClient cloneMe() {
    logger.debug("Cloning Pz2Client");
    ServiceProxyClient clone = new ServiceProxyClient();
    clone.client = this.client;
    clone.serviceUrl = this.serviceUrl;
    clone.initDocPaths = this.initDocPaths;
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
  
  public byte[] postInitDoc (String filePath) throws IOException {
    logger.info("Looking to post the file in : [" + filePath +"]");
    HttpPost post = new HttpPost(serviceUrl+"?command=init&includeDebug=yes");
    File initDoc = new File(filePath);
    post.setEntity(new FileEntity(initDoc));
    byte[] response = client.execute(post, handler);
    logger.info("Response on POST was: " + new String(response,"UTF-8"));
    return response;
  }
  
  public String[] getInitDocPaths () {
    logger.info("Get init doc paths ");
    logger.info("length: " + initDocPaths.length);
    return initDocPaths;
  }
  
  public void setServiceProxyUrl (String url) {
    serviceUrl = url;
  }
  
  public String getServiceProxyUrl () {
    return serviceUrl;
  }

}
