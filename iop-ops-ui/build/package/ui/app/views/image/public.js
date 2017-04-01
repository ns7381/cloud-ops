define(['App', "common/ui/modal", 'bs/paging'], function(App, Modal) {
    return App.View({
        ready: function() {
            this.set('$paging', this.$("#public_images-paging"));
            this.set('$imageList', this.$(".publicImage"));
            this.renderList();
        },
        $paging: $([]),
        $imageList: $([]),
        renderList: function(page, pageSize) {
            var self = this;

            page = page || 1;
            pageSize = pageSize || 5;

            this.render({
                source: self.getPathId('+/list'),
                data: App.remote('/api/v1/images/public/page/'+ page +'/'+ pageSize),
                dataFilter: function(err, data) {
                    if (err) {
                        this.onError(err, function(err) {
                            Modal.error('获取公有镜像列表失败。原因：'+err.message);
                        });
                        data = {
                            totalCount: 0,
                            result: []
                        }
                    }
                    return data;
                }
            }, function(err, html, data, source) {
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
        }
    });
});