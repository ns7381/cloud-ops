define(['App', 'common/ui/modal', 'common/ui/datatables', 'common/ui/validator'], function (App, Modal, DataTables) {
    return App.View({
        $table: $([]),
        ready: function() {
            var self = this;

            self.set('$table', self.$('#NetworkTable'));

            this.initTable(function() {
                var $tableTop = self.$('#NetworkTable_top');

                self.bind('click', $('.btn-add', $tableTop), self.addNetwork);
                self.bind('click', $('.btn-delete', self.$table), self.deleteNetwork);
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
                        'data': "Name",
                        'minWidth': DataTables.width("name")
                    },
                    {
                        'data': "Mode",
                        'width': "6em"
                    },
                    {
                        'data': "Cidr",
                        'width': "8em"
                    },

                    {
                        'data': "Gateway",
                        'width': "8em"
                    },
                    {
                        'data': {},
                        'width': DataTables.width("opt-1"),
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
                '/api/v1/networks/page/{pageNo}/{pageSize}'
            );
        },
        addNetwork: function() {
            var self = this;
            Modal.show({
                title: "创建子网",
                remote: function() {
                    var def = $.Deferred();
                    self.render(App.remote('+/add.html'), function(err, html) {
                        def.resolve(html);
                    });
                    return def.promise();
                },
                onloaded: function(dialog) {
                    var $dialog = dialog.getModalDialog(),
                        $form = $("form", $dialog);
                    var $wizard = this.$element;

                    var $networkmodeElement = $('#mode', $dialog),
                        $macVlanIdElement = $('#macVlanId', $dialog);
                        $networkmodeElement.on('change', function () {
                        var networkMode=$("#mode", $dialog).val();
                        if (networkMode!="macVlanTrunk")
                        {
                            $macVlanIdElement.attr('disabled', 'disabled');
                        }else{
                            $macVlanIdElement.removeAttr('disabled');
                            self.ajax.get('/api/v1/clusters/vlan', function(err, data) {
                                if (err) {
                                    data = [];
                                }
                                self.initClusterVlan($wizard, data);
                            });
                        }
                    });
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
                            var createNetworkOptions = {
                                name:     $("#name", $dialog).val(),
                                cidr:     $("#cidr", $dialog).val(),
                                gateway:  $("#gateway", $dialog).val(),
                                mode:     $("#mode", $dialog).val(),
                                vlanId:      $("#macVlanId", $dialog).val(),
                                ip_range: self.parseIpRange($("#ip-pool", $dialog).val())
                            };
                            var keywords = "子网" + App.highlight(createNetworkOptions.data),
                                processor = Modal.processing("正在创建" + keywords);
                            dialog.close();
                            self.ajax.postJSON('/api/v1/networks/create', createNetworkOptions, function(err, data) {
                                if (err) {
                                    processor.error(keywords + '创建失败。原因：' + err.message)
                                } else {
                                    processor.success(keywords + '创建成功');
                                    self.$table.reloadTable();
                                }
                            })
                        }
                    }
                ]
            });
        },
        deleteNetwork: function(e) {
            var self = this;
            var row = $(e.currentTarget).data("row.dt"),
                rowData = row.data(),
                networkId = rowData.Uuid,
                networkName = rowData.Name;
            var keywords = '网络' + App.highlight(networkName);
            Modal.confirm('确定要删除'+keywords+'吗?', function(bsure) {
                if (bsure) {
                    var processor = Modal.processing("正在删除"+keywords);
                    self.ajax.delete("/api/v1/networks/"+networkId, function(err, data){
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
        initClusterVlan: function($wrapper,vlanList) {
            var $selectClusterVlan = $('select[name="macVlanId"]', $wrapper),
                clusterVlanPairs = [{id:"network-vlan-select-default", name:"选择一个VLAN"}];
            $.each(vlanList, function(i, vlanId) {
                if (vlanId) {
                    clusterVlanPairs.push({value: vlanId});
                }
            });
            $selectClusterVlan.html(App.uiSelect(clusterVlanPairs));
            $('#network-vlan-select-default').val("");
        },
        parseIpRange: function(ipPool) {
            var result = [];
            if (!ipPool) {
                return result;
            }
            var ipRangs= ipPool.split(";"), info;
            for (var pool in ipRangs){
                if (!ipRangs[pool]) {
                    continue;
                }
                info = ipRangs[pool].split(":");
                var oneRange = {};
                oneRange["ip_start"] = info[0];
                oneRange["ip_end"] = info[1];
                result.push(oneRange);
            }
            return result
        }
    });
});
