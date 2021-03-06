package com.indexdata.mkjsf.pazpar2.data;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;

import org.apache.log4j.Logger;

import com.indexdata.mkjsf.errors.ErrorHelper;
import com.indexdata.mkjsf.errors.ErrorInterface;
import com.indexdata.mkjsf.pazpar2.data.sp.SpResponseDataObject;
import com.indexdata.mkjsf.pazpar2.data.sp.SpResponses;
import com.indexdata.mkjsf.utils.Utils;

/**
 * Provides references to all current data objects and has general methods for clearing certain response data.
 *  
 * @author Niels Erik
 *
 */
@SessionScoped @Named
public class Responses implements Serializable {
    
  private static final long serialVersionUID = -7543231258346154642L;
  protected Map<String,ResponseDataObject> dataObjects = new ConcurrentHashMap<String,ResponseDataObject>();
  private static Logger logger = Logger.getLogger(Responses.class);
  private ErrorHelper errorHelper = null;
  private SpResponses sp = null;

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
        if (dataObjects.get(name) instanceof SpResponseDataObject &&
           ((SpResponseDataObject)dataObjects.get(name)).unsupportedCommand()) {
            logger.debug("Command  [" + name + "] not supported by this service");                    
        } else {
          logger.info("Error detected in " + name);
          return true;
        }
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
  
  /**
   * Empties all data objects populated after a search (including the search response itself)
   * 
   */
  public void resetSearchAndBeyond() {
    logger.debug("Resetting show,stat,termlist,bytarget,record,search response objects.");
    dataObjects.put("show", new ShowResponse());
    dataObjects.put("stat", new StatResponse());
    dataObjects.put("termlist", new TermListsResponse());
    dataObjects.put("bytarget", new ByTargetResponse());
    dataObjects.put("record", new RecordResponse());
    dataObjects.put("search", new SearchResponse());
    getSp().resetSearchAndBeyond(false);
  }
  
  /**
   * Empties all data objects populated after a service was initialized, including the init response itself
   * but excluding a possible auth response
   */
  public void resetInitAndBeyond () {
    dataObjects.put("init", new InitResponse());
    dataObjects.put("info", new InfoResponse());
    resetSearchAndBeyond();
    getSp().resetInitAndBeyond(false);
  }
    
  public InitResponse getInit () {    
    return ((InitResponse) dataObjects.get("init"));
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
  
  public ByTargetResponse getByTarget() {
    return ((ByTargetResponse) dataObjects.get("bytarget"));
  }
  
  public InfoResponse getInfo() {
    return ((InfoResponse) dataObjects.get("info"));
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
    if (getShow()!=null && getShow().getActiveClients().length()>0) {
      logger.debug("Active clients: "+getShow().getActiveClients());
      return String.valueOf(
                    Math.max(Integer.parseInt(getShow().getActiveClients()),
                             getStat().getActiveClients()));
    } else {
      return "";
    }
  }

  public void download(String commandName) throws UnsupportedEncodingException, IOException {
    logger.info(Utils.objectId(this) + " got a download request for "
        + commandName);
    ResponseDataObject object = dataObjects.get(commandName);    
    FacesContext facesContext = FacesContext.getCurrentInstance();
    ExternalContext externalContext = facesContext.getExternalContext();
    if (object.getIsBinary()) {
      externalContext.setResponseHeader("Content-Type","application/octet-stream");
      externalContext.setResponseHeader("Content-Length",String.valueOf(object.getBinary().length));
      externalContext.setResponseHeader("Content-Disposition","attachment;filename=\"" + commandName + ".data\"");
      externalContext.getResponseOutputStream().write(object.getBinary());      
    } else {
      externalContext.setResponseHeader("Content-Type","application/xml; charset=\"utf-8\"");
      externalContext.setResponseHeader("Content-Length",String.valueOf(dataObjects.get(commandName).getXml().getBytes("UTF-8").length));
      externalContext.setResponseHeader("Content-Disposition","attachment;filename=\"" + commandName + ".xml\"");
      externalContext.getResponseOutputStream().write(dataObjects.get(commandName).getXml().getBytes("UTF-8"));
    }
    facesContext.responseComplete();
  }
  
  public SpResponses getSp() {
    return (sp == null ? new SpResponses(this) : sp);
  }
  
}