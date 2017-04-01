define(['App', "common/ui/modal", "common/ui/validator", 'common/ui/fileupload', 'bs/paging'], function(App, Modal) {
    return App.View({
        binds: [
            [
                'click', '.btn-add', function(e) {
                    this.addImage(e);
                }
            ]
        ],
        ready: function() {
            this.set('$paging', this.$("#self_images-paging"));
            this.set('$imageList', this.$(".selfImage"));
            this.renderList();
        },
        $paging: $([]),
        $imageList: $([]),
        renderList: function(page, pageSize) {
            var self = this;

            page = page || 1;
            pageSize = pageSize || 5;

            self.render({
                source: self.getPathId("+/list"),
                data: App.remote('/api/v1/images/page/'+page+'/'+pageSize),
                dataFilter: function(err, data) {
                    if (err) {
                        this.onError(err, function(err) {
                            Modal.error('获取镜像列表失败。原因：'+err.message);
                        });
                        data = {
                            totalCount: 0,
                            result: []
                        }
                    }
                    return data;
                }
            }, function(err, html, data) {
                self.$imageList.html(html);
                if(data.totalCount <= 5) {
                    self.$paging.hide();
                } else {
                    self.$paging.show();
                    self.pageList(page, data.totalCount, pageSize);
                }
            });
        },
        pageList: function(page, total, pageSize) {
            var self = this;

            page = page || 1;
            total = total || 0;
            pageSize = pageSize || 5;

            this.$paging.paging({
                dom: 'incp',
                currentPage: page,
                numberOfPages: pageSize,
                totalCount: total,
                alignment: "center",
                onPageClicked: function(evt, e, type, current) {
                    var pages = $(this).paging('getPages');
                    if (current > 0 && current <=  pages.total) {
                        self.renderList(current, pages.numberOfPages);
                    }
                },
                onPageNumberChanged: function(evt, e, oldNum) {
                    var newNum = $(e.target).val() || oldNum;
                    self.renderList(1, newNum);
                },
                itemContentClass: function (type, page, current) {
                    switch (type) {
                        case "first":
                        case "last":
                        case "prev":
                        case "next":
                            return "pagination-button-" + type;
                        default:
                            return "";
                    }
                },
                shouldShowPage: function (type, page, current) {
                    var result = true;
                    switch (type) {
                        case "page":
                            result = false;
                            break;
                        default:
                            result = true;
                    }
                    return result;
                },
                info: true,
                currentPageInfo: true,
                numbers: true,
                language: {
                    tooltip: function (type, page, current) {
                        switch (type) {
                            case "first":
                                return "首页";
                            case "prev":
                                return "上一页";
                            case "next":
                                return "下一页";
                            case "last":
                                return "末页";
                            case "page":
                                return (page === current) ? "当前页 " + page : "第 " + page + " 页";
                        }
                    },
                    info: function(numberOfPages, totalPages) {
                        return '共 <em>' + total + '</em> 项';
                    },
                    currentPageInfo: function(current, totalPages) {
                        return "页数" + current + "/" + totalPages;
                    },
                    numbers: function(selectHtml, numberOfPages) {
                        return "每页：" + selectHtml;
                    }
                }
            });
        },
        addImage: function(e) {
            var self = this;
            Modal.wizard({
                title: "创建镜像",
                remote: function (dialog) {
                    var def = $.Deferred();
                    self.render({
                        url: "+/add.html",
                        data: App.remote('/api/v1/images/registry/url'),
                        dataFilter: function(err, data) {
                            if (err) {
                                data = {username: '', registry_url: ''}
                            }
                            data = {username: data.username, registry_url: data.registry_url};
                            return data;
                        }
                    }, function(err, html, data) {
                        dialog.setData('data', data);
                        def.resolve(html);
                    });
                    return def.promise();
                },
                onloaded: function (dialog) {
                    var $dialog = dialog.getModalDialog(),
                        $form = $("form", $dialog);
                    $form.each(function() {
                        $(this).validate({
                            errorContainer: $dialog,
                            errorPlacement: "right",
                            rules: {
                                'image_name': {
                                    required: true
                                },
                                'tag': {
                                    required: true
                                }
                            }
                        });
                    });
                    var data = dialog.getData('data'),
                        username = data.username,
                        registry_url = data.registry_url;
                    var $importMode = $('input[name=import_mode]', $dialog),
                        $image_name = $('#image_name', $dialog),
                        $fileDiv = $('#fileDiv', $dialog),
                        $gitDiv = $('#gitDiv', $dialog),
                        $registryUrl = $('#registry_url', $dialog);
                    $importMode.on('change', function () {
                        $fileDiv.toggle();
                        $gitDiv.toggle();
                    });
                    $image_name.on('change', function () {
                        $registryUrl.text(registry_url+"/"+username+"/"+$image_name.val())
                    });
                },
                onChanged: function(currentStep) {
                    var $wizard = this.$element,
                        $currentStep = this.getStepPane();
                    if (currentStep.name === "basicConfig") {
                        $("#image_file", $currentStep).fileUpload({
                            accept: [
                                {title:"Tar files",extensions:"tar"}
                            ],
                            emptyText:"请选择上传文件（*.tar）",
                            startLabel:"",
                            chunkSize:0,
                            onvalidatorerror:function(agr1){
                                Modal.error(agr1);
                            }
                        });
                    }
                },
                formSubmitting: {
                    steps: [2],
                    validations: {
                        1:function($stepPane) {
                            return $('form', $stepPane).valid();
                        },
                        2:function($stepPane) {
                            return $('form', $stepPane).valid();
                        }
                    },
                    action: function (dialog) {
                        var $dialog = dialog.getModalDialog();
                        // do submit form
                        var formData = this.serializeObject(),
                            importMode = formData.import_mode,
                            imageGit = formData.image_git;

                        var $imageFile = $("#image_file", $dialog), isUploaded = $imageFile.fileUpload("isUploaded");

                        if (importMode=="git" && !imageGit) {
                            Modal.warning("请填写Git地址");
                            return false;
                        }

                        var valid = $(".form-horizontal", $dialog).valid();
                        if (!valid) return false;
                        var $btnUpload = $('.btn-upload', $dialog);
                        $btnUpload.prop("disabled", true);
                        if (importMode = 'file') {
                            var doSave = function(arg1,arg2,arg3){
                                var processor = Modal.processing('正在上传');
                                if(!arg2.error){
                                    dialog.close();
                                    processor.success( '添加镜像成功');
                                    dialog.close();
                                    self.renderList()
                                }else{
                                    processor.error('添加镜像失败。原因：' + arg2.message);
                                    $('.alert-danger').html(arg2.responseText);
                                }
                                $btnUpload.prop("disabled", false);
                            };
                            if ($imageFile.fileUpload("getFile") && !isUploaded) {
                                $imageFile.fileUpload("setOption","server","/api/v1/images/upload/file");
                                $imageFile.fileUpload("setOption","fileVal","image_file");
								$imageFile.fileUpload("setOption","formData",formData);
                                $imageFile.fileUpload("setOption","oncomplete",doSave);
                                $imageFile.fileUpload("setOption","onerror",doSave);
                                $imageFile.fileUpload("startUpload");
                            } else {
                                doSave();
                            }
                        } else {
                            var processor = Modal.processing('正在上传');
                            self.ajax.post("/api/v1/images/upload/git", formData, function(err, resp) {
                                if (err) {
                                    self.onError(err, function(err) {
                                        processor.error('添加镜像失败。原因：' + err.message);
                                    });
                                    $('.alert-danger').html(err.responseText);
                                } else {
                                    processor.success('添加镜像成功');
                                    dialog.close();
                                    self.renderList();
                                }
                                $btnUpload.prop("disabled", false);
                            });
                        }
                    }
                }
            });
        }
    });
});