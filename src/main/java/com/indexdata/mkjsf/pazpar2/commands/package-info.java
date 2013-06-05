/**
 * 
 * Each Pazpar2 command is represented by a class with methods for 
 * setting parameters and ultimately running the command against 
 * the selected Pazpar2 service. 
 * <p>
 * The UI can access the command objects through the bean Pazpar2Commands, 
 * which is exposed to the UI under the name 'pzreq'.
 * </p>
 * <p>
 * For commands that has Service Proxy extension parameters, the UI 
 * can access the extension parameters through the getSp() method
 * on the command.
 * </p>
 * <p>
 * The UI can access Service Proxy-only commands through the getSp()
 * method on the pzreq bean.
 * </p>
 * Examples
 * <ul>
 *  <li>pzreq.search.query references getter and setter for the 
 *          query parameter of the search command.</li>
 * 
 *  <li>pzreq.search.run() executes the search command with current
 *          parameters</li>
 *                      
 *  <li>pzreq.record.sp.recordquery references a Service Proxy-only 
 *          parameter to the record command</li> 
 *         
 *  <li>pzreq.sp.auth.run() executes a Service Proxy-only command</li> 
 * </ul>          
 *          
 */
package com.indexdata.mkjsf.pazpar2.commands;