define(['App', 'echarts2', 'echarts2/chart/line'], function(App, ec2) {
    return App.View({
        data: function() {
            var hostId = this.getParam('id');
            return App.remote('/api/v1/hosts/'+hostId+'/getrecord');
        },
        dataFilter: function(err, data) {
            return {
                name: this.getParam('name'),
                records: data || []
            };
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
                        res[11-i] = item.MemUsage;
                    }
                });
            }
            return res;
        },
        trimDiskUsage: function(result) {
            var res = [,,,,,,,,,,,];
            if ($.isArray(result)) {
                $.each(result, function(i, item) {
                    if (item.DiskUsage) {
                        res[11-i] = item.DiskUsage;
                    }
                });
            }
            return res;
        },
        ready: function() {
            var self = this;

			setTimeout(function() {
                self.$('.progress > .progress-bar')
                    .one($.support.transition.end, function(e) {
                        var value = $(this).attr('aria-valuenow') || 0;
                        value > 100 && $(this).removeClass("progress-bar-info").addClass("progress-bar-danger");
                    }).each(function() {
                    var value = $(this).attr('aria-valuenow') || 0;
                    if (value) {
                        if (value > 100) {
                            value = 100;
                        }
                        $(this).css('width', value+'%');
                    } else {
                        $(this).css('width', 0);
                    }
                });
            }, 1000/60);
			
            var records = this.getData('records');
            var maxMem = parseInt(records[0].MemTotal);
            if (maxMem%2 != 0) {
                maxMem++;
            }
            var maxDisk = parseInt(records[0].DiskTotal);
            var $tabConstantlyMonitor = $("#tab-constantly-monitor"),
                $tabContent = $('.tab-content', $tabConstantlyMonitor);
			$tabConstantlyMonitor.tabShow(function(index, $tabPane) {
				 var relativeId = $(this).attr('href'),
                    $tabPane = $(relativeId, $tabContent);
                var $chartMonitorCPU = $("#chart-monitor-cpu", $tabPane),
                    $chartMonitorRAM = $("#chart-monitor-ram", $tabPane),
                    $chartMonitorDISK = $("#chart-monitor-disk", $tabPane),
                    chartMonitorCPU, chartMonitorRAM, chartMonitorDISK;
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
                            data: self.trimRecordTime(records)
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
                                data: self.trimCPUsage(records)
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
                                data: self.trimMemUsage(records)
                                //data: [0, 500, 1000, 1024, 1228, 800, 300, 1800, 1524, 430, 1310, 550]
                            }
                        ]
                    }));
                }

                // chart disk
                if ($chartMonitorDISK.length) {
                    chartMonitorDISK = ec2.init($(".chart-pane", $chartMonitorDISK).get(0))
                    chartMonitorDISK.setOption($.extend(true, {}, chartBaseOption, {
                        title: {
                            text: "磁盘使用率（GB）"
                        },
                        yAxis: [
                            {
                                min: 0,
                                max: maxDisk,
                                splitNumber: 2
                            }
                        ],
                        series: [
                            {
                                name: '磁盘使用率',
                                data:self.trimDiskUsage(records)
                            }
                        ]
                    }));
                }
			});
//            $('a[data-toggle="pill"]', $tabConstantlyMonitor).on('shown.bs.tab', function() {
//            });
        },
       
    });
});