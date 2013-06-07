/**
 * Service Proxy responses produced by Service Proxy commands.
 * 
 * <p>The most recent respons from each command is accessible to the UI
 * through the class SpResponses, which is exposed to the UI as <code>pzresp.sp</code></p>
 * 
 * <p>Examples:</p>
 * <ul>
 *  <li><code>pzresp.sp.auth.status<code> returns the status of the most recent authentication request</li>
 *  <li><code>pzresp.sp.categories.tagetCategories</code> returns target categories for filtering by category</li>
 * </ul>  
 * 
 */
package com.indexdata.mkjsf.pazpar2.data.sp;