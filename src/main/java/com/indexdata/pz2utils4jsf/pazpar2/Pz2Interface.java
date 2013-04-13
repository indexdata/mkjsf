package com.indexdata.pz2utils4jsf.pazpar2;

import java.io.Serializable;
import java.util.List;

import com.indexdata.pz2utils4jsf.controls.ResultsPager;
import com.indexdata.pz2utils4jsf.errors.ErrorInterface;
import com.indexdata.pz2utils4jsf.pazpar2.data.ByTarget;
import com.indexdata.pz2utils4jsf.pazpar2.data.RecordResponse;
import com.indexdata.pz2utils4jsf.pazpar2.data.ShowResponse;
import com.indexdata.pz2utils4jsf.pazpar2.data.StatResponse;
import com.indexdata.pz2utils4jsf.pazpar2.data.TermListsResponse;
import com.indexdata.pz2utils4jsf.pazpar2.data.TermResponse;

public interface Pz2Interface extends Serializable {

  /**
   * Executes a Pazpar2 search using the given query string
   * 
   * @param query
   */
  public void doSearch(String query);
  
  /**
   * Executes a Pazpar2 search using the current query 
   */
  public void doSearch();
  
  /**
   * Updates display data objects by issuing the following pazpar2 commands: 
   * 'show', 'stat', 'termlist' and 'bytarget'.
   *  
   * Returns a count of the remaining active clients from the most recent search.
   * 
   * After refreshing the data from pazpar2 the UI components displaying those 
   * data should be re-rendered.
   * 
   * @return count of activeclients 
   */
  public String update();
  
  /**
   * Updates the data objects given by a comma separated list of one or more commands - 
   * i.e. "show,state,termlist,bytarget".
   *  
   * May not be useful for the UI directly. 
   *  
   * @param commands Command separated list of pazpar2 commands.
   * @return count of activeclients 
   * 
   */
  public String update (String commands);
            
  /**
   * Will retrieve or remove the record with the given recid from memory.
   * 
   * A pazpar2 'record' command will then be issued. The part of the UI 
   * showing record data should thus be re-rendered.
   *  
   * @param recid
   * @return
   */
  public String toggleRecord(String recid);
    
  /**
   * Returns the 'show' data as retrieved from pazpar2 by the most 
   * recent update request
   * 
   * @return pazpar2 'show' response object
   */
  public ShowResponse getShow();
    
  /**
   * Returns the 'stat' data as retrieved from pazpar2 by the most 
   * recent update request
   * 
   * @return pazpar2 'stat' response object
   */
  public StatResponse getStat();
  
  /**
   * Resolves whether the backend has a record with the given recid in memory 
   * 
   * @return true if the bean currently holds the record with recid
   */  
  public boolean hasRecord (String recId);
  
  /**
   * Resolves whether the back-end has any records in memory (in 'show') for 
   * display in UI
   * 
   * @return true if there are records to display
   */
  public boolean hasRecords ();
        
  /**
   * Returns a pazpar2 record as retrieved by the most recent 'record'
   * request 
   * @return record data object
   */
  public RecordResponse getRecord();
  
  /**
   * Returns a set of term lists (targets and facets) as retrieved by the 
   * most recent 'termlist' command 
   * @return set of termlists
   */
  public TermListsResponse getTermLists ();
  
  /**
   * Returns up to 'count' terms from the facet given by the 'facet' parameter
   * @param facet  name of the facet
   * @param count  maximum number of facet terms to return
   * @return facet term list limited to 'count' terms
   */
  public List<TermResponse> getFacetTerms (String facet, int count);
    
  /**
   * Returns all the terms of a given facet - or as many as pazpar2 returns
   * @param facet name of the facet
   * @return facet term list
   */
  public List<TermResponse> getFacetTerms (String facet);
  
  /**
   * Returns a ByTarget data object as retrieved by the most recent 'bytarget' 
   * request to pazpar2
   * 
   * @return ByTarget response data object
   */
  public ByTarget getByTarget();
    
  /**
   * Initiates a pager object, a component holding the data to draw a sequence
   * of page numbers to navigate by and mechanisms to navigate with
   * 
   * @param pageRange number of pages to display in the pager
   * @return ResultsPager the initiated pager component
   */
  public ResultsPager setPager(int pageRange);
  
  /**
   * Gives a component for drawing a pager to navigate by.
   * @return ResultsPager pager component
   */
  public ResultsPager getPager();
  
  /**
   * Returns the current hash key used, as used for internal session state tracking
   * and potentially for browser history entries as well
   * 
   * A UI author would not normally be concerned with retrieving this. It's used by the
   * framework internally
   *  
   * @return string that can be used for browsers window.location.hash
   */
  public String getCurrentStateKey ();
      
  /**
   * Sets the current state key, i.e. when user clicks back or forward in browser history.
   * Would normally be automatically handled by the frameworks components.
   *  
   * @param key corresponding to browsers hash string
   */
  public void setCurrentStateKey(String key);
  
  /** 
   * @return true if any errors encountered so far
   */
  public boolean hasErrors();
  
  /**
   * 
   * @return true if errors encountered during execution of commands
   */
  public boolean hasCommandErrors();
  
  /**
   * 
   * @return true if errors encountered when configuring the service
   */
  public boolean hasConfigurationErrors();
  
  /**
   * Returns one (of possibly multiple) errors encountered during execution of commands
   * Will prefer to show the search errors - if any - as the search command is usually 
   * executed first.  
   * 
   * @return
   */
  public ErrorInterface getCommandError();
  
  /**
   * Returns all errors encountered during configuration of the application, in particular
   * the Pazpar2 client. 
   * 
   * @return
   */
  public List<ErrorInterface> getConfigurationErrors();

     
}
