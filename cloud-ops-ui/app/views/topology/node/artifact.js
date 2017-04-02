define(["common/dialog", 'common/ajax', 'common/alert','common/tool', 'common/validate', 'common/table'], function (Dialog, Ajax, Alert, Tool) {
    var appPage = {};
    appPage.init = function () {
        var nodeId = Tool.getParams("nodeId");
        window.artifactActionFormatter = function (value, row, index) {
            return [
                '<a class="edit" data-toggle="tooltip" href="javascript:void(0)" title="编辑">',
                '<i class="fa fa-pencil"></i>',
                '</a>',
                '<a class="delete ml10" data-toggle="tooltip" href="javascript:void(0)" title="删除">',
                '<i class="fa fa-trash-o"></i>',
                '</a>'
            ].join('');
        };
        window.artifactActionEvents = {
            'click .edit': function (e, value, row, index) {
                Dialog.modal({
                    title: '编辑外部文件',
                    template: 'topology/node/artifact/edit.html',
                    data: row,
                    show: function (dialog) {
                        formValidate(dialog.getModal());
                    },
                    clickOk: function (dialog) {
                        var $modal = dialog.getModal();
                        var valid = $(".form-horizontal", $modal).valid();
                        if (!valid) return false;
                        var artifact = $(".form-horizontal", $modal).serializeObject();
                        Ajax.putJson("topology/node/artifact", artifact, function () {
                            dialog.close();
                            Alert.success('外部文件' + row.name + '修改成功');
                            $("#artifact-table").bootstrapTable('refresh');
                        }, function (msg) {
                            Alert.error('外部文件' + row.name + "修改失败!原因：" + msg);
                        });
                    }
                });
            },
            'click .delete': function (e, value, row, index) {
                Dialog.confirm("确定要删除外部文件" + row.name + "吗", function (result) {
                    if (result) {
                        Ajax.delete("topology/node/artifact/" + row.id, function (data) {
                            Alert.success('外部文件' + row.name + '删除成功');
                            $("#artifact-table").bootstrapTable('refresh');
                        }, function (err) {
                            Alert.error('外部文件' + row.name + '删除失败!原因：' + err);
                        });
                    }
                });
            }
        };
        $('#artifact-table').bootstrapTable({toolbar: "#artifact-toolbar"});
        $('#artifact-toolbar').on('click', '#btn-add', function () {
            Dialog.modal({
                title: '新建外部文件',
                template: 'topology/node/artifact/add.html',
                shown: function (dialog) {
                    formValidate(dialog.getModal());
                },
                clickOk: function (dialog) {
                    var $modal = dialog.getModal();
                    var valid = $(".form-horizontal", $modal).valid();
                    if (!valid) return false;
                    var artifact = $(".form-horizontal", $modal).serializeObject();
                    artifact.deploymentNodeId = nodeId;
                    Ajax.postJson("topology/node/artifact", artifact, function () {
                        dialog.close();
                        Alert.success('外部文件' + artifact.name + '创建成功');
                        $("#artifact-table").bootstrapTable('refresh');
                    }, function (msg) {
                        Alert.error('外部文件' + artifact.name + '创建失败!原因：' + msg);
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