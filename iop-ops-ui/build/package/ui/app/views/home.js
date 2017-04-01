define(['App', 'jquery'], function(App, $) {
    return App.View({
        data: App.remote('/api/v1/clusters/list'),
        dataFilter: function(err, data) {
            if (!err) {
                data = data || {};
                var cpuinfo = data.CpuUseInfo ? data.CpuUseInfo.split("/") : [0, 1];
                var diskinfo = data.RootDiskUseinfo ? data.RootDiskUseinfo.split("/") : [0, 1];
                var meminfo = data.MemoryUseInfo ? data.MemoryUseInfo.split("/") : [0, 1];
                var dockerdiskinfo = data.DockerVGUserinfo ? data.DockerVGUserinfo.split("/") : [0, 1];
                var volumediskinfo = data.VolumeVGUserinfo ? data.VolumeVGUserinfo.split("/") : [0, 1];
				
                data.cpuradio = (cpuinfo[0] / cpuinfo[1]) || 0;
                data.diskradio = (diskinfo[0] / diskinfo[1]) || 0;
                data.memradio = (meminfo[0] / meminfo[1]) || 0;
				data.dockerdiskradio = (dockerdiskinfo[0] / dockerdiskinfo[1]) || 0;
				data.volumediskradio = (volumediskinfo[0] / volumediskinfo[1]) || 0;
            } else {
                data = {};
                data.cpuradio = 0;
                data.diskradio = 0;
                data.memradio = 0;
				data.dockerdiskradio = 0;
				data.volumediskradio = 0;
            }
            return data;
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

            /*
            var $tabConstantlyMonitor = this.$('#tab-constantly-monitor');
            $tabConstantlyMonitor.tabShow(function(index, $tabPane) {
                self.load(self.firstInclude($tabPane));
            });
            */
        }
    });
});