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
                columns: [
                    {
                        "width": DataTables.width("check"),
                        "defaultContent": "<label><input type='checkbox'></label>"
                    },
                    {
                        "data": "name",
                        "width": DataTables.width("name")
                    },
                    {
                        "data": "description",
                        "width": DataTables.width("name")
                    },
                    {
                        "data": "startAt",
                        "width": DataTables.width("datetime")
                    },
                    {
                        "data": "endAt",
                        "width": DataTables.width("datetime")
                    },
                    {
                        "data": "step",
                        "width": "5em"
                    },
                    {
                        "data": {},
                        "width": DataTables.width("opt"),
                        "render": function (data) {
                            return [
                                '<a class="btn-opt btn-view" data-toggle="tooltip" href="javascript:void(0)" title="执行结果">',
                                '<i class="fa fa-file-word-o"></i>',
                                '</a>'
                            ].join('');
                        }
                    }
                ]
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
        }
    });
});