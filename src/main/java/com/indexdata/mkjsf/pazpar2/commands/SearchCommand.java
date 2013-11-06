package com.indexdata.mkjsf.pazpar2.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import org.apache.log4j.Logger;

import com.indexdata.mkjsf.pazpar2.Pz2Service;
import com.indexdata.mkjsf.pazpar2.commands.sp.SearchCommandSp;
import com.indexdata.mkjsf.pazpar2.commands.sp.ServiceProxyCommand;
import com.indexdata.mkjsf.pazpar2.data.ResponseDataObject;

/**
 * <b><code>search</code></b> Pazpar2 command, referenced as: <code>pzreq.search</code> 
 * 
 * @author Niels Erik
 *
 */
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

  /**
   * Sets the <code>query</code> parameter. See Pazpar2 documentation for details.
   */  
  public void setQuery(String query) {    
    setParameter(new QueryParameter("query","=",query));
  }
  
  public void setBooleanOperatorForQuery(String operator) {
    Pazpar2Command copy = this.copy();
    ((QueryParameter) getParameter("query")).setBooleanOperator(operator);
    checkInState(copy);
  }
  
  /** 
   * Returns the simple part of the <code>query</code> parameter value, excluding parts that 
   * were added as expressions (that is, not set with <code>setQuery()</code>).
   */
  public String getQuery () {    
    return getParameter("query") == null ? null  : ((QueryParameter)getParameter("query")).getSimpleValue();
  }

  /** 
   * Returns the complete <code>query</code> parameter value, including expressions.
   */
  public String getExtendedQuery () {    
    return getParameter("query") == null ? null  : ((QueryParameter)getParameter("query")).getValueWithExpressions();
  }
    
  /**
   * Sets the <code>filter</code> parameter. See Pazpar2 documentation for details.
   */  
  public void setFilter(String compoundExpression) {
    if (compoundExpression != null && compoundExpression.length()>0) {
      String[] subExpressions = compoundExpression.split(",");
      for (int i=0; i<subExpressions.length; i++) {
        if (subExpressions[i].split("[=~]").length==1) {
          removeFilters(subExpressions[i].split("[=~]")[0]);
        } else if (subExpressions[i].split("[=~]").length==2) {
          if (getParameter("filter") == null) {
            setParameter(new FilterParameter(new Expression(subExpressions[i])));
          } else {
            if (getParameter("filter").hasExpressions(subExpressions[i].split("[=~]")[0])) {
              getParameter("filter").removeExpressions(subExpressions[i].split("[=~]")[0]);
            }
            getParameter("filter").addExpression(new Expression(subExpressions[i]));
          }
        } else {
          logger.error("Could not parse filter expression [" + subExpressions[i] + "]");
        }
      }
    }
  }
  
  /**
   * Sets the <code>filter</code> parameter. See Pazpar2 documentation for details.
   */  
  public void setFilter(String field, String operator, String value, String label) {
    setParameter(new FilterParameter(new Expression(field,operator,value,label)));
  }

  /**
   * Checks if there are any filter expressions matching any of the given expressionFields
   * @param expressionFields expression fields (left-of-operator entities) to look for
   * @return true if expression(s) found with any of <code>expressionFields</code> 
   */
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

  
  /** 
   * Returns the <code>filter</code> parameter value.
   */
  public String getFilter() {
    return getParameter("filter") == null ? null : ((FilterParameter)getParameter("filter")).getValueWithExpressions();
  }
  
  /**
   * Returns the first filter expression of the given type
   * @param expressionField expression field (left-of-operator entity) to look for
   * @return the first filter expression found with the field <code>expressionField</code> or null if none found 
   */
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

  
  /**
   * Returns list of all filter expressions 
   */
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

  /**
   * Adds a filter expression with a label for display. The filter is added to the end
   * of an ordered list.  
   * 
   * @param field
   * @param operator
   * @param value
   * @param label
   */
  public void addFilter(String field, String operator, String value, String label) {
    if (getParameter("filter") == null) {
      setFilter(field + operator + value);
    } else {
      addExpression("filter",new Expression(field,operator,value,(label != null ? label : value)));
    }
  }

  /**
   * Clears the filter parameter
   */
  public void removeFilters () {
    removeParameter("filter");
  }

  /**
   * Removes a filter expression by exact attributes
   * 
   * @param field
   * @param operator
   * @param value
   */
  public void removeFilter(String field, String operator, String value) {
    removeExpression("filter",new Expression(field, operator, value, null));
  }
  
  /**
   * Removes all filter expressions matching a field listed in <code>fieldsToRemove</code>
   * @param fieldsToRemove
   */
  public void removeFilters(String... fieldsToRemove) {    
    removeExpressions("filter",fieldsToRemove);    
  }  

  /**
   * Removes filter expressions coming after the expression matching the provided filter expression, 
   * if they have a field listed in <code>fieldsToRemove</code>. To be used for bread crumb like UI 
   * controls.
   * 
   * @param field
   * @param operator
   * @param value
   * @param fieldsToRemove
   */
  public void removeFiltersAfter(String field, String operator, String value, String... fieldsToRemove) {     
    removeExpressionsAfter("filter",new Expression(field,operator,value,null),fieldsToRemove);    
  }

  /**
   * Sets the <code>limit</code> parameter. See Pazpar2 documentation for details.
   */  
  public void setLimit (String compoundExpression) {   
    if (compoundExpression != null && compoundExpression.length()>0) {
      String[] subExpressions = compoundExpression.split(",");
      for (int i=0; i<subExpressions.length; i++) {
        if (subExpressions[i].split("[=~]").length==1) {
          removeLimits(subExpressions[i].split("[=~]")[0]);
        } else if (subExpressions[i].split("[=~]").length==2) {
          if (getParameter("limit") == null) {
            setParameter(new LimitParameter(new Expression(subExpressions[i])));
          } else {
            if (getParameter("limit").hasExpressions(subExpressions[i].split("[=~]")[0])) {
              getParameter("limit").removeExpressions(subExpressions[i].split("[=~]")[0]);
            }
            getParameter("limit").addExpression(new Expression(subExpressions[i]));
          }
        } else {
          logger.error("Could not parse limit expression [" + subExpressions[i] + "]");
        }
      }
    }
  }
  
  /**
   * Sets the <code>limit</code> parameter including a label. See Pazpar2 documentation for details.
   */  
  public void setLimit(String field, String operator, String value, String label) {
    setParameter(new LimitParameter(new Expression(field,operator,value,label)));
  }
      
  /** 
   * Returns the <code>limit</code> parameter value.
   */
  public String getLimit () {
    return getParameter("limit") == null ? null : ((LimitParameter)getParameter("limit")).getValueWithExpressions();    
  }
    
  /**
   * Checks if there are any limit expressions matching any of the given expressionFields
   * @param expressionFields expression fields (left-of-operator entities) to look for
   * @return true if expression(s) found with any of <code>expressionFields</code> 
   */
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
  
  /**
   * Returns the first limit expression of the given type
   * @param expressionField expression field (left-of-operator entity) to look for
   * @return the first limit expression found with the field <code>expressionField</code> or null if none found 
   */
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
  
  /**
   * Return a list of all current limit expressions
   */
  public List<Expression> getLimitExpressions() {
    return getParameter("limit").getExpressions();
  }
  
  /**
   * Returns a list of limit expressions with fields that matches one of <code>expressionFields</code>
   * 
   * @param expressionFields limit expressions to look for
   */
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
  
  /**
   * Adds a limit expression with a label for display. The limit is added to the end
   * of an ordered list.  
   * 
   * @param field
   * @param operator
   * @param value
   * @param label
   */
  public void addLimit(String field, String operator, String value, String label) {
    if (getParameter("limit") == null) {
      setLimit(field, operator, value, label);
    } else {
      addExpression("limit",new Expression(field,operator,value,label));      
    }
  }
  
  /**
   * Clears the limit parameter
   */
  public void removeLimits() {
    removeParameter("limit");
  }
  
  /**
   * Removes all limit expressions that have fields as listed in <code>fieldsToRemove</code>
   * @param fieldsToRemove
   */
  public void removeLimits(String... fieldsToRemove) {    
    removeExpressions("limit",fieldsToRemove);    
  }
  
  /**
   * Removes a limit expression by exact attributes
   * 
   * @param field
   * @param operator
   * @param value
   */
  public void removeLimit(String field, String operator, String value) {
    removeExpression("limit",new Expression(field, operator, value, null));    
  }
  
  /**
   * Removes limit expressions coming after the provided limit expression, if they have a field listed in
   * <code>fieldsToRemove</code>. To be used for bread crumb like UI controls.
   * 
   * @param field
   * @param operator
   * @param value
   * @param fieldsToRemove
   */
  public void removeLimitsAfter(String field, String operator, String value, String... fieldsToRemove) {     
    removeExpressionsAfter("limit",new Expression(field,operator,value,null),fieldsToRemove);    
  }

        
  /**
   * Sets the <code>startrecs</code> parameter. See Pazpar2 documentation for details.
   */  
  public void setStartrecs (String startrecs) {
    setParameter(new CommandParameter("startrecs","=",startrecs));
  }
  
  /** 
   * Returns the <code>startrecs</code> parameter value.
   */
  public String getStartrecs () {
    return getParameterValue("startrecs");
  }
  
  /**
   * Sets the <code>maxrecs</code> parameter. See Pazpar2 documentation for details.
   */  
  public void setMaxrecs (String maxrecs) {
    setParameter(new CommandParameter("maxrecs","=",maxrecs));
  }
  
  /** 
   * Returns the <code>maxrecs</code> parameter value.
   */
  public String getMaxrecs () {
    return getParameterValue("maxrecs");
  }
  
  /**
   * Sets the <code>sort</code> parameter. See Pazpar2 documentation for details.
   */  
  public void setSort (String sort) {
    setParameter(new CommandParameter("sort","=",sort));
  }
  
  /** 
   * Returns the <code>sort</code> parameter value.
   */
  public String getSort () {
    return getParameterValue("sort");
  }
  
  /**
   * Sets the <code>rank</code> parameter. See Pazpar2 documentation for details.
   */  
  public void setRank (String rank) {
    setParameter(new CommandParameter("rank","=",rank));
  }
  
  /** 
   * Returns the <code>rank</code> parameter value.
   */
  public String getRank () {
    return getParameterValue("rank");
  }
  
  /**
   * Sets the <code>mergekey</code> parameter. See Pazpar2 documentation for details.
   */  
  public void setMergekey (String mergekey) {
    setParameter(new CommandParameter("mergekey","=",mergekey));
  }
  
  /** 
   * Returns the <code>mergekey</code> parameter value.
   */
  public String getMergekey () {
    return getParameterValue("mergekey");
  }
  
  
  /**
   * Adds an expression - for instance a facet criterion, with an optional label - to the query parameter
   * 
   * <p>Example:</p>
   * <ul>
   *  <li><code>{au}{=}{"Steinbeck, John"}{Steinbeck, John}</code>
   * </ul>
   */
  public void addQueryExpression(String field, String operator, String term, String label) {
    if (term != null && term.length()>0) { 
      addExpression("query", new Expression(field,operator,term,label));                  
    }            
  }
  
  /**
   * Removes a query expression - for instance a facet criterion - by its exact attributes
   * 
   * @param field
   * @param operator
   * @param value
   */
  public void removeQueryExpression(String field, String operator, String value) {
    removeExpression("query",new Expression(field, operator, value, null));    
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
      String currentQuery= getQuery();
      setParameter(new QueryParameter("query","=", currentQuery + " and " + facetExpression));      
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
    return new SearchCommandSp(this);
  }

  @Override
  public boolean spOnly() {
    return false;
  }

}
