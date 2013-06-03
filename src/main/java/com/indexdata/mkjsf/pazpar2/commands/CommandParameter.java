package com.indexdata.mkjsf.pazpar2.commands;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

public class CommandParameter implements Serializable {

  private static Logger logger = Logger.getLogger(CommandParameter.class);
  
  private static final long serialVersionUID = 625502285668766749L;  
  String name = null;
  String operator = null;
  String value = null;
  List<Expression> expressions = new ArrayList<Expression>();
  private static List<String> nologparams = Arrays.asList("password");
  
  public CommandParameter (String name) {
    logger.trace("Instantiating command parameter '" + name + "'");
    this.name = name;
  }
  
  public CommandParameter (String name, String operator, String value, Expression... expressions) {
    logger.trace("Instantiating command parameter " + name + " with value [" + value + "] and expressions: [" + expressions + "]");
    this.name = name;
    this.operator = operator;
    this.value = value;
    for (Expression expr : expressions) {
      this.expressions.add(expr);
    }
  }
  
  public CommandParameter (String name, String operator, Expression... expressions) {
    logger.trace("Instantiating command parameter " + name + " with expressions: [" + expressions + "]");
    this.name = name;
    this.operator = operator;    
    for (Expression expr : expressions) {
      this.expressions.add(expr);
    }
  }


  public CommandParameter (String name, String operator, String value) {
    if (!nologparams.contains(name)) logger.trace("Instantiating command parameter '" + name + "' with String: [" + value + "]");    
    this.name = name;
    this.operator = operator;
    this.value = value;    
  }
  
  public CommandParameter (String name, String operator, int value) {
    logger.trace("Instantiating command parameter '" + name + "' with int: [" + value + "]");
    this.name = name;
    this.operator = operator;
    this.value = value+"";    
  }

  
  public String getName () {
    return name;
  }
  
  public List<Expression> getExpressions () {
    return expressions;
  }
  
  public List<Expression> getExpressions(String... expressionFields) {
    List<String> requestedFields = Arrays.asList(expressionFields);
    List<Expression> exprs = new ArrayList<Expression>();
    for (Expression expr : expressions) {
      if (requestedFields.contains(expr.getField())) {
        exprs.add(expr);
      }
    }
    return exprs;
  }
  
  public void addExpression(Expression expression) {
    logger.debug("Adding expression [" + expression + "] to '" + name + "'");
    this.expressions.add(expression);
  }
  
  public void removeExpression(Expression expression) {
    for (Expression expr : expressions) {
      if (expr.toString().equals(expression.toString())) {
        expressions.remove(expr);
        break;
      }
    }    
  }
  
  public void removeExpressionsAfter (Expression expression, String... expressionFields) {
    List<String> exprFieldsToRemove = Arrays.asList(expressionFields);
    int fromIdx = 0;    
    for (Expression expr : expressions) {      
      fromIdx++;
      if (expr.toString().equals(expression.toString())) {        
        break;
      }      
    }
    if (fromIdx<expressions.size()) {      
      Iterator<Expression> candidatesForRemoval = expressions.subList(fromIdx, expressions.size()).iterator();
      while (candidatesForRemoval.hasNext()) {
        Expression exp = candidatesForRemoval.next();
        if (exprFieldsToRemove.contains(exp.getField())) {
          expressions.remove(exp);
        }
      }
    }
  }
  
  public void removeExpressions (String... expressionFields) {
    List<String> fieldsToRemove = Arrays.asList(expressionFields);
    Iterator<Expression> i = expressions.iterator();
    while (i.hasNext()) {
       Expression expr = i.next(); 
       if (fieldsToRemove.contains(expr.getField())) {
         logger.trace("Removing expression: " + expr.toString());
         i.remove();
       }
    }
  }
  
  public boolean hasOperator() {
    return operator != null;
  }
  
  public boolean hasValue() {
    return value != null && value.length()>0;
  }
  
  public boolean hasExpressions() {
    return expressions.size()>0;
  }
  
  public boolean hasExpressions(String expressionField) {    
    for (Expression expr : expressions) {
      if (expr.getField().equals(expressionField)) {
        return true;
      }
    }     
    return false;    
  }
  
  public String getEncodedQueryString () {
    try {
      return name + operator + URLEncoder.encode(getValueWithExpressions(),"UTF-8");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
      return null;
    }
  }
    
  public String getSimpleValue() {    
    return value; 
  }
  
  public String getValueWithExpressions () {
    StringBuilder completeValue = new StringBuilder((value==null ? "" : value));    
    for (Expression expr : expressions) {      
      completeValue.append(" and " + expr.toString());
    }
    return completeValue.toString();    
  }  
  
  @Override
  public boolean equals (Object otherParameter) {
    return
        ((otherParameter instanceof CommandParameter)
         && this.getValueWithExpressions().equals(((CommandParameter) otherParameter).getValueWithExpressions()));
  }
  
  @Override
  public int hashCode () {
    return getValueWithExpressions().hashCode();
  }
  
  public String toString() {
    return getValueWithExpressions();
  }
  
  public CommandParameter copy() {
    logger.trace("Copying parameter '"+ name + "' for modification");
    CommandParameter newParam = new CommandParameter(name);
    newParam.value = this.value;
    newParam.operator = this.operator;
    for (Expression expr : expressions) {
      newParam.addExpression(expr.copy());      
    }
    return newParam;
  }
  
}
