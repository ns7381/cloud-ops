/**
 * Created by wangd on 2016/7/8.
 */
define(['App', 'common/ui/modal', 'common/ui/datatables', 'common/ui/validator'], function (App, Modal, DataTables) {
    return App.View({
        ready: function() {
            var self = this;

            var $table = this.$('#VolumesTable');

            this.set('$table', $table);

            this.initTable(function() {
                var $tableTop = self.$('#VolumesTable_top');
                self.bind('click', $('.btn-add', $tableTop), self.addVolume);
				self.bind('click', $('.btn-extend', $table), self.extendVolume);
				self.bind('click', $('.btn-bind', $table), self.bindVolume);
				self.bind('click', $('.btn-unbind', $table), self.unbindVolume);
                self.bind('click', $('.btn-delete', $table), self.deleteVolume);
            });
        },
        $table: $([]),
        initTable: function(callback) {
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
                        "width": DataTables.width("name")
                    },
                    {
                        "data": "Uuid",
                        "width": "9em"
                    },
                    {
                        "data": "Lvm.Host.Ip",
                        "width": DataTables.width("ip")
                    },
                    {
                        "data": "HostDir",
                        "minWidth": "10em"
                    },
					{
                        "data": "ContainerDir",
                        "minWidth": "10em"
                    },
                    {
                        "data": "ContainerId",
                        "class": "6em"
                    },
                    {
                        "data": "Size",
                        "width": "6em"
                    },
                    {
                        "data": "Lvm.Type",
                        "width": "4.5em"
                    },
					{
                        "data": "Status",
                        "width": "4.5em"
                    },
                    {
                        "data": "CreateTime",
                        "width": DataTables.width("datetime")
                    },
                    {
                        "data": {},
                        "width": DataTables.width("opt"),
                        "render": function(data) {
							if (data.ContainerId) {
								return (
	                                '<a class="btn-extend btn-opt" data-toggle="tooltip" title="扩容"><i class="fa fa-arrows-h fa-fw"></i></a>' +
	                                '<a class="btn-unbind btn-opt" data-toggle="tooltip" title="解绑"><i class="fa fa-chain-broken fa-fw"></i></a>' +
	                                '<a class="btn-delete btn-opt" data-toggle="tooltip" title="删除"><i class="fa fa-trash-o fa-fw"></i></a>'
	                            );
							} else {
	                            return (
	                                '<a class="btn-extend btn-opt" data-toggle="tooltip" title="扩容"><i class="fa fa-arrows-h fa-fw"></i></a>' +
	                                '<a class="btn-bind btn-opt" data-toggle="tooltip" title="绑定"><i class="fa fa-link fa-fw"></i></a>' +
	                                '<a class="btn-delete btn-opt" data-toggle="tooltip" title="删除"><i class="fa fa-trash-o fa-fw"></i></a>'
	                            );
							}
                        }
                    }
                ]
            }, callback);
        },
        tableAjax: function() {
            return DataTables.parseAjax(
                this,
                this.$table,
                "/api/v1/volumes/page/{pageNo}/{pageSize}"
            );
        },
        addVolume: function() {
            var self = this;
            Modal.show({
                title: "申请卷",
                remote: function(dialog) {
                    var def = $.Deferred();
                    self.render({
                        url: "+/add.html",
                        data: App.remote('/api/v1/hosts/list'),
                        dataFilter: function(err, data) {
                            if (err) {
                                data = [];
                            }
                            dialog.setData('selectHosts', data);
                        },
                        callback: function(err, html) {
                            def.resolve(html);
                        }
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
                    var selectHosts = dialog.getData('selectHosts') || [], hosts = [];
                    $.each(selectHosts, function(i, host) {
                        if (host) {
                            hosts.push(host);
                        }
                    });
                    self.initHosts($dialog, hosts);
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
                            var driverOpts = {
                                size:   $("#size", $dialog).val(),
                                type:   $("#type", $dialog).val(),
                                hostIp: $("#hostName",$dialog).val()
                            };
                            var createVolumeOptions = {
                                name:   $("#name", $dialog).val(),
                                driver: $("#driver", $dialog).val(),
                                driverOpts: driverOpts
                            };
                            var keywords = "卷" + App.highlight(createVolumeOptions.name),
                                processor = Modal.processing("正在申请" + keywords);
                            dialog.close();
                            self.ajax.post('/api/v1/volumes/create',App.json.stringify(createVolumeOptions),function(err, data){
                                if (err) {
                                    processor.error(keywords + '申请失败。原因：'+err.message);
                                } else {
                                    processor.success(keywords + '申请成功');
                                    self.$table.reloadTable();
                                }
                            })
                        }
                    }
                ]
            });
        },
        deleteVolume: function(e) {
            var self = this;
            var row = $(e.currentTarget).data("row.dt"),
                rowData = row.data(),
                volumeId = rowData.Uuid,
                volumeName = rowData.Name;
            var keywords = '容器卷' + App.highlight(volumeName);
            Modal.confirm('确定要删除'+keywords+'吗?',function(result){
                if(result) {
                    var processor = Modal.processing("正在删除"+keywords);
                    self.ajax.delete("/api/v1/volumes/"+volumeId, function(err, data){
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
		extendVolume: function(e) {
            var self = this;
            var row = $(e.currentTarget).data("row.dt"),
                rowData = row.data(),
                volumeId = rowData.Uuid,
				volumeSize = parseInt(rowData.Size),
                volumeName = rowData.Name;
            var keywords = '容器卷' + App.highlight(volumeName);
			Modal.show({
                title: "卷扩容",
                remote: function() {
                    var def = $.Deferred();
                    self.render(App.remote("+/extend.html"), function(err, html) {
                        def.resolve(html);
                    });
                    return def.promise();
                },
                onloaded: function(dialog) {
                    var $dialog = dialog.getModalDialog(),
                       $tableSizeCheck = $("#table-size-check", $dialog);
                    $tableSizeCheck.on("draw.dt", function() {
                        dialog.setPosition();
                    });
                    DataTables.init($tableSizeCheck, {
                        data: [
                            {
                                'id': "1",
                                'size': rowData.Size+1024
                            },
                            {
                                'id': "2",
                                'size': rowData.Size+2048
                            },
                            {
                                'id': "3",
                                'size': rowData.Size+4096
                            }
                        ],
                        columns: [
                            {
                                'data': "",
                                'width': "2.5em",
                                'render': function(data, type, rowData, meta) {
                                    return '<label><input name="size_select" value="'+rowData.size+'" type="radio"'+(meta.row == 0 ? 'checked="checked"':'')+'></label>';
                                }
                            },
                            {
                                'data': "size",
                                'width': DataTables.width("size")
                            }
                        ]
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
                        label: "扩容",
                        cssClass: "btn-primary",
                        action: function(dialog) {
                            var $dialog = dialog.getModalDialog(),
                               $tableSizeCheck = $("#table-size-check", $dialog);
                            dialog.close();
							var size=$("input[name='size_select']:checked", $dialog).val();
                            var params={
                                "size":size
                            };
                            var processor = Modal.processing('正在扩容'+keywords);
                            self.ajax.post("/api/v1/volumes/extend/"+volumeId,App.json.stringify(params),function(err, data){
                                if (err) {
                                    processor.error(keywords+'扩容失败。原因：'+err.message);
                                } else {
                                    processor.success(keywords+'扩容成功');
                                    self.$table.reloadTable();
                                }
                            });

                        }
                    }
                ]
            });
        },
		bindVolume: function(e) {
            var self = this;
            var row = $(e.currentTarget).data("row.dt"),
                rowData = row.data(),
                volumeId = rowData.Uuid,
                volumeName = rowData.Name;
            var keywords = '容器卷' + App.highlight(volumeName);
			Modal.show({
                title: "绑定容器",
                remote: function() {
                    var def = $.Deferred();
                    self.render(App.remote("+/bind.html"), function(err, html) {
                        def.resolve(html);
                    });
                    return def.promise();
                },
                onloaded: function(dialog) {
                    var $dialog = dialog.getModalDialog(),
                       $tableContainer = $("#table-container", $dialog);
                    self.set('$tableContainer', $tableContainer);
                    $tableContainer.on("draw.dt", function() {
                        dialog.setPosition();
                    });
                    self.initTableContainer(volumeId);
                },
                buttons: [
                    {
                        label: "取消",
                        action: function(dialog) {
                            dialog.close();
                        }
                    },
                    {
                        label: "绑定",
                        cssClass: "btn-primary",
                        action: function(dialog) {
                            var $dialog = dialog.getModalDialog(),
                               $tableContainer = $("#table-container", $dialog);
							var containerId=$("input[name='container_select']:checked", $dialog).val();
                            dialog.close();
							
                            var params={
                                "volumeId":volumeId,
								"id":containerId
                            };
                            var processor = Modal.processing('正在绑定'+keywords);
                            self.ajax.post("/api/v1/containers/mount/"+containerId,App.json.stringify(params),function(err, data){
                                if (err) {
                                    processor.error(keywords+'绑定失败。原因：'+err.message);
                                } else {
                                    processor.success(keywords+'绑定成功');
                                    self.$table.reloadTable();
                                }
                            });

                        }
                    }
                ]
            });
        },
		unbindVolume: function(e) {
            var self = this;
            var row = $(e.currentTarget).data("row.dt"),
                rowData = row.data(),
                volumeId = rowData.Uuid,
				containerId = rowData.ContainerId,
                volumeName = rowData.Name;
            var keywords = '容器卷' + App.highlight(volumeName);
			Modal.confirm('确定要给'+keywords+'解绑吗?',function(result){
                if(result) {
                    var processor = Modal.processing("正在解绑" + keywords);
                    var unbindOptions = {
                        volumeId: volumeId,
                        id: containerId
                    };
                    self.ajax.postJSON("/api/v1/containers/unmount/"+containerId, unbindOptions, function (err, data) {
                        if (err) {
                            processor.error(keywords + '解绑失败。原因：' + err.message);
                        } else {
                            processor.success(keywords + '解绑成功');
                            self.$table.reloadTable();
                        }
                    });
                }
            });
        },
		initTableContainer: function(volumeId) {
            var self = this;
            if (!self.$tableContainer.isDataTable()) {
                DataTables.init(this.$tableContainer, {
                    ajax: {
                        url:"/api/v1/containers/json"
                    },
                    dataSrc: function (data) {
                       return data;
                    },
                    columns: [
                        {
                            'data': "",
                            'width': "2.5em",
                            'render': function(data, type, rowData, meta) {
                                return '<label><input name="container_select" value="'+rowData.Uuid+'" type="radio"'+(meta.row == 0 ? 'checked="checked"':'')+'></label>';
                            }
                        },
                        {
                            'data': "Uuid",
                            'width': "12em"
                        },
                        {
                            'data': "Name",
                            'minWidth': DataTables.width("name")
                        },
                        {
                            'data': "Image.Repo",
                            'width': "12em"
                        },
                        {
                            'data': "Spec.Name",
                            'width': "8em"
                        },
						{
                            'data': "Host.HostName",
                            'width': "12em"
                        }
                    ]
                }, function(){});
            }
        },
		
        initHosts: function($wrapper, hostlist) {
            var $selectHost = $('select[name="hostName"]', $wrapper),
                hostsPairs = [{id:"host-select-default", name:"选择一个主机"}];
            $.each(hostlist, function(i, host) {
                if (host.HostName) {
                    hostsPairs.push({value: host.Ip});
                }
            });
            $selectHost.html(App.uiSelect(hostsPairs));
            $('#host-select-default').val("");
        }
    });
});
