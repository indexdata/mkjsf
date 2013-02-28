package com.indexdata.pz2utils4jsf.controls;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.indexdata.pz2utils4jsf.controls.PageLink;
import com.indexdata.pz2utils4jsf.pazpar2.Pz2Session;
import com.indexdata.pz2utils4jsf.pazpar2.data.ShowResponse;

public class ResultsPager implements Serializable {

  private static final long serialVersionUID = 8854795222615583071L;
  private Pz2Session pz2session = null;
  private int pageRangeLength = 13;
  
  public ResultsPager(Pz2Session session) {
    this.pz2session = session;     
  }
  
  public ResultsPager(Pz2Session session, int pageRange) {
    this.pz2session = session;
    this.pageRangeLength = pageRange;
  }
  
  private boolean hasHits () {
    return (getShow().getMerged()>0);
  }
  
  public int getCurrentPageNum () {
    if (hasHits() && getShow().getNum()>0) {      
      return (getShow().getStart()/getShow().getNum())+1;
    } else {
      return 0;
    }
  }
  
  public int getPageSize() {
    return getShow().getNum();
  }
    
  public int getFirstDisplayedPageNum () {
    if (hasHits()) {
      if (getCurrentPageNum() - (pageRangeLength/2) < 1) {
        return 1;
      } else {
        return (getCurrentPageNum()-(pageRangeLength/2));
      }
    } else {
      return 0;
    }
  }
    
  public int getLastDisplayedPageNum () {
    if (hasHits()) {
      if ((getFirstDisplayedPageNum() + pageRangeLength-1) > getLastPageNum()) {
        return getLastPageNum();
      } else {
        return getFirstDisplayedPageNum() + pageRangeLength - 1;
      }
    } else {
      return 0;
    }
  }
  
  public int getLastPageNum () {
    if (hasHits()) {
      return (int) Math.ceil(new Double(getShow().getMerged())/new Double(getShow().getNum()));
    } else {
      return 0;
    }
  }
  
  public List<PageLink> setPageLinks (int rangeLength) {
    this.pageRangeLength = rangeLength;
    return getPageLinks();
  }
  
  public List<PageLink> getPageLinks () {    
    ArrayList<PageLink> range = new ArrayList<PageLink>();
    if (hasHits()) {
      for (int i = getFirstDisplayedPageNum(); i>0 && i<=getLastDisplayedPageNum();i++) {
        range.add(new PageLink(i+"",i,this));
      }
    }
    return range;
  }

  
  public PageLink getPreviousPageLink (String text) {    
    String linkText = (text!=null && text.length()>0 ? text : "Prev");
    if (hasHits() && getCurrentPageNum()>1) {      
      return new PageLink(linkText,getCurrentPageNum()-1,this);
    } else {
      return new PageLink(linkText,0,this);
    }
  }
  
  public PageLink getNextPageLink (String text) {    
    String linkText = (text!=null && text.length()>0 ? text : "Next");
    if (hasHits() && getCurrentPageNum()<getLastPageNum()) {
      return new PageLink(linkText,getCurrentPageNum()+1,this);
    } else {
      return new PageLink(linkText,0,this);
    }    
  }
  
  public int getCurrentPage() {
    return (getShow().getStart()/getPageSize()+1);
  }
  
  public void goToPage(int page) {    
    pz2session.setStart((page-1)*getPageSize());
  }
  
  public void goToPreviousPage() {
    if (hasPreviousPage()) {
      goToPage(getCurrentPage()-1);  
    }    
  }
  
  public void goToNextPage() {
    if (hasNextPage()) {
      goToPage(getCurrentPage()+1);
    }
  }
  
  public boolean hasPreviousPage() {
    return getCurrentPage()>1;
  }
      
  public boolean hasNextPage () {
    return getCurrentPage() < getLastPageNum();
  }
  
  public boolean hasPageAfterLastDisplayed() {
    return getLastDisplayedPageNum() < getLastPageNum();
  }

  
  private ShowResponse getShow() {
    return pz2session.getShow();
  }
    
}
