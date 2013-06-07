/**
 * Pazpar2 responses produced by Pazpar2 commands are parsed by the
 * ResponseParser, which outputs response data objects that are accessible to the UI
 * through the class Responses. 
 * 
 * <p>Responses is exposed to the UI as <code>pzresp</code></p>
 * 
 * <p>Examples:</p>
 * <ul>
 *  <li><code>pzresp.show.hits<code> returns a list of hits from the most recent show command</li>
 *  <li><code>pzresp.show.hits.[0]</code> returns the first hit</li>
 *  <li><code>pzresp.show.hits.[0].author</code> returns the author attribute of the first hit</li>
 *  <li><code>pzresp.show.hits.[0].getOneValue("my-hit-field")</code> returns an arbitrary attribute of the data object</li>
 * </ul>  
 * 
 */
package com.indexdata.mkjsf.pazpar2.data;