isc.defineClass('TMC_LoadingPopup', isc.OBPopup);

isc.TMC_LoadingPopup.addProperties({
  width: 320,
  height: 150,
  title: null,
  showMinimizeButton: false,
  showMaximizeButton: false,
  
  showCloseButton : false,
  
  view: null,
  params: null,
  actionHandler: null,
  orders: null,

  mainform: null,
  okButton: null,
  cancelButton: null,

  initWidget: function () {

    // Form that contains the parameters
    this.mainform = isc.DynamicForm.create({
      numCols: 1,
      fields: [{
        name: 'StatusText',
        title: 'Confirmation',
        value:'Retrieve data ?',
        disabled : true,
        height: 20,
        width: 200,
        type: '_id_10' //String reference
      }]
    });

    // OK Button
    this.okButton = isc.OBFormButton.create({
      title: 'OK',
      popup: this,
      action: function () {
    	  var pop = this.popup;
    	  
    	  var fieldParam = [{
              name: 'StatusTextAfter',
              title: 'Status',
              value:'Retrieving data, please wait... Do not close / refresh browser.',
              type: '_id_10',
              disabled : true,
              height: 20,
              width: 350
            }];

          pop.mainform.setFields(fieldParam);
            
    	  //pop.mainform.fields[0].title = 'Status';
    	  //pop.mainform.fields[0].value = 'Retrieving data, please wait... Do not close / refresh';
    	  pop.cancelButton.setDisabled(true);
          this.setDisabled(true);
   //start
    	  var hasilCallback;
    	  
    	  var callback = function(rpcResponse, data, rpcRequest) {
  			var isSukses = true;
  		    //isc.say(OB.I18N.getLabel('OBEXAPP_SumResult', [data.total]));
  		    hasilCallback = data.jawaban;
  		    console.log("masuk callback, hasil : "+hasilCallback);
  		    if (hasilCallback != '') {
  		    	isSukses = false;
  		    	isc.say(hasilCallback)
  		    }
  		   // console.log(pop);
  		     pop.close();
  		   //rpcRequest.clientContext.popup.closeClick();
  		    
  		    //rpcRequest.clientContext.popup.close();
  		   
  		    if(typeof pop.view.viewGrid !== 'undefined'){
  		      pop.view.viewGrid.refreshGrid();
  		      //rpcRequest.clientContext.popup.view.viewGrid.refreshGrid();
  		      if (isSukses) {
  		    	  pop.view.messageBar.setMessage(isc.OBMessageBar.TYPE_SUCCESS,'Success','Berhasil menarik data');
  		      } else {
  		    	  pop.view.messageBar.setMessage(isc.OBMessageBar.TYPE_ERROR,'Error','Data tidak tertarik sepenuhnya');
  		      }
  		    };
  			
  		    if(typeof pop.params.button !== 'undefined'){
  		      pop.params.button.contextView.viewGrid.refreshGrid();
  		      //rpcRequest.clientContext.popup.params.button.contextView.viewGrid.refreshGrid();
  		      if (isSukses) {
  		    	  pop.params.button.contextView.messageBar.setMessage(isc.OBMessageBar.TYPE_SUCCESS,'Success','Berhasil menarik data');
  		      } else {
  		    	  pop.params.button.contextView.messageBar.setMessage(isc.OBMessageBar.TYPE_ERROR,'Error','Data tidak tertarik sepenuhnya');
  		      }
  		    };
  		  }
    	  
    	  var callbackerror = function() {
    		  pop.close();
    		  if(typeof pop.view.viewGrid !== 'undefined'){
      		      pop.view.viewGrid.refreshGrid();
      		      console.log("masuk callback ERROR, hasil : "+hasilCallback);
      		      //rpcRequest.clientContext.popup.view.viewGrid.refreshGrid();
      		    	  pop.view.messageBar.setMessage(isc.OBMessageBar.TYPE_ERROR,'Error','Terdapat Error !. Data tidak tertarik sepenuhnya');
      		    };
      			
      		    if(typeof pop.params.button !== 'undefined'){
      		      pop.params.button.contextView.viewGrid.refreshGrid();
      		    	  pop.params.button.contextView.messageBar.setMessage(isc.OBMessageBar.TYPE_ERROR,'Error','Terdapat Error !. Data tidak tertarik sepenuhnya');
      		    };
    	  }
    	  		
    	  	  //OB.RemoteCallManager.rpcRequest.setTimeout(0);
    	  		isc.RPCManager.setDefaultTimeout(0);
    	  		isc.RPCRequest.setTimeout(0);
    	      /*OB.RemoteCallManager.call(pop.actionHandler, {
    		    headers: pop.params.headers,
    		    action: pop.params.action
    		    //,dateParam: this.popup.mainform.getField('Date').getValue(), //send the parameter to the server too
    		  }, {}, callback, {popup: pop},callbackerror);
    		  */
//end
      }
    });

    // Cancel Button
    this.cancelButton = isc.OBFormButton.create({
      title: 'Cancel',
      popup: this,
      action: function () {
        this.popup.close();
      }
    });

    //Add the elements into a layout
    this.items = [
      isc.VLayout.create({
        defaultLayoutAlign: "center",
        align: "center",
        width: "100%",
        layoutMargin: 10,
        membersMargin: 6,
        members: [
          isc.HLayout.create({
            defaultLayoutAlign: "center",
            align: "center",
            layoutMargin: 30,
            membersMargin: 6,
            members: this.mainform
          })
          ,
          isc.HLayout.create({
          defaultLayoutAlign: "center",
          align: "center",
          membersMargin: 10,
          members: [this.okButton, this.cancelButton]
        })
        
      ]
    })
  ];

  this.Super('initWidget', arguments);
/*
  //eksekusi di background
  var hasilCallback,pop = this;

  
  var callback = function(rpcResponse, data, rpcRequest) {
	var isSukses = true;
    //isc.say(OB.I18N.getLabel('OBEXAPP_SumResult', [data.total]));
    hasilCallback = data.jawaban;
    if (hasilCallback != '') {
    	isSukses = false;
    	isc.say(hasilCallback)
    }
   // console.log(pop);
     pop.close();
   //rpcRequest.clientContext.popup.closeClick();
    
    //rpcRequest.clientContext.popup.close();
   
    if(typeof pop.view.viewGrid !== 'undefined'){
      pop.view.viewGrid.refreshGrid();
      //rpcRequest.clientContext.popup.view.viewGrid.refreshGrid();
      if (isSukses) {
    	  pop.view.messageBar.setMessage(isc.OBMessageBar.TYPE_SUCCESS,'Success','Berhasil menarik data');
      } else {
    	  pop.view.messageBar.setMessage(isc.OBMessageBar.TYPE_ERROR,'Error','Data tidak tertarik sepenuhnya');
      }
    };
	
    if(typeof pop.params.button !== 'undefined'){
      pop.params.button.contextView.viewGrid.refreshGrid();
      //rpcRequest.clientContext.popup.params.button.contextView.viewGrid.refreshGrid();
      if (isSukses) {
    	  pop.params.button.contextView.messageBar.setMessage(isc.OBMessageBar.TYPE_SUCCESS,'Success','Berhasil menarik data');
      } else {
    	  pop.params.button.contextView.messageBar.setMessage(isc.OBMessageBar.TYPE_ERROR,'Error','Data tidak tertarik sepenuhnya');
      }
    };
  }
  
  
  OB.RemoteCallManager.call(this.actionHandler, {
    headers: this.headers,
    action: this.params.action
    //,dateParam: this.popup.mainform.getField('Date').getValue(), //send the parameter to the server too
  }, {}, callback, {popup: this}, callbackerror);
 */
  
}

});
