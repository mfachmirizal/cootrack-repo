(function () {
    //refresh token
    var buttonRefreshToken = {
        action: function(){
            var ids = [], i, view = this.view, grid = view.viewGrid, selectedRecords = grid.getSelectedRecords(), idtab = view.tabId ;
            var callback, headers = [], hasilCallback ='';
            var string_id="",postParams = [];
            var command = "";
            var baseContainer = this;
            
            baseContainer.setDisabled(true);
            //			for (i = 0; i < selectedRecords.length; i++) {
            //			  string_id= string_id+selectedRecords[i].id+",";
            //			}

            //			if (string_id.length > 0 ) {
            //			  string_id = string_id.substring(0,string_id.length-1);
            //			}
            //command = 'GET_TOKEN';
            //postParams['Command'] = command;
            //postParams['inpIdTab'] = idtab;
            //OB.Utilities.openProcessPopup(OB.Application.contextUrl + '/com.gai.renbang.ijinbelajar.ad_process.ApproveIjin/ApproveIJIN.html', false, postParams, 350, 900);
            //alert('test : '+string_id);
            headers.push('iseng_di_isi');
            callback = function(rpcResponse, data, rpcRequest) {
                //isc.say(OB.I18N.getLabel('OBEXAPP_SumResult', [data.total]));
                hasilCallback = data.jawaban;
                if (hasilCallback != '') {
                    isc.say(hasilCallback)
                }
                //isc.say('Token : '+hasilCallback);
                console.log('Hasil CB : '+hasilCallback);
                grid.refreshGrid();
                baseContainer.setDisabled(false);
            }
            OB.RemoteCallManager.call('com.tripad.cootrack.handler.RefreshTokenHandler', {headers: headers}, {}, callback);
        },
        buttonType: 'tmc_refreshtoken',
        prompt: 'Refresh Token dari OpenApi',
        updateState: function(){
            var view = this.view, form = view.viewForm, grid = view.viewGrid, selectedRecords = grid.getSelectedRecords();
            if (view.isShowingForm && form.isNew) {
                this.setDisabled(true);
            } else if (view.isEditingGrid && grid.getEditForm().isNew) {
                this.setDisabled(true);
            } else {
                this.setDisabled(selectedRecords.length !== 0);
            }
        }
    };

    //register button
    OB.ToolbarRegistry.registerButton(buttonRefreshToken.buttonType, isc.OBToolbarIconButton, buttonRefreshToken, 100, ['2FE2C1A87557457E8CF5711190BCAF43']);
}());

