define(['App', 'common/ui/datatables', 'common/ui/modal'],
    function (App, DataTables, Modal) {
        return App.View({
            app_id: "",
            app_name: "",
            environmentId: "",
            environmentName: "",
            table: null,
            $table: $([]),
            ready: function () {
                var self = this;
                this.app_id = App.getParam('id');
                this.app_name = App.getParam('name');
                this.environmentId = App.getParam('environmentId');
                this.environmentName = App.getParam('environmentName');
                self.$table = $('#fileTable');

                this.initTable(function () {
                    var $tableTop = $(self.$table.selector + "_top");

                    self.bind('click', $('.btn-add', $tableTop), self.addRow);
                    self.bind("click", $(".btn-delete", self.$table), self.deleteRow);
                });
            },
            tableAjax: function () {
                return DataTables.parseAjax(
                    this,
                    this.$table,
                    "v1/package-files?applicationId=" + this.app_id
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
                            "data": "name",
                            "width": DataTables.width("name")
                        },
                        {
                            "data": "type",
                            "width": DataTables.width("name")
                        },
                        {
                            "data": {},
                            "width": DataTables.width("opt"),
                            "render": function (data) {
                                return [
                                    '<a class="btn-opt btn-delete" data-toggle="tooltip" href="javascript:void(0)" title="删除">',
                                    '<i class="fa fa-trash-o"></i>',
                                    '</a>'
                                ].join('');
                            }
                        }
                    ]
                }, callback);
            },
            addRow: function () {
                var self = this, topologies = [], topologyChoose = {}, nodes = [];
                Modal.show({
                    title: "新建配置",
                    remote: function () {
                        var def = $.Deferred();
                        self.render(App.remote("+/add.html"), {appId: self.app_id}, function (err, html) {
                            def.resolve(html);
                        });
                        return def.promise();
                    },
                    onloaded: function (dialog) {
                        var $dialog = dialog.getModalDialog();
                        self.formValidate($dialog);
                        $("#type", $dialog).on('change', function () {
                            $("#seedDiv", $dialog).toggle();
                        });
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
                            var file = $(".form-horizontal", $modal).serializeObject();
                            var keywords = App.highlight("配置" + file.name, 2);
                            var processor = Modal.processing('正在保存' + keywords + '信息');
                            self.ajax.postJSON("v1/package-files", file, function (err, data) {
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
            deleteRow: function (e) {
                var self = this;
                var row = $(e.currentTarget).data("row.dt"),
                    rowData = row.data(),
                    id = rowData.id,
                    name = rowData.name;
                var keywords = App.highlight('配置' + name, 2);
                Modal.confirm({
                    title: "删除配置",
                    message: "确定要删除" + keywords + "吗",
                    callback: function (result) {
                        if (result) {
                            var processor = Modal.processing("正在删除" + keywords);
                            self.ajax.delete("v1/package-files/" + id, function (err, data) {
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