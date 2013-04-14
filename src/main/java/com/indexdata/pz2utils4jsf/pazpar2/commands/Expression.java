package com.indexdata.pz2utils4jsf.pazpar2.commands;

import com.indexdata.pz2utils4jsf.pazpar2.commands.Expression;

public class Expression {
  
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
