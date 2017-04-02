define(['App', 'jquery'], function(App, $) {
    return App.View({
        ready: function() {
            var self = this;
            var $tab = this.$("#tab-image");
            $tab.tabShow(function(index, $tabPane) {
                self.load(self.firstInclude($tabPane));
            });
        }
    });
});