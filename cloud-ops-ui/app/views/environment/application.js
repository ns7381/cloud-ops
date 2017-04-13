define(['App', 'common/ui/datatables', 'common/ui/modal', 'rq/text!app/templates/environment/application/locationTpl.html'],
    function (App, DataTables, Modal, LocationTpl) {
        return App.View({
            $table: $([]),
            applicationId: '',
            environmentName: '',
            ready: function () {
                var self = this;
                self.applicationId = App.getParam('id');
                self.environmentName = App.getParam('name');
                self.$table = $('#appTable');

                this.initTable(function () {
                    var $tableTop = $(self.$table.selector + "_top");

                    self.bind('click', $('.btn-add', $tableTop), self.addApp);
                    self.bind('click', $('.btn-edit', self.$table), self.editApp);
                    self.bind("click", $(".btn-delete", self.$table), self.deleteApp);
                });
            },
            tableAjax: function () {
                return DataTables.parseAjax(
                    this,
                    this.$table,
                    "v1/applications?environmentId=" + this.applicationId
                );
            },
            initTable: function (callback) {
                var self = this;
                DataTables.init(this.$table, {
                    serverSide: true,
                    ajax: this.tableAjax(),
                    columns: [
                        {
                            "width": DataTables.width("check"),
                            "defaultContent": "<label><input type='checkbox'></label>"
                        },
                        {
                            "data": {},
                            "width": DataTables.width("name"),
                            "render": function (data) {
                                return '<a href="' + self.getUrl('+/detail', {'id': data.id, 'name': data.name}) + '">' + data.name + '</a>';
                            }
                        },
                        {
                            "data": "topologyName",
                            "width": DataTables.width("name")
                        },
                        {
                            "data": {},
                            "width": DataTables.width("opt"),
                            "render": function (data) {
                                return [
                                    '<a class="btn-opt btn-edit" data-toggle="tooltip" href="javascript:void(0)" title="编辑">',
                                    '<i class="fa fa-pencil"></i>',
                                    '</a>',
                                    '<a class="btn-opt btn-delete" data-toggle="tooltip" href="javascript:void(0)" title="删除">',
                                    '<i class="fa fa-trash-o"></i>',
                                    '</a>'
                                ].join('');
                            }
                        }
                    ]
                }, callback);
            },
            addApp: function () {
                var self = this, topologies = [], topology = {};
                Modal.show({
                    title: "新建应用",
                    size: {
                        width: '740px'
                    },
                    remote: function () {
                        var def = $.Deferred();
                        self.render({
                            url: '+/add.html',
                            data: App.remote("/v1/topologies/computes"),
                            dataFilter: function (err, data) {
                                topologies = data;
                                return {topologies: data};
                            },
                            callback: function (err, html, data) {
                                def.resolve(html);
                            }
                        });
                        return def.promise();
                    },
                    onloaded: function (dialog) {
                        var $dialog = dialog.getModalDialog();
                        self.formValidate($dialog);
                        var $topologyId = $("#topologyId", dialog.getModal());
                        var $location = $("#location", dialog.getModal());
                        $topologyId.off('change').on('change', function () {
                            var topologyId = $topologyId.val();
                            $location.empty();
                            $.each(topologies, function (k, topo) {
                                if (topo.id == topologyId) {
                                    topology = topo;
                                    $location.append(self.template.render(LocationTpl, topo));
                                    dialog.setPosition();
                                }
                            });
                        });
                        $topologyId.trigger("change");
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
                            var app = $(".form-horizontal", $modal).serializeObject();
                            var locations = {};
                            $.each(topology.nodes, function (k, node) {
                                var location = {};
                                location.user = app[node.name + "user"];
                                location.password = app[node.name + "password"];
                                location.hosts = app[node.name + "hosts"];
                                locations[node.name] = location;
                                delete app[node.name + "user"];
                                delete app[node.name + "password"];
                                delete app[node.name + "hosts"];
                            });
                            app.locations = locations;
                            app.environmentId = self.applicationId;
                            app.topologyName = topology.name;
                            var keywords = App.highlight("应用" + app.name, 2);
                            var processor = Modal.processing('正在保存' + keywords + '信息');
                            self.ajax.postJSON("v1/applications", app, function (err, data) {
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
            editApp: function (e) {
                var self = this;
                var row = $(e.currentTarget).data("row.dt"),
                    rowData = row.data(),
                    id = rowData.id,
                    name = rowData.name;
                var keywords = App.highlight('模板配置' + name, 4);
                Modal.show({
                    title: "编辑模板配置",
                    remote: function () {
                        var def = $.Deferred();
                        self.render(App.remote("+/edit.html"), rowData, function (err, html) {
                            def.resolve(html);
                        });
                        return def.promise();
                    },
                    onloaded: function (dialog) {
                        var $dialog = dialog.getModalDialog();
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
                            var topology = $(".form-horizontal", $modal).serializeObject();
                            var processor = Modal.processing('正在保存' + keywords + '信息');
                            self.ajax.post("v1/topologies/" + id, topology, function (err, data) {
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
            deleteApp: function () {
                var self = this;
                var row = $(e.currentTarget).data("row.dt"),
                    rowData = row.data(),
                    id = rowData.id,
                    name = rowData.name;
                var keywords = App.highlight('模板配置' + name, 4);
                Modal.confirm({
                    title: "删除模板配置",
                    message: "确定要删除" + keywords + "吗",
                    callback: function (result) {
                        if (result) {
                            var processor = Modal.processing("正在删除" + keywords);
                            self.ajax.delete("v1/topology/" + id, function (err, data) {
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