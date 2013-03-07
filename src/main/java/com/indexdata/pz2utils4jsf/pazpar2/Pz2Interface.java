package com.indexdata.pz2utils4jsf.pazpar2;

import java.io.Serializable;
import java.util.List;

import com.indexdata.pz2utils4jsf.controls.ResultsPager;
import com.indexdata.pz2utils4jsf.pazpar2.TargetFilter;
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
   * Sets a query to used by the next search command
   * 
   * @param query a query on pazpar2 query syntax
   * 
   */
  public void setQuery (String query);
  
  /**
   * Gets the current query  
   * @return a pazpar2 query string
   */
  public String getQuery ();
  
  /**
   * Sets a facet to limit the current query by,
   * then executes the search 
   * 
   * @param facetKey  i.e.  'au' for author
   * @param term  i.e. 'Dickens, Charles'
   */
  public void setFacet(String facetKey, String term);
  
  /**
   * Removes a facet set by setFacet(...), then executes
   * the search.
   * 
   * Will not remove facets set by setFacetOnQuery(...)
   *  
   * @param facetKey i.e. 'au' for author
   * @param term i.e. 'Dickens, Charles'
   */
  public void removeFacet (String facetKey, String term);
  
  /**
   * Sets a facet to limit the current query by. The 
   * facet is appended to the query string itself (rather
   * as a separately managed entity. It will thus appear
   * in a query field as retrieved by getQuery(). It will
   * not be removed by removeFacet(...)
   * 
   * @param facetKey  i.e. 'au' for author
   * @param term i.e. 'Dickens, Charles'
   */
  public void setFacetOnQuery(String facetKey, String term);
  
  /**
   * Adds a target filter to limit the current query by, then
   * executes the current search.
   * 
   * @param targetId pazpar2's ID for the target to limit by
   * @param targetName a descriptive name for the target
   */
  public void setTargetFilter (String targetId, String targetName);
  
  /**
   * Removes the current target filter from the search
   * 
   */
  public void removeTargetFilter ();
  
  /**
   * 
   * @return The target filter set on the current search command
   */
  public TargetFilter getTargetFilter();
  
  /**
   * Resolves if the current search command has a target filter - to
   * be used by the UI for conditional rendering of target filter info.
   * 
   * @return true if the current search command is limited by a target 
   * filter
   */
  public boolean hasTargetFilter();
  
  /**
   * Sets the ordering of records (hits) in the 'show' display object
   */
  
  /**
   * Sets the sort order for results, the updates the 'show' data object
   * from pazpar2. Set valid sort options in the documentation for pazpar2.
   * 
   * The parts of the UI that display 'show' data should be rendered following
   * this request.
   * 
   * @param sortOption
   */
  public void setSort(String sortOption);
  
  /**
   * Retrieves the current sort order for results
   * @return sort order - i.e. 'relevance'
   */
  public String getSort();
  
  /**
   * Sets the number of records that pazpar2 should show at a time. Is 
   * followed by an update of the show data object from pazpar2.  
   * 
   * To be used by the UI for paging. After setting page size the parts
   * of the UI that displays 'show' data should be rendered. 
   * 
   * @param perPageOption i.e. 10, default is 20.
   */
  public void setPageSize (int perPageOption);
  
  /**
   * Retrieves the currently defined number of items to show at a time
   * 
   * @return number of result records that will be shown from pazpar2
   */
  public int getPageSize();
  
  /**
   * Sets the first record to show - starting at record '0'. After setting
   * first record number, the 'show' data object will be updated from pazpar2,
   * and the parts of the UI displaying show data should be re-rendered.
   * 
   * To be used by the UI for paging.
   * 
   * @param start first record to show
   */
  public void setStart (int start);
  
  /**
   * Retrieves the sequence number of the record that pazpaz2 will return as
   * the first record in 'show'
   * 
   * @return sequence number of the first record to be shown (numbering starting at '0')
   * 
   */
  public int getStart();
  
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
  
  public boolean hasErrors();
  
  public String getErrorMessages();
  
  public String getFirstErrorMessage();
   
}
