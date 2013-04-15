package com.indexdata.mkjsf.pazpar2.data;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.indexdata.mkjsf.pazpar2.data.Pazpar2ResponseData;
import com.indexdata.mkjsf.pazpar2.data.TermListResponse;
import com.indexdata.mkjsf.pazpar2.data.TermResponse;

public class TermListResponse extends Pazpar2ResponseData {

  private static Logger logger = Logger.getLogger(TermListResponse.class);
  private static final long serialVersionUID = 3838585739723097393L;
  String name = "";
  List<TermResponse> terms = new ArrayList<TermResponse>();
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public List<TermResponse> getTerms() {    
    return terms;
  }
  
  public List<TermResponse> getTerms(int count) {
    List<TermResponse> firstTerms = new ArrayList<TermResponse>();
    for (int i=0; i<count && i<terms.size(); i++) {
      firstTerms.add(terms.get(i));
    }
    logger.trace("Returning " + count + " " + name + " terms: " + firstTerms);
    return firstTerms;
  }
  
  public void setTerms(List<TermResponse> terms) {
    this.terms = terms;
  }
  
  public void addTerm(TermResponse term) {
    terms.add(term);
  }  
  
  public String toString () {
    return terms.toString();
  }
  
}
