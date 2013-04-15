  var renderTargetsReqVar;
  var renderOnRecordTargetsReqVar;
  
  // Renders UI elements listed in 'renderWhileActiveclients', optionally doing 
  // another update round-trip to Pazpar2 when done.
  function renderTargets(doRefresh)
  {
	//console.log('rendering ' + renderWhileActiveclients);
	var sourcecomp = document.getElementById("pz2watch:activeclientsField");
    jsf.ajax.request(sourcecomp, null,{render: renderWhileActiveclients + " pz2watch:errorMessages"});
    if (doRefresh) {
		//console.log('Will do another ajax request after a timeout in order to render: pz2watch:activeclientsField');  
	    renderTargetsReqVar=setTimeout(
	     function() {              
	       //console.log('Making request for pz2watch:activeclientsField');
	       jsf.ajax.request(sourcecomp, null,{render: "pz2watch:activeclientsField"});       
	     }
	     ,500);
    } else {
    	//console.log("No further updates from server requested");
    }
  }
  
  // Renders UI elements listed in 'renderWhileActiveclientsRecord', optionally doing 
  // another record request when done.
  function renderOnRecordTargets(doRefresh)
  {
	console.log('rendering ' + renderWhileActiveclientsRecord);
	var sourcecomp = document.getElementById("pz2watch:activeclientsFieldRecord");
    jsf.ajax.request(sourcecomp, null,{render: renderWhileActiveclientsRecord});
    if (doRefresh) {
		console.log('Will do another ajax request after a timeout in order to render: pz2watch:activeclientsFieldRecord');  
	    renderOnRecordTargetsReqVar=setTimeout(
	     function() {              
	       console.log('Making request for pz2watch:activeclientsFieldRecord');
	       jsf.ajax.request(sourcecomp, null,{render: "pz2watch:activeclientsFieldRecord"});       
	     }
	     ,1000);
    } else {
    	//console.log("No further updates from server requested");
    }
  }

  // Listens for browser initiated changes to 'window.location.hash' and sends the hash
  // changes to the back-end (to have the back-end pull up a previous Pazpar2 state)
  function windowlocationhashListener () {
	  if (trackHistory) {
	      //console.log("browser hash update detected");
	      var stateKey = document.getElementById("pz2watch:windowlocationhash");
	      if (window.location.hash != stateKey.value) {
	        //console.log("updating stateKey with new browser hash: " + window.location.hash);
	        stateKey.value = window.location.hash;
	        if (! stateKey.value) window.location.hash = '#1';
	        stateKey.onchange();
	      } else {
	        //console.log("State hash already has the value of the new browser hash - not updating state hash");
	      }    	  
	  }	  
  }
  
  // Listens for ViewExpiredException message. Reloads the current page, stripped of
  // it's jsessionid and hash content
  function viewExpirationListener (data) {
	  if (data.status === "success" && data.responseXML) {  
		  var errorElements = data.responseXML.getElementsByTagName("error-name");
		  if (errorElements.length>0) {
			  var errorname = errorElements.item(0).textContent || errorElements.item(0).text;
			  if (errorname === "class javax.faces.application.ViewExpiredException") {
				  var newloc = window.location.protocol + "//" + window.location.host + window.location.pathname.replace(/;jsessionid.*/,'');
				  alert('Sorry, this session has expired. A new one will be loaded.');
				  window.location.replace(newloc);
			  }			  
		  }
	  }
	  
  }

  // Composite listener, invoking all field update listeners
  function fieldUpdateListener (data) {
	  if (data.status === "success") {
		var updates = data.responseXML.getElementsByTagName("update");
		for (var i=0, max=updates.length; i<max; i++) {
			var lsnri = fieldListeners.getListener(updates[i].getAttribute("id"));
			if (lsnri) {
				lsnri.invoke(updates[i]);
			}
		}
	  } 
  }
        
  var Pz2listeners = function () {
	var lsnrs = {};
	this.addListener = function (key, lsnr) {
		lsnrs[key] =lsnr;
	};
	this.getListener = function (key) {
		return lsnrs[key];
	};
  };

  var fieldListeners = new Pz2listeners();

  // Listens for back-end initiated changes to the state key and updates
  // window.location.hash with changes, to record them in browsing history
  var StateListener = function () {
	this.invoke = function (field) {
      var stateKeyDoc = StringtoXML(field.textContent || field.text);
      var stateKeyValue = stateKeyDoc.childNodes[0].getAttribute("value");
      //console.log('Application hash update detected. New value: ' + stateKeyValue);
      if (stateKeyValue !== window.location.hash) {
        window.location.hash = stateKeyValue;
        //console.log("Browsers hash updated accordingly.");
      } else {
        //console.log("Browsers hash already has the value of the state hash. Not updating browser hash."); 
      }
	};
  };
  
  // Listens for updates to general 'activeclients' field, then invokes renderTargets
  var ActiveclientsListener = function () {
    this.invoke = function (field) {
      var updateDoc = StringtoXML(field.textContent || field.text);
      var activeClientsValue = (updateDoc.childNodes[0].textContent || updateDoc.childNodes[0].text);
      //console.log('Activeclients response detected: ' + activeClientsValue);
      clearTimeout(renderTargetsReqVar);
      if (activeClientsValue > '0') {
        renderTargets(true);
      } else {
    	renderTargets(false);
      }
	};
  };
  
  // Listens for updates to record 'activeclients' field, then invokes renderOnRecordTargets
  var ActiveclientsRecordListener = function () {
	    this.invoke = function (field) {
	      var updateDoc = StringtoXML(field.textContent || field.text);
	      var activeClientsRecordValue = (updateDoc.childNodes[0].textContent || updateDoc.childNodes[0].text);
	      console.log('Activeclients response for record detected: ' + activeClientsRecordValue);
	      clearTimeout(renderOnRecordTargetsReqVar);
	      if (activeClientsRecordValue > '0') {
	        renderOnRecordTargets(true);
	      } else {
	    	console.log('Active clients is 0, final rendering');
	    	renderOnRecordTargets(false);
	      }
		};
	  };

    
  // Inserts field update listeners, state listeners, view expired listener
  // into Ajax response handling 	  
  jsf.ajax.addOnEvent(fieldUpdateListener);
  jsf.ajax.addOnEvent(viewExpirationListener);
  
  function StringtoXML(text){
		var doc;
	    if (window.ActiveXObject){
	      doc=new ActiveXObject('Microsoft.XMLDOM');
	      doc.async=false;
	      doc.loadXML(text);
	    } else {
	      var parser=new DOMParser();
	      doc=parser.parseFromString(text,'text/xml');
	    }
	    return doc;
  }

  // Sets up field update listeners
  var setUpListeners = function () {
    //console.log("Starts tracking activeclientsField");
    fieldListeners.addListener("pz2watch:activeclientsField", new ActiveclientsListener());
    fieldListeners.addListener("pz2watch:activeclientsFieldRecord", new ActiveclientsRecordListener());
    if (trackHistory) {
        //console.log("Starts tracking windowlocationhash field");
        fieldListeners.addListener("pz2watch:windowlocationhash", new StateListener());
        //console.log("Setting listener for browser onhashchange");
        window.onload = window.onhashchange = windowlocationhashListener;
    }
  };