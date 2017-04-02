define(['App', "jquery", 'common/ui/modal'], function(App, $, Modal) {
    return App.View({
        ready: function() {
            var self = this;

            var $panel = this.$('#tab-pane-conf');

            self.bind('click', $('.btn-addd', $panel), function(e) {
                var $this = $(e.currentTarget),
                    table = $this.closest('table'),
                    $table = $(table);
                var tr = $this.closest('tr').html();
                $('thead', $table).append('<tr>'+ tr +'</tr>');
                $('.btn-addd', $table).removeClass('btn-addd').addClass('btn-del').text('删除');
                $('.btn-del:last', $table).removeClass('btn-del').addClass('btn-addd').text('添加');
            });

            self.bind('click', $('.btn-del', $panel), function(e) {
                var tr = $(e.currentTarget).closest('tr');
                $(tr).remove();
            });

            var containerId = self.getParam('id'),
                containerName = self.getParam('name');

            var $volumeTable = self.$('#volumeTable');

            self.ajax.get('/api/v1/volumes/from/'+containerId, function(err, result) {
                if (err) {
                    Modal.error('获取详情失败。原因：'+err.message);
                } else if ($.isArray(result)) {
                    var preHtml = '';
                    $.each(result, function(i, item) {
                        preHtml = (
                            '<tr>' +
                                '<td><input value="'+item.Name+'" type="text" readonly="readonly"></td>' +
                                '<td><input value="'+item.HostDir+'" type="text" readonly="readonly"></td>' +
                                '<td><input value="'+item.ContainerDir+'" type="text" readonly="readonly"></td>' +
                                '<td><input value="'+item.Size+'" type="text" readonly="readonly"></td>' +
                                '<td class="cell-em-6"><a class="btn btn-del">删除</a></td>' +
                            '</tr>'
                        ) + preHtml;
                    });
                    $("tbody > tr:last-child", $volumeTable).before(preHtml)
                }
            });

            var sHtml = "<option value=''>绑定已有卷，请选择</option>", $volume = self.$("#volume");
            self.ajax.get('/api/v1/volumes/', function(err, result) {
                if (err) {
                    Modal.error('获取详情失败。原因：'+err.message);
                } else if ($.isArray(result.result)) {
                    $.each(result.result, function(i, item) {
                        sHtml+="<option value="+item.Uuid+">"+item.Name+"</option>";
                    });
                }
                $volume.html(sHtml);
            });

            self.bind('change', $volume, function(e) {
                var $select = $(e.currentTarget),
                    volumeId = $select.val();
                if (volumeId) {
                    self.ajax.get('/api/v1/volumes/'+volumeId+'/json',function(err, result) {
                        if (err) {
                            Modal.error('获取详情失败。原因：'+err.message)
                        } else {
                            $select.parent().next().children().val(result.HostDir);
                            $select.parent().next().next().children().val(result.ContainerDir);
                            $select.parent().next().next().next().children().val(result.Size);
                        }
                    });
                } else {
                    $select.parent().next().children().val("");
                    $select.parent().next().next().children().val("");
                    $select.parent().next().next().next().children().val("");
                }
            });

            var $volumeBtn = self.$("#volumeBtn");
            self.bind('click', $volumeBtn, function() {
                var volumeName = $("option:selected", $volume).text(),
                    volumeId = $volume.val();
                if (volumeId) {
                    var params = {
                        volumeId:volumeId
                    };
                    var keywords = '容器' + App.highlight(containerName)+"的编辑信息",
                        processor = Modal.processing('正在保存'+keywords);
                    self.ajax.postJSON('/api/v1/containers/mount/'+containerId, params, function(err, data) {
                        if (err) {
                            processor.error(keywords+'保存失败。原因：'+err.message)
                        } else {
                            processor.success(keywords+"保存成功");

                            $volume.parent().html("<input type='text' value="+volumeName+" name='volumeName' readonly>");
                            $("tbody > tr:last", $volumeTable).append("<td class='cell-em-6'><a class='btn btn-del'>删除</a></td>");
                            var addHtml= (
                                '<tr>' +
                                    '<td><select name="volume" id="volume" class="form-control" tabindex="1"></select></td>' +
                                    '<td><input type="text" readonly="readonly"></td>' +
                                    '<td><input type="text" readonly="readonly"></td>' +
                                    '<td><input type="text" readonly="readonly"></td>' +
                                '</tr>'
                            );
                            $("tbody", $volumeTable).append(addHtml);
                            $volume.html(sHtml).trigger("change");
                        }
                    });
                }
            });
        }
    });
});