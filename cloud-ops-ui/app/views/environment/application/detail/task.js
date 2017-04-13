define(['App', 'common/ui/datatables', 'common/ui/modal'], function (App, DataTables, Modal, LocationTpl) {
    return App.View({
        $table: $([]),
        applicationId: '',
        environmentName: '',
        ready: function () {
            var self = this;
            self.applicationId = App.getParam('id');
            self.$table = $('#taskTable');

            this.initTable(function () {
                var $tableTop = $(self.$table.selector + "_top");

                // self.bind('click', $('.btn-add', $tableTop), self.addTopology);
                self.bind('click', $('.btn-view', self.$table), self.viewTask);
                // self.bind("click", $(".btn-delete", self.$table), self.deleteTopology);
                self.bind('click', $('.btn-detail', self.$table), self.viewServer);
            });
        },
        tableAjax: function () {
            return DataTables.parseAjax(
                this,
                this.$table,
                "v1/workflows?objectId=" + this.applicationId
            );
        },
        initTable: function (callback) {
            var self = this;
            DataTables.init(this.$table, {
                serverSide: true,
                ajax: this.tableAjax(),
                columns: [{
                    "targets": [0],
                    "class": "cell-em-2",
                    "render": function (data, type, full) {
                        return '<a class="btn-opt btn-detail">' +
                            '<i class=" glyphicon glyphicon-chevron-down" data-toggle="tooltip" title="展开"></i>'
                            + '<i class=" glyphicon glyphicon-chevron-up" data-toggle="tooltip" title="收起" style="display:none"></i></a>';
                    }
                }, {
                    "data": "name",
                    "width": DataTables.width("name")
                }, {
                    "data": "startAt",
                    "width": DataTables.width("datetime")
                }, {
                    "data": "endAt",
                    "width": DataTables.width("datetime")
                }, {
                    "data": "step",
                    "width": "5em"
                }, {
                    "data": {},
                    "width": DataTables.width("opt"),
                    "render": function (data) {
                        return [
                            '<a class="btn-opt btn-view" data-toggle="tooltip" href="javascript:void(0)" title="执行结果">',
                            '<i class="fa fa-file-word-o"></i>',
                            '</a>'
                        ].join('');
                    }
                }]
            }, callback);
        },
        viewTask: function (e) {
            var self = this;
            var row = $(e.currentTarget).data("row.dt"),
                rowData = row.data(),
                id = rowData.id;
            Modal.show({
                title: "查看结果",
                size: {
                    width: '740px'
                },
                message: rowData.message,
                buttons: [{
                    label: "关闭",
                    action: function (dialog) {
                        dialog.close();
                    }
                }]
            });
        },
        viewServer: function (e) {
            var self = this;
            var tr = $(e.currentTarget).closest('tr'),
                row = self.table.row(tr),
                rowData = row.data(),
                index = tr.attr('index'),
                that = $(e.currentTarget);
            var $Rtr = $('.DTFC_RightWrapper').find('tr[index="'+index+'"]');
            if (row.child.isShown()) {
                that.children('.glyphicon-chevron-up').hide();
                that.children('.glyphicon-chevron-down').show();
                row.child.hide();
                tr.removeClass('shown');
                $Rtr.next('tr').remove();
            } else {
                that.children('.glyphicon-chevron-down').hide();
                that.children('.glyphicon-chevron-up').show();
                row.child(self.format(rowData)).show();
                tr.addClass('shown');
                $Rtr.after('<tr></tr>');
                $Rtr.next('tr').height(tr.next('tr').height());
                $('[data-toggle="tooltip"]').tooltip({container: "body"});
            }
            self.$table.resize();
        },
        format: function (rowData) {
            var self = this;

            var serverStr = '<table cellpadding="5" cellspacing="0" border="0" style="padding-left:50px;">',
                optStr =
                    '<a class="btn-opt btn-edit-server" data-toggle="tooltip" title="修改"><li class="fa fa-pencil fa-fw"></li></a>' +
                    '<a class="btn-opt btn-delete-server" data-toggle="tooltip" title="删除"><i class="fa fa-trash-o fa-fw"></i></a>';
            $.each(rowData.steps, function (k, step) {
                serverStr +=
                    "<tr data-server='" + JSON.stringify(server) + "' data-index='" + k + "' data-listen='" + index + "'>" +
                    '<td>后端云主机' + (k + 1) + ':</td><td>IP: ' + server.ip + '</td><td> 端口: ' + server.port + '</td><td> 权重: ' +
                    server.weight + '</td><td id=' + index + '_' + k + '> 状态:' + '</td>' +
                    '<td>' + optStr + '</td></tr>';
            });
            if (servers.length < 1) {
                serverStr += '<tr><td>还未绑定后端云主机</td></tr>';
            } else if (self.instance.status == "RUNNING") {
                self.ajax.postJSON(self.postUrl, self.postParam, function (err, data) {
                    if (data && data.result && data.result[0].status == "SUCCESS") {
                        if (data.result[0].result) {
                            var arr = data.result[0].result.trim().split("\n");
                            for (var i = 0; i < arr.length; i++) {
                                $('#' + index + '_' + i, self.$table).text('状态: ' + (arr[i] == "UP" ? "可用" : "不可用"));
                            }
                        }
                    } else {
                        self.onError(err, function (err) {
                            Modal.error("获取后端云主机状态失败");
                        })
                    }
                });
            }
            serverStr += "</table>";
            return serverStr;
        }
    });
});