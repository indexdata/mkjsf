package com.indexdata.pz2utils4jsf.utils;

public class Utils {
  
  public static String objectId(Object o) {
    int lastdot = o.toString().lastIndexOf('.');
    if (lastdot>-1 && lastdot+1<o.toString().length()) {
      return o.toString().substring(lastdot+1);
    } else {
      return o.toString();
    }
  }

}
