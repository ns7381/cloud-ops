define(['App', 'common/ui/datatables', 'common/ui/modal', 'common/ui/validator', 'common/ui/choseinput'], function (App, DataTables, Modal) {
    return App.View({
        ready: function() {
            var self = this;

            var $table = this.$('#UserTable');

            this.set('$table', $table);

            this.initTable(function() {
                var $tableTop = self.$('#UserTable_top');
                self.bind('click', $('.btn-add', $tableTop), self.addUser);
                self.bind('click', $('.btn-edit', $table), self.editUser);
                self.bind('click', $('.btn-delete', $table), self.deleteUser);
            });
        },
        $table: $([]),
        initTable: function(callback) {
            DataTables.init(this.$table, {
                data: [
                    {
                        'id': 1,
                        'name': "admin",
                        'trueName': "admin",
                        'phone': "13120394123",
                        'email': "1234@xx.com",
                        'status': "NORMAL"
                    },
                    {
                        'id': 2,
                        'name': "tenant",
                        'trueName': "tenant",
                        'phone': "135677394123",
                        'email': "4567@xx.com",
                        'status': "NORMAL"
                    }
                ],
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
                        "data": "trueName",
                        "width": DataTables.width("name")
                    },
                    {
                        "data": "phone",
                        "width": "10em"
                    },
                    {
                        "data": "email",
                        "minWidth": DataTables.width("name")
                    },
                    {
                        "data": "status",
                        "width": "6em",
                        "render": function (data) {
                            return App.statusLabel("user:"+data);
                        }
                    },
                    {
                        "width": DataTables.width("opt-2"),
                        "render": function (data) {
                            return (
                                '<a class="btn-edit btn-opt" data-toggle="tooltip" title="编辑"><i class="fa fa-pencil fa-fw"></i></a>' +
                                '<a class="btn-delete btn-opt" data-toggle="tooltip" title="删除"><i class="fa fa-trash-o fa-fw"></i></a>'
                            );
                        }
                    }
                ]
            }, callback);
        },
        addUser: function() {
            var self = this;
            Modal.show({
                title: "新建",
                remote: function() {
                    var def = $.Deferred();
                    self.render(App.remote("+/add.html"), function(err, html) {
                        def.resolve(html);
                    });
                    return def.promise();
                },
                onloaded: function(dialog) {
                    var $dialog = dialog.getModalDialog(),
                        $form = $("form", $dialog);
                    $form.validate({
                        errorContainer: $dialog,
                        errorPlacement: "left top",
                        rules: {
                            'name': {
                                required: true
                            },
                            'password': {
                                required: true
                            },
                            'password_again': {
                                required: true
                            },
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
                            },
                            'organId': {
                                required: false,
                                maxlength: 36,
                                ignore: ""
                            },
                            'organName': {
                                required: false
                            }
                        },
                        messages: {
                            'password_again': {
                                equalTo: '请与密码保持一致'
                            }
                        }
                    });
                },
                buttons: [
                    {
                        label: "取消",
                        action: function(dialog) {
                            dialog.close();
                        }
                    },
                    {
                        label: "确定",
                        cssClass: "btn-primary",
                        action: function(dialog) {
                            var $dialog = dialog.getModalDialog(),
                                $form = $("form", $dialog);
                            if (!$form.length || !$form.valid()) return false;
                            // do submit form
                            dialog.close();
                        }
                    }
                ]
            });
        },
        editUser: function(e) {
            var self = this;
            var rowData = $(e.currentTarget).data("row.dt").data();
            Modal.show({
                title: '编辑用户信息',
                remote: function () {
                    var def = $.Deferred();
                    self.render(App.remote("+/edit.html"), rowData, function(err, html) {
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
        deleteUser: function(e) {
            var self = this;
            var rowData = $(e.currentTarget).data("row.dt").data(),
                id = rowData.id,
                name = rowData.name;
            var keywords = App.highlight(name);
            Modal.confirm('确定要删除' + keywords + '吗?', function (bsure) {

            });
        }
    });
});