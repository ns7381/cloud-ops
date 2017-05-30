/**
 * Created by Nathan on 2017/4/10.
 */
define(['App', 'bs/tab'], function (App) {
    return App.View({
        data: function () {
            return {"name": App.getParam('name'), id : App.getParam("id"),
                "environmentId": App.getParam('environmentId'), environmentName : App.getParam("environmentName"),
                environmentType : App.getParam("environmentType")};
        },
        ready: function () {
            var tab = this.getParam('tab');
            var self = this;
            var $tab = $("#application-detail-tab");

            $tab.tabShow(function (index, $tabPane) {
                self.load(self.firstInclude($tabPane));
            });
            if (tab) {
                $('[data-toggle="tab"][href^="#' + tab + '"]', $tab).tab("show");
            }
        }
    });
});
