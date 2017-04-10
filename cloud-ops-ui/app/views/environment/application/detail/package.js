define(['App', 'common/ui/datatables', 'common/ui/modal', 'common/ui/validator', 'bs/tab'], function (App, DataTables, Modal) {
    return App.View({
        app_id: "",
        $table: $([]),
        application: {},
        data: function () {
            this.app_id = App.getParam('id');
            return App.remote("/v1/applications/" + this.app_id);
        },
        dataFilter: function (err, data) {
            return {topology: data};
        },
        ready: function () {
            var self = this;
            self.$table = $('#packageTable');
            this.initTable(function () {
                var $tableTop = $(self.$table.selector + "_top");

                self.bind('click', $('.btn-add', $tableTop), self.addTopology);
                self.bind('click', $('.btn-edit', self.$table), self.editTopology);
                self.bind("click", $(".btn-delete", self.$table), self.deleteTopology);
            });
        },
        tableAjax: function () {
            return DataTables.parseAjax(
                this,
                this.$table,
                "v1/resource-packages?applicationId=" + this.app_id
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
                        "data": "version",
                        "width": DataTables.width("name")
                    },
                    {
                        "data": "createdAt",
                        "width": DataTables.width("name")
                    },
                    {
                        "data": "status",
                        "width": DataTables.width("name")
                    },
                    {
                        "data": {},
                        "width": DataTables.width("opt"),
                        "render": function (data) {
                            return [
                                '<a class="btn-opt btn-edit" data-toggle="tooltip" href="javascript:void(0)" title="编辑">',
                                '<i class="fa fa-pencil"></i>',
                                '</a>',
                                '<a class="btn-opt btn-delete" data-toggle="tooltip" href="javascript:void(0)" title="删除">',
                                '<i class="fa fa-trash-o"></i>',
                                '</a>'
                            ].join('');
                        }
                    }
                ]
            }, callback);
        }
    });
});