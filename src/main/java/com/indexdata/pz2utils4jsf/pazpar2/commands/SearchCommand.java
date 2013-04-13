package com.indexdata.pz2utils4jsf.pazpar2.commands;

import javax.enterprise.context.SessionScoped;

import org.apache.log4j.Logger;

import com.indexdata.pz2utils4jsf.pazpar2.Expression;
import com.indexdata.pz2utils4jsf.pazpar2.state.StateManager;

@SessionScoped
public class SearchCommand extends Pazpar2Command {
  
  private static final long serialVersionUID = -1888520867838597236L;
  private static Logger logger = Logger.getLogger(SearchCommand.class);
  private SingleTargetFilter singleTargetFilter = null;
  
  public SearchCommand(StateManager stateMgr) {
    super("search",stateMgr);
  }
  
  public void setSession (String sessionId) {
    setParameter(new CommandParameter("session","=",sessionId));
  }
  
  public void setQuery(String query) {    
    setParameter(new CommandParameter("query","=",query));
  }
  
  public String getQuery () {    
    return getParameter("query") == null ? null  : getParameter("query").getValueWithExpressions();
  }
  
  /**
   * Sets a facet, in CQL, to restrict the current results,
   * then executes the search 
   * 
   * @param facetKey  i.e.  'au' for author
   * @param term  i.e. 'Dickens, Charles'
   */
  public void setFacet(String facetKey, String term) {
    if (term != null && term.length()>0) {         
      getParameter("query").addExpression(new Expression(facetKey,"=",term));            
    }            
  }
  
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
  public void setFacetOnQuery (String facetKey, String term) {
    String facetExpression = facetKey + "=" + term;    
    if (term != null && term.length()>0) {
      String currentQuery= getParameterValue("query");
      setParameter(new CommandParameter("query","=", currentQuery + " and " + facetExpression));      
    }            
  }
      
  /**
   * Removes a facet set by setFacet(...), then executes
   * the search.
   * 
   * Will not remove facets set by setFacetOnQuery(...)
   *  
   * @param facetKey i.e. 'au' for author
   * @param term i.e. 'Dickens, Charles'
   */
  public void removeFacet(String facetKey, String term) {
    if (getParameter("query") != null) {
      getParameter("query").removeExpression(new Expression(facetKey,"=",term));
    }
  }
  
  public void setFilter(String filterExpression) {
    setParameter(new CommandParameter("filter","=",filterExpression));
  }
  
  public String getFilter() {
    return getParameter("filter") == null ? null : getParameter("filter").getValueWithExpressions();
  }
  
  public boolean hasFilter () {
    return getFilter().length()>0;
  }
  
  /**
   * Adds a single target filter to restrict the current query by, 
   * then executes the current search.
   * 
   * This is a special case of the general setFilter function, 
   * allowing to associate a descriptive target name with the 
   * filter expression for display in UI. 
   * 
   * @param targetId pazpar2's ID for the target to limit by
   * @param targetName a descriptive name for the target
   */
  public void setSingleTargetFilter (String targetId, String targetName) {    
    if (hasSingleTargetFilter(new SingleTargetFilter(targetId,targetName))) {
      logger.debug("Already using target filter " + this.singleTargetFilter.getFilterExpression());
    } else {      
      this.singleTargetFilter = new SingleTargetFilter(targetId,targetName);
      setParameter(new CommandParameter("filter","=",this.singleTargetFilter.getFilterExpression()));            
    }    
  }

  public SingleTargetFilter getSingleTargetFilter () {
    logger.debug("request to get the current single target filter");
    return singleTargetFilter;
  }
   
  /**
   * Removes the current target filter from the search
   * 
   */
  public void removeSingleTargetFilter () {
    logger.debug("Removing target filter " + singleTargetFilter.getFilterExpression());
    this.singleTargetFilter = null;
    removeParameter("filter");             
  }
  
  /**
   * 
   * @return The target filter set on the current search command
   */
  public boolean hasSingleTargetFilter() {
    logger.debug("Checking if a single target filter is set: " + (singleTargetFilter != null));
    return singleTargetFilter != null;    
  }

  /**
   * Resolves if the current search command has a target filter - to
   * be used by the UI for conditional rendering of target filter info.
   * 
   * @return true if the current search command is limited by a target 
   * filter
   */
  protected boolean hasSingleTargetFilter(SingleTargetFilter targetFilter) {
    return hasSingleTargetFilter() && targetFilter.equals(this.singleTargetFilter);
  }

  
  public void setLimit (String limit) {
    // TODO
  }
      
  public void addFilter(String filterExpression) {
    if (hasParameterSet("filter")) {
      setFilter(filterExpression);
    } else {
      //TODO
      getParameter("filter");
    }
  }
  
  public void removeFilters () {
    
  }
  
  public void removeFilter(String filterExpression) {
    
  }
    
  public SearchCommand copy () {
    SearchCommand newCommand = new SearchCommand(stateMgr);
    for (String parameterName : parameters.keySet()) {
      newCommand.setParameterSilently(parameters.get(parameterName).copy());      
    }
    newCommand.singleTargetFilter = this.singleTargetFilter;
    return newCommand;
  }


}
