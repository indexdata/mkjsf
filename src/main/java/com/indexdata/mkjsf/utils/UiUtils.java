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
  public static String quote(String string) {
    return "\"" + string + "\"";
  }
  
  /**
   * Gets at most maxElements elements of the given elementName
   * @param container The parent element containing the elements to retrieve
   * @param elementName The name of the element(s) to retrieve
   * @param maxElements Maximum number of elements to retrieve
   * @return At most maxElements data objects of the given type
   */
  public static List<ResponseDataObject> getMaxNumElements(ResponseDataObject container, String elementName, int maxElements) {
    if (container.getElements(elementName)!=null) {
      int elementCount = container.getElements(elementName).size();
      if (elementCount>0) {
        return ((ArrayList<ResponseDataObject>) container.getElements(elementName)).subList(0, Math.min(maxElements,elementCount));        
      } 
    }   
    return container.getElements(elementName);
  }
  
  /**
   * Gets at most maxElements data objects, up to a total string length of maxTotalValueLength
   * @param container The parent element containing the elements to retrieve
   * @param elementName The name of the element(s) to retrieve
   * @param maxElements  Maximum number of elements to retrieve
   * @param maxTotalValueLength The maximum total string length of the values of the elements retrieved
   * @param hardLimit If set to true, the list will be cut of at or below the total string length, if false, the list
   *                  will contain the first element that exceeds the length limit - for instance thus guaranteeing 
   *                  that at least one of the elements will be returned, no matter it's length. 
   * @return Delimited list of elements
   */
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
