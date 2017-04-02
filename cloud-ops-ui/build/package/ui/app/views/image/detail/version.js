define(['App', 'common/ui/datatables', 'common/ui/modal'], function(App, DataTables, Modal) {
    return App.View({
        $table: $([]),
        ready: function() {
            var self = this;
            var $table = self.$('#table-tag');
            self.set('$table', $table);
            this.initTable(function() {
                var $tableTop = self.$('#table-tag_top');

                self.bind("click", $('.btn-add', $tableTop), self.addVersion);
                self.bind("click", $('.btn-delete', $table), self.deleteVersion);
            });
        },
        initTable: function(callback) {
            DataTables.init(this.$table, {
                ajax: this.tableAjax(),
                columns: [
                    {
                        "width": DataTables.width("check"),
                        "defaultContent": "<label><input type='checkbox'></label>"
                    },
                    {
                        "data": "Repo",
                        "minWidth": DataTables.width("name")
                    },
                    {
                        "data": "Size",
                        "width": "6em"
                    },
                    {
                        "data": "Tag",
                        "width": "6em"
                    },
                    {
                        "data": "Status",
                        "width": "6em"
                    },
                    {
                        "data": "Createtime",
                        "width": DataTables.width("datetime")
                    },
                    {
                        'data': "",
                        'width': DataTables.width("opt"),
                        "render": function(data) {
                            return (
                                '<a class="btn-delete btn-opt" data-toggle="tooltip" title="删除"><i class="fa fa-trash-o fa-fw"></i></a>'
                            );
                        }
                    }
                ]
            }, callback);
        },
        tableAjax: function() {
            var repo = this.getParam('repo');
            return DataTables.parseAjax(
                this,
                this.$table,
                '/api/v1/images/version?imagename=' + repo
            );
        },
        addVersion: function() {
            var self = this,
                repo = this.getParam('repo');
            Modal.show({
                title: "新增版本",
                remote: function (dialog) {
                    var def = $.Deferred();
                    self.render({
                        url: "+/add.html",
                        data: App.remote('/api/v1/images/registry/url'),
                        dataFilter: function(err, data) {
                            dialog.setData('data', data);
                            return {
                                imageName: repo
                            };
                        },
                        callback: function(err, html) {
                            def.resolve(html);
                        }
                    });
                    return def.promise();
                },
                onloaded: function (dialog) {
                    var $dialog = dialog.getModalDialog(),
                        $form = $("form", $dialog);
                    $form.validate({
                        errorContainer: $dialog,
                        errorPlacement: "left top",
                        rules: {
                            'name': {
                                required: true
                            },
                            'tag': {
                                required: true
                            }
                        }
                    });
                    $('#name', $form).val(repo);
                },
                buttons: [
                    {
                        label: "取消",
                        action: function(dialog) {
                            dialog.close();
                        }
                    },
                    {
                        label: "确定",
                        cssClass: "btn-primary",
                        action: function (dialog) {
                            var $dialog = dialog.getModalDialog(),
                                $addImage = $("#add_image_tag", $dialog);
                            if (!$addImage.length || !$addImage.valid()) return false;
                            var import_mode = $('#import_image input[name="import_mode"]:checked', $dialog).val();
                            $addImage.attr("method", "post");
                            $addImage.attr("enctype", "multipart/form-data");
                            $addImage.attr("action", "/api/v1/images/tag/upload/"+import_mode);
                            if(import_mode=="file" && !$("#image_file", $dialog).val()){
                                Modal.warning("请上传文件");
                                return false;
                            }
                            if(import_mode=="git" && !$("#image_git", $dialog).val()){
                                Modal.warning("请填写Git地址");
                                return false;
                            }
                            var processor = Modal.processing('正在上传');
                            $('.btn-upload', $dialog).prop("disabled", true);
                            $addImage.ajaxSubmit({
                                success: function (data) {
                                    processor.success('上传成功');
                                    dialog.close();
                                },
                                error: function (err) {
                                    processor.error('上传失败。原因：');
                                    $('.alert-danger', $dialog).html(err.responseText);
                                    $('.btn-upload', $dialog).attr("disabled", false);
                                }
                            });

                        }
                    }
                ]
            });
        },
        deleteVersion: function(e) {
            var self = this;
            var row = $(e.currentTarget).data("row.dt"),
                rowData = row.data(),
                imageId = rowData.Uuid,
                imageName = rowData.Repo+":"+rowData.Tag;
            var keywords = '镜像'+App.highlight(imageName);
            Modal.confirm('确定要删除'+keywords+'吗?', function(bsure) {
                if (bsure) {
                    var processor = Modal.processing("正在删除"+keywords);
                    self.ajax.delete("/api/v1/images/"+imageId, function(err, data) {
                        if (err) {
                            processor.error(keywords+'删除失败。原因：'+err.message);
                        } else {
                            processor.success(keywords+'删除成功');
                            self.$table.reloadTable();
                        }
                    });
                }
            });
        }
    });
});