package com.indexdata.mkjsf.utils;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import com.indexdata.mkjsf.pazpar2.data.ResponseDataObject;

@Named("pzui")
@ApplicationScoped
public class UiUtils {

  /**
   * Concatenates a list of strings
   * @param strings
   * @return
   */
  public static String concat(String... strings) {
    StringBuilder concatenated = new StringBuilder("");
    for (String string : strings) {
      concatenated.append(string);
    }
    return concatenated.toString();
  }
  
  public static String concatMaxLength(int maxTotalLength, String... strings) {
    String concatenated = concat(strings);
    return maxLength(concatenated,maxTotalLength);
  }
  
  public static String maxLength (String string, int maxLength) {
    if (string == null || string.length()<=maxLength) {
      return string;
    } else {
      return string.substring(0,maxLength);
    }
  }
  
  public static String maxLengthMoreLabel (String string, int maxLength, String moreLabel) {
    if (string == null || string.length()<=maxLength) {
      return string;
    } else {
      String maxString = maxLength(string,maxLength);
      if (string.length()>maxString.length()) {
        maxString += moreLabel;
      }
      return maxString;
    }
  }
    
  /**
   * Encloses a string with quotation marks
   * @param string
   * @return
   */
  public static String quotes(String string) {
    return "\"" + string + "\"";
  }
  
  public static List<ResponseDataObject> getMaxNumElements(ResponseDataObject container, String elementName, int maxElements) {
    if (container.getElements(elementName)!=null) {
      int elementCount = container.getElements(elementName).size();
      if (elementCount>0) {
        return ((ArrayList<ResponseDataObject>) container.getElements(elementName)).subList(0, Math.min(maxElements,elementCount));        
      } 
    }   
    return container.getElements(elementName);
  }
  
  public List<ResponseDataObject> getMaxElements(ResponseDataObject container, String elementName, int maxElements, int maxTotalValueLength, boolean hardLimit) {
    List<ResponseDataObject> maxNumList = getMaxNumElements(container,elementName,maxElements);
    if (maxNumList!=null) {
      int elementCount = maxNumList.size();
      if (elementCount>0) {
        int i = 0;
        int totalLength = 0;
        for (ResponseDataObject element : maxNumList) {
          totalLength += element.getValue().length();           
          if (totalLength>maxTotalValueLength) {
            if (!hardLimit) {
              i++;
            }
            break;
          } else {
            i++;
          }
        }
        return maxNumList.subList(0, i);
      }       
    }  
    return maxNumList;    
  }
}
