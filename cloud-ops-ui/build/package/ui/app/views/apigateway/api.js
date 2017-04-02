define(['App', 'common/ui/datatables', 'common/ui/modal'], function(App, DataTables, Modal) {
    return App.View({
        data: App.remote("/apigateway/api"),
        dataFilter: function(err, data) {
            return {data: data};
        },
        ready: function() {
            var self = this;

            var $table = this.$('#ApisTable');

            this.set('$table', $table);

            this.initTable(function() {
                var $tableTop = self.$('#ApisTable_top');
                self.bind('click', $('.btn-add', $tableTop), self.addApi);
                self.bind("click", $(".btn-delete", $table), self.deleteApi);
                self.bind("click", $(".btn-edit", $table), self.editApi);
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
                        "width": DataTables.width("check"),
                        "defaultContent": "<label><input type='checkbox'></label>"
                    },
                    {
                        "data": "name",
                        'render': function (data, type, rowData, cellApi) {
                            return '<a href="' + self.getUrl('+/detail', {
                                    id: rowData.id,
                                    name: rowData.name
                                }) + '">' + data + '</a>';
                        }
                    },
                    {
                        'data': "versionName",
                        'minWidth': "4em"
                    },
                    {
                        "data": "groupName",
                        "minWidth": "4em"
                    },
                    {
                        "data": "externalPath",
                        "minWidth": "4em"
                    },
                    {
                        "data": "description",
                        "minWidth": "4em"
                    },
                    {
                        "data": "createTime",
                        "width": DataTables.width("datetime")
                    },
                    {
                        'data': "",
                        "width": DataTables.width("opt"),
                        'render': function (data, type, rowData, cellApi) {
                            return (
                                '<a class="btn btn-opt btn-delete" title="删除" data-toggle="tooltip">' +
                                '<i class="fa fa-trash-o fa-fw"></i>' +
                                '</a>' +
                                '<a class="btn btn-opt btn-edit" title="编辑" data-toggle="tooltip">' +
                                '<i class="fa fa-pencil-square-o"></i>' +
                                '</a>'
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
                "/apigateway/api"
            );
        },
        addApi: function () {
            var self = this;
            var $wizard = this.$element;
            var pathPreValue;
            Modal.wizard({
                title: "新增API",
                remote: function () {
                    var def = $.Deferred();
                    self.render(App.remote("+/add.html"), function(err, html) {
                        def.resolve(html);
                    });
                    return def.promise();
                },
                onloaded: function (dialog) {
                    //初始化镜像表格
                    var $dialog = dialog.getModalDialog();

                    //init select group
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
                        self.initApiGroups($wizard, array);
                    });
                   //init select version when slect group
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
                            self.initApiVersions($wizard, array);
                        });
                    });

                    $dialog.on('click', '.btn-addd', function() {
                        var table = $(this).closest('table'),
                            $table = $(table);
                        var tr = $(this).closest('tr').html();
                        $('thead', $table).append('<tr>'+ tr +'</tr>');
                        $('.btn-addd', $table).removeClass('btn-addd').addClass('btn-del').text('删除');
                        $('.btn-del:last', $table).removeClass('btn-del').addClass('btn-addd').text('添加');
                    });
                    $dialog.on('click', '.btn-del', function() {
                        var tr = $(this).closest('tr');
                        $(tr).remove();
                    });
                },
                onChanged: function (currentStep) {
                    var $wizard = this.$element,
                        $currentStep = this.getStepPane();
                    if (currentStep.name == "apiConfig") {
                        var groupName=$("#selectGroup").find("option:selected").text();
                        var versionName=$("#selectVersion").find("option:selected").text();
                         pathPreValue="/"+groupName+"/"+versionName+"/";
                       $("#pathPre").html(pathPreValue)
                    }
                },
                formSubmitting: {
                    steps: [3],
                    action: function (dialog) {
                        var $dialog = dialog.getModalDialog();

                        var serverFormData = this.serializeObject()
                        var apiName,selectGroup,selectVersion,apiProtocol,description,apiMethod;
                        apiName=serverFormData["apiName"];
                        $("select[id='selectGroup']").each(function(){
                            selectGroup = $(this).val();
                        });
                        $("select[id='selectVersion']").each(function(){
                            selectVersion = $(this).val();
                        });
                        $("select[id='apiProtocol']").each(function(){
                            apiProtocol = $(this).val();
                        });
                        $("select[id='apiMethod']").each(function(){
                            apiMethod = $(this).val();
                        });
                        //parase api backends
                        var apiBackends=[];
                        var temp={};
                        $.each(serverFormData["backendDomain"], function(i, backend) {
                            if (backend.replace(/(^\s*)|(\s*$)/g, "").length !=0)
                            {
                                var temp={host:backend,path:serverFormData["backendPath"][i]}
                                apiBackends.push(temp)
                            }

                        });
                        var createApiOption = {
                            name:apiName,
                            description:serverFormData["description"],
                            protocol:apiProtocol,
                            method:apiMethod,
                            header:serverFormData["requestHeader"],
                            request: serverFormData["requestParam"],
                            response:serverFormData["responseParam"],
                            group:{id:selectGroup},
                            version:{id:selectVersion},
                            backends:apiBackends,
                            externalPath:pathPreValue+serverFormData["requestPath"]

                        };

                        var keywords = "API " + App.highlight(createApiOption.name),
                            processor = Modal.processing("正在创建" + keywords);
                        dialog.close();
                        self.ajax.postJSON('/apigateway/api/add', createApiOption, function(err, data){
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
        deleteApi: function(e) {
            var self = this;
            var row = $(e.currentTarget).data("row.dt"),
                rowData = row.data(),
                apiId = rowData.id,
                apiName = rowData.name;
            var keywords = 'API ' + App.highlight(apiName);
            Modal.confirm('确定要删除'+keywords+'吗?',function(result){
                if(result) {
                    var processor = Modal.processing("正在删除"+keywords);
                    self.ajax.delete("/apigateway/api/"+apiId, function(err, data){
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
        $tableGroup: $([]),
        initApiGroups: function($wrapper,versionGroupList) {
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
        initApiVersions: function($wrapper,versionList) {
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