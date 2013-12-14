package com.indexdata.mkjsf.pazpar2.commands;

import org.apache.log4j.Logger;

/**
 * Represents a filter parameter as it applies to the Pazpar2 search command
 * 
 * <p>A filter parameter consists of one or more expressions separated by commas.</p> 
 *  
 * @author Niels Erik
 *
 */
public class FilterParameter extends CommandParameter {

  private static final long serialVersionUID = -3697328835895528654L;
  private static Logger logger = Logger.getLogger(FilterParameter.class);

  public FilterParameter(String name) {
    super(name);
  }

  public FilterParameter(Expression... expressions) {
    super("filter", "=", expressions);
  }

  public String getValueWithExpressions () {
    StringBuilder completeValue = new StringBuilder("");
    boolean first = true;
    for (Expression expr : expressions) {      
      if (!first) 
        completeValue.append(",");
      else 
        first=false;      
      completeValue.append(pz2escape(expr.toString()));
    }
    return completeValue.toString();    
  }  
  
  /**
   * Escapes backslash (\), comma (,) and pipe (|) from an expression string.
   *  
   * @param expressionString
   * @return escaped expressionString
   */
  private String pz2escape (String expressionString) {
    String escaped = expressionString.replaceAll("\\\\","\\\\\\\\");
    escaped = escaped.replaceAll(",","\\\\,");
    return escaped;
  }
  
  public FilterParameter copy() {
    logger.trace("Copying parameter '"+ name + "' for modification");
    FilterParameter newParam = new FilterParameter(name);
    newParam.value = this.value;
    newParam.operator = this.operator;
    for (Expression expr : expressions) {
      newParam.addExpression(expr.copy());      
    }
    return newParam;
  }


}
