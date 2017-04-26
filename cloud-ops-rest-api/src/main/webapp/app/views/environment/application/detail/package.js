define(['App', 'common/ui/datatables', 'common/ui/modal', 'common/ui/websocket', 'common/ui/validator', 'common/ui/filedownload', 'bs/tab', 'jq/form'], function (App, DataTables, Modal, WS) {
    return App.View({
        app_id: "",
        app_name: "",
        environmentId: "",
        environmentName: "",
        table: null,
        $table: $([]),
        topology: {},
        patch_containers: [],
        war_containers: [],
        data: function () {
            this.app_id = App.getParam('id');
            this.app_name = App.getParam('name');
            this.environmentId = App.getParam('environmentId');
            this.environmentName = App.getParam('environmentName');
            return App.remote("/v1/applications/" + this.app_id);
        },
        dataFilter: function (err, data) {
            this.topology = data;
        },
        ready: function () {
            var self = this;
            self.patch_containers = [];
            self.app_id = App.getParam('id');
            self.$table = $('#packageTable');
            $.each(self.topology.nodes || [], function (k, node) {
                if (node.type == "tosca.nodes.deploy.Tomcat") {
                    $.each(node.artifacts || [], function (name, artifact) {
                        if (artifact.type == "tosca.artifacts.PatchFile") {
                            self.patch_containers.push(node);
                        } else if (artifact.type == "tosca.artifacts.WarFile") {
                            self.war_containers.push(node);
                        }
                    })
                }
            });
            this.initTable(function () {
                var $tableTop = $(self.$table.selector + "_top");
                self.bind('click', $('.btn-config', $tableTop), self.configPackage);
                self.bind('click', $('.btn-add', $tableTop), self.addPackage);
                // self.bind('click', $('.btn-git', $tableTop), self.gitPackage);
                // self.bind('click', $('.btn-upload', $tableTop), self.uploadPackage);
                // self.bind('click', $('.btn-patch', $tableTop), self.patchPackage);
                self.bind('click', $('.btn-download', self.$table), self.downloadPackage);
                self.bind('click', $('.btn-deploy', self.$table), self.deploy);
                self.bind('click', $('.btn-delete', self.$table), self.deletePackage);
            });
            self.initWebsocket();
        },
        tableAjax: function () {
            return DataTables.parseAjax(
                this,
                this.$table,
                "v1/resource-packages?applicationId=" + this.app_id
            );
        },
        initTable: function (callback) {
            var self = this;
            self.table = DataTables.init(this.$table, {
                serverSide: false,
                ajax: this.tableAjax(),
                columns: [
                    {
                        data: {},
                        "width": DataTables.width("check"),
                        "defaultContent": "<label><input type='checkbox'></label>"
                    },
                    {
                        "data": "version",
                        "width": DataTables.width("name")
                    },
                    {
                        "data": "createdAt",
                        "width": DataTables.width("name")
                    },
                    {
                        "data": "status",
                        "width": DataTables.width("name")
                    },
                    {
                        "data": {},
                        "width": DataTables.width("opt"),
                        "render": function (data) {
                            if (data.status == "FINISH" && (data.type == "PATCH" && self.patch_containers.length)
                                || (data.type == "WAR" && self.war_containers.length)) {
                                return [
                                    '<a class="btn-opt btn-download" data-toggle="tooltip" href="javascript:void(0)" title="下载">',
                                    '<i class="fa fa-cloud-download"></i>',
                                    '</a>',
                                    '<a class="btn-opt btn-deploy" data-toggle="tooltip" href="javascript:void(0)" title="部署">',
                                    '<i class="fa fa-play"></i>',
                                    '</a>',
                                    '<a class="btn-opt btn-delete" data-toggle="tooltip" href="javascript:void(0)" title="删除">',
                                    '<i class="fa fa-trash-o"></i>',
                                    '</a>'
                                ].join('');
                            } else if(data.warPath){
                                return [
                                    '<a class="btn-opt btn-download" data-toggle="tooltip" href="javascript:void(0)" title="下载">',
                                    '<i class="fa fa-cloud-download"></i>',
                                    '</a>',
                                    '<a class="btn-opt btn-delete" data-toggle="tooltip" href="javascript:void(0)" title="删除">',
                                    '<i class="fa fa-trash-o"></i>',
                                    '</a>'
                                ].join('');
                            } else {
                                return [
                                    '<a class="btn-opt btn-delete" data-toggle="tooltip" href="javascript:void(0)" title="删除">',
                                    '<i class="fa fa-trash-o"></i>',
                                    '</a>'
                                ].join('');
                            }
                        }
                    }
                ]
            }, callback);
        },
        configPackage: function () {
            var self = this;
            Modal.show({
                title: "打包配置",
                remote: function () {
                    var def = $.Deferred();
                    self.render({
                        url: "+/config.html",
                        data: App.remote('v1/package-configs?applicationId=' + self.app_id),
                        dataFilter: function (err, data) {
                            return data;
                        },
                        callback: function (err, html) {
                            def.resolve(html);
                        }
                    });
                    return def.promise();
                },
                onloaded: function (dialog) {
                    var $dialog = dialog.getModalDialog();
                    self.getBranches($dialog);
                    self.formValidate($dialog);
                },
                buttons: [{
                    label: "取消",
                    action: function (dialog) {
                        dialog.close();
                    }
                }, {
                    label: "确定",
                    cssClass: "btn-primary",
                    action: function (dialog) {
                        var $modal = dialog.getModal();
                        var valid = $(".form-horizontal", $modal).valid();
                        if (!valid) return false;
                        var config = $(".form-horizontal", $modal).serializeObject();
                        config.applicationId = self.app_id;
                        var keywords = App.highlight("打包配置" + config.gitUrl, 4);
                        var processor = Modal.processing('正在保存' + keywords + '信息');
                        self.ajax.postJSON("v1/package-configs", config, function (err, data) {
                            if (err) {
                                processor.error(keywords + '创建失败!原因：' + err.message);
                            } else {
                                dialog.close();
                                processor.success(keywords + '创建成功');
                                self.$table.reloadTable();
                            }
                        });
                    }
                }]
            });
        },
        addPackage: function () {
            var self = this;
            Modal.show({
                title: "新增",
                remote: function () {
                    var def = $.Deferred();
                    self.render({
                        url: "+/add.html",
                        data: App.remote('v1/package-configs?applicationId=' + self.app_id),
                        dataFilter: function (err, data) {
                            return {config: data, rows: self.table.data("row.dt").data()};
                        },
                        callback: function (err, html) {
                            def.resolve(html);
                        }
                    });
                    return def.promise();
                },
                onloaded: function (dialog) {
                    var $dialog = dialog.getModalDialog();
                    self.formValidate($dialog);
                    $('#type', $dialog).on('change', function () {
                        var $method = $('#method', $dialog),
                            type = $(this).val(),
                            method = $method.val();
                        self.changeTypeOrMethod(type, method, $dialog);
                    });
                    $('#method', $dialog).on('change', function () {
                        var $method = $('#method', $dialog),
                            type = $('#type', $dialog).val(),
                            method = $method.val();
                        self.changeTypeOrMethod(type, method, $dialog);
                    });
                    $('#type', $dialog).trigger('change');
                    $('#method', $dialog).trigger('change');
                },
                buttons: [{
                    label: "取消",
                    action: function (dialog) {
                        dialog.close();
                    }
                }, {
                    label: "确定",
                    cssClass: "btn-primary",
                    action: function (dialog) {
                        var $modal = dialog.getModal();
                        var valid = $(".form-horizontal", $modal).valid();
                        if (!valid) return false;
                        var package = $(".form-horizontal", $modal).serializeObject();
                        package.applicationId = self.app_id;
                        var keywords = App.highlight((package.type == "WAR" ? "全量包" : "增量包") + package.version, 3);
                        var processor = Modal.processing('正在保存' + keywords + '信息');
                        if ($('#method', $modal).val() == "file" && $("#file").val()) {
                            var $warForm = $("#package-form", $modal);
                            $warForm.attr("method", "post");
                            $warForm.attr("enctype", "multipart/form-data");
                            $warForm.attr("action", "v1/resource-packages/upload");

                            $warForm.ajaxSubmit({
                                success: function (data) {
                                    if (data && !data.error) {
                                        dialog.close();
                                        processor.success(keywords + '保存成功');
                                        self.$table.reloadTable();
                                    } else if (data && data.error) {
                                        processor.warning(keywords + '保存失败');
                                        Modal.processing().error(data.message);
                                    } else {
                                        processor.error(keywords + '保存失败');
                                    }
                                },
                                error: function (err) {
                                    processor.error(keywords + '保存失败。原因：' + App.json.parse(err.responseText).message);
                                }
                            });
                        } else if($('#type', $modal).val() == "WAR" ){
                            self.ajax.putJSON('v1/resource-packages/git', package, function (err, data) {
                                if (err) {
                                    processor.error(keywords + '保存失败!原因：' + err.message);
                                } else {
                                    dialog.close();
                                    processor.success(keywords + '保存成功');
                                    self.$table.reloadTable();
                                }
                            });
                        } else {
                            delete package.build;
                            delete package.type;
                            delete package.version;
                            self.ajax.post('v1/resource-packages/patch' + self.setUrlK(package), function (err, data) {
                                if (err) {
                                    processor.error(keywords + '保存失败!原因：' + err.message);
                                } else {
                                    dialog.close();
                                    processor.success(keywords + '保存成功');
                                    self.$table.reloadTable();
                                }
                            });
                        }
                    }
                }]
            });
        },
        downloadPackage: function (e) {
            var self = this;
            var row = $(e.currentTarget).data("row.dt"),
                rowData = row.data(),
                id = rowData.id,
                name = rowData.version;
            var keywords = App.highlight('程序包' + name, 3);
            Modal.confirm({
                title: "下载程序包",
                message: '确定要下载' + keywords + '吗?',
                callback: function (result) {
                    if (result) {
                        var processor = Modal.processing("正在下载" + keywords);
                        $.fileDownload(App.getRootUrl('v1/resource-packages/' + id + "/download"), {
                            successCallback: function (url) {
                                processor.success(keywords + '下载成功');
                            },
                            failCallback: function (html, url) {
                                processor.error(keywords + '下载失败');
                            }
                        });
                    }
                }
            });
        },
        deletePackage: function (e) {
            var self = this;
            var row = $(e.currentTarget).data("row.dt"),
                rowData = row.data(),
                id = rowData.id,
                name = rowData.version;
            var keywords = App.highlight('程序包' + name, 3);
            Modal.confirm({
                title: "删除程序包",
                message: "确定要删除" + keywords + "吗",
                callback: function (result) {
                    if (result) {
                        var processor = Modal.processing("正在删除" + keywords);
                        self.ajax.delete("v1/resource-packages/" + id, function (err, data) {
                            if (data) {
                                processor.success(keywords + '删除成功');
                                self.$table.reloadTable();
                            }
                            if (err) {
                                self.onError(err, function (err) {
                                    processor.error(keywords + '删除失败。原因：' + err.message);
                                })
                            }
                        });
                    }
                }
            });
        },
        deploy: function (e) {
            var self = this;
            var row = $(e.currentTarget).data("row.dt"),
                rowData = row.data(),
                id = rowData.id,
                type = rowData.type,
                name = rowData.version;
            var keywords = App.highlight('程序包' + name, 3),
                html = '',
                containers = type == "PATCH" ? self.patch_containers : self.war_containers;
            $.each(containers, function (k, v) {
                html += '<input type="radio" name="container" value="' + v.name + '"' + (k == 0 ? "checked" : "") + '>' + v.name;
            });
            html = containers.length == 1 ? containers[0].name : html;
            Modal.confirm({
                title: "部署程序包",
                message: '确定将' + keywords + '部署在'+html+'上吗?',
                callback: function (result) {
                    if (result) {
                        var $modal = this.getModalBody(),
                            choose = containers.length == 1 ? containers[0].name :$(":radio:checked", $modal).val();
                        var processor = Modal.processing("正在部署" + keywords);
                        self.ajax.put('v1/applications/' + self.app_id + "/node/"+choose+"/deploy/"+id, function (err, data) {
                            if (err) {
                                processor.error(keywords + '部署失败!原因：' + err.message);
                            } else {
                                processor.success(keywords + '开始部署');
                                App.go(self.getUrl("./", {'id': self.app_id, 'name': self.app_name,
                                    'environmentId': self.environmentId, 'environmentName': self.environmentName, 'tab': "application-task"}));
                            }
                        });
                    }
                }
            });
        },
        changeTypeOrMethod: function (type, method, $dialog) {
            if (type == "PATCH" && method == "file") {
                $('#versionDiv', $dialog).show();
                $('#fileDiv', $dialog).show();
                $('#gitDiv', $dialog).hide();
                $('#patchDiv', $dialog).hide();
            } else if (type == "PATCH" && method == "build") {
                $('#versionDiv', $dialog).hide();
                $('#fileDiv', $dialog).hide();
                $('#gitDiv', $dialog).hide();
                $('#patchDiv', $dialog).show();
            } else if (type == "WAR" && method == "file") {
                $('#versionDiv', $dialog).show();
                $('#fileDiv', $dialog).show();
                $('#gitDiv', $dialog).hide();
                $('#patchDiv', $dialog).hide();
            } else if (type == "WAR" && method == "build") {
                $('#versionDiv', $dialog).show();
                $('#fileDiv', $dialog).hide();
                $('#gitDiv', $dialog).show();
                $('#patchDiv', $dialog).hide();
            }
        },
        setUrlK: function (ojson) {
            var s = '', name, key, init = true;
            for (var p in ojson) {
                if (!ojson[p]) {
                    return null;
                }
                if (ojson.hasOwnProperty(p)) {
                    name = p
                }
                key = ojson[p];
                s += (init ? "?" : "&") + name + "=" + encodeURIComponent(key);
                init = false;
            }
            return s;
        },
        initWebsocket: function () {
            var self = this;
            WS.open("package.status", function (event) {
                var payload = JSON.parse(event.data), isContained = false;
                var $tr = $('tr[index="' + payload.id + '"]');
                if ($tr[0] && self.table) {
                    var row = self.table.row($tr[0]),
                        rowData = row.data();
                    rowData.status = payload.status;
                    row.data(rowData);
                }
            });
        },
        getBranches: function ($modalBody) {
            var self = this;
            $modalBody.on('click', '#btn-connect', function () {
                var gitUrl = $("#gitUrl", $modalBody).val().trim(),
                    gitUsername = $("#gitUsername", $modalBody).val().trim(),
                    gitPassword = $("#gitPassword", $modalBody).val().trim();
                var config = {gitUrl: gitUrl, gitUsername: gitUsername, gitPassword: gitPassword};
                self.ajax.putJSON("v1/package-configs/branches", config, function (err, branches) {
                    if (branches) {
                        if (branches.length == 0) {
                            Modal.warning("当前配置的Git仓库不可用");
                            return false;
                        }
                        var $branch = $("#branch", $modalBody);
                        $branch.empty();
                        $.each(branches, function (n, branch) {
                            $branch.append("<option value='" + branch + "'>" + branch + "</option>");
                        });
                    }
                    if (err) {
                        self.onError(err, function (err) {
                            Modal.error('当前配置的Git仓库不可用' + err.message);
                        })
                    }
                });
            });
        },
        formValidate: function ($modal) {
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
    });
});