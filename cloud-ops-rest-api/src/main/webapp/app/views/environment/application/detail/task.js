define(['App', 'common/ui/datatables', 'common/ui/modal'], function (App, DataTables, Modal, LocationTpl) {
    return App.View({
        $table: $([]),
        table: $([]),
        applicationId: '',
        environmentName: '',
        ready: function () {
            var self = this;
            self.applicationId = App.getParam('id');
            self.$table = $('#taskTable');

            this.initTable(function () {
                var $tableTop = $(self.$table.selector + "_top");

                // self.bind('click', $('.btn-add', $tableTop), self.addTopology);
                self.bind('click', $('.btn-view', self.$table), self.viewMessage);
                // self.bind("click", $(".btn-delete", self.$table), self.deleteTopology);
                self.bind('click', $('.btn-detail', self.$table), self.viewTask);
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
            self.table = DataTables.init(this.$table, {
                serverSide: false,
                ajax: this.tableAjax(),
                columns: [{
                    "width": DataTables.width("check"),
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
                    "data": "status",
                    "width": "5em"
                }]
            }, callback);
        },
        viewMessage: function (e) {
            var self = this,
                tr = $(e.currentTarget).closest('tr'),
                message = tr.data("message");
            Modal.show({
                title: "查看结果",
                size: {
                    width: '740px'
                },
                message: message,
                buttons: [{
                    label: "关闭",
                    action: function (dialog) {
                        dialog.close();
                    }
                }]
            });
        },
        viewTask: function (e) {
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

            var stepTpl = '<table cellpadding="5" cellspacing="0" border="0" style="padding-left:50px;">',
                optStr =
                    '<a class="btn-opt btn-view" data-toggle="tooltip" title="查看结果"><li class="fa fa-file-word-o"></li></a>';
            $.each(rowData.steps, function (k, step) {
                stepTpl +=
                    "<tr data-message='" + step.description + "'>" +
                    '<td>操作步骤' + (k + 1) + ':</td><td>名称: ' + step.name + '</td><td>执行节点: ' + step.hostIp + '</td><td> 开始时间: ' +
                    step.startAt + '</td><td> 结束时间:' + step.endAt+'</td>' +'</td><td> 状态:' + step.status+'</td>' +
                    '<td>' + optStr + '</td></tr>';
            });
            if (rowData.steps.length < 1) {
                stepTpl += '<tr><td>还未执行操作</td></tr>';
            }
            stepTpl += "</table>";
            return stepTpl;
        }
    });
});