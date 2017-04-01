define(['App', 'jquery'], function(App, $) {
    return App.View({
        data: function() {
            var repo = this.getParam('repo');
            return App.remote('/api/v1/images/image/detail?imagename='+repo);
        },
        dataFilter: function(err, data) {
            if (err) {
                data = {
                    Repo: this.getParam('repo')
                };
            }
            return data;
        },
        ready: function() {
            var self = this;

            var $tab = this.$("#tab-image-detail");
            $tab.tabShow(function(index, $tabPane) {
                self.load(self.firstInclude($tabPane));
            });


        }
    });
});