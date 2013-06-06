package com.indexdata.mkjsf.pazpar2.commands;

/**
 * Represents a query parameter as it applies to the Pazpar2 search command
 * 
 * <p>A query parameter can consist of a term value and/or one or more expressions 
 * separated by boolean operators.</p>
 * 
 * <p>A complex query can be represented in the object as either one long string 
 * set by <code>setQuery(string)</code> or as a series of expressions set by 
 * <code>setQueryExpression(...)</code> (or a combination of the two). The difference
 * between the two approaches would be the option of easily removing individual 
 * expressions again or otherwise treat them has separate entities in the UI.</p>
 * 
 * @author Niels Erik
 *
 */
public class QueryParameter extends CommandParameter {

  private static final long serialVersionUID = -3649052232241100927L;
  private String booleanOperator = "AND";

  public QueryParameter(String name) {
    super(name);
  }

  public QueryParameter(String name, String operator, String value,
      Expression... expressions) {
    super(name, operator, value, expressions);
  }

  public QueryParameter(String name, String operator, Expression... expressions) {
    super(name, operator, expressions);
  }

  public QueryParameter(String name, String operator, String value) {
    super(name, operator, value);
  }

  public QueryParameter(String name, String operator, int value) {
    super(name, operator, value);
  }
  
  public void setBooleanOperator (String operator) {
    this.booleanOperator = operator;
  }
  
  public String getValueWithExpressions () {
    StringBuilder completeValue = new StringBuilder((value==null ? "" : value));
    boolean first = true;
    for (Expression expr : expressions) {
      if (value == null && first) {
        first = false;
        completeValue.append(expr.toString());
      } else {
        completeValue.append(" "+booleanOperator+" " + expr.toString());
      }
    }
    return completeValue.toString();    
  }  
  
  public QueryParameter copy() {    
    QueryParameter newParam = new QueryParameter(name);
    newParam.value = this.value;
    newParam.operator = this.operator;
    newParam.booleanOperator = this.booleanOperator;
    for (Expression expr : expressions) {
      newParam.addExpression(expr.copy());      
    }
    return newParam;
  }


}
