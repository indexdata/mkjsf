package com.indexdata.mkjsf.pazpar2;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

import com.indexdata.masterkey.pazpar2.client.Pazpar2HttpResponse;

/**
 * Contains one HTTP response to a command executed against a Pazpar2 service in such a 
 * way as to give the response parser a common interface to responses, whether 
 * they are from Pazpar2, from the Service Proxy, or are error messages created 
 * by the JSF application during processing.
 *  
 * @author Niels Erik
 *
 */
public class ClientCommandResponse implements HttpResponseWrapper {
    
  private int statusCode;
  private String contentType;
  private byte[] content = null;
  private String contentString = null;
  private byte[] bytesForParsing = null;
  
  /**
   * Used for storing Pazpar2 based response
   * 
   * @param pz2response
   * @param content
   */
  public ClientCommandResponse(Pazpar2HttpResponse pz2response, ByteArrayOutputStream content) {    
    this.content = content.toByteArray();
    this.statusCode = pz2response.getStatusCode();
    this.contentType = pz2response.getContentType();
  }
    
  /**
   * Used for storing error response
   *  
   * @param statusCode
   * @param content
   * @param contentType
   */
  public ClientCommandResponse(int statusCode, String content, String contentType) {
    this.statusCode = statusCode;
    this.contentString = content;
    this.contentType = contentType;
  }
  
  /**
   * Used for storing Service Proxy based response
   * 
   * @param statusCode
   * @param content
   * @param contentType
   */
  public ClientCommandResponse(int statusCode, byte[] content, String contentType) {
    this.statusCode = statusCode;
    this.content = content;
    this.contentType = contentType;
  }

  @Override
  public int getStatusCode() {    
    return statusCode;
  }

  @Override
  public String getContentType() {
    return contentType;
  }

  /**
   * Gets the response as a String - unless the response is marked as binary
   */
  @Override
  public String getResponseString() {
    if (content == null) {
      return contentString;
    } else if (isBinary()) {
      return "[binary response]";
    } else {
      try {
        return new String(content,"UTF-8");
      } catch (UnsupportedEncodingException e) {      
        e.printStackTrace();
        return "<error>unsupported encoding</error>";
      }
    }
  }

  @Override
  public byte[] getBytes() {
    return content;
  }
  
  /**
   * Overrides the original response with a modified response. Used for
   * one instance of a response that is not named by the command that 
   * created it - such as the parser expects. 
   * 
   * @param parseString
   */
  public void setResponseToParse(String parseString) {    
    try {
      this.bytesForParsing = parseString.getBytes("UTF-8");
    } catch (UnsupportedEncodingException e) {      
      e.printStackTrace();
    }
  }

  /**
   * Used by the parser to get the response for further processing. 
   * @return
   */
  public byte[] getResponseToParse() {
    if (bytesForParsing != null) {
      return bytesForParsing;
    } else if (content != null) {
      return content;
    } else {
      try {
        return contentString.getBytes("UTF-8");
      } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
        return null;
      }
    }
  }
  
  @Override
  public boolean isBinary() {    
    return !contentType.contains("xml");
  }

}
