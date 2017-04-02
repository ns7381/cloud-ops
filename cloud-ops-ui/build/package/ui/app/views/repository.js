define(['App', 'common/ui/datatables', 'common/ui/modal'], function (App, DataTables, Modal) {
    return App.View({
        $table: $([]),
        ready: function () {
            var self = this;
            self.$table = $('#repository-table');

            this.initTable(function () {
                var $tableTop = $(self.$table.selector + "_top");

                self.bind('click', $('.btn-add', $tableTop), self.addRepository);
                self.bind("click", $(".btn-delete", self.$table), self.deleteRepository);
            });
        },
        tableAjax: function () {
            return DataTables.parseAjax(
                this,
                this.$table,
                "resource/repository"
            );
        },
        initTable: function (callback) {
            DataTables.init(this.$table, {
                serverSide: true,
                ajax: this.tableAjax(),
                columns: [
                    {
                        "width": DataTables.width("check"),
                        "defaultContent": "<label><input type='checkbox'></label>"
                    },
                    {
                        "data": "repositoryUrl",
                        "width": DataTables.width("name")
                    },
                    {
                        "data": "localDir",
                        "width": DataTables.width("name")
                    },
                    {
                        "data": "status",
                        "width": '9em',
                        "render": function (data) {
                            return App.statusLabel("repository:"+Status);
                        }
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
        addRepository: function () {
            var self = this;
            Modal.show({
                title: "新建仓库",
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
                        var repository = $(".form-horizontal", $modal).serializeObject();
                        var keywords = App.highlight("仓库" + repository.repositoryUrl, 2);
                        var processor = Modal.processing('正在保存' + keywords + '信息');
                        self.ajax.post("resource/repository", repository, function (err, data) {
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
        deleteRepository: function () {
            var self = this;
            var row = $(e.currentTarget).data("row.dt"),
                rowData = row.data(),
                id = rowData.id,
                name = rowData.repositoryUrl;
            var keywords = App.highlight('仓库' + name, 2);
            Modal.confirm({
                title: "删除仓库",
                message: "确定要删除" + keywords + "吗",
                callback: function (result) {
                    if (result) {
                        var processor = Modal.processing("正在删除" + keywords);
                        self.ajax.delete("resource/repository/" + id, function (err, data) {
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
    })
});