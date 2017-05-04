define(['App', 'common/ui/datatables', 'common/ui/modal', 'common/ui/validator'], function (App, DataTables, Modal) {
    return App.View({
        ready: function () {
            var self = this;

            var $table = this.$('#UserTable');

            this.set('$table', $table);

            this.initTable(function () {
                var $tableTop = self.$('#UserTable_top');
                self.bind('click', $('.btn-add', $tableTop), self.addUser);
                self.bind('click', $('.btn-edit', $table), self.editUser);
                self.bind('click', $('.btn-delete', $table), self.deleteUser);
            });
        },
        $table: $([]),
        tableAjax: function () {
            return DataTables.parseAjax(
                this,
                this.$table,
                "v1/users"
            );
        },
        initTable: function (callback) {
            DataTables.init(this.$table, {
                serverSide: false,
                ajax: this.tableAjax(),
                columns: [
                    {
                        "width": DataTables.width("check"),
                        "defaultContent": "<label><input type='checkbox'></label>"
                    },
                    {
                        "data": "username",
                        "width": DataTables.width("name")
                    },
                    {
                        "data": "roles",
                        "width": DataTables.width("name"),
                        "render": function (data) {
                            var html = "";
                            $.each(data || [], function (k, role) {
                                html += role.role + "   ";
                            });
                            return html;
                        }
                    },
                    {
                        "width": DataTables.width("opt-2"),
                        "render": function (data) {
                            return (
                                // '<a class="btn-edit btn-opt" data-toggle="tooltip" title="编辑"><i class="fa fa-pencil fa-fw"></i></a>' +
                                '<a class="btn-delete btn-opt" data-toggle="tooltip" title="删除"><i class="fa fa-trash-o fa-fw"></i></a>'
                            );
                        }
                    }
                ]
            }, callback);
        },
        addUser: function () {
            var self = this;
            Modal.show({
                title: "新建用户",
                remote: function () {
                    var def = $.Deferred();
                    self.render({
                        url: "+/add.html",
                        data: App.remote('v1/users/roles'),
                        dataFilter: function (err, data) {
                            return {roles: data};
                        },
                        callback: function (err, html) {
                            def.resolve(html);
                        }
                    });
                    return def.promise();
                },
                onloaded: function (dialog) {
                    var $dialog = dialog.getModalDialog(),
                        $form = $("form", $dialog);
                    self.formValidate($form);
                    self.initCheckBox();
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
                        var $dialog = dialog.getModalDialog(),
                            $form = $("form", $dialog);
                        if (!$form.length || !$form.valid()) return false;
                        var user = $(".form-horizontal", $dialog).serializeObject(),
                            roles = [];
                        if ($.isArray(user.role)) {
                            $.each(user.role, function (k, role) {
                                roles.push({role: role});
                            });
                        } else if (user.role) {
                            roles.push({role: user.role});
                        }
                        user.roles = roles;
                        delete user.role;
                        delete user.password_again;
                        var keywords = App.highlight("用户" + user.username, 2),
                            processor = Modal.processing('正在保存' + keywords + '信息');
                        self.ajax.postJSON("v1/users", user, function (err, data) {
                            if (err) {
                                processor.error(keywords + '创建失败!原因：' + err.message);
                            } else {
                                dialog.close();
                                processor.success(keywords + '创建成功');
                                self.$table.reloadTable();
                            }
                        });
                    }
                }
                ]
            });
        },
        editUser: function (e) {
            var self = this;
            var rowData = $(e.currentTarget).data("row.dt").data();
            Modal.show({
                title: '编辑用户信息',
                remote: function () {
                    var def = $.Deferred();
                    self.render(App.remote("+/edit.html"), rowData, function (err, html) {
                        def.resolve(html);
                    });
                    return def.promise();
                },
                onloaded: function (dialog) {
                    var $dialog = dialog.getModalDialog(),
                        $form = $("form", $dialog);
                    $form.validate({
                        errorContainer: $dialog,
                        errorPlacement: "left top",
                        rules: {
                            'phone': {
                                mobile: true
                            },
                            'email': {
                                required: false,
                                email: true,
                                maxlength: 50
                            },
                            'trueName': {
                                maxlength: 50
                            }
                        }
                    });
                },
                buttons: [{
                    label: '取消',
                    action: function (dialog) {
                        dialog.close();
                    }
                }, {
                    label: '保存',
                    cssClass: "btn-primary",
                    action: function (dialog) {
                        var $dialog = dialog.getModalDialog(),
                            $form = $("form", $dialog);
                        if (!$form.length || !$form.valid()) return false;
                        // do submit form
                        dialog.close();
                    }
                }]
            });
        },
        deleteUser: function (e) {
            var self = this;
            var rowData = $(e.currentTarget).data("row.dt").data(),
                id = rowData.id,
                name = rowData.username;
            var keywords = App.highlight('用户' + name, 2);
            Modal.confirm({
                title: "删除用户",
                message: "确定要删除" + keywords + "吗",
                callback: function (result) {
                    if (result) {
                        var processor = Modal.processing("正在删除" + keywords);
                        self.ajax.delete("v1/users/" + id, function (err, data) {
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
        initCheckBox: function () {
            $('input[type="checkbox"],input[type="radio"]').iCheck({
                checkboxClass: "icheckbox-info",
                radioClass: "iradio-info"
            });
        },
        formValidate: function ($form) {
            $form.validate({
                rules: {
                    'name': {
                        required: true
                    },
                    'password': {
                        required: true
                    },
                    'password_again': {
                        required: true,
                        equalTo: "#password"
                    }
                },
                messages: {
                    'password_again': {
                        equalTo: '请与密码保持一致'
                    }
                }
            });
        }
    });
});