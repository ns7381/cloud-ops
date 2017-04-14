define(['App', 'common/ui/modal', 'common/ui/validator', 'bs/tab'], function (App, Modal) {
    return App.View({
        app_id: "",
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
            self.bind('click', $('.edit-attr'), self.changeAttr);
            $('[data-toggle="popover"]').popover({html: true});
        },
        changeAttr: function (e) {
            var self = this,
                node = $(e.currentTarget).data("node"),
                key = $(e.currentTarget).data("key"),
                value = $(e.currentTarget).data("value");

        }
    });
});