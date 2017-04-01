define(['App', "common/ui/modal", 'echarts2', 'echarts2/chart/line'], function(App, Modal, ec2) {
    return App.View({
        data: function() {
            var containerId = this.getParam('id');
            return App.remote('/api/v1/containers/'+containerId+'/getrecord');
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
            var result = this.getData('result');
            var maxMem = result[0] && result[0].MemTotal ? parseInt(result[0].MemTotal/1024/1024) || 0 : 0;
            if (maxMem%2 != 0) {
                maxMem++;
            }
            var $tabConstantlyMonitor = this.$("#tab-constantly-monitor");
            $tabConstantlyMonitor.tabShow(function(index, $tabPane) {
                var $chartMonitorCPU = $("#chart-monitor-cpu", $tabPane),
                    $chartMonitorRAM = $("#chart-monitor-ram", $tabPane),
                    chartMonitorCPU, chartMonitorRAM;
                var chartBaseOption = {
                    title: {
                        show: true,
                        x: "center",
                        y: "top",
                        textStyle: {
                            color: "#555",
                            fontSize: 14
                        }
                    },
                    grid: {
                        x: 38,
                        y: 28,
                        x2: 15,
                        y2: 20
                    },
                    xAxis: [
                        {
                            type: 'category',
                            boundaryGap : false,
                            axisLine: {
                                lineStyle: {
                                    color: "#b5b5b5"
                                }
                            },
                            splitLine: {
                                lineStyle: {
                                    color: "#ebebeb",
                                    type: "dashed"
                                }
                            },
                            data: self.trimRecordTime(result)
                            //data: ["14:05", "14:10", "14:15", "14:20", "14:25", "14:30", "14:35", "14:40", "14:45", "14:50", "14:55", "15:00"]
                        }
                    ],
                    yAxis: [
                        {
                            type: 'value',
                            boundaryGap : false,
                            axisLabel: {
                                color: "#333"
                            },
                            axisLine: {
                                lineStyle: {
                                    color: "#b5b5b5"
                                }
                            },
                            splitLine: {
                                lineStyle: {
                                    color: "#ebebeb",
                                    type: "dashed"
                                }
                            }
                        }
                    ],
                    series: [
                        {
                            type: 'line',
                            smooth: true,
                            itemStyle: {
                                normal: {
                                    color: "#48b",
                                    lineStyle: {
                                        shadowColor: 'rgba(0,0,0,0.4)'
                                    }
                                }
                            }
                        }
                    ]
                };
                // chart cpu
                if ($chartMonitorCPU.length) {
                    chartMonitorCPU = ec2.init($(".chart-pane", $chartMonitorCPU).get(0));
                    chartMonitorCPU.setOption($.extend(true, {}, chartBaseOption, {
                        title: {
                            text: "CPU占用率（%）"
                        },
                        yAxis: [
                            {
                                min: 0,
                                max: 100,
                                splitNumber: 2
                            }
                        ],
                        series: [
                            {
                                name: 'CPU使用率',
                                data: self.trimCPUsage(result)
                            }
                        ]
                    }));
                }

                // chart ram
                if ($chartMonitorRAM.length) {
                    chartMonitorRAM = ec2.init($(".chart-pane", $chartMonitorRAM).get(0))
                    chartMonitorRAM.setOption($.extend(true, {}, chartBaseOption, {
                        title: {
                            text: "内存使用率（MB）"
                        },
                        yAxis: [
                            {
                                min: 0,
                                max: maxMem,
                                splitNumber: 2
                            }
                        ],
                        series: [
                            {
                                name: '内存使用率',
                                data: self.trimMemUsage(result)
                            }
                        ]
                    }));
                }
            });
        },
        trimCPUsage: function(result) {
            var res = [,,,,,,,,,,,];
            if ($.isArray(result)) {
                $.each(result, function(i, item) {
                    if (item.CpuUsage) {
                        res[11-i] = parseFloat(item.CpuUsage);
                    }
                });
            }
            return res;
        },
        trimRecordTime: function(result) {
            var res = [,,,,,,,,,,,];
            if ($.isArray(result)) {
                $.each(result, function(i, item) {
                    if (item.RecordTime) {
                        res[11-i] = item.RecordTime;
                    }
                });
            }
            return res;
        },
        trimMemUsage: function(result) {
            var res = [,,,,,,,,,,,];
            if ($.isArray(result)) {
                $.each(result, function(i, item) {
                    if (item.MemUsage) {
                        res[11-i] = item.MemUsage/1024/1024;
                    }
                });
            }
            return res;
        }
    });
});