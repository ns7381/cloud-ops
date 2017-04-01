/**
 * Created by wangd on 2017/1/4.
 */
define(['App', 'common/ui/modal', 'common/ui/datatables', 'common/ui/validator'], function (App, Modal, DataTables) {
    return App.View({
        $table: $([]),
        ready: function() {
            var self = this;

            self.set('$table', self.$('#VersionTable'));

            this.initTable(function() {
                var $tableTop = self.$('#VersionTable_top');
                self.bind('click', $('.btn-add', $tableTop), self.addVersionGroup);
                self.bind('click', $('.btn-delete', self.$table), self.deleteVersionGroup);
                self.bind('click', $('.btn-online', self.$table), self.onlineVersion);
                self.bind('click', $('.btn-offline', self.$table), self.offlineVersion);
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
                        'data': "tag",
                        //  'minWidth': DataTables.width("name"),
                        'width': "6em"
                    },
                    {
                        'data': "groupName",
                        'width': "6em"
                    },
                    {
                        'data': "domain",
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
                                '<a class="btn-delete btn-opt" data-toggle="tooltip" title="删除"><i class="fa fa-trash-o fa-fw"></i></a>'+
                                '<a class="btn-online btn-opt" data-toggle="tooltip" title="上线"><i class="fa fa-toggle-on"></i></a>'+
                                '<a class="btn-offline btn-opt" data-toggle="tooltip" title="下线"><i class="fa fa-toggle-off"></i></a>'

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
                '/apigateway/version'
            );
        },
        addVersionGroup: function() {
            alert("teststt")
            var self = this;
            var $wizard = this.$element;
            Modal.show({
                title: "创建版本",
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
                    self.ajax.get('/apigateway/group', function(err, data) {
                        if (err) {
                            data = {result: []};
                        }
                        var groups = data.result, array = [];
                        $.each(groups, function(i,group) {
                            if (group.name) {
                                array.push(group);
                            }
                        });
                        self.initGroups($wizard, array);
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
                            var groupId;
                            $("select[id='versionGroup']").each(function(){
                                groupId = $(this).val();
                            });
                            var createVersionOptions = {
                                tag:     $("#name", $dialog).val(),
                                description:   $("#description", $dialog).val(),
                                group:{id:groupId}
                            };

                            var keywords = '分组' + App.highlight(createVersionOptions.name),
                                processor = Modal.processing("正在创建" + keywords);
                            dialog.close();
                            self.ajax.postJSON('/apigateway/version/add', createVersionOptions, function(err, data) {
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
        deleteVersionGroup: function(e) {
            var self = this;
            var row = $(e.currentTarget).data("row.dt"),
                rowData = row.data(),
                id = rowData.id,
                name = rowData.Name;
            var keywords = '版本' + App.highlight(name);
            Modal.confirm('确定要删除'+keywords+'吗?', function(bsure) {
                if(bsure) {
                    var processor = Modal.processing("正在删除"+keywords);
                    self.ajax.delete("/apigateway/version/"+id, function(err, data) {
                        if (err) {
                            processor.error(keywords+'删除失败。原因：'+err.message);
                        } else {
                            processor.success(keywords+'删除成功');
                            self.$table.reloadTable();
                        }
                    });
                }
            });
        },
        onlineVersion: function(e) {
            var self = this;
            var row = $(e.currentTarget).data("row.dt"),
                rowData = row.data(),
                id = rowData.id,
                name = rowData.name;
            var keywords = '分组' + App.highlight(name);
            Modal.confirm('确定要上线'+keywords+'吗?', function(bsure) {
                if(bsure) {
                    var processor = Modal.processing("正在上线"+keywords);
                    self.ajax.post("/apigateway/version/online/"+id, function(err, data) {
                        if (err) {
                            processor.error(keywords+'上线失败。原因：'+err.message);
                        } else {
                            processor.success(keywords+'上线成功');
                            self.$table.reloadTable();
                        }
                    });
                }
            });
        },
        offlineVersion: function(e) {
            var self = this;
            var row = $(e.currentTarget).data("row.dt"),
                rowData = row.data(),
                id = rowData.id,
                name = rowData.name;
            var keywords = '分组' + App.highlight(name);
            Modal.confirm('确定要下线'+keywords+'吗?', function(bsure) {
                if(bsure) {
                    var processor = Modal.processing("正在下线"+keywords);
                    self.ajax.post("/apigateway/version/offline/"+id, function(err, data) {
                        if (err) {
                            processor.error(keywords+'下线失败。原因：'+err.message);
                        } else {
                            processor.success(keywords+'下线成功');
                            self.$table.reloadTable();
                        }
                    });
                }
            });
        },
        initGroups: function($wrapper,versionGroupList) {
            var $selectGroup = $('select[name="versionGroup"]', $wrapper),
                groupPairs = [{id:"group-select-default", name:"选择一个分组"}];
            $.each(versionGroupList, function(i, group) {
                if (group) {
                    groupPairs.push({name: group.name,value: group.id});
                }
            });
            $selectGroup.html(App.uiSelect(groupPairs));
            $('#group-select-default').val("");
        }
    });
});