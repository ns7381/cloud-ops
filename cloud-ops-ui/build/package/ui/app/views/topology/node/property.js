define(["common/dialog", 'common/ajax', 'common/alert','common/tool', 'common/validate', 'common/table'], function (Dialog, Ajax, Alert, Tool) {
    var appPage = {};
    appPage.init = function () {
        var nodeId = Tool.getParams("nodeId");
        window.propertyActionFormatter = function (value, row, index) {
            return [
                '<a class="edit" data-toggle="tooltip" href="javascript:void(0)" title="编辑">',
                '<i class="fa fa-pencil"></i>',
                '</a>',
                '<a class="delete ml10" data-toggle="tooltip" href="javascript:void(0)" title="删除">',
                '<i class="fa fa-trash-o"></i>',
                '</a>'
            ].join('');
        };
        window.propertyActionEvents = {
            'click .edit': function (e, value, row, index) {
                Dialog.modal({
                    title: '编辑属性',
                    template: 'topology/node/property/edit.html',
                    data: row,
                    show: function (dialog) {
                        formValidate(dialog.getModal());
                    },
                    clickOk: function (dialog) {
                        var $modal = dialog.getModal();
                        var valid = $(".form-horizontal", $modal).valid();
                        if (!valid) return false;
                        var property = $(".form-horizontal", $modal).serializeObject();
                        Ajax.putJson("topology/node/property", property, function () {
                            dialog.close();
                            Alert.success('属性' + row.name + '修改成功');
                            $("#property-table").bootstrapTable('refresh');
                        }, function (msg) {
                            Alert.error('属性' + row.name + "修改失败!原因：" + msg);
                        });
                    }
                });
            },
            'click .delete': function (e, value, row, index) {
                Dialog.confirm("确定要删除属性" + row.name + "吗", function (result) {
                    if (result) {
                        Ajax.delete("topology/node/property/" + row.id, function (data) {
                            Alert.success('属性' + row.name + '删除成功');
                            $("#property-table").bootstrapTable('refresh');
                        }, function (err) {
                            Alert.error('属性' + row.name + '删除失败!原因：' + err);
                        });
                    }
                });
            }
        };
        $('#property-table').bootstrapTable({toolbar: "#property-toolbar"});
        $('#property-toolbar').on('click', '#btn-add', function () {
            Dialog.modal({
                title: '新建属性',
                template: 'topology/node/property/add.html',
                shown: function (dialog) {
                    formValidate(dialog.getModal());
                },
                clickOk: function (dialog) {
                    var $modal = dialog.getModal();
                    var valid = $(".form-horizontal", $modal).valid();
                    if (!valid) return false;
                    var property = $(".form-horizontal", $modal).serializeObject();
                    property.deploymentNodeId = nodeId;
                    Ajax.postJson("topology/node/property", property, function () {
                        dialog.close();
                        Alert.success('属性' + property.name + '创建成功');
                        $("#property-table").bootstrapTable('refresh');
                    }, function (msg) {
                        Alert.error('属性' + property.name + '创建失败!原因：' + msg);
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