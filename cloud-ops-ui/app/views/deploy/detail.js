define(['common/tool', 'bs/tab'], function (Tool) {
    var appPage = {};
    appPage.init = function () {
        $('a[data-toggle="tab"]', $("#deployDetailTab")).on('show.bs.tab', function (e) {
            e.target.hash // 激活的标签页
            e.relatedTarget // 前一个激活的标签页
            require(["deploy/"+e.target.hash.substring(1, e.target.hash.length)], function (module) {
                module.init();
            });
        })
    };

    return appPage;
}); 