package com.indexdata.mkjsf.pazpar2.commands;

import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import org.apache.log4j.Logger;

import com.indexdata.mkjsf.pazpar2.Pz2Service;
import com.indexdata.mkjsf.pazpar2.commands.sp.ServiceProxyCommand;
import com.indexdata.mkjsf.pazpar2.data.ResponseDataObject;

@SessionScoped @Named
public class SearchCommand extends Pazpar2Command implements ServiceProxyCommand {
  
  private static final long serialVersionUID = -1888520867838597236L;
  private static Logger logger = Logger.getLogger(SearchCommand.class);  
    
  public SearchCommand() {
    super("search");
  }
  
  public ResponseDataObject run() {
    logger.info("Running " + getCommandName());
    Pz2Service.get().getStateMgr().hasPendingStateChange("search",false);
    Pz2Service.get().getPzresp().resetSearchAndBeyond();
    Pz2Service.get().getPzreq().getRecord().removeParametersInState();        
    Pz2Service.get().getPzreq().getShow().setParameterInState(new CommandParameter("start","=",0));    
    Pz2Service.get().getSearchClient().setSearchCommand(this);
    return super.run();
  }
    
  public void setQuery(String query) {    
    setParameter(new QueryParameter("query","=",query));
  }
  
  public void setBooleanOperatorForQuery(String operator) {
    Pazpar2Command copy = this.copy();
    ((QueryParameter) getParameter("query")).setBooleanOperator(operator);
    checkInState(copy);
  }
  
  public String getQuery () {    
    return getParameter("query") == null ? null  : getParameter("query").getSimpleValue();
  }
  
  public String getExtendedQuery () {    
    return getParameter("query") == null ? null  : getParameter("query").getValueWithExpressions();
  }
  
  public void setFilter(String filterExpression) {
    if (filterExpression != null && filterExpression.length()>0) {
      if (filterExpression.split("[=~]").length==1) {
        removeFilters(filterExpression.split("[=~]")[0]);
      } else if (filterExpression.split("[=~]").length==2) {
        setParameter(new FilterParameter(new Expression(filterExpression)));
      } else {
        logger.error("Could not parse filter expression [" + filterExpression + "]");
      }
    }
  }
  
  public void setFilter(String field, String operator, String value, String label) {
    setParameter(new FilterParameter(new Expression(field,operator,value,label)));
  }
  
  public boolean hasFilterExpression(String... expressionFields) {
    logger.trace("Checking for filter expression for " + Arrays.deepToString(expressionFields));
    for (String field : expressionFields) {
      if (getFilterExpressions(field) != null && getFilterExpressions(field).size()>0) {
        logger.trace("Filter expression found (" + field + ")");
        return true;
      }  
    }
    logger.trace("No filter expressions found");
    return false;
  }

  
  public String getFilter() {
    return getParameter("filter") == null ? null : ((FilterParameter)getParameter("filter")).getValueWithExpressions();
  }
  
  public Expression getOneFilterExpression(String expressionField) {
    List<Expression> exprs = getFilterExpressions(expressionField);
    if (exprs != null && exprs.size()>0) {
      if (exprs.size()>1) {
        logger.warn("More that one filter expression found for [" + expressionField + "] but only asked to return the first one");
      }
      return exprs.get(0);
    } else {
      return null;
    }    
  }

  
  public List<Expression> getFilterExpressions() {
    return getParameter("filter").getExpressions();
  }
    
  public List<Expression> getFilterExpressions(String... expressionFields) {
    logger.trace("Checking for filter parameter");
    if (parameters.get("filter")!=null) {
      logger.trace("Found");
      return getParameter("filter").getExpressions(expressionFields);
    } else {
      logger.trace("Not found");
      return null;
    }
  }
  
  public boolean hasFilter () {
    return getFilter().length()>0;
  }
  
  public void addFilter(String field, String operator, String value, String label) {
    if (getParameter("filter") == null) {
      setFilter(field + operator + value);
    } else {
      addExpression("filter",new Expression(field,operator,value,(label != null ? label : value)));
    }
  }
  
  public void removeFilters () {
    removeParameter("filter");
  }
  
  public void removeFilter(String field, String operator, String value) {
    removeExpression("filter",new Expression(field, operator, value, null));
  }
  
  public void removeFilters(String... fieldsToRemove) {    
    removeExpressions("filter",fieldsToRemove);    
  }  
    
  public void removeFiltersAfter(String field, String operator, String value, String... fieldsToRemove) {     
    removeExpressionsAfter("filter",new Expression(field,operator,value,null),fieldsToRemove);    
  }

  public void setLimit (String limitExpression) {   
    if (limitExpression != null && limitExpression.length()>0) {
      setParameter(new LimitParameter(new Expression(limitExpression)));
    }
  }
  
  public void setLimit(String field, String operator, String value, String label) {
    setParameter(new LimitParameter(new Expression(field,operator,value,label)));
  }
      
  public String getLimit () {
    return getParameter("limit") == null ? null : ((FilterParameter)getParameter("limit")).getValueWithExpressions();    
  }
    
  public boolean hasLimitExpression(String... expressionFields) {
    logger.trace("Checking for limit expression for " + Arrays.deepToString(expressionFields));
    for (String field : expressionFields) {
      if (getLimitExpressions(field) != null && getLimitExpressions(field).size()>0) {
        logger.trace("Limit expression found (" + field + ")");
        return true;
      }  
    }
    logger.trace("No limit expressions found");
    return false;
  }
  
  public Expression getOneLimitExpression(String expressionField) {
    List<Expression> exprs = getLimitExpressions(expressionField);
    if (exprs != null && exprs.size()>0) {
      if (exprs.size()>1) {
        logger.warn("More that one limit expression found for [" + expressionField + "] but only asked to return the first one");
      }
      return exprs.get(0);
    } else {
      return null;
    }    
  }
  
  public List<Expression> getLimitExpressions() {
    return getParameter("limit").getExpressions();
  }
  
  public List<Expression> getLimitExpressions(String... expressionFields) {
    logger.trace("Checking for limit parameter");
    if (parameters.get("limit")!=null) {
      logger.trace("Found");
      return getParameter("limit").getExpressions(expressionFields);
    } else {
      logger.trace("Not found");
      return null;
    }
  }
  
  public void addLimit(String field, String operator, String value, String label) {
    if (getParameter("limit") == null) {
      setLimit(field, operator, value, label);
    } else {
      addExpression("limit",new Expression(field,operator,value,label));      
    }
  }
  
  public void removeLimits() {
    removeParameter("limit");
  }
  
  public void removeLimits(String... fieldsToRemove) {    
    removeExpressions("limit",fieldsToRemove);    
  }
  
  public void removeLimit(String field, String operator, String value) {
    removeExpression("limit",new Expression(field, operator, value, null));    
  }
  
  public void removeLimitsAfter(String field, String operator, String value, String... fieldsToRemove) {     
    removeExpressionsAfter("limit",new Expression(field,operator,value,null),fieldsToRemove);    
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
   * Sets a facet, in CQL, to restrict the current results
   * 
   * @param facetKey  i.e.  'au' for author
   * @param term  i.e. 'Dickens, Charles'
   */
  public void setFacet(String facetKey, String term) {
    if (term != null && term.length()>0) { 
      addExpression("query", new Expression(facetKey,"=",term,null));                  
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
   * Removes a facet set by setFacet(...)
   * 
   * Will not remove facets set by setFacetOnQuery(...)
   *  
   * @param facetKey i.e. 'au' for author
   * @param term i.e. 'Dickens, Charles'
   */
  public void removeFacet(String facetKey, String term) {
    if (getParameter("query") != null) {
      removeExpression("query",new Expression(facetKey,"=",term,null));
    }
  }
      
  public SearchCommand copy () {
    SearchCommand newCommand = new SearchCommand();
    for (String parameterName : parameters.keySet()) {
      newCommand.setParameterInState(parameters.get(parameterName).copy());      
    }
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
