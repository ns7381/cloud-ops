/**
 * Created by Nathan on 2017/4/10.
 */
define(['App', 'common/ui/codeeditor', 'common/ui/fileupload'], function (App) {
    return App.View({
        topoId: '',
        data: function () {
            this.topoId = App.getParam("id");
            return App.remote(["/v1/topologies/" + this.topoId, "/topologies/" + this.topoId + "/archives"]);
        },
        dataFilter: function (err, data) {
            return {"name": App.getParam('name'), id: App.getParam("id"), topology: data[0], archives: data[1]};
        },
        ready: function () {
            var self = this;
            $("#tab-service-detail").tabShow(function (index, $tabPane, $tab) {
                $("textarea", $tabPane).codeEditor({
                    // value: "",  //初始值
                    mode: index.split("--")[1], //格式，比如java语言是java
                    theme: "default", //皮肤样式
                    lineNumbers: true, //是否显示行号
                    autofocus: true, //是否自动聚焦
                    readOnly: false, //是否可编辑
                    oncreated: null //初始化后的回调方法
                });
            });


            $("#file").fileUpload({
                multiSelection: false,
                // auto: true,
                url: App.getRootUrl("/topologies/" + self.topoId + "/archives"),
                accept: [
                    {title: "sh,yaml,yml files", extensions: "sh,yaml,yml"}
                ],
                emptyText: "请选择上传文件（*.sh，*.yaml，*.yml）",
                chunkSize: 0
            });
        }
    });
});
