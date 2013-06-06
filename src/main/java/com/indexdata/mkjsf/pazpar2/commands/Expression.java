package com.indexdata.mkjsf.pazpar2.commands;

import java.io.Serializable;

import org.apache.log4j.Logger;

/**
 * Represents a complex command parameter value, in form of an expression with 
 * an equality operator
 * <p>
 * An expression consist of a left-of-operator field or key, an equality operator (= or ~), 
 * a right-of-operator value, and optionally a label describing the value for UI display.
 * </p> 
 * <p>Examples:</p>
 * <ul>
 *  <li><code>pz:id=1234</code> "My Target"</li>
 *  <li><code>category~libcatalog</code> "Library Catalogs"</li>
 *  <li><code>author="Steinbeck, John"</code></li>
 * </ul>
 * @author Niels Erik
 *
 */
public class Expression implements Serializable {
  
  private static final long serialVersionUID = -751704027842027769L;
  private static Logger logger = Logger.getLogger(Expression.class);
  String leftEntity;
  String operator;
  String rightEntity;
  String label;  
  
  /**
   * Instantiates an expression with a label
   * 
   * @param leftEntity left-of-operator field name (or 'key')
   * @param operator an equality operator
   * @param rightEntity right-of-operator value
   * @param label to be used for display, for instance in a UI control that adds or removes the expression
   *  from a command parameter
   */
  public Expression (String field, String operator, String value, String label) {
    this.leftEntity = field;
    this.operator = operator;
    this.rightEntity = value;    
    this.label = label;
  }
  
  /**
   * Instantiates an expression by parsing the provided expression string, which must be
   * on the form {name}({=}or{~}){value}.
   * <p>
   * Currently only '=' and '~' are recognized as operators
   * </p>
   * 
   * @param expressionString
   */
  public Expression (String expressionString) {
    String[] parts = expressionString.split("[=~]");
    this.leftEntity = parts[0];
    this.operator = expressionString.contains("=") ? "=" : "~";
    this.rightEntity = parts[1];
    this.label=rightEntity;
  }
  
  /** 
   * Clones the expression
   * 
   * @return a clone of this expression
   */
  public Expression copy() {
    logger.trace("Copying " + this.toString());
    return new Expression(leftEntity, operator, rightEntity, label);
  }
  
  public String toString() {
    return leftEntity + operator + rightEntity;
  }
  
  /**
   * Returns the label describing the value of the expression or,
   * if no label was provided, the value itself.
   * 
   * @return label or right-of-operator value if no label provided
   */
  public String getLabel() {
    return label;
  }
  
  /**
   * Returns the left-of-operator field (or name or key).
   * 
   * @return entity left of operator
   */
  public String getField () {
    return leftEntity;
  }
  
  /**
   * Returns the operator 
   * 
   * @return the operator of the expression
   */
  public String getOperator() {
    return operator;
  }
  
  /**
   * Returns the right-of-operator value of the expression
   * 
   * @return entity right of operator
   */
  public String getValue() {
    return rightEntity;
  }
  
}
