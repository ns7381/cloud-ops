define(["common/dialog", 'common/ajax', 'common/alert','common/tool', 'common/validate', 'common/table'], function (Dialog, Ajax, Alert, Tool) {
    var appPage = {};
    var deploymentTopologyId;
    appPage.init = function () {
        deploymentTopologyId = Tool.getParams("topologyId");
        window.nodeActionFormatter = function (value, row, index) {
            return [
                '<a class="edit" data-toggle="tooltip" href="javascript:void(0)" title="编辑">',
                '<i class="fa fa-pencil"></i>',
                '</a>',
                '<a class="delete ml10" data-toggle="tooltip" href="javascript:void(0)" title="删除">',
                '<i class="fa fa-trash-o"></i>',
                '</a>'
            ].join('');
        };
        window.nameFormatter = function (value, row, index) {
            return '<a href="#/topology/node/detail?nodeId=' + row.id + '&nodeName=' + row.name + '">' + row.name + '</a>';
        };
        window.nodeActionEvents = {
            'click .edit': function (e, value, row, index) {
                Dialog.modal({
                    title: '编辑节点',
                    template: 'topology/node/edit.html',
                    data: row,
                    show: function (dialog) {
                        formValidate(dialog.getModal());
                    },
                    clickOk: function (dialog) {
                        var $modal = dialog.getModal();
                        var valid = $(".form-horizontal", $modal).valid();
                        if (!valid) return false;
                        var node = $(".form-horizontal", $modal).serializeObject();
                        Ajax.putJson("topology/node", node, function () {
                            dialog.close();
                            Alert.success('节点' + row.name + '修改成功');
                            $("#node-table").bootstrapTable('refresh');
                        }, function (msg) {
                            Alert.error('节点' + row.name + "修改失败!原因：" + msg);
                        });
                    }
                });
            },
            'click .delete': function (e, value, row, index) {
                Dialog.confirm("确定要删除节点" + row.name + "吗", function (result) {
                    if (result) {
                        Ajax.delete("topology/node/" + row.id, function (data) {
                            Alert.success('节点' + row.name + '删除成功');
                            $("#node-table").bootstrapTable('refresh');
                        }, function (err) {
                            Alert.error('节点' + row.name + '删除失败!原因：' + err);
                        });
                    }
                });
            }
        };
        $('#node-table').bootstrapTable({toolbar: "#node-toolbar"});
        $('#node-toolbar').on('click', '#btn-add', function () {
            Dialog.modal({
                title: '新建节点',
                template: 'topology/node/add.html',
                shown: function (dialog) {
                    formValidate(dialog.getModal());
                },
                clickOk: function (dialog) {
                    var $modal = dialog.getModal();
                    var valid = $(".form-horizontal", $modal).valid();
                    if (!valid) return false;
                    var node = $(".form-horizontal", $modal).serializeObject();
                    node.deploymentTopologyId = deploymentTopologyId;
                    Ajax.postJson("topology/node", node, function () {
                        dialog.close();
                        Alert.success('节点' + node.name + '创建成功');
                        $("#node-table").bootstrapTable('refresh');
                    }, function (msg) {
                        Alert.error('节点' + node.name + '创建失败!原因：' + msg);
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