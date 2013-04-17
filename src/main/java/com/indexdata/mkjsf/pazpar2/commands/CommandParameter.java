package com.indexdata.mkjsf.pazpar2.commands;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.indexdata.mkjsf.pazpar2.commands.CommandParameter;

public class CommandParameter implements Serializable {

  private static Logger logger = Logger.getLogger(CommandParameter.class);
  
  private static final long serialVersionUID = 625502285668766749L;  
  String name = null;
  String operator = null;
  String value = null;
  Map<String,Expression> expressions = new HashMap<String,Expression>();
  private static List<String> nologparams = Arrays.asList("password");
  
  public CommandParameter (String name) {
    logger.debug("Instantiating command parameter '" + name + "'");
    this.name = name;
  }
  
  public CommandParameter (String name, String operator, String value, Expression... expressions) {
    logger.debug("Instantiating command parameter " + name + " with expressions: [" + expressions + "]");
    this.name = name;
    this.operator = operator;
    this.value = value;
    for (Expression expr : expressions) {
      this.expressions.put(expr.toString(), expr);
    }
  }

  public CommandParameter (String name, String operator, String value) {
    if (!nologparams.contains(name)) logger.debug("Instantiating command parameter '" + name + "' with String: [" + value + "]");    
    this.name = name;
    this.operator = operator;
    this.value = value;    
  }
  
  public CommandParameter (String name, String operator, int value) {
    logger.debug("Instantiating command parameter '" + name + "' with int: [" + value + "]");
    this.name = name;
    this.operator = operator;
    this.value = value+"";    
  }

  
  public String getName () {
    return name;
  }
  
  public Map<String,Expression> getExpressions () {
    return expressions;
  }
  
  public void addExpression(Expression expression) {
    logger.debug("Adding expression [" + expression + "] to '" + name + "'");
    this.expressions.put(expression.toString(),expression);
  }
  
  public void removeExpression(Expression expression) {
    this.expressions.remove(expression.toString());
  }
  
  
  public boolean hasOperator() {
    return operator != null;
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
    for (String key : expressions.keySet()) {      
      completeValue.append(" and " + expressions.get(key));
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
    logger.debug("Copying parameter '"+ name + "' for modification");
    CommandParameter newParam = new CommandParameter(name);
    newParam.value = this.value;
    newParam.operator = this.operator;
    for (String key : expressions.keySet()) {
      newParam.addExpression(expressions.get(key).copy());      
    }
    return newParam;
  }
  
}
