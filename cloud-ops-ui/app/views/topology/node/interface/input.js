define(["common/dialog", 'common/ajax', 'common/alert','common/tool', 'common/validate', 'common/table'], function (Dialog, Ajax, Alert, Tool) {
    var appPage = {};
    appPage.init = function () {
        var interfaceId = Tool.getParams("interfaceId");
        window.inputActionFormatter = function (value, row, index) {
            return [
                '<a class="edit" data-toggle="tooltip" href="javascript:void(0)" title="编辑">',
                '<i class="fa fa-pencil"></i>',
                '</a>',
                '<a class="delete ml10" data-toggle="tooltip" href="javascript:void(0)" title="删除">',
                '<i class="fa fa-trash-o"></i>',
                '</a>'
            ].join('');
        };
        window.inputActionEvents = {
            'click .edit': function (e, value, row, index) {
                Dialog.modal({
                    title: '编辑输入',
                    template: 'topology/node/interface/input/edit.html',
                    data: row,
                    show: function (dialog) {
                        formValidate(dialog.getModal());
                    },
                    clickOk: function (dialog) {
                        var $modal = dialog.getModal();
                        var valid = $(".form-horizontal", $modal).valid();
                        if (!valid) return false;
                        var input = $(".form-horizontal", $modal).serializeObject();
                        Ajax.putJson("topology/node/interface/input", input, function () {
                            dialog.close();
                            Alert.success('输入' + row.name + '修改成功');
                            $("#input-table").bootstrapTable('refresh');
                        }, function (msg) {
                            Alert.error('输入' + row.name + "修改失败!原因：" + msg);
                        });
                    }
                });
            },
            'click .delete': function (e, value, row, index) {
                Dialog.confirm("确定要删除输入" + row.name + "吗", function (result) {
                    if (result) {
                        Ajax.delete("topology/node/interface/input/" + row.id, function (data) {
                            Alert.success('输入' + row.name + '删除成功');
                            $("#input-table").bootstrapTable('refresh');
                        }, function (err) {
                            Alert.error('输入' + row.name + '删除失败!原因：' + err);
                        });
                    }
                });
            }
        };
        $('#input-table').bootstrapTable({toolbar: "#input-toolbar"});
        $('#input-toolbar').on('click', '#btn-add', function () {
            Dialog.modal({
                title: '新建输入',
                template: 'topology/node/interface/input/add.html',
                shown: function (dialog) {
                    formValidate(dialog.getModal());
                },
                clickOk: function (dialog) {
                    var $modal = dialog.getModal();
                    var valid = $(".form-horizontal", $modal).valid();
                    if (!valid) return false;
                    var input = $(".form-horizontal", $modal).serializeObject();
                    input.deploymentNodeInterfaceId = interfaceId;
                    Ajax.postJson("topology/node/interface/input", input, function () {
                        dialog.close();
                        Alert.success('输入' + input.name + '创建成功');
                        $("#input-table").bootstrapTable('refresh');
                    }, function (msg) {
                        Alert.error('输入' + input.name + '创建失败!原因：' + msg);
                    });
                }
            });
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