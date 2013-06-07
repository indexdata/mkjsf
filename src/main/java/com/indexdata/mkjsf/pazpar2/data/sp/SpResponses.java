package com.indexdata.mkjsf.pazpar2.data.sp;

import java.io.Serializable;

import com.indexdata.mkjsf.pazpar2.data.Responses;

/**
 * Provides references to all current Service Proxy-only data objects.
 *  
 * @author Niels Erik
 *
 */
public class SpResponses implements Serializable {
  
  private static final long serialVersionUID = -3831357590639961167L;
  private Responses responses = null;  

  public SpResponses(Responses responses) {
    this.responses = responses;  
  }
  
  /**
   * Resets all responses from the search request to any request 
   * that can follow search (i.e. show and bytarget but not init or auth) 
   * @param includePazpar2Responses Set to true if the invocation should
   *                                reset Pazpar2 responses 
   *                                (non-SP-specific responses) as well. 
   */
  public void resetSearchAndBeyond(boolean includePazpar2Responses) {
    if (includePazpar2Responses) {
      responses.resetSearchAndBeyond();
    }
  }

  /**
   * Resets all responses from the init request to any request 
   * that can follow init (i.e. search and show but not auth) 
   * @param includePazpar2Responses Set to true if the invocation should
   *                                reset Pazpar2 responses 
   *                                (non-SP-specific responses) as well. 
   */
  public void resetInitAndBeyond (boolean includePazpar2Responses) {
    responses.put("categories", new CategoriesResponse());
    if (includePazpar2Responses) {
      responses.resetInitAndBeyond();
    }
    resetSearchAndBeyond(includePazpar2Responses);    
  }
  
  /**
   * Resets all responses from the authentication request to any request 
   * that can follow authentication. 
   * @param includePazpar2Responses Set to true if the invocation should
   *                                reset Pazpar2 responses 
   *                                (non-SP-specific responses) as well. 
   */
  public void resetAuthAndBeyond (boolean includePazpar2Responses) {
    responses.put("auth", new AuthResponse());
    resetInitAndBeyond(includePazpar2Responses);    
  }

  
  public AuthResponse getAuth () {
    return ((AuthResponse) responses.getResponseObject("auth"));
  }

  public CategoriesResponse getCategories() {
    return ((CategoriesResponse) responses.getResponseObject("categories"));
  }

}
