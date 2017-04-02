define(["common/dialog", 'common/ajax', 'common/tool', 'common/alert', "bs/fileinput", 'common/validate', 'common/table', 'jq/fileDownload'], function (Dialog, Ajax, Tool, Alert, WebUploader) {
    var appPage = {};
    var repository = [],renderData={},packages=[];
    appPage.init = function () {
        var deploymentId = Tool.getParams("deployId");
        Ajax.get('resource/repository', function (data) {
            renderData.repositories = data;
        });
        Ajax.get('resource/package?deploymentId='+deploymentId, function (data) {
            packages = data;
        });
        window.versionStatusFormatter = function (value, row, index) {
            return '<span class="text-info">' + generateRequestStatus(row.status) + '</span>';
            function generateRequestStatus(status) {
                if (status == null) {
                    return "";
                }
                var label = "";
                switch (status) {
                    case 'FINISH':
                        label = "完成";
                        break;
                    case 'CLONING':
                        label = '<div class="progress progress-striped active">' +
                            '<div class="progress-bar" role="progressbar" style="width: 100%;">' +
                            '<div class="text">正在检出</div></div>' +
                            '</div>';
                        break;
                    case 'BUILDING':
                        label = '<div class="progress progress-striped active">' +
                            '<div class="progress-bar" role="progressbar" style="width: 100%;">' +
                            '<div class="text">正在构建</div></div>' +
                            '</div>';
                        break;
                    case 'COMPARE':
                        label = '<div class="progress progress-striped active">' +
                            '<div class="progress-bar" role="progressbar" style="width: 100%;">' +
                            '<div class="text">正在比对</div></div>' +
                            '</div>';
                        break;
                    case 'SAVING':
                        label = '<div class="progress progress-striped active">' +
                            '<div class="progress-bar" role="progressbar" style="width: 100%;">' +
                            '<div class="text">正在保存</div></div>' +
                            '</div>';
                        break;
                    case 'FAIL':
                        label = "失败";
                        break;
                }
                return label;
            }
        };
        window.versionActionFormatter = function (value, row, index) {
            return [
                '<a class="download ml10" data-toggle="tooltip" href="javascript:void(0)" title="下载">',
                '<i class="fa fa-cloud-download"></i>',
                '</a>',
                '<a class="delete ml10" data-toggle="tooltip" href="javascript:void(0)" title="删除">',
                '<i class="fa fa-trash-o"></i>',
                '</a>'
            ].join('');
        };
        window.versionActionEvents = {
            'click .delete': function (e, value, row, index) {
                Dialog.confirm("确定要删除版本" + row.name + "吗", function (result) {
                    if (result) {
                        Ajax.delete("resource/package/" + row.id, function (data) {
                            Alert.success('版本' + row.name + '删除成功');
                            $("#packageTable").bootstrapTable('refresh');
                            for(var i = packages.length-1; i>=0; i--){
                                if (packages[i].name == row.name) packages.splice(i, 1);
                            }
                        }, function (err) {
                            Alert.error('版本' + row.name + '删除失败!原因：' + err);
                        });
                    }
                });
            },
            "click .download": function (e, value, row, index) {
                Dialog.confirm("确定要下载文件吗吗", function (result) {
                    if (result) {
                        $.fileDownload('resource/package/'+row.id +"/download",{
                            successCallback: function (url) {
                                Alert.success('下载文件成功');
                            },
                            failCallback: function (html, url) {
                                Alert.error('下载文件失败');
                            }
                        });
                    }
                });
            }
        };
        $('#packageTable').bootstrapTable({toolbar: "#packageTable-toolbar"});
        Tool.webSocket("/messages?routing-key=package.status", function (event) {
            var payload = JSON.parse(event.data), isContained = false;
            $('#packageTable').bootstrapTable('updateByUniqueId', {id: payload.id, row: payload});
            if (payload.status == "FINISH") {
                $.each(packages, function (k, v) {
                    if (v.id == payload.id) {
                        isContained = true;
                    }
                });
                if (!isContained) {
                    packages.push(payload);
                }
            }
        });
        $('#packageTable-toolbar').off('click');
        $('#packageTable-toolbar').on('click', '#btn-git', function () {
            Dialog.modal({
                title: 'git打包',
                template: 'deploy/package/git.html',
                data: renderData,
                show: function (dialog) {
                    formValidate(dialog.getModal());
                    var $repositoryId = $("#repositoryId", dialog.getModal());
                    var $branch = $("#branch", dialog.getModal());
                    $repositoryId.off('change').on('change', function () {
                        var repositoryId = $repositoryId.val();
                        $branch.empty();
                        Ajax.get('resource/repository/' + repositoryId + "/branches", function (data) {
                            $.each(data, function (k, branch) {
                                $branch.append("<option value='" + branch + "'>" + branch + "</option>")
                            })
                        });
                    });
                    $repositoryId.trigger("change");
                },
                clickOk: function (dialog) {
                    var $modal = dialog.getModal();
                    var valid = $(".form-horizontal", $modal).valid();
                    if (!valid) return false;
                    var formData = $(".form-horizontal", $modal).serializeObject();
                    formData.deploymentId = deploymentId;
                    Ajax.putJson("resource/package/git", formData, function () {
                        dialog.close();
                        Alert.success('版本' + formData.name + '创建成功');
                        $("#packageTable").bootstrapTable('refresh');
                    }, function (msg) {
                        Alert.error('版本' + formData.name + "创建失败!原因：" + msg);
                    });
                }
            });
        });
        $('#packageTable-toolbar').on('click', '#btn-patch', function () {
            Dialog.modal({
                title: '生成PATCH包',
                template: 'deploy/package/patch.html',
                data: packages,
                show: function (dialog) {
                    formValidate(dialog.getModal());
                },
                clickOk: function (dialog) {
                    var $modal = dialog.getModal();
                    var valid = $(".form-horizontal", $modal).valid();
                    if (!valid) return false;
                    var formData = $(".form-horizontal", $modal).serializeObject();
                    formData.deploymentId = deploymentId;
                    Ajax.post("resource/package/patch"+setUrlK(formData), function () {
                        dialog.close();
                        Alert.success('PATCH包创建成功');
                        $("#packageTable").bootstrapTable('refresh');
                    }, function (msg) {
                        Alert.error('版本PATCH包创建失败!原因：' + msg);
                    });
                }
            });
        });
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
        $('#packageTable-toolbar').on('click', '#btn-war', function () {
            Dialog.modal({
                title: '上传',
                template: 'deploy/package/upload.html',
                buttons: [],
                shown: function (dialog) {
                    $('#file-input').fileinput({
                        uploadUrl: "resource/package/upload",
                        allowedFileExtensions: ['zip', 'war', 'rar', 'sql'],
                        enctype: 'multipart/form-data',
                        uploadExtraData: function () {
                            var obj = {};
                            $('.form-horizontal', dialog.getModalBody()).find('input,textarea').each(function() {
                                var id = $(this).attr('name'), val = $(this).val();
                                obj[id] = val;
                            });
                            obj.deploymentId = deploymentId;
                            return obj;
                        }
                    }).on('fileuploaded', function (event, data, previewId, index) {
                        var fileName = data.files[0].name, response = data.response;
                        if (response) {
                            dialog.close();
                            Alert.success(fileName + '上传成功');
                            $("#packageTable").bootstrapTable('refresh');
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
                'code': {
                    required: true,
                    maxlength: 32
                },
                'name': {
                    required: true,
                    maxlength: 128
                },
                'version': {
                    required: true,
                    maxlength: 11
                },
                'status': {
                    required: true
                },
                'patch': {
                    required: true,
                    maxlength: 128
                },
                'warPath': {
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