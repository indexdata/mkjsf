package com.indexdata.mkjsf.pazpar2.commands;

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
