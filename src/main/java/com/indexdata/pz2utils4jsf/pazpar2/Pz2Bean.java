package com.indexdata.pz2utils4jsf.pazpar2;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.indexdata.pz2utils4jsf.config.Pz2ConfigureByWebXml;
import com.indexdata.pz2utils4jsf.controls.ResultsPager;
import com.indexdata.pz2utils4jsf.pazpar2.Pz2Interface;
import com.indexdata.pz2utils4jsf.pazpar2.Pz2Session;
import com.indexdata.pz2utils4jsf.pazpar2.TargetFilter;
import com.indexdata.pz2utils4jsf.pazpar2.data.ByTarget;
import com.indexdata.pz2utils4jsf.pazpar2.data.RecordResponse;
import com.indexdata.pz2utils4jsf.pazpar2.data.ShowResponse;
import com.indexdata.pz2utils4jsf.pazpar2.data.StatResponse;
import com.indexdata.pz2utils4jsf.pazpar2.data.TermListsResponse;
import com.indexdata.pz2utils4jsf.pazpar2.data.TermResponse;

@Named("pz2")
@SessionScoped
public class Pz2Bean implements Pz2Interface, Serializable {

  private static final long serialVersionUID = 3440277287081557861L;
  Pz2Session pz2;

  @Inject 
  private Pz2ConfigureByWebXml pz2conf;

  public Pz2Bean () {
    pz2 = new Pz2Session(pz2conf);    
  }
  /* (non-Javadoc)
   * @see com.indexdata.pz2utils4jsf.pazpar2.Pz2Interface#doSearch(java.lang.String)
   */
  public void doSearch(String query) {
    pz2.doSearch(query);
  }

  /* (non-Javadoc)
   * @see com.indexdata.pz2utils4jsf.pazpar2.Pz2Interface#doSearch()
   */
  public void doSearch() {
    pz2.doSearch();
  }

  /* (non-Javadoc)
   * @see com.indexdata.pz2utils4jsf.pazpar2.Pz2Interface#update()
   */
  public String update() {
    return pz2.update();
  }

  /* (non-Javadoc)
   * @see com.indexdata.pz2utils4jsf.pazpar2.Pz2Interface#update(java.lang.String)
   */
  public String update(String commands) {
    return pz2.update(commands);
  }

  /* (non-Javadoc)
   * @see com.indexdata.pz2utils4jsf.pazpar2.Pz2Interface#setQuery(java.lang.String)
   */
  public void setQuery(String query) {
    pz2.setQuery(query);
  }

  /* (non-Javadoc)
   * @see com.indexdata.pz2utils4jsf.pazpar2.Pz2Interface#getQuery()
   */
  public String getQuery() {
    return pz2.getQuery();
  }

  /* (non-Javadoc)
   * @see com.indexdata.pz2utils4jsf.pazpar2.Pz2Interface#setFacet(java.lang.String, java.lang.String)
   */
  public void setFacet(String facetKey, String term) {
    pz2.setFacet(facetKey, term);
  }

  /* (non-Javadoc)
   * @see com.indexdata.pz2utils4jsf.pazpar2.Pz2Interface#removeFacet(java.lang.String, java.lang.String)
   */
  public void removeFacet(String facetKey, String term) {
    pz2.removeFacet(facetKey, term);
  }

  /* (non-Javadoc)
   * @see com.indexdata.pz2utils4jsf.pazpar2.Pz2Interface#setFacetOnQuery(java.lang.String, java.lang.String)
   */
  public void setFacetOnQuery(String facetKey, String term) {
    pz2.setFacetOnQuery(facetKey, term);
  }

  /* (non-Javadoc)
   * @see com.indexdata.pz2utils4jsf.pazpar2.Pz2Interface#setTargetFilter(java.lang.String, java.lang.String)
   */
  public void setTargetFilter(String targetId, String targetName) {
    pz2.setTargetFilter(targetId, targetName);
  }
  
  /* (non-Javadoc)
   * @see com.indexdata.pz2utils4jsf.pazpar2.Pz2Interface#removeTargetFilter()
   */
  public void removeTargetFilter () {
    pz2.removeTargetFilter();
  }

  /* (non-Javadoc)
   * @see com.indexdata.pz2utils4jsf.pazpar2.Pz2Interface#getTargetFilter()
   */
  public TargetFilter getTargetFilter() {
    return pz2.getTargetFilter();
  }

  /* (non-Javadoc)
   * @see com.indexdata.pz2utils4jsf.pazpar2.Pz2Interface#hasTargetFilter()
   */
  public boolean hasTargetFilter() {
    return pz2.hasTargetFilter();
  }

  /* (non-Javadoc)
   * @see com.indexdata.pz2utils4jsf.pazpar2.Pz2Interface#setSort(java.lang.String)
   */
  public void setSort(String sortOption) {
    pz2.setSort(sortOption);
  }

  /* (non-Javadoc)
   * @see com.indexdata.pz2utils4jsf.pazpar2.Pz2Interface#getSort()
   */
  public String getSort() {
    return pz2.getSort();
  }

  /* (non-Javadoc)
   * @see com.indexdata.pz2utils4jsf.pazpar2.Pz2Interface#setPageSize(int)
   */
  public void setPageSize(int perPageOption) {
    pz2.setPageSize(perPageOption);
  }

  /* (non-Javadoc)
   * @see com.indexdata.pz2utils4jsf.pazpar2.Pz2Interface#getPageSize()
   */
  public int getPageSize() {
    return pz2.getPageSize();
  }

  /* (non-Javadoc)
   * @see com.indexdata.pz2utils4jsf.pazpar2.Pz2Interface#setStart(int)
   */
  public void setStart(int start) {
    pz2.setStart(start);
  }
  
  /* (non-Javadoc)
   * @see com.indexdata.pz2utils4jsf.pazpar2.Pz2Interface#getStart()
   */
  public int getStart() {
    return pz2.getStart();
  }

  /* (non-Javadoc)
   * @see com.indexdata.pz2utils4jsf.pazpar2.Pz2Interface#toggleRecord(java.lang.String)
   */
  public String toggleRecord(String recid) {
    return pz2.toggleRecord(recid);
  }
  
  /* (non-Javadoc)
   * @see com.indexdata.pz2utils4jsf.pazpar2.Pz2Interface#getShow()
   */
  public ShowResponse getShow() {
    return pz2.getShow();
  }
  
  /* (non-Javadoc)
   * @see com.indexdata.pz2utils4jsf.pazpar2.Pz2Interface#getStat()
   */
  public StatResponse getStat() {
    return pz2.getStat();
  }
    
  /* (non-Javadoc)
   * @see com.indexdata.pz2utils4jsf.pazpar2.Pz2Interface#hasRecord(java.lang.String)
   */
  public boolean hasRecord(String recId) {    
    return pz2.hasRecord(recId);
  }
  
  /* (non-Javadoc)
   * @see com.indexdata.pz2utils4jsf.pazpar2.Pz2Interface#getRecord()
   */
  public RecordResponse getRecord() {
    return pz2.getRecord();
  }
  
  /* (non-Javadoc)
   * @see com.indexdata.pz2utils4jsf.pazpar2.Pz2Interface#getTermLists()
   */
  public TermListsResponse getTermLists() {
    return pz2.getTermLists();
  }
  
  /* (non-Javadoc)
   * @see com.indexdata.pz2utils4jsf.pazpar2.Pz2Interface#getFacetTerms(java.lang.String, int)
   */
  public List<TermResponse> getFacetTerms(String facet, int count) {
    return pz2.getFacetTerms(facet, count);
  }
  
  /* (non-Javadoc)
   * @see com.indexdata.pz2utils4jsf.pazpar2.Pz2Interface#getFacetTerms(java.lang.String)
   */
  public List<TermResponse> getFacetTerms(String facet) {  
    return pz2.getFacetTerms(facet);
  }  
  
  /* (non-Javadoc)
   * @see com.indexdata.pz2utils4jsf.pazpar2.Pz2Interface#getByTarget()
   */
  public ByTarget getByTarget() {  
    return pz2.getByTarget();
  }
  
  /* (non-Javadoc)
   * @see com.indexdata.pz2utils4jsf.pazpar2.Pz2Interface#hasRecords()
   */
  public boolean hasRecords() {
    return pz2.hasRecords();
  }
  
  /* (non-Javadoc)
   * @see com.indexdata.pz2utils4jsf.pazpar2.Pz2Interface#setPager(int)
   */
  public ResultsPager setPager(int pageRange) {
    return pz2.setPager(pageRange);
  }

  /* (non-Javadoc)
   * @see com.indexdata.pz2utils4jsf.pazpar2.Pz2Interface#getPager()
   */
  public ResultsPager getPager() {
    return pz2.getPager();
  }
  
  /* (non-Javadoc)
   * @see com.indexdata.pz2utils4jsf.pazpar2.Pz2Interface#getCurrentStateKey()
   */
  public String getCurrentStateKey() {
    return pz2.getCurrentStateKey();
  }
    
  /* (non-Javadoc)
   * @see com.indexdata.pz2utils4jsf.pazpar2.Pz2Interface#setCurrentStateKey(java.lang.String)
   */
  public void setCurrentStateKey(String key) {
    pz2.setCurrentStateKey(key);    
  }

}
