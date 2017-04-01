define(["common/dialog", 'common/ajax', 'common/alert','common/tool', 'common/validate', 'common/table'], function (Dialog, Ajax, Alert, Tool) {
    var appPage = {};
    appPage.init = function () {
        window.taskActionFormatter = function (value, row, index) {
            return [
                '<a class="log" data-toggle="tooltip" href="javascript:void(0)" title="查看日志">',
                '<i class="fa fa-file-word-o"></i>',
                '</a>'
            ].join('');
        };
        window.taskActionEvents = {
            'click .log': function (e, value, row, index) {
                Dialog.modal({
                    title: '查看日志',
                    nl2br: true,
                    message: row.message,
                    clickOk: function (dialog) {
                        dialog.close();
                    }
                });
            }
        };
        window.taskStatusFormatter = function (value, row, index) {
            return '<span class="text-info">' + generateRequestStatus(row.step) + '</span>';
            function generateRequestStatus(status) {
                if (status == null) {
                    return "";
                }
                var label = "";
                switch (status) {
                    case 'SUCCESS':
                        label = "执行完成";
                        break;
                    case 'FAIL':
                        label = "执行失败";
                        break;
                    default :
                        label = '<div class="progress progress-striped active">' +
                            '<div class="progress-bar" role="progressbar" style="width: 100%;">' +
                            '<div class="text">'+status+'</div></div>' +
                            '</div>';
                        break;
                }
                return label;
            }
        };
        $('#task-table').bootstrapTable({toolbar: "#task-toolbar"});
        $("#task-table").bootstrapTable('refresh');
        Tool.webSocket("/messages?routing-key=task.status", function (event) {
            var payload = JSON.parse(event.data);
            $('#task-table').bootstrapTable('updateByUniqueId', {id: payload.id, row: payload});
        });
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