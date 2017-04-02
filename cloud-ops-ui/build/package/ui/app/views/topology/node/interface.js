define(["common/dialog", 'common/ajax', 'common/alert','common/tool', 'common/validate', "bs/fileinput",'common/table'], function (Dialog, Ajax, Alert, Tool) {
    var appPage = {};
    appPage.init = function () {
        var nodeId = Tool.getParams("nodeId");
        window.interfaceNameFormatter = function (value, row, index) {
            return '<a href="#/topology/node/interface/input?interfaceId=' + row.id + '&interfaceName=' + row.name + '">' + row.name + '</a>';
        };
        window.interfaceActionFormatter = function (value, row, index) {
            return [
                '<a class="edit" data-toggle="tooltip" href="javascript:void(0)" title="编辑">',
                '<i class="fa fa-pencil"></i>',
                '</a>',
                '<a class="upload ml10" data-toggle="tooltip" href="javascript:void(0)" title="更新脚本">',
                '<i class="fa fa-upload"></i>',
                '</a>',
                '<a class="delete ml10" data-toggle="tooltip" href="javascript:void(0)" title="删除">',
                '<i class="fa fa-trash-o"></i>',
                '</a>'
            ].join('');
        };
        window.interfaceActionEvents = {
            'click .upload': function (e, value, row, index) {
                Dialog.modal({
                    title: '更新脚本',
                    template: 'topology/node/interface/upload.html',
                    buttons: [],
                    shown: function (dialog) {
                        $('#file-input').fileinput({
                            language: 'zh',
                            uploadUrl: "topology/node/interface/upload?id=" + row.id,
                            allowedFileExtensions: ['sh'],
                            showCaption: false
                        }).on('fileuploaded', function(event, data, previewId, index) {
                            var fileName = data.files[0].name, response = data.response;
                            if(response) {
                                dialog.close();
                                Alert.success(fileName + '上传成功');
                                $("#interface-table").bootstrapTable('refresh');
                            } else {
                                Alert.error(fileName + '上传失败');
                                $('#file-input').fileinput('reset');
                            }
                        });
                    }
                });
            },
            'click .edit': function (e, value, row, index) {
                Dialog.modal({
                    title: '编辑接口',
                    template: 'topology/node/interface/edit.html',
                    data: row,
                    show: function (dialog) {
                        formValidate(dialog.getModal());
                    },
                    clickOk: function (dialog) {
                        var $modal = dialog.getModal();
                        var valid = $(".form-horizontal", $modal).valid();
                        if (!valid) return false;
                        var interface = $(".form-horizontal", $modal).serializeObject();
                        Ajax.putJson("topology/node/interface", interface, function () {
                            dialog.close();
                            Alert.success('接口' + row.name + '修改成功');
                            $("#interface-table").bootstrapTable('refresh');
                        }, function (msg) {
                            Alert.error('接口' + row.name + "修改失败!原因：" + msg);
                        });
                    }
                });
            },
            'click .delete': function (e, value, row, index) {
                Dialog.confirm("确定要删除接口" + row.name + "吗", function (result) {
                    if (result) {
                        Ajax.delete("topology/node/interface/" + row.id, function (data) {
                            Alert.success('接口' + row.name + '删除成功');
                            $("#interface-table").bootstrapTable('refresh');
                        }, function (err) {
                            Alert.error('接口' + row.name + '删除失败!原因：' + err);
                        });
                    }
                });
            }
        };
        $('#interface-table').bootstrapTable({toolbar: "#interface-toolbar"});
        $('#interface-toolbar').on('click', '#btn-add', function () {
            Dialog.modal({
                language: 'zh',
                title: '新建接口',
                template: 'topology/node/interface/add.html',
                shown: function (dialog) {
                    formValidate(dialog.getModal());
                    $('#file-input').fileinput({
                        uploadUrl: "topology/node/interface",
                        allowedFileExtensions: ['sh'],
                        enctype: 'multipart/form-data',
                        uploadExtraData: function () {
                            var obj = {};
                            $('.form-horizontal', dialog.getModalBody()).find('input,textarea').each(function() {
                                var id = $(this).attr('name'), val = $(this).val();
                                obj[id] = val;
                            });
                            obj.deploymentNodeId = nodeId;
                            return obj;
                        }
                    }).on('fileuploaded', function (event, data, previewId, index) {
                        var fileName = data.files[0].name, response = data.response;
                        if (response) {
                            dialog.close();
                            Alert.success(fileName + '上传成功');
                            $("#interface-table").bootstrapTable('refresh');
                        } else {
                            Alert.error(fileName + '上传失败');
                            $('#file-input').fileinput('reset');
                        }
                    });
                },
                buttons: []
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