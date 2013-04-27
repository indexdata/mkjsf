package com.indexdata.mkjsf.pazpar2.sp;

import static com.indexdata.mkjsf.utils.Utils.nl;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

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
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.indexdata.masterkey.pazpar2.client.exceptions.Pazpar2ErrorException;
import com.indexdata.mkjsf.config.Configuration;
import com.indexdata.mkjsf.config.ConfigurationReader;
import com.indexdata.mkjsf.errors.ConfigurationException;
import com.indexdata.mkjsf.pazpar2.CommandResponse;
import com.indexdata.mkjsf.pazpar2.SearchClient;
import com.indexdata.mkjsf.pazpar2.commands.CommandParameter;
import com.indexdata.mkjsf.pazpar2.commands.Pazpar2Command;
import com.indexdata.mkjsf.pazpar2.commands.sp.AuthCommand;
import com.indexdata.mkjsf.pazpar2.sp.auth.ServiceProxyUser;
import com.indexdata.mkjsf.utils.Utils;

public class ServiceProxyClient implements SearchClient {
    
  private static final long serialVersionUID = -4031644009579840277L;
  private static Logger logger = Logger.getLogger(ServiceProxyClient.class);
  public static final String MODULENAME = "proxyclient";
  public static final String SERVICE_PROXY_URL = "SERVICE_PROXY_URL";
  public static final String SP_INIT_DOC_PATHS = "SP_INIT_DOC_PATHS";
  private String selectedServiceUrl = "";
  private List<String> serviceUrls = new ArrayList<String>();
  private List<String> initDocPaths = null;
  private Configuration config = null;
  
  ProxyPz2ResponseHandler handler = new ProxyPz2ResponseHandler();
  private transient HttpClient client;  
  private Pazpar2Command checkAuth = null;
  private Pazpar2Command ipAuth = null;

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
      config = configReader.getConfiguration(this);      
      serviceUrls = getMultiProperty(config.get(SERVICE_PROXY_URL));
      if (serviceUrls.size()==1) {
        selectedServiceUrl = serviceUrls.get(0);
      }
      this.initDocPaths = getMultiProperty(config.get(SP_INIT_DOC_PATHS));
      checkAuth = new AuthCommand(null);
      checkAuth.setParameterInState(new CommandParameter("action","=","check"));
      ipAuth = new AuthCommand(null);
      ipAuth.setParameterInState(new CommandParameter("action","=","ipauth"));
    } catch (ConfigurationException c) {
      c.printStackTrace();
    }    
  }
  
  private List<String> getMultiProperty(String prop) {
    List<String> props = new ArrayList<String>();
    if (prop != null) {      
      StringTokenizer tokenizer = new StringTokenizer(prop,",");
      while (tokenizer.hasMoreElements()) {
        props.add(tokenizer.nextToken());
      }     
    }
    return props;
  }
  
  public boolean authenticate (ServiceProxyUser user) {
    try {      
      logger.info("Authenticating [" + user.getProperty("name") + "]");            
      Pazpar2Command auth = new AuthCommand(null);
      auth.setParametersInState(new CommandParameter("action","=","login"), 
                                new CommandParameter("username","=",user.getProperty("name")), 
                                new CommandParameter("password","=",user.getProperty("password")));                                
      byte[] response = send(auth);
      String responseStr = new String(response,"UTF-8");
      logger.info(responseStr);      
      if (responseStr.contains("FAIL")) {
        user.isAuthenticated(false);
        return false;
      } else {
        user.isAuthenticated(true);
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
  
  public boolean checkAuthentication (ServiceProxyUser user) {    
    try {
      byte[] response = send(checkAuth);
      logger.info(new String(response,"UTF-8"));
      String responseStr = new String(response,"UTF-8");    
      if (responseStr.contains("FAIL")) {  
        user.isAuthenticated(false);
        return false;
      } else {        
        user.isAuthenticated(true);
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
  
  public boolean ipAuthenticate (ServiceProxyUser user) {
    try {
      byte[] response = send(ipAuth);
      logger.info(new String(response,"UTF-8"));
      String responseStr = new String(response,"UTF-8");    
      if (responseStr.contains("FAIL")) {
        user.isAuthenticated(false);
        return false;
      } else {
        user.isAuthenticated(true);
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
  
  public boolean isAuthenticatingClient () {
    return true;
  }
  
  public boolean isAuthenticated (ServiceProxyUser user) {
    if (user.getProperty("name") != null && user.getProperty("password") != null) {
      return checkAuthentication(user);
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
    String url = selectedServiceUrl + "?" + command.getEncodedQueryString(); 
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
    clone.serviceUrls = this.serviceUrls;
    clone.selectedServiceUrl = this.selectedServiceUrl;
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
    doc.add(nl+ MODULENAME + " was configured to access the Pazpar2 service proxy at: " + (selectedServiceUrl.length()>0 ? selectedServiceUrl : "[not defined yet]"));
    return null;
  }
  
  public byte[] postInitDoc (String filePath) throws IOException {
    logger.info("Looking to post the file in : [" + filePath +"]");
    HttpPost post = new HttpPost(selectedServiceUrl+"?command=init&includeDebug=yes");
    File initDoc = new File(filePath);
    logger.info("Posting to SP: ");
    if (logger.isDebugEnabled()) {
      BufferedReader reader = new BufferedReader(new FileReader(initDoc));
      String line;
      while ( (line = reader.readLine()) != null) {
        System.out.println(line);
      }
      reader.close();
    }
    post.setEntity(new FileEntity(initDoc));
    byte[] response = client.execute(post, handler);
    logger.debug("Response on POST was: " + new String(response,"UTF-8"));    
    return response;
  }
  
  public List<String> getInitDocPaths () {
    logger.debug("Get init doc paths ");
    logger.debug("length: " + initDocPaths.size());
    return initDocPaths;
  }
  
  public byte[] postInitDoc(byte[] initDoc) throws IOException {
    HttpPost post = new HttpPost(selectedServiceUrl+"?command=init&includeDebug=yes");
    post.setEntity(new ByteArrayEntity(initDoc));
    byte[] response = client.execute(post, handler);
    logger.debug("Response on POST was: " + new String(response,"UTF-8"));    
    return response;
  }
  
  public void setServiceProxyUrl (String url) {
    selectedServiceUrl = url;
  }
  
  public String getServiceProxyUrl () {
    return selectedServiceUrl;
  }
  
  public List<String> getServiceProxyUrls () {
    return serviceUrls;
  }
    
  public Configuration getConfiguration () {
    return config;
  }
  
}
