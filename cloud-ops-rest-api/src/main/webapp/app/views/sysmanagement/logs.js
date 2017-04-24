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
                serverSide: false,
                ajax: this.tableAjax(),
                columns: [
                    {
                        'data': "",
                        'width': DataTables.width("check"),
                        'defaultContent': '<label><input type="checkbox"></label>'
                    },
                    {
                        'data': "username",
                        'width': DataTables.width("name")
                    },
                    {
                        'data': "method",
                        'width': "4em"
                    },
                    {
                        'data': "path",
                        'width': "30em"
                    },
                    {
                        'data': "responseStatus",
                        'width': "4em"
                    },
                    {
                        'data': "host",
                        'width': "10em"
                    },
                    {
                        'data': "createdAt",
                        'minWidth': "8em"
                    }
                ]
            }, callback);
        },
        tableAjax: function() {
            return DataTables.parseAjax(
                this,
                this.$table,
                'v1/audits'
            );
        }
    });
});
