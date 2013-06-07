package com.indexdata.mkjsf.utils;

/**
 * A few utilities, mostly for logging
 *  
 * @author Niels Erik
 *
 */
public class Utils {
  
  public static String nl = System.getProperty("line.separator");  

  public static String objectId(Object o) {
    int lastdot = o.toString().lastIndexOf('.');
    if (lastdot>-1 && lastdot+1<o.toString().length()) {
      return o.toString().substring(lastdot+1);
    } else {
      return o.toString();
    }
  }
  
  public static String baseObjectName(Object o) {
    String objName = o.getClass().getName();
    if (objName.contains("$")) {
      return objectId(objName.substring(0,objName.indexOf("$")));      
    } else {
      return objectId(objName);
    }
  }
  
}
