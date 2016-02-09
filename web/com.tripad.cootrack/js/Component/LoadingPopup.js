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
        title: 'Status',
        value:'Retrieving data, please wait...',
        disabled : true,
        height: 20,
        width: 200,
        type: '_id_10' //Date reference
      }]
    });

    // OK Button
    this.okButton = isc.OBFormButton.create({
      title: 'OK',
      popup: this,
      action: function () {

        /*
    	    console.log(this.title);
            console.log(this.popup.cancelButton.title);
            console.log(this.popup.mainform.fields[0].value);
            var pop = this.popup;
            //this.disabled =true;
            //this.popup.cancelButton.disabled = true;
            pop.mainform.fields[0].value = 'HMM';

            //pop.view.messageBar.setMessage(isc.OBMessageBar.TYPE_SUCCESS,'Success','Berhasil menarik data');
            console.log(pop.items[0].members[1].members[0]);
            pop.items[0].members[1].members[0].title = "EVOL";
            pop.redraw();

          action: this.popup.params.action
          //,dateParam: this.popup.mainform.getField('Date').getValue(), //send the parameter to the server too
        }, {}, callback, {popup: this.popup});
        /////////////////////////////////////////////////////////////////////////////////////////////////////////
	*/
   //start
    	  var hasilCallback,pop = this.popup;
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

    	  console.log('hheader : '+pop.params.headers);
    	  console.log('action : '+pop.params.action)
    		  OB.RemoteCallManager.call(pop.actionHandler, {
    		    headers: pop.params.headers,
    		    action: pop.params.action
    		    //,dateParam: this.popup.mainform.getField('Date').getValue(), //send the parameter to the server too
    		  }, {}, callback, {popup: this});
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
