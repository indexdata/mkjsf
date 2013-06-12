package com.indexdata.mkjsf.pazpar2;

import static com.indexdata.mkjsf.utils.Utils.nl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
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

import com.indexdata.mkjsf.config.Configuration;
import com.indexdata.mkjsf.config.ConfigurationReader;
import com.indexdata.mkjsf.errors.ConfigurationException;
import com.indexdata.mkjsf.errors.MissingConfigurationContextException;
import com.indexdata.mkjsf.pazpar2.commands.CommandParameter;
import com.indexdata.mkjsf.pazpar2.commands.Pazpar2Command;
import com.indexdata.mkjsf.pazpar2.commands.sp.AuthCommand;
import com.indexdata.mkjsf.pazpar2.commands.sp.ServiceProxyCommand;
import com.indexdata.mkjsf.pazpar2.data.CommandError;
import com.indexdata.mkjsf.utils.Utils;

/**
 * Search client handling Service Proxy requests. 
 *   
 * @author Niels Erik
 *
 */
public class ServiceProxyClient implements SearchClient {
    
  private static final long serialVersionUID = -4031644009579840277L;
  private static Logger logger = Logger.getLogger(ServiceProxyClient.class);
  public static final String MODULENAME = "proxyclient";
  
  public static final String SP_INIT_DOC_PATHS = "SP_INIT_DOC_PATHS";
  private String serviceUrl = "";
  
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
  public void configure (ConfigurationReader configReader) throws MissingConfigurationContextException {
    logger.info(Utils.objectId(this) + " is configuring using the provided " + Utils.objectId(configReader));
    try {
      config = configReader.getConfiguration(this);      
      serviceUrl = config.get("SERVICE_PROXY_URL");
      this.initDocPaths = config.getMultiProperty(SP_INIT_DOC_PATHS,",");
      checkAuth = new AuthCommand();
      checkAuth.setParameterInState(new CommandParameter("action","=","check"));
      ipAuth = new AuthCommand();
      ipAuth.setParameterInState(new CommandParameter("action","=","ipauth"));
    } catch (MissingConfigurationContextException mcce) {
      throw mcce;
    } catch (ConfigurationException ce) {
      logger.error("Failed to configure Service Proxy client");
      ce.printStackTrace();
    }
  }
    
  public boolean isAuthenticatingClient () {
    return true;
  }
    
  /**
   * Makes the request
   * @param request
   * @return HTTP response as a String
   * @throws ClientProtocolException
   * @throws IOException
   */
  public ClientCommandResponse send(Pazpar2Command command) {
    ClientCommandResponse commandResponse = null;
    String url = serviceUrl + "?" + command.getEncodedQueryString(); 
    logger.info("Sending request "+url);    
    HttpGet httpget = new HttpGet(url);     
    byte[] response = null;
    try {
      response = client.execute(httpget, handler);
      if (handler.getStatusCode()==200 && (handler.getContentType().contains("xml") || handler.getContentType().contains("octet-stream"))) {
        logger.trace("Creating command response holding content of type " + handler.getContentType());
        commandResponse = new ClientCommandResponse(handler.getStatusCode(),response,handler.getContentType());
      } else {
        logger.error("Service Proxy status code: " + handler.getStatusCode());
        String errorXml = "";
        if (handler.getContentType().contains("xml")) {
          errorXml = CommandError.insertErrorXml(command.getCommandName(), String.valueOf(handler.getStatusCode()), "Service Proxy error: "+handler.getStatusCode(), new String(response,"UTF-8"));        
        } else {
          if (handler.getContentType().contains("html")) {
            String htmlStrippedOfTags = (new String(response,"UTF-8")).replaceAll("\\<[^>]*>","");
            if (htmlStrippedOfTags.toLowerCase().contains("domain")) {
              errorXml = CommandError.createErrorXml(command.getCommandName(), String.valueOf(handler.getStatusCode()), "Unexpected response type from Service Proxy", "Expected XML from SP but got HTML. It contains the word domain suggesting that the service address was not found.", htmlStrippedOfTags);              
            } else {
              errorXml = CommandError.createErrorXml(command.getCommandName(), String.valueOf(handler.getStatusCode()), "Unexpected response type from Service Proxy", "Expected XML from SP but got HTML", htmlStrippedOfTags);              
            }
          } else {
            errorXml = CommandError.createErrorXml(command.getCommandName(), String.valueOf(handler.getStatusCode()), "Unexpected response type from Service Proxy: "+handler.getContentType(), "Could not process non-XML response from Service Proxy", new String(response,"UTF-8"));
          }          
        }
        commandResponse = new ClientCommandResponse(handler.getStatusCode(),errorXml,handler.getContentType());
      }       
    } catch (Exception e) {
      e.printStackTrace();
      commandResponse = new ClientCommandResponse(handler.getStatusCode(),CommandError.createErrorXml(command.getCommandName(), String.valueOf(handler.getStatusCode()), e.getClass().getSimpleName(), (e.getMessage()!= null ? e.getMessage() : "") + (e.getCause()!=null ? e.getCause().getMessage() : ""), e.getStackTrace().toString()),handler.getContentType());
    }
    return commandResponse; 
  }
  
  public class ProxyPz2ResponseHandler implements ResponseHandler<byte[]> {
    private StatusLine statusLine = null;
    private Header contentType = null;
    public byte[] handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
      byte[] resp = null;
      HttpEntity entity = response.getEntity();      
      statusLine = response.getStatusLine();
      if (entity != null) {        
        resp = EntityUtils.toByteArray(entity);        
        contentType = response.getEntity().getContentType();        
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
    public String getContentType () {
      return (contentType != null ? contentType.getValue() : "Content-Type not known"); 
    }
  }

  public int getStatusCode () {
    return handler.getStatusCode();
  }
  
  public String getReasonPhrase() {
    return handler.getReasonPhrase();
  }

  /**
   * Does nothing in Service Proxy context
   */
  @Override
  public void setSearchCommand(Pazpar2Command command) {
    // Do nothing, Service Proxy is handling this    
  }

  @Override
  public HttpResponseWrapper executeCommand(Pazpar2Command command) {
    return send(command);
  }

  public ServiceProxyClient cloneMe() {
    logger.debug("Cloning Pz2Client");
    ServiceProxyClient clone = new ServiceProxyClient();
    clone.client = this.client;
    clone.serviceUrl = this.serviceUrl;
    clone.initDocPaths = this.initDocPaths;
    return clone;
  }

  /**
   * Returns default configuration parameters for the client.
   */
  @Override
  public Map<String, String> getDefaults() {    
    return new HashMap<String,String>();
  }

  /**
   * Returns the configuration name of the client
   */
  @Override
  public String getModuleName() {
    return MODULENAME;
  }
  
  @Override
  public List<String> documentConfiguration () {
    List<String> doc = new ArrayList<String>();
    doc.add(nl+ MODULENAME + " was configured to access the Pazpar2 service proxy at: " + (serviceUrl.length()>0 ? serviceUrl : "[not defined yet]"));
    return null;
  }
  
  public ClientCommandResponse postInitDoc (String filePath) throws IOException {
    logger.info("Looking to post the file in : [" + filePath +"]");
    HttpPost post = new HttpPost(serviceUrl+"?command=init&includeDebug=yes");
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
    return new ClientCommandResponse(handler.getStatusCode(),response,handler.getContentType());    
  }
  
  public List<String> getInitDocPaths () {
    logger.debug("Get init doc paths ");
    logger.debug("length: " + initDocPaths.size());
    return initDocPaths;
  }
  
  public HttpResponseWrapper postInitDoc(byte[] initDoc, Pazpar2Command command) {
    String requestParameters = command.getEncodedQueryString();
    logger.info("Initiating session with init doc and [" + requestParameters +"]");
    HttpPost post = new HttpPost(serviceUrl+"?" + requestParameters);
    post.setEntity(new ByteArrayEntity(initDoc));
    ClientCommandResponse commandResponse = null;
    byte[] response;
    try {
      response = client.execute(post, handler);
      if (handler.getStatusCode()==200) {
        commandResponse = new ClientCommandResponse(handler.getStatusCode(),response,handler.getContentType());
      } else {
        logger.error("Service Proxy status code: " + handler.getStatusCode());
        commandResponse = new ClientCommandResponse(handler.getStatusCode(),CommandError.insertErrorXml("init", String.valueOf(handler.getStatusCode()), "Service Proxy error: "+handler.getStatusCode(), new String(response,"UTF-8")),"text/xml");                               
      }
    } catch (ClientProtocolException e) {
      logger.error(e.getMessage());
      e.printStackTrace();
      commandResponse = new ClientCommandResponse(-1,CommandError.createErrorXml("init", String.valueOf(handler.getStatusCode()), "Client protocol exception", e.getMessage(), e.getStackTrace().toString()),"text/xml");      
    } catch (IOException e) {
      logger.error(e.getMessage());
      e.printStackTrace();
      commandResponse = new ClientCommandResponse(-1,CommandError.createErrorXml("init", String.valueOf(handler.getStatusCode()), "IO exception", e.getMessage(),e.getStackTrace().toString()),"text/xml");      
    }
    return commandResponse;    
  }
  
  /**
   * Sets the URL of the Service Proxy that should service requests. 
   */
  public void setServiceUrl (String url) {    
    serviceUrl = url;
  }
          
  public Configuration getConfiguration () {
    return config;
  }

  @Override
  public String getServiceUrl() {    
    return serviceUrl;
  }

  /**
   * Returns true if a Service Proxy URL was defined yet.
   */
  @Override
  public boolean hasServiceUrl() {
    return serviceUrl != null && serviceUrl.length()>0;
  }
  
}
