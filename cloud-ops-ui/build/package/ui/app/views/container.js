define(['App', 'common/ui/datatables', 'common/ui/modal'], function(App, DataTables, Modal) {
    return App.View({
        data: App.remote("/api/v1/containers/json"),
        dataFilter: function(err, data) {
            return {data: data};
        },
        ready: function() {
            var self = this;

            var $table = this.$('#ContainersTable');

            this.set('$table', $table);

            this.initTable(function() {
                var $tableTop = self.$('#ContainersTable_top');

                self.bind('click', $('.btn-add', $tableTop), self.addContainer);

                self.bind("click", $(".btn-delete", $table), self.deleteContainer);
                self.bind("click", $(".btn-start", $table), self.startContainer);
                self.bind("click", $(".btn-restart", $table), self.restartContainer);
                self.bind("click", $(".btn-bindip", $table), self.bindIp);
                self.bind("click", $(".btn-unbindip", $table), self.unbindIp);
                self.bind("click", $(".btn-snapshot", $table), self.snapshotContainer);
                self.bind("click", $(".btn-stop", $table), self.stopContainer);
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
                        "data": "Name",
                        "width": DataTables.width("name"),
                        'render': function (data, type, rowData, cellApi) {
                            return '<a href="' + self.getUrl('+/detail', {
                                    id: rowData.Uuid,
                                    name: rowData.Name
                                }) + '">' + data + '</a>';
                        }
                    },
                    {
                        'data': "Uuid",
                        'width': "9em"
                    },
                    {
                        "data": "Ip",
                        "width": DataTables.width("ip")
                    },
                    {
                        "data": "FloatingIp",
                        "width": DataTables.width("ip")
                    },
                    {
                        "data": "Image.Repo",
                        "minWidth": "6em"
                    },
                    {
                        "data": "Status",
                        "minWidth": "7em"
                    },
                    {
                        "data": "Host.Ip",
                        "width": DataTables.width("ip")
                    },
                    {
                        "data": "CreateTime",
                        "width": DataTables.width("datetime")
                    },
                    {
                        "data": "Owner",
                        "minWidth": "5em"
                    },
                    {
                        'data': "",
                        "width": DataTables.width("opt"),
                        'render': function (data, type, rowData, cellApi) {
                            var moreOpts = '';
                            if (rowData.FloatingIp) {
                                moreOpts += (
                                    '<div class="dropdown" title="更多" data-toggle="tooltip">'+
                                        '<a class="btn-opt btn-more dropdown-toggle" data-toggle="dropdown" aria-expanded="false"></a>'+
                                        '<ul class="dropdown-menu">'+
                                            '<li><a class="btn-opt btn-restart"><i class="fa fa-repeat"></i>重启</a></li>'+
                                            '<li><a class="btn-opt btn-unbindip"><i class="fa fa-chain-broken"></i>解绑浮动IP</a></li>'+
                                            '<li><a class="btn-opt btn-snapshot"><i class="fa fa-camera"></i>快照</a></li>'+
                                            '<li><a class="btn-opt btn-delete"><i class="fa fa-trash-o"></i>删除</a></li>'+
                                        '</ul>' +
                                    '</div>'
                                );
                            } else {
                                moreOpts += (
                                    '<div class="dropdown" title="更多" data-toggle="tooltip">'+
                                        '<a class="btn-opt btn-more dropdown-toggle" data-toggle="dropdown" aria-expanded="false"></a>'+
                                        '<ul class="dropdown-menu">'+
                                            '<li><a class="btn-opt btn-restart"><i class="fa fa-repeat"></i>重启</a></li>'+
                                            '<li><a class="btn-opt btn-bindip"><i class="fa fa-link"></i>绑定浮动IP</a></li>'+
                                            '<li><a class="btn-opt btn-snapshot"><i class="fa fa-camera"></i>快照</a></li>'+
                                            '<li><a class="btn-opt btn-delete"><i class="fa fa-trash-o"></i>删除</a></li>'+
                                        '</ul>' +
                                    '</div>'
                                );
                            }
                            return (
                                '<a class="btn btn-opt btn-start" title="启动" data-toggle="tooltip">' +
                                    '<i class="fa fa-toggle-on"></i>' +
                                '</a>' +
                                '<a class="btn btn-opt btn-stop" title="停止" data-toggle="tooltip">' +
                                    '<i class="fa fa-toggle-off"></i>' +
                                '</a>' +
                                moreOpts
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
                "/api/v1/containers/page/{pageNo}/{pageSize}"
            );
        },
        addContainer: function () {
            var self = this;
            Modal.wizard({
                title: "创建容器",
                remote: function () {
                    var def = $.Deferred();
                    self.render(App.remote("+/add.html"), function(err, html) {
                        def.resolve(html);
                    });
                    return def.promise();
                },
                onloaded: function (dialog) {
                    //初始化镜像表格
                    var $dialog = dialog.getModalDialog(),
                        $tableImageSelect = $("#table-image-select", $dialog);
                    self.set('$tableImage', $tableImageSelect);

                    $tableImageSelect.on("draw.dt", function () {
                        dialog.setPosition();
                    });

                    self.initTableImage();

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
					var validates = [];
					$dialog.find("form").each(function(){
						validates.push($(this).validate({
							errorContainer: '_form',
	                        errorPlacement: "left top",
	                        rules: {
	                            appName: {
	                                required: true
	                            },
								imageName: {
	                                required: true
	                            },
								imageVersion: {
	                                required: true
	                            },
								networkName: {
	                                required: true
	                            },
								specName: {
	                                required: true
	                            },
								networkName: {
	                                required: true
	                            }
	                        }
						})
					)}
				)},
                onChanged: function (currentStep) {
                    var $wizard = this.$element,
                        $currentStep = this.getStepPane();
                    if (currentStep.name == "basicConfig") {
                        var selectImageData=$("#table-image-select", $wizard).getTableSelected();
                        if (selectImageData==undefined) {
							Modal.error("请选择镜像！");
						}else{
							var selectImage=selectImageData[0]
                       		var selectImageName="registry.iop.com:5000/"+selectImage.data.User+"/"+selectImage.data.Repo;
                        	$('#imageName', $currentStep).text(selectImageName);
							self.ajax.get('/api/v1/images/version?imagename='+selectImage.data.Repo, function(err, data) {
								if (err) {
	                                data = {result: []};
									Modal.error("获取镜像版本失败！");
	                            }else{
									var selectImageVersions = data.result, array = [];
									if (selectImageVersions==undefined){
										Modal.error("请先为该镜像创建版本！");
									}else{
										$.each(selectImageVersions, function(i, image) {
			                                if (image.Tag) {
			                                    array.push(image.Tag);
			                                }
		                           		});
										self.initImageVersion($wizard, array);
									}
		                            
		                            
								}
	
	                        });
						}

                        self.ajax.get('/api/v1/networks/page/0/1000',function(err, data){
                            if (err) {
                                data = {result: []};
								Modal.error("获取网络列表失败！");
                            }else{
								var networkResult = data.result, networkList=[];
								if (networkResult==undefined){
									Modal.error("请先创建网络！");
								}else{
		                            $.each(networkResult, function(i, network) {
		                                if (network) {
		                                    networkList.push(network);
		                                }
		                            });
		                            self.initNetwork($wizard, networkList);
								}
							}
                        });

                        self.ajax.get('/api/v1/specs/page/0/1000',function(err, data){
                            if (err) {
                                data = {result: []};
								Modal.error("获取规格列表失败！");
                            }else{
								var specResult = data.result, sepcList=[];
								if (specResult==undefined){
									Modal.error("请先创建规格！");
								}else{
		                            $.each(specResult, function(i, spec) {
		                                if (spec) {
		                                    sepcList.push(spec);
		                                }
		                            });
		                            self.initSpec($wizard,sepcList);
								}
							}
                        });
						//                        self.ajax.get('/api/v1/volumes/page/0/1000',function(err, data) {
//                            if (err) {
//                                data = {result: []};
//                            }
//                            var selectVolumes = data.result, array = [];
//                            $.each(selectVolumes, function(i, volume) {
//                                if (volume) {
//                                    array.push(volume);
//                                }
//                            });
//                            self.initVolumes($wizard, array);
//                        });
                    }
                },
                formSubmitting: {
                    steps: [3],
					validations:{
						2:function($stepPane) {
							return $('form',$stepPane).valid();
						},
						3:function($stepPane) {
							return $('form',$stepPane).valid();
						}
					},
                    action: function (dialog) {
                        var $dialog = dialog.getModalDialog();

                        var serverFormData = this.serializeObject(), start;
                        if (serverFormData["startUp"]=="true") {
                            start = true;
                        } else {
                            start = false;
                        }
                        var existVolumes=[];
                        $("select[id='volumes']").each(function() {
                            existVolumes.push($(this).val());
                        });
                        var specId;
                        $("select[id='specName']").each(function(){
                            specId = $(this).val();
                        });
                        var hostConfig = {
                            DS_Start:start,
                            DS_VolumeType:"SATA",
                            DS_Volume_Ids:existVolumes,
                            VolumeDriver:"LVM",
                            NetworkMode:serverFormData["networkName"],
                            DS_Spec_Id: specId
                        };

                        var selectImageData=$("#table-image-select", $dialog).getTableSelected();
                        var selectImageName="registry.iop.com:5000/"+selectImageData[0].data.User+"/"+selectImageData[0].data.Repo;
                        var selectImageId=selectImageData[0].data.Uuid
						var cmds=[];
                        cmds.push(serverFormData["command"])

                        //parase container port
                        var exposedPors={};
                        var temp={};
                        $.each(serverFormData["containerPort"], function(i, port) {
                            if (port.replace(/(^\s*)|(\s*$)/g, "").length !=0)
                            {
                                var key = port+"/"+serverFormData["protocol"][i];
                                exposedPors[key] = temp;
                            }

                        });
                        //parase container env
                        var appenv=[];
                        $.each(serverFormData["envKey"], function(i, key) {
                            if (key.replace(/(^\s*)|(\s*$)/g, "").length !=0)
                            {
                                appenv.push(key + "=" + serverFormData["envValue"][i])
                            }
                        });
                        //parase container volume
                        var volume=[];
                        $("input[name='containerDir']").each(function(){
                            var key = $(this).val();
                            if (key.replace(/(^\s*)|(\s*$)/g, "").length !=0) {
                                volume.push(key);
                            }
                        });
                        var createContainerOptions = {
                            name:                    serverFormData["appName"],
							HostName:			     serverFormData["hostName"],
                            image:                   selectImageId,
                            HostConfig:              hostConfig,
                            ExposedPorts:            exposedPors,
                            Env:                     appenv,
                            DS_Volumes_ContainerDir: volume,
                            Cmd:                     cmds
                        };
                        var keywords = "应用" + App.highlight(createContainerOptions.name),
                            processor = Modal.processing("正在创建" + keywords);
                        dialog.close();
                        self.ajax.postJSON('/api/v1/containers/create?name='+serverFormData["appName"], createContainerOptions, function(err, data){
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
        deleteContainer: function(e) {
            var self = this;
            var row = $(e.currentTarget).data("row.dt"),
                rowData = row.data(),
                containerId = rowData.Uuid,
                containerName = rowData.Name;
            var keywords = '容器' + App.highlight(containerName);
            Modal.confirm('确定要删除'+keywords+'吗?',function(result){
                if(result) {
                    var processor = Modal.processing("正在删除"+keywords);
                    self.ajax.delete("/api/v1/containers/"+containerId+"?force=1&v=0", function(err, data){
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
        startContainer: function(e){
            var self = this;
            var row = $(e.currentTarget).data("row.dt"),
                rowData = row.data(),
                containerId = rowData.Uuid,
                containerName = rowData.Name;
            var keywords = '容器' + App.highlight(containerName);
            Modal.confirm('确定要启动'+keywords+'吗?',function(result){
                if(result) {
                    var processor = Modal.processing("正在启动"+keywords);
                    self.ajax.post("/api/v1/containers/"+containerId+"/start", function(err, data) {
                        if (err) {
                            processor.error(keywords+'启动失败。原因：'+err.message);
                        } else {
                            processor.success(keywords+'启动成功');
                            self.$table.reloadTable();
                        }
                    });
                }
            });
        },
        restartContainer: function(e){
            var self = this;
            var row = $(e.currentTarget).data("row.dt"),
                rowData = row.data(),
                containerId = rowData.Uuid,
                containerName = rowData.Name;
            var keywords = '容器' + App.highlight(containerName);
            Modal.confirm('确定要重启'+keywords+'吗?',function(result){
                if(result) {
                    var processor = Modal.processing("正在启动"+keywords);
                    self.ajax.post("/api/v1/containers/"+containerId+"/restart", function(err, data){
                        if (err) {
                            processor.error(keywords+'启动失败。原因：'+err.message);
                        } else {
                            processor.success(keywords+'启动成功');
                            self.$table.reloadTable();
                        }
                    });
                }
            });
        },
        bindIp: function(e) {
            var self = this;
            var row = $(e.currentTarget).data("row.dt"),
                rowData = row.data(),
                containerId = rowData.Uuid,
                containerName = rowData.Name,
                containerIp = rowData.Ip;
            var keywords = '容器' + App.highlight(containerName);
            Modal.confirm('确定要给'+keywords+'绑定浮动IP吗?', function(bsure) {
                if (bsure) {
                    var processor = Modal.processing("正在绑定浮动IP："+keywords);
                    var bindFloatOptions = {
                        name:"local",
                        containerId:containerId,
                        containerIp:containerIp
                    };
                    self.ajax.postJSON("/api/v1/networks/applyandbound",bindFloatOptions,function(err, data){
                        if (err) {
                            processor.error(keywords+'绑定浮动IP失败。原因：'+err.message);
                        } else {
                            processor.success(keywords+'绑定浮动IP成功');
                            self.$table.reloadTable();
                        }
                    });
                }
            });
        },
        unbindIp: function(e) {
            var self = this;
            var row = $(e.currentTarget).data("row.dt"),
                rowData = row.data(),
                containerId = rowData.Uuid,
                containerName = rowData.Name,
                containerIp = rowData.Ip,
                containerFloatIp = rowData.FloatingIp;
            var keywords = '容器' + App.highlight(containerName);
            Modal.confirm('确定要给'+keywords+'解绑浮动IP吗?',function(result){
                if(result) {
                    var processor = Modal.processing("正在解绑浮动IP：" + keywords);
                    var unbindFloatOptions = {
                        containerFloatIp: containerFloatIp,
                        containerId: containerId,
                        containerIp: containerIp
                    };
                    self.ajax.postJSON("/api/v1/networks/unbound", unbindFloatOptions, function (err, data) {
                        if (err) {
                            processor.error(keywords + '解绑浮动IP失败。原因：' + err.message);
                        } else {
                            processor.success(keywords + '解绑浮动IP成功');
                            self.$table.reloadTable();
                        }
                    });
                }
            });
        },
        snapshotContainer: function(e){
            var self = this;
            var row = $(e.currentTarget).data("row.dt"),
                rowData = row.data(),
                containerId = rowData.Uuid,
                containerName = rowData.Name;
            var keywords = '容器'+ App.highlight(containerName);
            Modal.show({
                title: "快照信息",
                remote: function() {
                    var def = $.Deferred();
                    self.render(App.remote("+/backup.html"), function(err, html) {
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
                            hostinfo: {
                                required: true
                            }
                        },
                        messages: {
                            hostinfo: "请输入快照信息"
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
                        label: "备份",
                        cssClass: "btn-primary",
                        action: function(dialog) {
                            var $dialog = dialog.getModalDialog(),
                                $form = $("form", $dialog);
                            if (!$form.length || !$form.valid()) return false;
                            var imageversion=$("#tag", $dialog).val();
                            var description=$("#description", $dialog).val();
                            dialog.close();
                            var params={
                                "ContainerId":containerId,
                                "ImageVersion":imageversion,
                                "Description":description
                            };
                            var processor = Modal.processing('正在备份'+keywords);
                            self.ajax.post("/api/v1/containers/backup",App.json.stringify(params),function(err, data){
                                if (err) {
                                    processor.error(keywords+'备份失败。原因：'+err.message);
                                } else {
                                    processor.success(keywords+'备份成功');
                                    self.$table.reloadTable();
                                }
                            });

                        }
                    }
                ]
            });
        },
        stopContainer: function(e){
            var self = this;
            var row = $(e.currentTarget).data("row.dt"),
                rowData = row.data(),
                containerId = rowData.Uuid,
                containerName = rowData.Name;
            var keywords = '容器' + App.highlight(containerName);
            Modal.confirm('确定要停止'+keywords+'吗?',function(result){
                if(result) {
                    var processor = Modal.processing("正在停止"+keywords);
                    self.ajax.post("/api/v1/containers/"+containerId+"/stop", function(err, data){
                        if (err) {
                            processor.error(keywords+'停止失败。原因：'+err.message);
                        } else {
                            processor.success(keywords+'停止成功');
                            self.$table.reloadTable();
                        }
                    });
                }
            });
        },
        $tableImage: $([]),
        initTableImage: function(callback) {
            DataTables.init(this.$tableImage, {
                serverSide: true,
                ajax: this.tableImageAjax(),
                columns: [
                    {
                        "width": DataTables.width("check"),
                        "defaultContent": "<label><input type='checkbox'></label>"
                    },
                    {
                        "data": {},
                        "minWidth": "15em",
                        "render": function(data,type,full){
                            return "registry.iop.com:5000/"+data.User+"/"+data.Repo
                        }
                    },
                    {
                        "data": "Description",
                        "minWidth": DataTables.width("name")
                    }
                ]
            }, callback);
        },
        tableImageAjax: function() {
            return DataTables.parseAjax(
                this,
                this.$tableImage,
                "/api/v1/images/page/{pageNo}/{pageSize}"
            );
        },
        initImageVersion: function($wrapper,imageVersionList) {
            var $selectImageVersion = $('select[name="imageVersion"]', $wrapper),
                versionPairs = [{id:"image-select-default", name:"选择一个分组"}];
            $.each(imageVersionList, function(i, imageVersion) {
                if (imageVersion) {
                    versionPairs.push({value: imageVersion});
                }
            });
            $selectImageVersion.html(App.uiSelect(versionPairs));
            $('#image-select-default').val("");
        },
//        initVolumes: function($wrapper,volumes) {
//            var $selectVolumes = $('select[name="volumes"]', $wrapper),
//                volumePairs = [{id:"volume-select-default", name:"选择一个数据卷"}];
//            $.each(volumes, function(i, volume) {
//                if (volume.Name) {
//                    volumePairs.push({name: volume.Name,value: volume.Uuid});
//                }
//            });
//            $selectVolumes.html(App.uiSelect(volumePairs));
//            $('#volume-select-default').val("");
//        },
        initNetwork: function($wrapper,networks) {
            var $selectNetwork = $('select[name="networkName"]', $wrapper),
                networkPairs = [{id:"network-select-default", name:"选择一个网络"}];
            $.each(networks, function(i, network) {
                if (network) {
                    networkPairs.push({value: network.Name});
                }
            });
            $selectNetwork.html(App.uiSelect(networkPairs));
            $('#network-select-default').val("");
        },
        initSpec: function($wrapper,specs) {
            var $selectSpec = $('select[name="specName"]', $wrapper),
                specPairs = [{id:"spec-select-default", name:"选择一个规格"}];
            $.each(specs, function(i, spec) {
                if (spec) {
                    specPairs.push({name: spec.Name,value: spec.Uuid});

                }
            });
            $selectSpec.html(App.uiSelect(specPairs));
            $('#spec-select-default').val("");
        }
    });
});
