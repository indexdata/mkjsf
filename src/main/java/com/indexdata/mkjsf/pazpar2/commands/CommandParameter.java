package com.indexdata.mkjsf.pazpar2.commands;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Represents a Pazpar2 command parameter with a name, an operator, 
 * a simple value and/or one or more complex values (expressions).
 * <p>Examples</p>
 * <ul>
 *  <li>{name}{=}{value}</li>
 *  <li>{name}{=}{value} AND {expr1=value1} AND {expr2=value2}</li>
 *  <li>{name}{=}{expr1~value1},{expr2~value2}</li> 
 * </ul> 
 * @author Niels Erik
 *
 */
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
  
  /**
   * Instantiates a parameter with a simple value and one or more expressions
   * 
   * @param name
   * @param operator
   * @param value
   * @param expressions
   */
  public CommandParameter (String name, String operator, String value, Expression... expressions) {
    logger.trace("Instantiating command parameter " + name + " with value [" + value + "] and expressions: [" + expressions + "]");
    this.name = name;
    this.operator = operator;
    this.value = value;
    for (Expression expr : expressions) {
      this.expressions.add(expr);
    }
  }
  
  /**
   * Instantiates a parameter with one or more expressions
   * @param name
   * @param operator
   * @param expressions
   */
  public CommandParameter (String name, String operator, Expression... expressions) {
    logger.trace("Instantiating command parameter " + name + " with expressions: [" + expressions + "]");
    this.name = name;
    this.operator = operator;    
    for (Expression expr : expressions) {
      this.expressions.add(expr);
    }
  }


  /**
   * Instantiates a parameter with a simple value
   * @param name
   * @param operator
   * @param value
   */
  public CommandParameter (String name, String operator, String value) {
    if (!nologparams.contains(name)) logger.trace("Instantiating command parameter '" + name + "' with String: [" + value + "]");    
    this.name = name;
    this.operator = operator;
    this.value = value;    
  }
  
  /**
   * Instantiates a parameter with a numeric value
   * @param name
   * @param operator
   * @param value
   */
  public CommandParameter (String name, String operator, int value) {
    logger.trace("Instantiating command parameter '" + name + "' with int: [" + value + "]");
    this.name = name;
    this.operator = operator;
    this.value = value+"";    
  }

  /**
   * Returns the name (left of operator) of this parameter
   * 
   * @return name (left of operator) of this parameter
   */
  public String getName () {
    return name;
  }
  
  /**
   * Returns a list of all current expressions
   * 
   * @return a list of all current expressions
   */
  public List<Expression> getExpressions () {
    return expressions;
  }

  /**
   * Returns expressions selected by their left-hand keys - as in 'expressionField=value'.
   * <p>
   * If the parameter has expressions expr1=x,expr2=y,expr3=z,expr1=u then invoking this method 
   * with {"expr1","expr3"} would return expr1=x,expr3=z,expr1=u but not expr2=y.   
   * </p>
   * @param expressionFields The expression types to return
   * @return a list of expressions with the given keys to the left of the operator
   * 
   */
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
  
  /**
   * Adds an expression to the end of the list of current expressions (if any)
   * 
   * @param expression to add
   */
  public void addExpression(Expression expression) {
    logger.debug("Adding expression [" + expression + "] to '" + name + "'");
    this.expressions.add(expression);
  }
  
  /**
   * Removes a single expression identified by all its characteristics
   * 
   * @param expression to remove
   */
  public void removeExpression(Expression expression) {
    for (Expression expr : expressions) {
      if (expr.toString().equals(expression.toString())) {
        expressions.remove(expr);
        break;
      }
    }    
  }
  
  /**
   * Removes all expressions that appear after the provided expression and that 
   * have the given keys to the left of their operators - as in 'expressionField=value'.
   * <p>
   * This method is intended for bread crumb-like UI controls
   * </p>
   * @param expression The expression to use a starting point for removal (not inclusive)
   * @param expressionFields The expression fields to remove
   */
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
  
  /**
   * Removes expressions selected by their left-of-operator fields/keys - as in 'expressionField=value'.
   * <p>
   * If the parameter has expressions expr1=x,expr2=y,expr3=z,expr1=u then invoking this method 
   * with {"expr1","expr3"} would remove expr1=x,expr3=z and expr1=u but leave expr2=y.   
   * </p>
   * @param expressionFields The expression types (by field) to remove
   * @return a list of expressions with the given left-of-operator keys
   * 
   */
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

  /**
   *   
   * @return true if an operator was defined for this parameter yet
   */
  public boolean hasOperator() {
    return operator != null;
  }
  
  /**
   * Returns true if this parameter has a simple value
   * 
   * @return true if this parameter has a simple value
   */
  public boolean hasValue() {
    return value != null && value.length()>0;
  }
  
  /**
   * Returns true if this parameter has expressions (complex values)
   * 
   * @return true if this parameter has expressions (complex values)
   */
  public boolean hasExpressions() {
    return expressions.size()>0;
  }
  
  /**
   * Returns true if this parameter has expressions of the given type,
   * that is, expressions where the left-of-operator key equals 'expressionField'
   * 
   * @param expressionField the type of expression to look for
   * @return true if this parameter has expressions of the given type,
   *  that is, expressions where the left-of-operator key equals 'expressionField'
   */
  public boolean hasExpressions(String expressionField) {    
    for (Expression expr : expressions) {
      if (expr.getField().equals(expressionField)) {
        return true;
      }
    }     
    return false;    
  }
  
  /**
   * Returns a URL encoded string of this parameter with name, operator, simple value and/or expressions
   * 
   * @return URL encoded string of this parameter with name, operator, simple value and/or expressions
   */
  public String getEncodedQueryString () {
    try {
      return name + operator + URLEncoder.encode(getValueWithExpressions(),"UTF-8");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
      return null;
    }
  }
    
  /**
   * Returns the simple parameter value or null if no simple value was set for this parameter
   * 
   * @return the simple parameter value, null if no simple value was set for this parameter 
   */
  public String getSimpleValue() {    
    return value; 
  }
  
  /**
   * Returns the simple parameter value and/or any expressions, separated by 'AND'
   * 
   * @return the simple parameter value and/or any expressions separated by 'AND'
   */
  public String getValueWithExpressions () {
    StringBuilder completeValue = new StringBuilder((value==null ? "" : value));
    boolean first=true;
    for (Expression expr : expressions) {      
      if (value == null && first) {
        first = false;
        completeValue.append(expr.toString());
      } else {
        completeValue.append(" AND " + expr.toString());
      }
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
  
  /**
   * Clones the CommandParameter
   * 
   * @return a deep, detached clone of this command parameter, for copying 
   * a parameter to a new state.  
   */
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
