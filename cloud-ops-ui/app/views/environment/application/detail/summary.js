define(['App', 'common/ui/modal', 'common/ui/validator', 'bs/tab'], function (App, Modal) {
    return App.View({
        app_id: "",
        application: {},
        data: function () {
            this.app_id = App.getParam('id');
            return App.remote("/v1/applications/" + this.app_id);
        },
        dataFilter: function (err, data) {
            return data;
        },
        ready: function () {
            var self = this;
            self.bind('click', $('.btn-attr'), self.changeAttr);
            $('[data-toggle="popover"]').popover({html: true});
            $('[data-toggle="popover"]').on('shown.bs.popover', function (e) {
                $('body').find('[data-toggle="popover"]').each(function () {
                    if(this!= e.target) {
                        $(this).popover('hide');
                    }
                });
                var $that = $(e.currentTarget),
                    node = $that.data("node"),
                    key = $that.data("key");
                var $input = $('.input-attr[data-node="'+node+'"][data-key="'+key+'"]'), val = $("." + node + key).text().trim();
                $input.val('').focus().val(val);
            })
        },
        changeAttr: function (e) {
            var $that = $(e.currentTarget);
            var self = this,
                node = $that.data("node"),
                key = $that.data("key"),
                value = $("."+node + key).text(),
                putData = {};
            var $input = $('.input-attr'), val = $input.val().trim();
            if (val == value) return false;
            putData[key] = val;
            $.ajax({
                url: "v1/applications/" + self.app_id + "/node/" + node + "/attributes",
                type: 'put',
                data: putData,
                success: function (data) {
                    $('[data-toggle="popover"]').popover('hide');
                    $("."+node + key).text(val);
                }
            });
            /*self.ajax.put("v1/applications/"+self.app_id+"/node/"+node+"/attributes?" + $.param(putData), function (err, data) {
                if (err) {
                    Modal.error(App.highlight("属性" + key, 2) + '修改失败!原因：' + err.message);
                } else {
                    $('[data-toggle="popover"]').popover('hide');
                    $("."+node + key).text(val);
                }
            });*/
        }
    });
});