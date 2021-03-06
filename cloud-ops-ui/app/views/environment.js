define(['App', 'common/ui/datatables', 'common/ui/modal'], function (App, DataTables, Modal) {
    return App.View({
        $table: $([]),
        data: function () {
            return App.remote("v1/environments");
        },
        dataFilter: function(err, data){
            return {locations: data};
        },
        ready: function () {
            var self = this;
            self.$table = $('#location-table');
            self.bind('click', $('.addLocation'), self.addLocation);
/*
            this.initTable(function () {
                var $tableTop = $(self.$table.selector + "_top");

                self.bind('click', $('.btn-add', $tableTop), self.addLocation);
                self.bind('click', $('.btn-edit', self.$table), self.editLocation);
                self.bind("click", $(".btn-delete", self.$table), self.deleteLocation);
            });*/
        },
        tableAjax: function () {
            return DataTables.parseAjax(
                this,
                this.$table,
                "v1/environments"
            );
        },
        addLocation: function () {
            var self = this;
            Modal.show({
                title: "新建环境",
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
                        var location = $(".form-horizontal", $modal).serializeObject();
                        var keywords = App.highlight("环境" + location.name, 2);
                        var processor = Modal.processing('正在保存' + keywords + '信息');
                        self.ajax.postJSON("v1/environments", location, function (err, data) {
                            if (err) {
                                processor.error(keywords + '创建失败!原因：' + err.message);
                            } else {
                                dialog.close();
                                processor.success(keywords + '创建成功');
                                App.go();
                            }
                        });
                    }
                }]
            });
        },
        editLocation: function (e) {
            var self = this;
            var row = $(e.currentTarget).data("row.dt"),
                rowData = row.data(),
                id = rowData.id,
                name = rowData.name;
            var keywords = App.highlight('环境' + name, 2);
            Modal.show({
                title: "编辑环境",
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
                        var location = $(".form-horizontal", $modal).serializeObject();
                        var processor = Modal.processing('正在保存' + keywords + '信息');
                        self.ajax.putJSON("v1/environments", location, function (err, data) {
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
        deleteLocation: function (e) {
            var self = this;
            var row = $(e.currentTarget).data("row.dt"),
                rowData = row.data(),
                id = rowData.id,
                name = rowData.name;
            var keywords = App.highlight('环境' + name, 2);
            Modal.confirm({
                title: "删除环境",
                message: "确定要删除" + keywords + "吗",
                callback: function (result) {
                    if (result) {
                        var processor = Modal.processing("正在删除" + keywords);
                        self.ajax.delete("v1/environments/" + id, function (err, data) {
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