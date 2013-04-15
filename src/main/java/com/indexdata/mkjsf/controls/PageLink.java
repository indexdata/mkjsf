package com.indexdata.mkjsf.controls;

import java.io.Serializable;

import com.indexdata.mkjsf.controls.ResultsPager;

public class PageLink implements Serializable {

  private static final long serialVersionUID = -468888598965842949L;
  String text = "";
  int page = 0;
  ResultsPager pager;
  public PageLink(String text, int page, ResultsPager pager) {
    this.text = text;
    this.page = page;
    this.pager = pager;
  }
  
  public boolean isLink() {
    return page>0;
  }
  
  public boolean isCurrent() {
    return (pager.getCurrentPageNum()==page);
  }
  
  public String getText() {
    return text;
  }
  
  public int getPage() {
    return page;
  }
  
  public int getStart() {
    return pager.getPageSize()*(page-1);
  }
}
