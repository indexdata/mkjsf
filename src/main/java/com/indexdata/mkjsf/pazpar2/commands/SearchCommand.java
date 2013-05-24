package com.indexdata.mkjsf.pazpar2.commands;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import org.apache.log4j.Logger;

import com.indexdata.mkjsf.pazpar2.ClientCommandResponse;
import com.indexdata.mkjsf.pazpar2.HttpResponseWrapper;
import com.indexdata.mkjsf.pazpar2.Pz2Bean;
import com.indexdata.mkjsf.pazpar2.commands.sp.ServiceProxyCommand;
import com.indexdata.mkjsf.pazpar2.data.ResponseDataObject;
import com.indexdata.mkjsf.pazpar2.data.ResponseParser;

@SessionScoped @Named
public class SearchCommand extends Pazpar2Command implements ServiceProxyCommand {
  
  private static final long serialVersionUID = -1888520867838597236L;
  private static Logger logger = Logger.getLogger(SearchCommand.class);
  private SingleTargetFilter singleTargetFilter = null;
    
  public SearchCommand() {
    super("search");
  }
  
  public ResponseDataObject run() {
    logger.info("Running " + getCommandName());
    logger.info("Using client " + Pz2Bean.get().getSearchClient());
    logger.info("Storing responses to " + Pz2Bean.get().getPzresp());
    Pz2Bean.get().getSearchClient().setSearchCommand(this);
    logger.info("Executing command " + getCommandName());
    HttpResponseWrapper httpResponse = Pz2Bean.get().getSearchClient().executeCommand(this);
    ResponseDataObject responseObject = ResponseParser.getParser().getDataObject((ClientCommandResponse) httpResponse);
    Pz2Bean.get().getPzresp().put(getCommandName(), responseObject);
    return responseObject;
  }

    
  public void setQuery(String query) {    
    setParameter(new CommandParameter("query","=",query));
  }
  
  public String getQuery () {    
    return getParameter("query") == null ? null  : getParameter("query").getValueWithExpressions();
  }
  
  public void setFilter(String filterExpression) {
    setParameter(new CommandParameter("filter","=",filterExpression));
  }
  
  public String getFilter() {
    return getParameter("filter") == null ? null : getParameter("filter").getValueWithExpressions();
  }
  
  public void addFilter(String filterExpression) {
    // TODO: implement
    if (hasParameterValue("filter")) {
      setFilter(filterExpression);
    } else {
      getParameter("filter");
    }
    throw new UnsupportedOperationException("removeFilter(filterExpression) yet to be implemented.");
  }
  
  public void removeFilters () {
    removeParameter("filter");
  }
  
  public void removeFilter(String filterExpression) {
    // TODO: implement
    throw new UnsupportedOperationException("removeFilter(filterExpression) yet to be implemented.");
  }

  public boolean hasFilter () {
    return getFilter().length()>0;
  }
  
  public void setLimit (String limitExpression) {
    setParameter(new CommandParameter("limit","=",limitExpression));
  }
  
  public String getLimit () {
    return getParameterValue("limit");
  }
      
  public void setStartrecs (String startrecs) {
    setParameter(new CommandParameter("startrecs","=",startrecs));
  }
  
  public String getStartrecs () {
    return getParameterValue("startrecs");
  }
  
  public void setMaxrecs (String maxrecs) {
    setParameter(new CommandParameter("maxrecs","=",maxrecs));
  }
  
  public String getMaxrecs () {
    return getParameterValue("maxrecs");
  }
  
  public void setSort (String sort) {
    setParameter(new CommandParameter("sort","=",sort));
  }
  
  public String getSort () {
    return getParameterValue("sort");
  }
  
  public void setRank (String rank) {
    setParameter(new CommandParameter("rank","=",rank));
  }
  
  public String getRank () {
    return getParameterValue("rank");
  }
  
  public void setMergekey (String mergekey) {
    setParameter(new CommandParameter("mergekey","=",mergekey));
  }
  
  public String getMergekey () {
    return getParameterValue("mergekey");
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
      logger.debug("Setting new single target filter for [" + targetName + "]");
      this.singleTargetFilter = new SingleTargetFilter(targetId,targetName);
      setParameter(new CommandParameter("filter","=",this.singleTargetFilter.getFilterExpression()));            
    }    
  }

  public SingleTargetFilter getSingleTargetFilter () {
    logger.debug("request to get the current single target filter " + singleTargetFilter);
    return singleTargetFilter;
  }
   
  /**
   * Removes the current target filter from the search
   * 
   */
  public String removeSingleTargetFilter () {
    logger.debug("Removing target filter " + singleTargetFilter.getFilterExpression());
    this.singleTargetFilter = null;
    removeParameter("filter");
    return null;
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
    logger.debug("Checking if target filter for [" + targetFilter.getTargetName() + "] is set.");
    return hasSingleTargetFilter() && targetFilter.equals(this.singleTargetFilter);
  }
    
  public SearchCommand copy () {
    SearchCommand newCommand = new SearchCommand();
    for (String parameterName : parameters.keySet()) {
      newCommand.setParameterInState(parameters.get(parameterName).copy());      
    }
    newCommand.singleTargetFilter = this.singleTargetFilter;
    return newCommand;
  }

  @Override
  public ServiceProxyCommand getSp() {
    return this;
  }

  @Override
  public boolean spOnly() {
    return false;
  }

}
