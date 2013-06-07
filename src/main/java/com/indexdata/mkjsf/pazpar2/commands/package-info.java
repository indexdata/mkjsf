/**
 * 
 * Each Pazpar2 command is represented by a class with methods for 
 * setting parameters and running the command against 
 * the selected Pazpar2 service. 
 * <p>
 * The UI can access the command objects through the bean Pazpar2Commands, 
 * which is exposed to the UI under the name <code>pzreq</code>.
 * </p>
 * <p>
 * For commands that has Service Proxy extension parameters, the UI 
 * can access the extension parameters through the getSp() method
 * on the command - using for instance <code>pzreq.record.sp</code>
 * </p>
 * <p>
 * The UI can access Service Proxy-only commands through the getSp()
 * method on the pzreq bean - for instance <code>pzreq.sp.categories</code>
 * </p>
 * <p>Examples:</p>
 * <ul>
 *  <li><code>pzreq.search.query</code> references getter and setter for the 
 *          query parameter of the search command.</li>
 * 
 *  <li><code>pzreq.search.run()</code> executes the search command with current
 *          parameters</li>
 *                      
 *  <li><code>pzreq.record.sp.recordquery</code> references getter and setter on a Service Proxy-only 
 *          <i>parameter</i> to the record command</li> 
 *         
 *  <li><code>pzreq.sp.auth.run()</code> executes a Service Proxy-only <i>command</i></li> 
 * </ul>          
 *          
 */
package com.indexdata.mkjsf.pazpar2.commands;