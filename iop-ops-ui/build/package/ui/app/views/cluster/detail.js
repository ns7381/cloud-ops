define(['App', 'common/ui/modal'], function(App, Modal) {
    return App.View({
        data: App.remote('/api/v1/clusters/detail'),
        dataFilter: function(err, data) {
            if (err) {
                this.onError(err, function(err) {
                    Modal.error("获取资源失败，原因是：" + err.message);
                });}
            data = data || {};
           // data = data.global_config;
            var blkstr = "";
            $.each(data.global_config.vlanPool, function(i,vlanRange) {
                        var str = vlanRange.vlanStart + ":" + vlanRange.vlanEnd;
                        blkstr = blkstr + str + ";"
                });
                data.global_config.vlans = blkstr
            return data
        }
    });
});