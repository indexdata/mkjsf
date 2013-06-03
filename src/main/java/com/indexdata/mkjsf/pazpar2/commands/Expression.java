package com.indexdata.mkjsf.pazpar2.commands;

import java.io.Serializable;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import com.indexdata.mkjsf.pazpar2.commands.Expression;

public class Expression implements Serializable {
  
  private static final long serialVersionUID = -751704027842027769L;
  private static Logger logger = Logger.getLogger(Expression.class);
  String leftEntity;
  String operator;
  String rightEntity;
  String label;  
  
  public Expression (String leftEntity, String operator, String rightEntity, String label) {
    this.leftEntity = leftEntity;
    this.operator = operator;
    this.rightEntity = rightEntity;    
    this.label = label;
  }
  
  public Expression (String expressionString) {
    StringTokenizer tokenizer = new StringTokenizer(expressionString,"=");
    this.leftEntity = tokenizer.nextToken();
    this.operator = "=";
    this.rightEntity = tokenizer.nextToken();
    this.label=rightEntity;
  }
  
  public Expression copy() {
    logger.trace("Copying " + this.toString());
    return new Expression(leftEntity, operator, rightEntity, label);
  }
  
  public String toString() {
    return leftEntity + operator + rightEntity;
  }
  
  public String getLabel() {
    return label;
  }
  
  public String getField () {
    return leftEntity;
  }
  
  public String getOperator() {
    return operator;
  }
  
  public String getValue() {
    return rightEntity;
  }
  
}
