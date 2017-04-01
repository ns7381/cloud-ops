/**
 * Created by wangd on 2017/2/17.
 */
define(['App', 'common/ui/datatables', 'common/ui/modal'], function(App, DataTables, Modal) {
    return App.View({
        data: App.remote("/apigateway/api"),
        dataFilter: function(err, data) {
            return {data: data};
        },
        ready: function() {
            var self = this;
            self.set('$table', self.$('#AuthorizeTable'));

            this.initTable(function() {
                var $tableTop = self.$('#AuthorizeTable_top');
                self.bind('click', $('.btn-add', $tableTop), self.addAuth);
                self.bind('click', $('.btn-delete', self.$table), self.deleteAuth);
            });
        },
        $table: $([]),
        initTable: function(callback) {
            var self = this;
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
                        'width': "6em",
                        'render': function (data, type, rowData, cellApi) {
                            return '<a href="' + self.getUrl('+/detail', {
                                    id: rowData.id,
                                    name: rowData.name
                                }) + '">' + data + '</a>';
                        }
                    },
                    {
                        'data': "appName",
                        'width': "6em"
                    },
                    {
                        'data': "groupName",
                        'width': "6em"
                    },
                    {
                        'data': "versionName",
                        'width': "6em"
                    },
                    {
                        'data': "id",
                        'width': "6em"
                    },
                    {
                        'data': "authInfo",
                        'width': "8em"
                    },
                    {
                        'data': "visitFrequency",
                        'width': "8em"
                    },
                    {
                    'data': "visitTotal",
                     'width': "8em"
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
                '/apigateway/auth'
            );
        },
        addAuth: function() {
            var self = this;
            var $wizard = this.$element;
            Modal.wizard({
                title: "创建授权",
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
                        self.initAuthGroups($wizard, array);
                    });
                    $("#selectGroup").change(function(){
                        var groupId=$("#selectGroup").val();
                        self.ajax.get('/apigateway/group/'+groupId, function(err, data) {
                            if (err) {
                                data = {result: []};
                            }
                            var versions = data.result, array = [];
                            $.each(versions, function(i,version) {
                                if (version.tag) {
                                    array.push(version);
                                }
                            });
                            self.initAuthApiVersions($wizard, array);
                        });
                    });
                },
                onChanged: function (currentStep) {
                var $wizard = this.$element,
                    $currentStep = this.getStepPane();

            },
                formSubmitting: {
                steps: [3],
                    action: function (dialog) {
                    var $dialog = dialog.getModalDialog();

                    var serverFormData = this.serializeObject()
                    var apiName,selectGroup,selectVersion,apiProtocol,description,apiMethod;
                    apiName=serverFormData["apiName"];
                        var groupId;
                        $("select[id='selectGroup']").each(function(){
                            groupId = $(this).val();
                        });
                        var versionId;
                        $("select[id='selectVersion']").each(function(){
                            versionId = $(this).val();
                        });
                        var createAuthOptions = {
                            name:     serverFormData["name"],
                            description: serverFormData["description"],
                            group:{id:groupId},
                            version:{id:versionId},
                            authMethod:serverFormData["authMethod"],
                            appName:serverFormData["appName"],
                            visitFrequency:serverFormData["visitFrequency"],
                            visitTotal:serverFormData["visitTotal"]

                        };
                        var keywords = '授权' + App.highlight(createAuthOptions.name),
                            processor = Modal.processing("正在创建" + keywords);
                        dialog.close();
                        self.ajax.postJSON('/apigateway/auth/add', createAuthOptions, function(err, data) {
                            if (err) {
                                processor.error(keywords + '创建失败。原因：'+err.message);
                            } else {
                                processor.success(keywords + '创建成功');
                                self.$table.reloadTable();
                            }
                        });
                }
            }
            });
        },
        deleteAuth: function(e) {
            var self = this;
            var row = $(e.currentTarget).data("row.dt"),
                rowData = row.data(),
                id = rowData.id,
                name = rowData.Name;
            var keywords = '授权' + App.highlight(name);
            Modal.confirm('确定要删除'+keywords+'吗?', function(bsure) {
                if(bsure) {
                    var processor = Modal.processing("正在删除"+keywords);
                    self.ajax.delete("/apigateway/auth/"+id, function(err, data) {
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
        initAuthGroups: function($wrapper,versionGroupList) {
            var $selectGroup = $('select[name="selectGroup"]', $wrapper),
                groupPairs = [{id:"group-select-default", name:"选择一个分组"}];
            $.each(versionGroupList, function(i, group) {
                if (group) {
                    groupPairs.push({name: group.name,value: group.id});
                }
            });
            $selectGroup.html(App.uiSelect(groupPairs));
            $('#group-select-default').val("");
        },
        initAuthApiVersions: function($wrapper,versionList) {
            var $selectGroup = $('select[name="selectVersion"]', $wrapper),
                versionPairs = [{id:"version-select-default", name:"选择一个版本"}];
            $.each(versionList, function(i, version) {
                if (version) {
                    versionPairs.push({name: version.tag,value: version.id});
                }
            });
            $selectGroup.html(App.uiSelect(versionPairs));
            $('#version-select-default').val("");
        }
    });
});