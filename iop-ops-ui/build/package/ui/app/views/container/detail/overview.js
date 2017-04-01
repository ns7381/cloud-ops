define(['App', "common/ui/modal"], function(App, Modal) {
    return App.View({
        data: function() {
            var containerId = this.getParam('id');
            return App.remote('/api/v1/containers/'+containerId+'/json');
        },
        dataFilter: function(err, resp) {
            if (err) {
                this.onError(err, function(err) {
                    Modal.error('获取详情失败。原因：'+err.message)
                });
                resp = {
                    Name: '',
                    HostConfig: '',
                    NetworkSettings: '',
                    State: '',
                    Node: '',
                    Image: ''
                };
            }
            return {containerInfo: resp};
        },
        ready: function() {
            var $containerName = this.$('#container-name'),
                $containerIP = this.$('#container-ip'),
                $containerState = this.$('#container-state'),
                $containerHost = this.$('#container-host'),
                $containerImage = this.$('#container-image');

            var containerInfo = this.getData('containerInfo');

            $containerName.text(containerInfo.Name);

            containerInfo.HostConfig = containerInfo.HostConfig || {};

            var driverMode = containerInfo.HostConfig.NetworkMode;

            var ipaddress;
            try {
                ipaddress = containerInfo.NetworkSettings.Networks[driverMode].IPAMConfig["IPv4Address"];
            } catch (e) {
                ipaddress = "";
            }

            $containerIP.text(ipaddress);

            containerInfo.State = containerInfo.State || {};

            if (containerInfo.State.Running) {
                $containerState.text("运行中");
            } else {
                $containerState.text("停止");
            }

            containerInfo.Node = containerInfo.Node || {};

            $containerHost.text(containerInfo.Node.IP);

            $containerImage.text(containerInfo.Image);
        }
    });
});