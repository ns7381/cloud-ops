define(['App', 'jquery', 'echarts2', 'echarts2/chart/line'], function(App, $, ec2) {
    return App.View({
        template: (
            '<div class="chart-box" id="chart-monitor-cpu">' +
                '<div class="chart-pane"></div> '+
            '</div>'
        ),
        ready: function() {
            var $chartContainer = this.$('#chart-monitor-cpu'),
                $chartPane = $('.chart-pane', $chartContainer);

            var self = this;

            if ($chartPane.length) {
                var charts = [], chart;

                $chartPane.each(function() {
                    chart = ec2.init(this);
                    chart.setOption(self.chartOptions);
                    charts.push(chart);
                });

                this.set('charts', charts);
            }
        },
        charts: null,
        chartOptions: {
            title: {
                show: true,
                x: "center",
                y: "top",
                text: "CPU占用率（%）",
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
                    data: ["14:05", "14:10", "14:15", "14:20", "14:25", "14:30", "14:35", "14:40", "14:45", "14:50", "14:55", "15:00"]
                }
            ],
            yAxis: [
                {
                    type: 'value',
                    boundaryGap : false,
                    min: 0,
                    max: 100,
                    splitNumber: 2,
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
                    },
                    name: 'CPU使用率',
                    data: [0, 0.5, 10, 20, 25, 2.5, 3, 35, 40, 4.5, 5, 55]
                }
            ]
        }
    });
});