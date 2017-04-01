/**
 * Created by wangd on 2017/1/4.
 */
define(['App', 'common/ui/modal', 'common/ui/datatables', 'common/ui/validator'], function (App, Modal, DataTables) {
    return App.View({
        $table: $([]),
        ready: function() {
            var self = this;

            self.set('$table', self.$('#GroupTable'));

            this.initTable(function() {
                var $tableTop = self.$('#GroupTable_top');

                self.bind('click', $('.btn-add', $tableTop), self.addGroup);
                self.bind('click', $('.btn-delete', self.$table), self.deleteGroup);
            });
        },
        initTable: function(callback) {
            DataTables.init(this.$table, {
                serverSide: true,
                ajax: this.tableAjax(),
                columns: [
                    {
                        'width': DataTables.width("check"),
                        'defaultContent': "<label><input type='checkbox'></label>"
                    },
                    {
                        'data': "name",
                      //  'minWidth': DataTables.width("name"),
                        'width': "6em"
                    },
                    {
                        'data': "description",
                        'width': "6em"
                    },
                    {
                        'data': "createTime",
                        'width': "8em"
                    },
                    {
                        'data': {},
                        'width': DataTables.width("opt-2"),
                        'render': function(data) {
                            return (
                                '<a class="btn-delete btn-opt" data-toggle="tooltip" title="删除"><i class="fa fa-trash-o fa-fw"></i></a>'

                            );
                        }
                    }
                ]
            }, callback);
        },
        tableAjax: function() {
            return DataTables.parseAjax(
                this,
                this.$table,
                '/apigateway/group'
            );
        },
        addGroup: function() {
            var self = this;
            Modal.show({
                title: "创建Group",
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
                            var createGroupOptions = {
                                name:     $("#name", $dialog).val(),
                                description:   $("#description", $dialog).val()
                            };
                            alert(JSON.stringify(createGroupOptions))
                            var keywords = '分组' + App.highlight(createGroupOptions.name),
                                processor = Modal.processing("正在创建" + keywords);
                            dialog.close();
                            self.ajax.postJSON('/apigateway/group/add', createGroupOptions, function(err, data) {
                                if (err) {
                                    processor.error(keywords + '创建失败。原因：'+err.message);
                                } else {
                                    processor.success(keywords + '创建成功');
                                    self.$table.reloadTable();
                                }
                            });
                        }
                    }
                ]
            });
        },
        deleteGroup: function(e) {
            var self = this;
            var row = $(e.currentTarget).data("row.dt"),
                rowData = row.data(),
                id = rowData.Uuid,
                name = rowData.Name;
            var keywords = '分组' + App.highlight(name);
            Modal.confirm('确定要删除'+keywords+'吗?', function(bsure) {
                if(bsure) {
                    var processor = Modal.processing("正在删除"+keywords);
                    self.ajax.delete("/apigateway/group/"+id, function(err, data) {
                        if (err) {
                            processor.error(keywords+'删除失败。原因：'+err.message);
                        } else {
                            processor.success(keywords+'删除成功');
                            self.$table.reloadTable();
                        }
                    });
                }
            });
        }
    });
});
