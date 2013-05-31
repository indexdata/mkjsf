package com.indexdata.mkjsf.pazpar2.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.indexdata.mkjsf.pazpar2.data.ResponseDataObject;

public class ResponseDataObject implements Serializable {

  Logger logger = Logger.getLogger(ResponseDataObject.class);
  private static final long serialVersionUID = -3909755656714679959L;
  String type = null;
  HashMap<String,String> attributes = new HashMap<String,String>();
  HashMap<String,List<ResponseDataObject>> elements = new HashMap<String,List<ResponseDataObject>>();
  String textContent = "";
  CommandError error = null;
  String xml = null;
  boolean isBinary = false;
  byte[] binary = null;
        
  public void setType (String type) {
    this.type = type;
  }
  
  public String getType () {
    return type;
  }
  
  public void setAttribute (String name, String value) {
    attributes.put(name, value);
  }
  
  public String getAttribute (String name) {
    return attributes.get(name);
  }
    
  public void addElement (String name, ResponseDataObject value) {    
    if (elements.containsKey(name)) {
      elements.get(name).add(value);
    } else {
      List<ResponseDataObject> list = new ArrayList<ResponseDataObject>();
      list.add(value);      
      elements.put(name,list);
    }
  }
  
  public List<ResponseDataObject> getElements (String name) {
    return elements.get(name);
  }
    
  public ResponseDataObject getOneElement (String name) {
    if (elements.get(name) != null) {
      return elements.get(name).get(0);
    } else {
      return null;
    }
  }
  
  /**
   * Returns the text content of the first element found with the given
   * name
   * @param name of the element 
   * @return text value, empty string if none found
   */
  public String getOneValue (String name) {
    if (getOneElement(name)!=null && getOneElement(name).getValue().length()>0) {
      return getOneElement(name).getValue();
    } else {
      return "";
    }
  }
  
  public String[] getValueArray (String name) {
    List<ResponseDataObject> elements = getElements(name);
    String[] valueArray = {};
    if (elements != null) {
      valueArray = new String[elements.size()];
      int i = 0;
      for (ResponseDataObject element : elements) {
        valueArray[i++] = element.getValue();
      }      
    }
    return valueArray;
  }
    
  public void appendContent (String content) {
    textContent = textContent + content;
  }
  
  public String getValue () {
    return textContent;
  }
  
  public String getProperty(String name) {
    List<ResponseDataObject> els = elements.get(name);
    if (els != null) {
      return els.get(0).getValue();
    } else {     
      return null;
    }
  }
  
  public int getIntValue(String name) {
    String val = getOneValue(name);
    if (val.length()==0) {
      return 0;
    } else {
      return Integer.parseInt(val);
    }
  }
    
  public boolean hasApplicationError () {
    return (getOneElement("applicationerror") != null);   
  }
  
  public CommandError getApplicationError() {
    return (CommandError) getOneElement("applicationerror");
  }
  
  public boolean hasServiceError() {
    return hasApplicationError() 
        && getApplicationError().isServiceError();        
  }
  
  public ServiceError getServiceError() {
    return (hasServiceError()? getApplicationError().getServiceError() : null);
  }
      
  public void setXml(String xml) {
    this.xml = xml; 
  }
  
  public String getXml() {
    if (type != null && type.equals("record")) {
      logger.debug("Getting XML for "+type + ": "+xml);
    }      
    return xml == null ? "" : xml;
  }
  
  public boolean getHasResults () {
    return (xml != null && xml.length()>0) || (getIsBinary() && binary.length>0);
  }
  
  public boolean getIsBinary () {
    return isBinary;
  }
    
  public void setBinary(byte[] bytes) {
    isBinary = true;
    binary = bytes;
  }
  
  public byte[] getBinary () {
    return binary;
  }
  
  
        
}
