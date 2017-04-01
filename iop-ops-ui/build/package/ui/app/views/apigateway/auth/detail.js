/**
 * Created by wangdk on 2017/2/21.
 */
define(['App', "common/ui/modal", 'echarts'], function(App, Modal, ec2) {
    return App.View({
        data: function() {
            var appId = this.getParam('id');
            var appName =this.getParam('name');
            return App.remote('/apigateway/auth/metric/'+appId);
        },
        dataFilter: function(err, result) {
            if (err) {
                this.onError(err, function(err) {
                    Modal.error('获取详情失败。原因：'+err.message);
                });
                result = [];
            }
            return {result: result};
        },
        ready: function() {
            var self = this;
            var $tabConstantlyMonitor = this.$("#app-visit-monitor")[0];
            var result = this.getData('result');
            var appVisitOption = {
                title: {
                    text: '流量统计',
                    x: 'center'
                },
                tooltip: {
                    trigger: 'item',
                    formatter: "{a} <br/>{b} : {c} ({d}%)"
                },
                legend: {
                    orient: 'vertical',
                    left: 'left',
                    data: ['成功次数', '失败次数', '超时次数']
                },
                series: [
                    {
                        name: '访问统计',
                        type: 'pie',
                        radius: '55%',
                        center: ['50%', '60%'],
                        data: self.trimVisitInfo(result),
                        itemStyle: {
                            emphasis: {
                                shadowBlur: 10,
                                shadowOffsetX: 0,
                                shadowColor: 'rgba(0, 0, 0, 0.5)'
                            }
                        }
                    }
                ]
            };
            chartAppVisist = ec2.init($tabConstantlyMonitor);
            chartAppVisist.setOption(appVisitOption);


            //real time metric
            var chart = ec2.init($("#app-visit-monitor-realtime")[0], null, {
                renderer: 'canvas'
            });
            // function randomData() {

            // var now = +new Date(1997, 9, 3);
            // var oneDay = 24 * 3600 * 1000;
            // var value = Math.random() * 1000;
            // for (var i = 0; i < 1000; i++) {
            //     data.push(randomData());
            // }
            var option = {
                title: {
                    text: '流量实时监控'
                },
                tooltip: {
                    trigger: 'axis',
                    formatter: function (params) {
                        params = params[0];
                        var date = new Date(params.name);
                        return date.getDate() + '/' + (date.getMonth() + 1) + '/' + date.getFullYear() + ' : ' + params.value[1];
                    },
                    axisPointer: {
                        animation: false
                    }
                },
                xAxis: {
                    type: 'time',
                    splitLine: {
                        show: false
                    }
                },
                yAxis: {
                    type: 'value',
                    boundaryGap: [0, '100%'],
                    splitLine: {
                        show: false
                    }
                },
                series: [{
                    name: '流量数据',
                    type: 'line',
                    showSymbol: false,
                    hoverAnimation: false,
                    data: self.trimVisitRealTimeInfo(result)
                }]
            };
            // setInterval(function () {
            //     var data=trimVisitRealTimeInfo(result)
            //     chart.setOption({
            //         series: [{
            //             data: data
            //         }]
            //     });
            // }, 6000);
            chart.setOption(option);
        },
        trimVisitInfo: function(result) {
            var totalVisitInfo=result["total"];
            var res=[]
            res.push( {value: totalVisitInfo["successNum"], name: '成功次数'});
            res.push( {value: totalVisitInfo["failedNum"], name: '失败次数'});
            res.push( {value: totalVisitInfo["timeoutNum"], name: '超时次数'});
            return res;
        },
        trimVisitRealTimeInfo:  function (result){
        // var appId=self.getParam('id');
        var data=[];
        var serverResult={};
        // self.ajax.get('/apigateway/auth/metric/'+appId, function(err, data) {
        //     if (err) {
        //         data = {};
        //     }
        //     serverResult = data;
        //     alert(serverResult)
        // });
        var  realTimeInfo=result["realTimeInfo"];
        $.each(realTimeInfo, function(i, item){
            var name=item["time"]
            var value=[];
            value.push(item["time"]);
            value.push(item["value"]);
            alert(name);
            alert(item["time"]);
            alert(item["value"]);
            data.push({"name":name,"value":value})
        });
        return data;
    }
    });
})