package com.indexdata.pz2utils4jsf.pazpar2.commands;

import javax.enterprise.context.SessionScoped;

import org.apache.log4j.Logger;

import com.indexdata.pz2utils4jsf.pazpar2.state.StateManager;

@SessionScoped
public class SearchCommand extends Pazpar2Command {
  
  private static final long serialVersionUID = -1888520867838597236L;
  private static Logger logger = Logger.getLogger(SearchCommand.class);
  
  public SearchCommand(StateManager stateMgr) {
    super("search",stateMgr);
  }
  
  public void setSession (String sessionId) {
    setParameter(new CommandParameter("session","=",sessionId));
  }
  
  public void setQuery(String query) {
    logger.info("writing query: " + query);
    setParameter(new CommandParameter("query","=",query));
  }
  
  public String getQuery () {
    logger.info("retrieving query");
    return getParameter("query") == null ? null  : getParameter("query").getValueWithExpressions();
  }
  
  public void setFilter(String filterExpression) {
    setParameter(new CommandParameter("filter","=",filterExpression));
  }
  
  public String getFilter() {
    return getParameter("filter") == null ? null : getParameter("filter").getValueWithExpressions();
  }

  
  public void setLimit (String limit) {
    
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
    return newCommand;
  }


}
