/**
 * Created by Nathan on 2017/4/10.
 */
define(['App', 'bs/tab'], function (App) {
    return App.View({
        data: function () {
            return {"name": App.getParam('name')};
        },
        ready: function () {
            var self = this;
            var $tab = $("#application-detail-tab");

            $tab.tabShow(function (index, $tabPane) {
                self.load(self.firstInclude($tabPane));
            });
        }
    });
});
