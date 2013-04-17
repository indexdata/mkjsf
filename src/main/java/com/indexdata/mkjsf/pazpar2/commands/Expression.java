package com.indexdata.mkjsf.pazpar2.commands;

import java.io.Serializable;

import com.indexdata.mkjsf.pazpar2.commands.Expression;

public class Expression implements Serializable {
  
  private static final long serialVersionUID = -751704027842027769L;
  String leftEntity;
  String operator;
  String rightEntity;
  public Expression (String leftEntity, String operator, String rightEntity) {
    this.leftEntity = leftEntity;
    this.operator = operator;
    this.rightEntity = rightEntity;    
  }
  
  public Expression copy() {
    return new Expression(leftEntity,operator,rightEntity);
  }
  
  public String toString() {
    return leftEntity + operator + rightEntity;
  }
  

}
