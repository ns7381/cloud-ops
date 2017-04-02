define(['App', 'jquery'], function (App, $) {
    return App.View({
        data: function() {
            return {id: this.getParam('id'), name: this.getParam('name')};
        },
        ready: function() {
            var self = this;

            setTimeout(function() {
                self.$('.progress > .progress-bar')
                    .one($.support.transition.end, function(e) {
                        var value = $(this).attr('aria-valuenow') || 0;
                        value > 100 && $(this).removeClass("progress-bar-info").addClass("progress-bar-danger");
                    }).each(function() {
                    var value = $(this).attr('aria-valuenow') || 0;
                    if (value) {
                        if (value > 100) {
                            value = 100;
                        }
                        $(this).css('width', value+'%');
                    } else {
                        $(this).css('width', 0);
                    }
                });
            }, 1000/60);

            var $tab = this.$("#tab-container");
            $tab.tabShow(function(index, $tabPane) {
                self.load(self.firstInclude($tabPane));
            });
        }
    });
});
