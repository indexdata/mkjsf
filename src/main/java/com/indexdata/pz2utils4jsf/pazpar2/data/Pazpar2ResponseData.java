package com.indexdata.pz2utils4jsf.pazpar2.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.indexdata.pz2utils4jsf.pazpar2.data.Pazpar2ResponseData;

public class Pazpar2ResponseData implements Serializable {
  
  private static final long serialVersionUID = -3909755656714679959L;
  String type = null;
  HashMap<String,String> attributes = new HashMap<String,String>();
  HashMap<String,List<Pazpar2ResponseData>> elements = new HashMap<String,List<Pazpar2ResponseData>>();
  String textContent = "";
  String errorText = null;
        
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
    
  public void addElement (String name, Pazpar2ResponseData value) {    
    if (elements.containsKey(name)) {
      elements.get(name).add(value);
    } else {
      List<Pazpar2ResponseData> list = new ArrayList<Pazpar2ResponseData>();
      list.add(value);
      elements.put(name,list);
    }
  }
  
  public List<Pazpar2ResponseData> getElements (String name) {
    return elements.get(name);
  }
  
  public Pazpar2ResponseData getOneElement (String name) {
    if (elements.get(name) != null) {
      return elements.get(name).get(0);
    } else {
      return null;
    }
  }
  
  public String getOneElementValue (String name) {
    if (getOneElement(name)!=null && getOneElement(name).getValue().length()>0) {
      return getOneElement(name).getValue();
    } else {
      return "";
    }
  }
  
  public void appendContent (String content) {
    textContent = textContent + content;
  }
  
  public String getValue () {
    return textContent;
  }
  
  public String getProperty(String name) {
    List<Pazpar2ResponseData> els = elements.get(name);
    if (els != null) {
      return els.get(0).getValue();
    } else {     
      return null;
    }
  }
  
  public int getIntValue(String name) {
    String val = getOneElementValue(name);
    if (val.length()==0) {
      return 0;
    } else {
      return Integer.parseInt(val);
    }
  }
    
  public boolean isError () {
    return (getOneElement("error") != null);   
  }
  
  public String getErrorMessage() {
    return getOneElementValue("error");
  }

      
}
