OB.TMC = OB.TMC || {};

OB.TMC.Request = {
  execute: function (params, view) {
    var i, selection = params.button.contextView.viewGrid.getSelectedRecords(),
    headers = [], handlerclass ='',
    callback;


    for (i = 0; i < selection.length; i++) {
      headers.push(selection[i].id);
    };

    //isc.say(selection[0].id);
    /*
    callback = function(rpcResponse, data, rpcRequest) {
    //isc.say(OB.I18N.getLabel('OBEXAPP_SumResult', [data.total]));
    hasilCallback = data.jawaban;
    if (hasilCallback != '') {
    isc.say(hasilCallback)
  }
  console.log('Hasil CB : '+hasilCallback);
  params.button.contextView.viewGrid.refreshGrid();
}
if (params.action === 'filteredstatuscarbyimei') {
handlerclass = 'com.tripad.cootrack.handler.RefreshListBPFromOA';
}
else if (params.action === 'getlistchildaccount') {
handlerclass = 'com.tripad.cootrack.handler.RefreshListBPFromOA';
}
console.log(handlerclass);
OB.RemoteCallManager.call(handlerclass , {
headers: headers,
action: params.action
}, {}, callback);
*/

if (params.action === 'filteredstatuscarbyimei') {
  handlerclass = 'com.tripad.cootrack.handler.RefreshListFilteredStatusCarByImei';
}
// else if (params.action === 'getlistchildaccount') {
//   handlerclass = 'com.tripad.cootrack.handler.RefreshListBPFromOA';
// }


// Create the PopUp
isc.TMC_LoadingPopup.create({
  headers: headers,
  view: view,
  params: params,
  actionHandler: handlerclass
}).show();


},

filteredstatuscarbyimei: function (params, view) {
  params.action = 'filteredstatuscarbyimei';
  OB.TMC.Request.execute(params, view);
}
};
