define(['App', "common/ui/modal"], function(App, Modal) {
    return App.View({
        data: function() {
            var containerId = this.getParam('id');
            return App.remote({
                url: '/api/v1/containers/'+containerId+'/logs',
                dataType: "text",
                mimeType: "text/plain; charset=x-user-defined",
                respFilter: function(err, resp, xhr) {
                    resp = App.parseBytesText(resp);
                    return [err, resp, xhr];
                }
            });
        },
        dataFilter: function(err, applogs) {
            if (err) {
                this.onError(err, function(err) {
                    Modal.error('获取日志失败。原因：'+err.message);
                });
                applogs = '';
            }
            applogs = $.trim(applogs);
            applogs = applogs.replace(/\n/g, "<br/>");
            return {applogs: applogs};
        }
    });
});