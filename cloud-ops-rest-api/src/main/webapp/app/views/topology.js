define(['App', 'common/ui/datatables', 'common/ui/modal'], function (App, DataTables, Modal) {
    return App.View({
        $table: $([]),
        ready: function () {
            var self = this;
            self.$table = $('#topology-table');

            this.initTable(function () {
                var $tableTop = $(self.$table.selector + "_top");

                self.bind('click', $('.btn-add', $tableTop), self.addTopology);
                self.bind('click', $('.btn-edit', self.$table), self.editTopology);
                self.bind("click", $(".btn-delete", self.$table), self.deleteTopology);
            });
        },
        tableAjax: function () {
            return DataTables.parseAjax(
                this,
                this.$table,
                "v1/topologies"
            );
        },
        initTable: function (callback) {
            var self = this;
            DataTables.init(this.$table, {
                serverSide: false,
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
                            return '<a href="' + self.getUrl('+/detail', {'id': data.id, 'name': data.name}) + '">' + data.name + '</a>'
                        }
                    },
                    {
                        "data": "description",
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
        addTopology: function () {
            var self = this;
            Modal.show({
                title: "新建模板配置",
                remote: function () {
                    var def = $.Deferred();
                    self.render(App.remote("+/add.html"), function (err, html) {
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
                        var keywords = App.highlight("模板配置" + topology.name, 4);
                        var processor = Modal.processing('正在保存' + keywords + '信息');
                        self.ajax.post("v1/topologies", topology, function (err, data) {
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
        editTopology: function (e) {
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
                        self.ajax.post("v1/topologies/"+id, topology, function (err, data) {
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
        deleteTopology: function () {
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