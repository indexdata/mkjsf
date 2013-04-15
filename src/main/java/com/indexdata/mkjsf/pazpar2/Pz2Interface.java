package com.indexdata.mkjsf.pazpar2;

import java.io.Serializable;

import com.indexdata.mkjsf.controls.ResultsPager;

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
   * Resolves whether the backend has a record with the given recid in memory 
   * 
   * @return true if the bean currently holds the record with recid
   */  
  public boolean hasRecord (String recId);
          
    
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
  

     
}
