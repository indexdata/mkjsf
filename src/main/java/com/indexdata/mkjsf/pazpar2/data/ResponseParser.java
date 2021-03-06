package com.indexdata.mkjsf.pazpar2.data;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.indexdata.mkjsf.pazpar2.ClientCommandResponse;
import com.indexdata.mkjsf.pazpar2.data.sp.AuthResponse;
import com.indexdata.mkjsf.pazpar2.data.sp.CategoriesResponse;
import com.indexdata.mkjsf.pazpar2.data.sp.TargetCategory;

/**
 * Parses the XML stored in ClientCommandResponses and builds ResponseDataObjects from it.
 *  
 * @author Niels Erik
 *
 */
public class ResponseParser extends DefaultHandler {

  private XMLReader xmlReader = null;
  private ResponseDataObject currentElement = null;
  private Stack<ResponseDataObject> dataElements = new Stack<ResponseDataObject>();
  private ResponseDataObject result = null;
  private String xml = null;
  private static Logger logger = Logger.getLogger(ResponseParser.class);

  public static List<String> docTypes = Arrays.asList(  "bytarget","termlist","show","stat","record","search","init","info",
                                        /* SP extras */ "auth", "categories" );                                        
  
  public ResponseParser() {
    try {
      initSax();
    } catch (ParserConfigurationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (SAXException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  public static ResponseParser getParser() {
    return new ResponseParser();
  }
  
  private void initSax() throws ParserConfigurationException, SAXException {
    SAXParserFactory spf = SAXParserFactory.newInstance();
    spf.setNamespaceAware(true);
    SAXParser saxParser = spf.newSAXParser();
    xmlReader = saxParser.getXMLReader();
    xmlReader.setContentHandler(this);         
  }
  
  /**
   * Parses a Pazpar2 XML response -- or an error response as XML -- and produces a 
   * ResponseDataObject object, i.e. a 'show' object
   * 
   * @param response XML response string from Pazpar2
   * @return Response data object
   */
  public ResponseDataObject getDataObject (ClientCommandResponse response) {
    this.xml = response.getResponseString();
    try {      
      xmlReader.parse(new InputSource(new ByteArrayInputStream(response.getResponseToParse())));
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace(); 
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();      
    }
    return result;
  }

  /** 
   * Receive notification at the start of element 
   * 
   */
  @Override
  public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
    if (localName.equals("init")) {
      currentElement = new InitResponse();
    } else if (localName.equals("search")) {
      currentElement = new SearchResponse();
    } else if (localName.equals("show")) {
      currentElement = new ShowResponse();      
    } else if (localName.equals("hit")) {
      currentElement = new Hit();
    } else if (localName.equals("location")) {
      currentElement = new Location();
    } else if (localName.equals("record")) {
      currentElement = new RecordResponse();            
    } else if (localName.equals("bytarget")) {
      currentElement = new ByTargetResponse();      
    } else if (localName.equals("target")) {
      currentElement = new Target();
    } else if (localName.equals("stat")) {
      currentElement = new StatResponse();      
    } else if (localName.equals("termlist")) {
      currentElement = new TermListsResponse();      
    } else if (localName.equals("list")) {
      currentElement = new TermListResponse();
      ((TermListResponse)currentElement).setName(atts.getValue("name"));
      ((TermListsResponse)dataElements.peek()).addTermList((TermListResponse)currentElement);
    } else if (localName.equals("term")) {
      if (dataElements.peek().getAttribute("name").equals("xtargets")) {
        currentElement = new TermXTargetResponse();        
      } else {
        currentElement = new TermResponse();
      }
      ((TermListResponse)dataElements.peek()).addTerm((TermResponse)currentElement);
    } else if (localName.equals("info")) {
      currentElement = new InfoResponse();
    } else if (localName.equals("version") && dataElements.peek().getType().equals("info")) {
      currentElement = new Pazpar2VersionResponse();
    } else if (localName.equals("applicationerror")) {
      currentElement = new CommandError();
    } else if (localName.equals("error") && (!dataElements.isEmpty() && dataElements.peek().getType().equals("applicationerror"))) {
      currentElement = new ServiceError(); 
    // Service Proxy extras  
    } else if (localName.equals("auth")) {  
      currentElement = new AuthResponse();
    } else if (localName.equals("categories")) {
      currentElement = new CategoriesResponse();
    } else if (localName.equals("category") && dataElements.peek().getType().equals("categories")) {
      currentElement = new TargetCategory();
    // Catch all
    } else {
      currentElement = new ResponseDataObject();
    }
    currentElement.setType(localName);
    for (int i=0; i< atts.getLength(); i++) {
       currentElement.setAttribute(atts.getLocalName(i), atts.getValue(i));
    }    
    if (!docTypes.contains(localName)) {
      if (dataElements.size() == 0) {
        logger.info("Encountered unknown top level element [" + localName + "]. Creating generic data object.");
        currentElement.setType(localName);
      } else {
        dataElements.peek().addElement(localName, currentElement);
      }
    }
    if (this.xml != null) { // Store XML for doc level elements
      currentElement.setXml(xml);
      xml = null;
    }
    dataElements.push(currentElement);    
  }
 
  @Override
  public void characters(char[] ch, int start, int length) throws SAXException {
    String data = new String(ch, start, length);        
    dataElements.peek().appendContent(data);    
  }
  
  @Override
  public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
    if (dataElements.size()==1) {
      result = dataElements.pop();
    } else {
      dataElements.pop();
    }
  }
}
