define(['App', 'jquery', 'common/ui/datatables', 'common/ui/modal', 'common/ui/validator'], function(App, $, DataTables, Modal) {
    return App.View({
        $table: $([]),
        $tableVg: $([]),
        ready: function() {
            var self = this;
            var $table = this.$("#table-cluster-hosts");
            this.set('$table', $table);

            this.initTable(function() {
                var $tableTop = self.$('#table-cluster-hosts_top');

                self.bind('click', $('.btn-add', $tableTop), self.addHost);
                self.bind('click', $('.btn-cluster-init', $tableTop), self.initCluster);
                self.bind('click', $('.btn-cluster-dilatation', $tableTop), self.dilatationCluster);

                self.bind('click', $('.btn-edit', $table), self.editHost);
                self.bind('click', $('.btn-cog', $table), self.configHost);
                self.bind('click', $('.btn-delete', $table), self.deleteHost);
            });
        },
        initTable: function(callback) {
            var self = this;
            // init table
            DataTables.init(this.$table, {
                serverSide: true,
                ajax: this.tableAjax(),
                columns: [
                    {
                        'data': "",
                        'width': DataTables.width("check"),
                        'defaultContent': '<label><input type="checkbox"></label>'
                    },
                    {
                        'data': "HostName",
                        'width': DataTables.width("name"),
                        'render': function(data, type, rowData, cellApi) {
                            return '<a href="' + self.getUrl('+/detail', {id: rowData.Uuid, name: rowData.HostName}) + '">' + data + '</a>';
                        }
                    },
                    {
                        'data': "Ip",
                        'width': DataTables.width("ip")
                    },
                    {
                        'data': "Spec",
                        'minWidth': "11em"
                    },
                    {
                        'data': "Role",
                        'width': "8em"
                    },
                    {
                        'data': "Label",
                        'minWidth': "4.5em"
                    },
                    {
                        'data': "DeployStatus",
                        'minWidth': "8em"
                    },
                    {
                        'data': "ContainerCount",
                        'width': "4.5em"
                    },
                    {
                        'data': "UpdateTime",
                        'width': DataTables.width("datetime")
                    },
                    {
                        'data': "",
                        'width': DataTables.width("opt-2"),
                        'render': function(data, type, rowData, cellApi) {
                            return (
                                '<a class="btn btn-opt btn-cog" title="配置" data-toggle="tooltip">' +
                                    '<i class="fa fa-cog"></i>' +
                                '</a>' +
                                '<a class="btn btn-opt btn-delete" title="删除" data-toggle="tooltip">' +
                                    '<i class="fa fa-trash-o"></i>' +
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
                '/api/v1/clusters/hosts/page/{pageNo}/{pageSize}'
            );
        },
        addHost: function() {
            var self = this;
            Modal.show({
                title: "主机导入",
                message: self.template.render(self.getPathId('+/add')),
                onshown: function(dialog) {
                    var $dialog = dialog.getModalDialog(),
                        $form = $("form", $dialog);
                    $form.validate({
                        errorContainer: $dialog,
                        errorPlacement: "left top",
                        rules: {
                            ip: {
                                IP: true,
                                required: true
                            },
                            user: {
                                required: true
                            },
                            password: {
                                required: true
                            }
                        }
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
                buttons: [
                    {
                        label: "取消",
                        action: function(dialog) {
                            dialog.close();
                        }
                    },
                    {
                        label: "导入",
                        cssClass: "btn-primary",
                        action: function(dialog) {
                            var $dialog = dialog.getModalDialog(),
                                $form = $("form", $dialog);

                            if (!$form.length || !$form.valid()) return false;
                            var hostinfo=[];
                            var serverFormData =$form.serializeObject();
                            var num=$form.serializeArray().length/3;
                            if (num==1){
                                hostinfo.push("{\"Ip\":\""+serverFormData["ip"] + "\",\"User\":\"" + serverFormData["user"] +"\",\"Password\":\""+ serverFormData["password"] +"\"}");
                            }else{
                                $.each(serverFormData["ip"], function(i, key) {
                                    if (key.replace(/(^\s*)|(\s*$)/g, "").length !=0)
                                    {
                                        hostinfo.push("{\"Ip\":\""+key + "\",\"User\":\"" + serverFormData["user"][i] +"\",\"Password\":\""+ serverFormData["password"][i] +"\"}");
                                    }
                                 });
                            }
                            var params;
                            try {
                                params = App.json.parse("["+hostinfo+"]");
                            } catch (e) {
                                params = {};
                            }
                            dialog.close();
                            self.ajax.postJSON('/api/v1/hosts/import', params, function(err, data) {
                                if (err) {
                                    Modal.error('导入失败。原因：'+err.message);
                                } else {
                                    Modal.success('导入成功');
                                    self.$table.reloadTable();
                                }
                            });
                        }
                    }
                ]
            });
        },
        initCluster: function() {
            var self = this;
            //check cluster disk configed
            self.ajax.get(
                '/api/v1/clusters/disk/config',
                function(err, data) {
                    if (data["error"]) {
                        Modal.error('请首先配置主机磁盘');
                    }else {
                        Modal.wizard({
                            title: "集群初始化",
                            remote: function() {
                                var def = $.Deferred();
                                self.render(
                                    App.remote("+/cluster_init.html"),
                                    function(err, html) {
                                        def.resolve(html);
                                    }
                                );
                                return def.promise();
                            },
                            onloaded: function(dialog) {
                                //var $dialog = dialog.getModalDialog();
                                var $wizard = this.$element;
                                // 表单校验
                                // $("form", $dialog).each(function() {
                                //     $(this).validate({
                                //         errorContainer: "_form",
                                //         errorPlacement: "left bottom",
                                //         rules: {
                                //             cidr: {
                                //                 required: true
                                //             },
                                //             dateway: {
                                //                 required: true,
                                //                 IP: true
                                //             },
                                //             haproxy: {
                                //                 IP: true
                                //             },
                                //             skydns: {
                                //                 IP: true
                                //             },
                                //             cpu_vr_ratio: {
                                //                 required: true
                                //             }
                                //         },
                                //         messages: {
                                //
                                //         }
                                //     });
                                // });
                                // iradio
                               // $('form input[type="radio"]', $dialog).iCheck({radioClass: "iradio-info"});
                                self.ajax.get('/api/v1/clusters/nic', function(err, data) {
                                    if (err) {
                                        data = [];
                                    }
                                    self.initClusterNic($wizard, data);
                                });
                            },
                            onChanged: function(stepObj) {
                                var $stepPane = this.getStepPane(),
                                    $tableNodesCheck = $("#table-nodes-check", $stepPane);
                                self.set('$tableNodesCheck', $tableNodesCheck);
                                if ($tableNodesCheck.length && !$.fn.dataTable.isDataTable($tableNodesCheck)) {
                                    DataTables.init($tableNodesCheck, {
                                        serverSide: true,
                                        ajax: self.tableNodesCheckAjax(),
                                        columns: [
                                            {
                                                'data': "",
                                                'width': "2.5em",
                                                'defaultContent': '<label><input type="checkbox"></label>'
                                            },
                                            {
                                                'data': "HostName",
                                                'width': DataTables.width("name")
                                            },
                                            {
                                                'data': "Ip",
                                                'width': DataTables.width("ip")
                                            },
                                            {
                                                'data': "Spec",
                                                'width': "6em"
                                            },
                                            {
                                                'data': {},
                                                'width': "8em",
                                                'render': function(data) {
                                                    var obj = {
                                                        "Swarm Manager": data.Role == "Swarm Manager" ? "selected" : "",
                                                        "Swarm Node": data.Role == "Swarm Node" ? "selected" : "",
                                                        "Haproxy": data.Role == "Haproxy" ? "selected" : "",
                                                        "SkydDns": data.Role == "SkydDns" ? "selected" : ""
                                                    };
                                                    return (
                                                        '<select id="'+ data.Uuid +'">' +
                                                        '<option value="docker" '+ obj['Docker'] +'>Docker</option>' +
                                                        // '<option value="swarm node" '+ obj["Swarm Node"] +'>Swarm Node</option>' +
                                                        //    '<option value="haproxy" '+ obj["Haproxy"] +'>Haproxy</option>' +
                                                        //    '<option value="skydns" '+ obj["SkydDns"] +'>SkydDns</option>' +
                                                        '</select>'
                                                    );
                                                }
                                            }
                                        ]
                                    });
                                }
                            },
                            formSubmitting: {
                                validations: {
                                    1: function($stepPane) {
                                        var valid = true;
                                        $('form', $stepPane).each(function() {
                                            if (!(valid = $(this).valid())) return false;
                                        });
                                        return valid;
                                    }
                                },
                                steps: [2],
                                action: function (dialog) {
                                    var $dialog = dialog.getModalDialog();
                                    var serverFormData = this.serializeObject();

                                    var globalConfig={};
                                    var clusterNetworkConfig=[];
                                    var clusterNetworkConfigFloating={};

                                    //floating ip config
                                    // clusterNetworkConfigFloating["cidr"]=serverFormData["cidr"];
                                    // clusterNetworkConfigFloating["gateway"]=serverFormData["gateway"];
                                    // clusterNetworkConfigFloating["mode"]="local";
                                    // clusterNetworkConfigFloating["ip_range"]=self.parseIpRange(serverFormData["ip-pool"]);
                                    // clusterNetworkConfig.push(clusterNetworkConfigFloating);
                                    // globalConfig["network_config"]=clusterNetworkConfig;

                                    //vlan pool config
                                    globalConfig["vlanPool"]=self.parseVlanRange(serverFormData["vlan-pool"]);
                                    //cluster domain config
                                    globalConfig["serviceNicName"]=serverFormData["serviceNicName"]
                                    globalConfig["manageNicName"]=serverFormData["manageNicName"]
                                    var dockerRegistry={};
                                    // dockerRegistry["ip"]=serverFormData["registry-ip"];
                                    dockerRegistry["ip"]="127.0.0.1";
                                    dockerRegistry["install_registry_flag"]=serverFormData["build-registry"];

                                    var etcdServers=[];
                                    // etcdServers.push(serverFormData["etcd-ips"]);
                                    etcdServers.push("127.0.0.1");
                                    globalConfig["docker_registry"]=dockerRegistry;
                                    globalConfig["etcd_servers"]=etcdServers;

                                    // var rabbitmqServers=[];
                                    // rabbitmqServers.push(serverFormData["rabbitmq-ips"]);
                                    // globalConfig["rabbitmq_servers"]=rabbitmqServers;

                                     var domainConfig={};
                                     domainConfig["domain"]=serverFormData["clusterDomain"];
                                     domainConfig["skydns"]="";
                                     domainConfig["haproxy"]="";
                                     globalConfig["domain_config"]=domainConfig;

                                    var resourceConfig={};
                                    // resourceConfig["cpu_virtual_rate"]=serverFormData["cpu_vr_ratio"]
                                    // if (serverFormData["ram_remain"]!=""){
                                    //     resourceConfig["reserve_memory"]=parseInt(serverFormData["ram_remain"]);
                                    // }else{
                                    resourceConfig["reserve_memory"]=0;
                                    // }
                                    // if(serverFormData["disk_remain"]!=""){
                                    //     resourceConfig["reserve_storage"]=parseInt(serverFormData["disk_remain"]);
                                    // }else{
                                    resourceConfig["reserve_storage"]=0;
                                    // }
                                    globalConfig["resource_config"]=resourceConfig;
                                    var mqConfig={}
                                    mqConfig["manager_mq_url"]= serverFormData["managerMq"]
                                    mqConfig["monitor_mq_url"]=serverFormData["monitorMq"]
                                    mqConfig["monitor_conn_version"]= serverFormData["monitorVersion"]
                                    globalConfig["mq_config"]=mqConfig

                                    var clusterConfig={};
                                    clusterConfig["global_config"]=globalConfig;
                                    var selectNodeData=$("#table-nodes-check", $dialog).getTableSelected();
                                    var nodesInfo=[];
                                    if(selectNodeData!=null){
                                        if (selectNodeData.length) {
                                            for (var i=0;i<selectNodeData.length;i++) {
                                                var deployClusterNodeConfig={};
                                                deployClusterNodeConfig["uuid"]= selectNodeData[i].data.Uuid;
                                                deployClusterNodeConfig["role"]= $('#'+selectNodeData[i].data.Uuid).val();
                                                nodesInfo.push(deployClusterNodeConfig);
                                            }
                                        } else {
                                            var deployClusterNodeConfig={};
                                            deployClusterNodeConfig["uuid"]= selectNodeData.data.Uuid;
                                            deployClusterNodeConfig["role"]= $('#'+selectNodeData.data.Uuid).val();
                                            nodesInfo.push(deployClusterNodeConfig);
                                        }
                                        clusterConfig["node_config"]=nodesInfo;
                                    }
                                    dialog.close();
                                    self.ajax.postJSON(
                                        '/api/v1/clusters/deploy',
                                        clusterConfig,
                                        function(err, data) {
                                            if (err) {
                                                Modal.error('创建失败。原因：'+err.message);
                                            } else {
                                                Modal.success('部署中');
                                                self.$table.reloadTable();
                                            }
                                        }
                                    );
                                }
                                //end

                            }
                        });
                    }
                }
            );

        },
        $tableNodesCheck: $([]),
        tableNodesCheckAjax: function() {
            return DataTables.parseAjax(
                this,
                this.$tableNodesCheck,
                '/api/v1/clusters/hosts/page/{pageNo}/{pageSize}'
            );
        },
        dilatationCluster: function() {
            var self = this;
            Modal.show({
                title: "集群扩容",
                remote: function() {
                    var def = $.Deferred();
                    self.render(App.remote("+/cluster_dilatation.html"), function(err, html) {
                        def.resolve(html);
                    });
                    return def.promise();
                },
                onloaded: function(dialog) {
                    var $dialog = dialog.getModalDialog(),
                        $tableNodesCheck = $("#table-nodes-check", $dialog);
                    $tableNodesCheck.on("draw.dt", function() {
                        dialog.setPosition();
                    });
                    DataTables.init($tableNodesCheck, {
                        data: [
                            {
                                'id': 1,
                                'name': "swarm1.nova.local",
                                'ip': "192.168.5.6",
                                'ram': "4G",
                                'ssd': "128G",
                                'disk': "4T",
                                'role': "Swarm Manager"
                            },
                            {
                                'id': 2,
                                'name': "swarm2.nova.local",
                                'ip': "192.168.5.7",
                                'ram': "4G",
                                'ssd': "128G",
                                'disk': "4T",
                                'role': "Swarm Node"
                            },
                            {
                                'id': 3,
                                'name': "swarm3.nova.local",
                                'ip': "192.168.5.8",
                                'ram': "4G",
                                'ssd': "128G",
                                'disk': "4T",
                                'role': "Haproxy"
                            }
                        ],
                        columns: [
                            {
                                'data': "",
                                'width': "2.5em",
                                'render': function(data, type, rowData, meta) {
                                    return '<label><input name="node_select" value="'+rowData.id+'" type="radio"'+(meta.row == 0 ? 'checked="checked"':'')+'></label>';
                                }
                            },
                            {
                                'data': "name",
                                'width': DataTables.width("name")
                            },
                            {
                                'data': "ip",
                                'width': DataTables.width("ip")
                            },
                            {
                                'data': "",
                                'minWidth': "8em",
                                'render': function(data, type, rowData, meta) {
                                    var configs = [];
                                    configs.push(rowData.ram || "0G");
                                    configs.push(rowData.ssd || "0G");
                                    configs.push(rowData.disk || "0G");
                                    return configs.join('/');
                                }
                            },
                            {
                                'data': "role",
                                'minWidth': "12em"
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
                            // do something
                            dialog.close();
                        }
                    }
                ]
            });
        },
        initClusterNic: function($wrapper,clusterNicList) {
            var $selectClusterManagementNic = $('select[name="manageNicName"]', $wrapper),
                $selectClusterServiceNic = $('select[name="serviceNicName"]', $wrapper),
                managerNicPairs = [{id:"manager-nic-select-default", name:"选择一个网卡"}],
                serviceNicPairs = [{id:"service-nic-select-default", name:"选择一个网卡"}];
            $.each(clusterNicList, function(i, nicName) {
                if (nicName) {
                    managerNicPairs.push({value: nicName});
                    serviceNicPairs.push({value: nicName});
                }
            });
            $selectClusterManagementNic.html(App.uiSelect(managerNicPairs));
            $selectClusterServiceNic.html(App.uiSelect(serviceNicPairs));
            $('#manager-nic-select-default').val("");
            $('#service-nic-select-default').val("");
        },
        editHost: function(e) {
            var self = this;
            var row = $(e.currentTarget).data('row.dt'),
                rowData = row.data();
            Modal.show({
                title: "编辑主机信息",
                remote: function() {
                    var def = $.Deferred();
                    self.render(App.remote("+/edit.html"), rowData, function(err, html) {
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
                            name: {
                                required: true
                            }
                        },
                        messages: {
                            name: "请输入主机名称"
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
        configHost: function(e) {
            var self = this;
            var row = $(e.currentTarget).data('row.dt'),
                rowData = row.data(),
                id = rowData.Uuid,
                name = rowData.HostName;
            Modal.show({
                title: "配置主机存储【"+name+"】",
                remote: function() {
                    var def = $.Deferred();
                    self.render(App.remote("+/cog.html"), function(err, html) {
                        def.resolve(html);
                    });
                    return def.promise();
                },
                onloaded: function(dialog) {
                    var $dialog = dialog.getModalDialog(),
                        $tableVg = $("#table-vg", $dialog);
                    self.set('$tableVg', $tableVg);
                    $tableVg.on("draw.dt", function() {
                        dialog.setPosition();
                    });
                    self.initTableVg(id);
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
                                $tableVg = $("#table-vg", $dialog);
                            var tableVgApi = $tableVg.dataTable().api(),
                                cogs = tableVgApi.rows().data();
                            var volumeinfo=[]
                            $.each(cogs, function(i, vg) {
                                var oneVG={}
                                oneVG["hostId"]=id
                                oneVG["name"]=vg.Name
                                oneVG["type"]=vg.Type
                                oneVG["labels"]=vg.Labels
                                oneVG["diskNames"]=vg.Disk
                                volumeinfo.push(oneVG);
                            });
                            dialog.close();
                            self.ajax.postJSON('/api/v1/volumes/init', volumeinfo, function(err, data) {
                                if (err) {
                                    Modal.error('卷信息初始化失败。原因：'+err.message);
                                } else {
                                    Modal.success('卷信息初始化成功');
                                    self.$table.reloadTable();
                                }
                            })
                        }
                    }
                ]
            });
        },
        deleteHost: function(e) {
            var self = this;
            var row = $(e.currentTarget).data('row.dt'),
                rowData = row.data(),
                hostId = rowData.Uuid,
                name = rowData.HostName;
            var keywords = '主机'+App.highlight(name);
            Modal.confirm("确定删除"+keywords+"吗？", function(bsure) {
                if (bsure) {
                    var processor = Modal.processing("正在删除"+keywords);
                    self.ajax.delete("/api/v1/hosts/"+hostId, function(err, data) {
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
        parseIpRange: function(ipPool) {
            var result = [];
            if (!ipPool) {
                return result;
            }
            var ipRangs = ipPool.split(";");
            var info;
            for (var pool in ipRangs) {
                if (!ipRangs[pool]) {
                    continue;
                }
                info=ipRangs[pool].split(":");
                var oneRange = {};
                oneRange["ip_start"] = info[0];
                oneRange["ip_end"] = info[1];
                result.push(oneRange);
            }
            return result
        },
        parseVlanRange: function(vlanPool) {
            var result = [];
            if (!vlanPool) {
                return result;
            }
            var vlanRangs = vlanPool.split(";");
            var info;
            for (var pool in vlanRangs) {
                if (!vlanRangs[pool]) {
                    continue;
                }
                info=vlanRangs[pool].split(":");
                var oneRange = {};
                oneRange["vlanStart"] = new  Number(info[0]);
                oneRange["vlanEnd"] = new  Number(info[1]);
                result.push(oneRange);
            }
            return result
        },
        initTableVg: function(id) {
            var self = this;
            var disks;
            if (!self.$tableVg.isDataTable()) {
                DataTables.init(this.$tableVg, {
                    ajax: {
                        url:"/api/v1/hosts/disk/"+id+"/json"
                    },
                    dataSrc: function (data) {
                        disks = data.Disk;
                        return data.Lvm;
                    },
                    columns: [
                        {
                            'data': "Name",
                            'minWidth': DataTables.width("name")
                        },
                        {
                            'data': "Labels",
                            'width': "4em"
                        },
                        {
                            'data': "Type",
                            'width': "3em"
                        },
                        {
                            'data': "Disk",
                            'width': "6em"
                        },
                        {
                            'data': "",
                            'width': DataTables.width("opt-1"),
                            'render': function(data, type, rowData, meta) {
                                return (
                                    '<a class="btn btn-opt btn-delete" title="删除" data-toggle="tooltip">' +
                                        '<i class="fa fa-trash-o"></i>' +
                                    '</a>'
                                );
                            }
                        }
                    ]
                }, function() {
                    var $tableVgTop = $(self.$tableVg.selector + "_top");
                    self.bind('click', $('.btn-add', $tableVgTop), function() {
                        self.addVg(disks);
                    });
                    self.bind('click', $('.btn-edit', self.$tableVg), self.editVg);
                    self.bind('click', $('.btn-delete', self.$tableVg), self.deleteVg);
                });
            }
        },
        addVg: function(disks) {
            var self = this;
            Modal.show({
                title: "新增卷组",
                remote: function() {
                    var def = $.Deferred();
                    self.render(App.remote("+/cog_add.html"), {disks: disks}, function(err, html) {
                        def.resolve(html);
                    });
                    return def.promise();
                },
                onloaded: function(dialog) {
                    var $dialog = dialog.getModalDialog(),
                        $form = $("form", $dialog);
                    $form.validate({
                        errorContainer: $dialog,
                        errorPlacement: "left",
                        rules: {
                            name: {
                                required: true
                            },
                            disks: {
                                required: true
                            }
                        },
                        messages: {
                            name: {
                                required: "卷组名称必须填写"
                            },
                            disks: {
                                required: "必须至少选择一个磁盘"
                            }
                        }
                    });
                    // icheck
                    $('input[type="checkbox"]', $dialog).iCheck({checkboxClass: "icheckbox-info"});
                },
                buttons: [
                    {
                        label: "取消",
                        action: function(dialog) {
                            dialog.close();
                        }
                    },
                    {
                        label: "创建",
                        cssClass: "btn-primary",
                        action: function(dialog) {
                            var $dialog = dialog.getModalDialog(),
                                $form = $("form", $dialog);
                            if (!$form.length || !$form.valid()) return false;
                            var formData = $form.serializeObject();
                            // do submit and reload table
                            // 模拟表格添加数据
                            var tableVgApi = self.$tableVg.dataTable().api();
                            var len = tableVgApi.rows().nodes().length;
                            tableVgApi.row.add({
                                id: "" + (len + 1),
                                Name: formData.name,
                                Labels: formData.labels,
                                Type: formData.disktype,
                                Disk: $.isArray(formData.disks) ? formData.disks : [formData.disks]
                            }).draw();
                            dialog.close();
                        }
                    }
                ]
            });
        },
        editVg: function(e) {
            var self = this;
            var row = $(e.currentTarget).data('row.dt'),
                rowData = row.data(),
                id = rowData.id,
                name = rowData.Name;
            Modal.show({
                title: "编辑VG信息",
                remote: function() {
                    var def = $.Deferred();
                    self.render(App.remote("+/cog_edit.html"), rowData, function(err, html) {
                        def.resolve(html);
                    });
                    return def.promise();
                },
                onloaded: function(dialog) {
                    var $dialog = dialog.getModalDialog(),
                        $form = $("form", $dialog);
                    $form.validate({
                        errorContainer: $dialog,
                        errorPlacement: "left",
                        rules: {
                            name: {
                                required: true
                            },
                            disks: {
                                required: true
                            }
                        },
                        messages: {
                            name: {
                                required: "VG名称必须填写"
                            },
                            disks: {
                                required: "必须至少选择一个磁盘"
                            }
                        }
                    });
                    // icheck
                    $('input[type="checkbox"]', $dialog).iCheck({checkboxClass: "icheckbox-info"});
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
                            var formData = $form.serializeObject();
                            // do submit
                            dialog.close();
                        }
                    }
                ]
            });
        },
        deleteVg: function(e) {
            var self = this;
            var $this = $(e.currentTarget),
                row = $this.data('row.dt'),
                rowData = row.data(),
                id = rowData.Uuid,
                name = rowData.Name,
                rowEl = $this.parents("tr:first").get(0);
            var keywords = '卷组' + App.highlight(name);
            Modal.confirm("确定删除"+keywords+"吗？", function(bsure) {
                if (bsure) {
                    var processor = Modal.processing("正在删除" + keywords);
                    self.ajax.delete('/api/v1/volumes/reset/'+id,function(err, data){
                        if (err) {
                            self.onError(err, function(err) {
                                processor.error(keywords+'删除失败。原因：'+err.message);
                            });
                        } else {
                            processor.success(keywords+'删除成功');
                            self.$tableVg.reloadTable();
                        }
                    });
                }
            });
        }
    });
});