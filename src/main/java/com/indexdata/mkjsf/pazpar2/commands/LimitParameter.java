package com.indexdata.mkjsf.pazpar2.commands;

import org.apache.log4j.Logger;

/**
 * Represents a limit parameter as it applies to the Pazpar2 search command
 * 
 * <p>A limit parameter consists of one or more expressions separated by commas.</p> 
 *  
 * @author Niels Erik
 *
 */
public class LimitParameter extends CommandParameter {

  private static final long serialVersionUID = -1410691265213389826L;
  private static Logger logger = Logger.getLogger(LimitParameter.class);

  public LimitParameter(String name) {
    super(name);
  }

  public LimitParameter(Expression... expressions) {
    super("limit", "=", expressions);
  }

  public String getValueWithExpressions () {
    StringBuilder completeValue = new StringBuilder("");
    boolean first = true;
    for (Expression expr : expressions) {      
      if (!first) 
        completeValue.append(",");
      else 
        first=false;      
      completeValue.append(expr.getField() + expr.getOperator() + expr.getValue());
      logger.trace("valueWithExpressions so far: [" + completeValue + "]");
    }
    return completeValue.toString();    
  }
    
  private String pz2escape (String str) {
    return str.replaceAll("[~|,=\\\\]","\\\\$0");
  }

  
  public LimitParameter copy() {
    logger.trace("Copying parameter '"+ name + "' for modification");
    LimitParameter newParam = new LimitParameter(name);
    newParam.value = this.value;
    newParam.operator = this.operator;
    for (Expression expr : expressions) {
      newParam.addExpression(expr.copy());      
    }
    return newParam;
  }


}
