/**
 * Created by wangdk on 2017/1/4.
 */
/**
 * Created by wangd on 2016/7/8.
 */
define(['App', 'common/ui/modal', 'common/ui/datatables', 'common/ui/validator'], function (App, Modal, DataTables) {
    return App.View({
        ready: function() {
            var self = this;

            var $table = this.$('#GroupTable');

            this.set('$table', $table);

            this.initTable(function() {
                var $tableTop = self.$('#VolumesTable_top');
                self.bind('click', $('.btn-add', $tableTop), self.addVolume);
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
                    // {
                    //     "data": "Containers",
                    //     "class": "cell-em-6"
                    // },
                    {
                        "data": "Size",
                        "width": "6em"
                    },
                    {
                        "data": "Lvm.Type",
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
                            return (
                                '<a class="btn-extend btn-opt" data-toggle="tooltip" title="扩容"><i class="fa fa-arrows-h fa-fw"></i></a>' +
                                '<a class="btn-bind btn-opt" data-toggle="tooltip" title="绑定"><i class="fa fa-link fa-fw"></i></a>' +
                                '<a class="btn-unbind btn-opt" data-toggle="tooltip" title="解绑"><i class="fa fa-chain-broken fa-fw"></i></a>' +
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
                            self.ajax.post('/api/v1/volumes/apply',App.json.stringify(createVolumeOptions),function(err, data){
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
                    self.ajax.delete("/api/v1/volumes/release/"+volumeId, function(err, data){
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

