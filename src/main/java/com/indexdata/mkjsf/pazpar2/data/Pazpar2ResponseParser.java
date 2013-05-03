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

import com.indexdata.mkjsf.pazpar2.data.ByTarget;
import com.indexdata.mkjsf.pazpar2.data.Hit;
import com.indexdata.mkjsf.pazpar2.data.Location;
import com.indexdata.mkjsf.pazpar2.data.Pazpar2ResponseData;
import com.indexdata.mkjsf.pazpar2.data.RecordResponse;
import com.indexdata.mkjsf.pazpar2.data.ShowResponse;
import com.indexdata.mkjsf.pazpar2.data.StatResponse;
import com.indexdata.mkjsf.pazpar2.data.Target;
import com.indexdata.mkjsf.pazpar2.data.TermListResponse;
import com.indexdata.mkjsf.pazpar2.data.TermListsResponse;
import com.indexdata.mkjsf.pazpar2.data.TermResponse;
import com.indexdata.mkjsf.pazpar2.data.TermXTargetResponse;

public class Pazpar2ResponseParser extends DefaultHandler {

  private XMLReader xmlReader = null;
  private Pazpar2ResponseData currentElement = null;
  private Stack<Pazpar2ResponseData> dataElements = new Stack<Pazpar2ResponseData>();
  private Pazpar2ResponseData result = null;
  private String xml = null;
  private static Logger logger = Logger.getLogger(Pazpar2ResponseParser.class);

  public static final List<String> docTypes = 
      Arrays.asList("bytarget","termlist","show","stat","record","search");
  
  public Pazpar2ResponseParser() {    
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
  
  public static Pazpar2ResponseParser getParser() {
    return new Pazpar2ResponseParser();
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
   * Pazpar2ResponseData object, i.e. a 'show' object
   * 
   * @param response XML response string from Pazpar2
   * @return Response data object
   */
  public Pazpar2ResponseData getDataObject (String response) {
    this.xml = response;
    try {      
      xmlReader.parse(new InputSource(new ByteArrayInputStream(response.getBytes("UTF-8"))));
    } catch (UnsupportedEncodingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();      
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (SAXException e) {
      // TODO Auto-generated catch block
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
    if (localName.equals("show")) {
      currentElement = new ShowResponse();      
    } else if (localName.equals("hit")) {
      currentElement = new Hit();
    } else if (localName.equals("location")) {
      currentElement = new Location();
    } else if (localName.equals("bytarget")) {
      currentElement = new ByTarget();      
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
    } else if (localName.equals("record")) {
      currentElement = new RecordResponse();      
    } else if (localName.equals("search")) {
      currentElement = new SearchResponse();
    } else if (localName.equals("applicationerror")) {
      currentElement = new CommandError();
    } else if (localName.equals("error") && dataElements.peek().getType().equals("applicationerror")) {
      currentElement = new Pazpar2Error();     
    } else {
      currentElement = new Pazpar2ResponseData();
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
