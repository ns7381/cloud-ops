define(['App', 'common/ui/datatables'], function (App, DataTables) {
    return App.View({
        ready: function() {
            var $table = this.$('#eventTypeTable');
            this.set('$table', $table);
            this.initTable();
        },
        $table: $([]),
        initTable: function(callback) {
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
                        'data': "ObjectId",
                        'width': DataTables.width("name")
                    },
                    {
                        'data': "ObjectType",
                        'width': "4.5em"
                    },
                    {
                        'data': "OperIp",
                        'width': DataTables.width("host")
                    },
                    {
                        'data': "OperType",
                        'width': "7em"
                    },
                    {
                        'data': "OperTime",
                        'width': DataTables.width("datetime")
                    },
                    {
                        'data': "OperCmd",
                        'minWidth': DataTables.width("name")
                    }
                ]
            }, callback);
        },
        tableAjax: function() {
            return DataTables.parseAjax(
                this,
                this.$table,
                '/api/v1/operation/page/{pageNo}/{pageSize}'
            );
        }
    });
});
