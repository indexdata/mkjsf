package com.indexdata.mkjsf.pazpar2.data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import org.apache.log4j.Logger;

import com.indexdata.mkjsf.errors.ErrorHelper;
import com.indexdata.mkjsf.errors.ErrorInterface;
import com.indexdata.mkjsf.pazpar2.data.sp.SpResponses;

@Named("pzresp") @SessionScoped
public class Responses implements Serializable {
    
  private static final long serialVersionUID = -7543231258346154642L;
  protected Map<String,ResponseDataObject> dataObjects = new ConcurrentHashMap<String,ResponseDataObject>();
  private static Logger logger = Logger.getLogger(Responses.class);
  private ErrorHelper errorHelper = null;

  public Responses() {    
  }
  
  public void put(String name, ResponseDataObject responseData) {
    dataObjects.put(name, responseData);
  }
  
  public void setErrorHelper(ErrorHelper helper) {    
    this.errorHelper = helper;
  }
  
  public boolean hasApplicationError () {
    if (getSearch().hasApplicationError()) {
      logger.info("Error detected in search");
      return true;
    }
    for (String name : dataObjects.keySet()) {
      if (dataObjects.get(name).hasApplicationError()) {
        logger.info("Error detected in " + name);
        return true;
      }
    }    
    return false;
  }
  
  /**
   * Returns a search command error, if any, otherwise the first
   * error found for an arbitrary command, if any, otherwise
   * an empty dummy error. 
   */    
  public ErrorInterface getCommandError() {
    CommandError error = new CommandError();
    if (dataObjects.get("search").hasApplicationError()) {
      error = dataObjects.get("search").getApplicationError();
      error.setErrorHelper(errorHelper);
    } else {
      for (String name : dataObjects.keySet()) {     
        if (dataObjects.get(name).hasApplicationError()) {     
          error = dataObjects.get(name).getApplicationError();
          error.setErrorHelper(errorHelper);
          break;
        } 
      }
    }
    return error;         
  }
  
  public void reset() {
    logger.debug("Resetting show,stat,termlist,bytarget,search response objects.");
    dataObjects = new ConcurrentHashMap<String,ResponseDataObject>();
    dataObjects.put("show", new ShowResponse());
    dataObjects.put("stat", new StatResponse());
    dataObjects.put("termlist", new TermListsResponse());
    dataObjects.put("bytarget", new ByTarget());
    dataObjects.put("record", new RecordResponse());
    dataObjects.put("search", new SearchResponse());
  }

  public ShowResponse getShow () {
    return ((ShowResponse) dataObjects.get("show"));
  }
  
  public StatResponse getStat () {
    return ((StatResponse) dataObjects.get("stat"));
  }
  
  public RecordResponse getRecord() {
    return ((RecordResponse) dataObjects.get("record"));
  }
  
  public SearchResponse getSearch() {
    return ((SearchResponse) dataObjects.get("search"));
  }
  
  public TermListsResponse getTermLists () {
    return ((TermListsResponse) dataObjects.get("termlist"));
  }
  
  public List<TermResponse> getFacetTerms (String facet, int count) {
    return (getTermLists().getTermList(facet).getTerms(count));
  }
    
  public List<TermResponse> getFacetTerms (String facet) {
    return (getTermLists().getTermList(facet).getTerms());
  }
  
  public ByTarget getByTarget() {
    return ((ByTarget) dataObjects.get("bytarget"));
  }

  public ResponseDataObject getResponseObject (String name) {
    return dataObjects.get(name);
  }
  
  public boolean hasRecords () {
    return getStat().getRecords() > 0            
           && getShow().getHits() != null 
           && getShow().getHits().size()>0;
  }
  
  public String getActiveClients() {    
    if (getShow()!=null) {
      logger.debug("Active clients: "+getShow().getActiveClients());
      return getShow().getActiveClients();
    } else {
      return "";
    }
  }

  public SpResponses getSp() {
    // TODO:
    return null;
  }

}
