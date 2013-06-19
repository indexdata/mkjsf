package com.indexdata.mkjsf.pazpar2.commands;

import com.indexdata.mkjsf.pazpar2.commands.sp.ServiceProxyCommand;
import com.indexdata.mkjsf.pazpar2.commands.sp.ShowCommandSp;


/**
 * Represents a Pazpar2 <code>show</code> command, can be accessed by <code>pzreq.show</code> 
 * 
 * @author Niels Erik
 *
 */
public class ShowCommand extends Pazpar2Command implements ServiceProxyCommand {

  private static final long serialVersionUID = -8242768313266051307L;

  public ShowCommand() {
    super("show");
    setParameterInState(new CommandParameter("start","=","0"));
  }

  /**
   * Sets Pazpar2 parameter <code>sort</code>. See Pazpar2 documentation for details. 
   */
  public void setSort (String sort) {
    setParameter(new CommandParameter("sort","=",sort));
  }
  
  /**
   * Gets parameter value for <code>sort</cod>
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
   * Sets Pazpar2 parameter <code>start</code>. See Pazpar2 documentation for details. 
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
  
  /**
   * Sets Pazpar2 parameter <code>num</code>. See Pazpar2 documentation for details. 
   */
  public void setNum (int num) {
    setParameter(new CommandParameter("num","=",num));
  }
    
  /** 
   * Get the parameter value for <code>num</code>
   */
  public int getNum () {
    return getParameter("num") != null ? Integer.parseInt(getParameter("num").value) : 0;
  }
  
  /**
   * Sets Pazpar2 parameter <code>block</code>. See Pazpar2 documentation for details. 
   */
  public void setBlock(String block) {
    setParameterInState(new CommandParameter("block","=",block));
  }

  /** 
   * Get the parameter value for <code>block</code>
   */
  public String getBlock() {
    return getParameterValue("block");
  }
  
  /**
   * Sets Pazpar2 parameter <code>mergekey</code>. See Pazpar2 documentation for details. 
   */
  public void setMergekey (String mergekey) {
    setParameter(new CommandParameter("mergekey","=",mergekey));
  }
  
  /** 
   * Get the parameter value for <code>mergekey</code>
   */
  public String getMergekey () {
    return getParameterValue("mergekey");
  }
  
  /**
   * Sets Pazpar2 parameter <code>rank</code>. See Pazpar2 documentation for details. 
   */
  public void setRank (String rank) {
    setParameter(new CommandParameter("rank","=",rank));
  }
  
  /** 
   * Get the parameter value for <code>rank</code>
   */
  public String getRank () {
    return getParameterValue("rank");
  }

  
  public ShowCommand copy () {
    ShowCommand newCommand = new ShowCommand();
    for (String parameterName : parameters.keySet()) {
      newCommand.setParameterInState(parameters.get(parameterName).copy());      
    }    
    return newCommand;
  }

  @Override
  public ServiceProxyCommand getSp() {
    return new ShowCommandSp(this);
  }

  @Override
  public boolean spOnly() {
    return false;
  }

}
