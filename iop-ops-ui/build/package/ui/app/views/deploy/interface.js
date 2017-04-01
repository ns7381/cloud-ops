define(["common/dialog", 'common/ajax', 'common/alert','common/tool', 'common/validate', 'common/table'], function (Dialog, Ajax, Alert, Tool) {
    var appPage = {};
    var packages = [];
    appPage.init = function () {
        var deploymentTopologyId = Tool.getParams("deploymentTopologyId");
        var deployId = Tool.getParams("deployId");
        var locationId = Tool.getParams("locationId");
        Ajax.get('resource/package?deploymentId='+deployId, function (data) {
            packages = data;
        });
        window.interfaceActionFormatter = function (value, row, index) {
            return [
                '<a class="action" data-toggle="tooltip" href="javascript:void(0)" title="调用">',
                '<i class="fa fa-play"></i>',
                '</a>'
            ].join('');
        };
        window.interfaceActionEvents = {
            'click .action': function (e, value, row, index) {
                row.packages = packages;
                Dialog.modal({
                    title: '调用',
                    template: 'deploy/interface/action.html',
                    data: row,
                    show: function (dialog) {
                        formValidate(dialog.getModal());
                    },
                    clickOk: function (dialog) {
                        var $modal = dialog.getModal();
                        var valid = $(".form-horizontal", $modal).valid();
                        if (!valid) return false;
                        var interface = $(".form-horizontal", $modal).serializeObject();
                        Ajax.putJson("deployment/"+deployId+"/interface/"+row.id+setUrlK(interface), function () {
                            dialog.close();
                            $('#deployDetailTab a:last').tab('show');
                        }, function (msg) {
                            dialog.close();
                            $('#deployDetailTab a:last').tab('show');
                        });
                    }
                });
            }
        };
        $('#interface-table').bootstrapTable({toolbar: "#interface-toolbar"});
        
    };
    //json转url参数 setUrlK({name:"a"},true编码)
    function setUrlK(ojson) {
        var s='',name, key, init = true;
        for(var p in ojson) {
            if(!ojson[p]) {return null;}
            if(ojson.hasOwnProperty(p)) { name = p };
            key = ojson[p];
            s += (init ? "?" : "&") + name + "=" + encodeURIComponent(key);
            init = false;
        };
        return s;
    };
    function formValidate($modal) {
        return $(".form-horizontal", $modal).validate({
            rules: {
                'name': {
                    required: true,
                    maxlength: 128
                },
                'description': {
                    maxlength: 1024
                }
            }
        });
    }

    return appPage;
}); 