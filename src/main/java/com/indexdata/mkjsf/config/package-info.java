/**
 * The library comes with two alternative mechanisms for configuration of an application. It is
 * possible, however, to apply a custom scheme or not to use configurations at all.
 * <p>The library does require that a configuration scheme is chosen - in beans.xml as described below, but the library 
 * does NOT impose any mandatory parameters in order to initialize. The library <i>does</i> know of certain parameters, 
 * if it encounters them.
 *
 * <p>Following classes can be configured: Pz2Service (the controller), Pz2Client, and ServiceProxyClient. Some currently 
 * acknowledged parameters are TYPE (service type) PAZPAR2_URL, SERVICE_ID (see Pazpar2 documentation for an explanation of 
 * service id), and SERVICE_PROXY_URL</p>
 * 
 * <h3>Selecting a configuration scheme</h3>
 * 
 * <p>The built-in configuration schemes are:</p>
 * <ol>
 *  <li>Configuration by context parameters in web.xml, this is the simple albeit less flexible choice</li>
 *  <li>The configuration scheme Index Data uses for other MasterKey applications, Mk2Config, this is the more versatile option</li>
 * </ol> 
 * 
 * <p>It must be determined deploy-time what configuration scheme to use, by selecting the preferred 
 * mechanism in the application's beans.xml. In this example the MasterKey configuration scheme is injected:</p> 
 * 
 * <pre>
 * &lt;beans xmlns="http://java.sun.com/xml/ns/javaee"
 *         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
 *         xsi:schemaLocation="
 *     http://java.sun.com/xml/ns/javaee 
 *     http://java.sun.com/xml/ns/javaee/beans_1_0.xsd"&gt;  
 *     &lt;alternatives>         
 *        &lt;class>com.indexdata.mkjsf.config.WebXmlConfigReader&lt;/class&gt;
 *        &lt;!-- Options                      Mk2ConfigReader     --&gt;
 *        &lt;!--                              WebXmlConfigReader  --&gt;        
 *     &lt;/alternatives&gt;          
 * &lt;/beans&gt;
 * </pre>
 * 
 * Please note that with Tomcat7 this beans.xml would be the one in your application's WEB-INF, which means you can set it once and
 * for all. With Glassfish and JBoss, it would be the one in the META-INF directory of the mkjsf jar (the artifact of this project)
 * meaning it would have to be re-applied with every update of new versions of mkjsf.  
 * 
 * <h3>Configuring the service using web.xml only</h3>
 * 
 * <p>Using the web.xml configuration scheme (choosing WebXmlConfigReader in beans.xml)
 * you can configure you application to use a locally installed Pazpar2 server like this:</p>
 * 
 * <pre>
 *  &lt;context-param&gt;
 *   &lt;param-name&gt;PAZPAR2_URL&lt;/param-name&gt;
 *   &lt;param-value&gt;http://localhost:8004/&lt;/param-value&gt;
 *  &lt;/context-param&gt;
 *  &lt;context-param&gt;
 *   &lt;description&gt;Service type. Possible values: SP, PZ2, TBD&lt;/description&gt;
 *   &lt;param-name&gt;TYPE&lt;/param-name&gt;
 *   &lt;param-value&gt;PZ2&lt;/param-value&gt;  
 *  &lt;/context-param&gt;
 * </pre>
 * 
 * Likewise you could configure your application to use our hosted Pazpar2 service with these settings:
 *
 * <pre>
 *  &lt;context-param&gt;
 *   &lt;param-name&gt;SERVICE_PROXY_URL&lt;/param-name&gt;
 *   &lt;param-value&gt;http://mkc.indexdata.com:9009/service-proxy/&lt;/param-value&gt;
 *  &lt;/context-param&gt;
 *  &lt;context-param&gt;
 *   &lt;description&gt;Service type. Possible values: SP, PZ2, TBD&lt;/description&gt;
 *   &lt;param-name&gt;TYPE&lt;/param-name&gt;
 *   &lt;param-value&gt;SP&lt;/param-value&gt;  
 *  &lt;/context-param&gt;
 * </pre>
 * 
 * 
 * <h3>Configuring the service using 'Mk2Config' scheme</h3>
 * 
 * <p>The Mk2ConfigReader scheme allows the configuration to exist outside of the web application archive. 
 * It supports name spaces for different parts of the application (as opposed to the web.xml scheme) and it 
 * supports different configurations for different virtual hosts using the same web application deployment.</p> 
 * <p>For the Mk2ConfigReader scheme to work, the web.xml must contain pointers to the configuration directory 
 * and properties file. The specific configuration itself would be in those files then.
 * In this example the configuration directory is in the web application itself (war://testconfig). Usually it 
 * would probably be somewhere else in your file system.</p> 
 * <pre>
 * &lt;context-param&gt;
 *  &lt;param-name&gt;MASTERKEY_ROOT_CONFIG_DIR&lt;/param-name&gt;
 *  &lt;param-value&gt;war://testconfig&lt;/param-value&gt;
 * &lt;/context-param&gt;
 * &lt;context-param&gt;
 *  &lt;description&gt;
 *   The sub directory to hold config file(s) for this Masterkey component.
 *  &lt;/description&gt;
 *  &lt;param-name&gt;MASTERKEY_COMPONENT_CONFIG_DIR&lt;/param-name&gt;
 *  &lt;param-value&gt;/jsfdemo&lt;/param-value&gt;
 * &lt;/context-param&gt;
 * &lt;context-param&gt;
 *  &lt;param-name&gt;MASTERKEY_CONFIG_FILE_NAME&lt;/param-name&gt;
 *  &lt;param-value>jsfdemo.properties&lt;/param-value&gt;
 * &lt;/context-param&gt;
 * &lt;context-param&gt;
 *  &lt;description&gt;
 *   Defines the lifespan of configuration parameters as retrieved
 *   from the file pointed to by MASTERKEY_CONFIG_FILE_NAME.
 *   Can be SERVLET (cached) or REQUEST (read for every request).
 *   Will default to SERVLET.
 *  &lt;/description&gt;
 *  &lt;param-name&gt;MASTERKEY_CONFIG_LIFE_TIME&lt;/param-name&gt;
 *  &lt;param-value&gt;REQUEST&lt;/param-value&gt;
 * &lt;/context-param&gt;
 * </pre>
 * 
 * <p>The jsfdemo.properties file might look like this for running against a 
 * local Pazpar2 service:</p>
 * 
 * <pre>
 * service.TYPE = pz2
 * pz2client.PAZPAR2_URL = http://localhost:8004/
 * </pre>
 * 
 * <p>Some of the other known parameters in this format could be:</p>
 * 
 * <pre>
 * service.TYPE = SP              
 * proxyclient.SERVICE_PROXY_URL = http://localhost:8080/service-proxy/
 * </pre> 
 * 
 * <p>It's possible to implement a custom configuration scheme by either ignoring whatever scheme is 
 * injected and then applying the required values otherwise, OR by extending the ConfigurationReader
 * and inject that class in beans.xml instead of any of the two predefined options. The extended
 * class must construct a Configuration object -- which is basically a set of key-value pairs -- 
 * and then set the desired values and hand it off to the Configurable (currently Pz2Service, Pz2Client, 
 * and ServiceProxyClient)</p> 
 * 
 * <p>It would also be easy enough to simply set the URL runtime from the UI pages, using methods on 
 * Pz2Service (named 'pz2').</p> 
 * 
 */
package com.indexdata.mkjsf.config;