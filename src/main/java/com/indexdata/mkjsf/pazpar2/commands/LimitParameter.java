package com.indexdata.mkjsf.pazpar2.commands;

import org.apache.log4j.Logger;

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
      completeValue.append(pz2escape(expr.toString()));
      logger.trace("valueWithExpressions so far: [" + completeValue + "]");
    }
    return completeValue.toString();    
  }
  
  public String pz2escape (String expressionString) {
    String escaped = expressionString.replaceAll("\\\\","\\\\\\\\");
    escaped = escaped.replaceAll(",","\\\\,");
    escaped = escaped.replaceAll("\\|", "\\\\|");
    return escaped;
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
