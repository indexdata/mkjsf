  var renderTargetsReqVar;
  var renderOnRecordTargetsReqVar;
    
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


  function windowlocationhashListener () {
	  if (trackHistory) {
	      //console.log("browser hash update detected");
	      var stateKey = document.getElementById("pz2watch:windowlocationhash");
	      if (window.location.hash != stateKey.value) {
	        //console.log("updating stateKey with new browser hash: " + window.location.hash);
	        stateKey.value = window.location.hash;
	        if (! stateKey.value) window.location.hash = '#initial';
	        stateKey.onchange();
	      } else {
	        //console.log("State hash already has the value of the new browser hash - not updating state hash");
	      }    	  
	  }	  
  }      

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

  var StateListener = function () {
	this.invoke = function (field) {
      var stateKeyDoc = StringtoXML(field.textContent || field.text);
      var stateKeyValue = stateKeyDoc.childNodes[0].getAttribute("value");
      //console.log('Application hash update detected. New value: ' + stateKeyValue);
      if (stateKeyValue != window.location.hash) {
        window.location.hash = stateKeyValue;
        //console.log("Browsers hash updated accordingly.");
      } else {
        //console.log("Browsers hash already has the value of the state hash. Not updating browser hash."); 
      }

	};
  };
  
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

    
  jsf.ajax.addOnEvent(fieldUpdateListener);
  
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