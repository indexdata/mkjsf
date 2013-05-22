package com.indexdata.mkjsf.pazpar2.commands;

import com.indexdata.mkjsf.pazpar2.commands.sp.ServiceProxyCommand;
import com.indexdata.mkjsf.pazpar2.state.StateManager;

public class ShowCommand extends Pazpar2Command implements ServiceProxyCommand {

  private static final long serialVersionUID = -8242768313266051307L;

  public ShowCommand(StateManager stateMgr) {
    super("show",stateMgr);
    setParameterInState(new CommandParameter("start","=","0"));
  }

  /**
   * Sets the sort order for results, the updates the 'show' data object
   * from pazpar2. Set valid sort options in the documentation for pazpar2.
   * 
   * The parts of the UI that display 'show' data should be rendered following
   * this request.
   * 
   * @param sortOption
   */
  public void setSort (String sort) {
    setParameter(new CommandParameter("sort","=",sort));
  }
  
  /**
   * Retrieves the current sort order for results
   * @return sort order - i.e. 'relevance'
   */
  public String getSort () {
    return getParameter("sort") != null ? getParameter("sort").value : "relevance";
  }
  
  /**
   * Sets the number of records that pazpar2 should show at a time. Is 
   * followed by an update of the show data object from pazpar2.  
   * 
   * To be used by the UI for paging. After setting page size the parts
   * of the UI that displays 'show' data should be rendered. 
   * 
   * @param perPageOption i.e. 10, default is 20.
   */
  public void setPageSize (String perPageOption) {    
    setParameters(new CommandParameter("num","=",perPageOption),
                  new CommandParameter("start","=",0));
  }
  
  /**
   * Retrieves the currently defined number of items to show at a time
   * 
   * @return number of result records that will be shown from pazpar2
   */
  public String getPageSize () {
    return getParameter("num") != null ? getParameter("num").value : "20";
  }
  
  /**
   * Sets the first record to show - starting at record '0'. After setting
   * first record number, the 'show' data object will be updated from pazpar2,
   * and the parts of the UI displaying show data should be re-rendered.
   * 
   * To be used by the UI for paging.
   * 
   * @param start first record to show
   */
  public void setStart (int start) {    
    setParameter(new CommandParameter("start","=",start));      
  }
  
  /**
   * Retrieves the sequence number of the record that pazpaz2 will return as
   * the first record in 'show'
   * 
   * @return sequence number of the first record to be shown (numbering starting at '0')
   * 
   */
  public int getStart() {
    return getParameter("start") != null ? Integer.parseInt(getParameter("start").value) : 0;
  }
  
  public void setNum (int num) {
    setParameter(new CommandParameter("num","=",num));
  }
  
  public int getNum () {
    return getParameter("num") != null ? Integer.parseInt(getParameter("num").value) : 0;
  }
  
  public void setBlock(String block) {
    setParameterInState(new CommandParameter("block","=",block));
  }
  
  public String getBlock() {
    return getParameterValue("block");
  }
  
  public void setMergekey (String mergekey) {
    setParameter(new CommandParameter("mergekey","=",mergekey));
  }
  
  public String getMergekey () {
    return getParameterValue("mergekey");
  }
  
  public ShowCommand copy () {
    ShowCommand newCommand = new ShowCommand(stateMgr);
    for (String parameterName : parameters.keySet()) {
      newCommand.setParameterInState(parameters.get(parameterName).copy());      
    }    
    return newCommand;
  }

  @Override
  public ServiceProxyCommand getSp() {
    return this;
  }

  @Override
  public boolean spOnly() {
    return false;
  }

}
