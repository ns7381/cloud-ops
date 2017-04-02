define(['App', 'jquery', 'echarts2', 'echarts2/chart/gauge'], function(App, $, ec2) {
    return App.View({
        binds: [
            [
                'resize', 'window', function() {
                    this.resize();
                }
            ]
        ],
        ready: function() {
            var $chart = this.$('#chart-alarm-monitor'),
                $chartPane = $(".chart-pane", $chart);

            this.set('$chart', $chart);
            this.set('$chartPane', $chartPane);

            if ($chartPane.length) {
                var charts = [];
                $chartPane.each(function() {
                    charts.push(ec2.init(this));
                });
                this.set('charts', charts);
            }

            this.set('level', $.extend({}, this.level, this.getData('level')));
            this.set('orient', this.getParam('orient') || "horizontal");

            this.update();
        },
        update: function() {
            var self = this;

            $('.chart-legend', this.$chart).html(
                this.legendHtml(this.alarmLevels())
            );

            if (this.charts.length) {
                $.each(this.charts, function(i, chart) {
                    chart.setOption(self.chartOptions());
                });
            }
        },
        resize: function() {
            if (this.charts.length) {
                $.each(this.charts, function(i, chart) {
                    chart.resize();
                });
            }
        },
        chartOptions: function() {
            var self = this,
                levels = this.alarmLevels(),
                levelValue = this.alarmLevelVal(levels),
                splitAreaNum = 3, splitNum = levels.length * 3;
            var options = {
                tooltip: {
                    formatter: function() {
                        var tip = "告警信息：<br/>", levelsTmp = [].concat(levels), levelsTip = [];
                        levelsTmp.sort(function(a, b) {
                            if (b.value) return 1;
                            return 0;
                        });
                        $.each(levelsTmp, function(i, level) {
                            levelsTip.push(level.name + "：" + level.value + " ( " + level.percent + "% ) ");
                        });
                        return tip + levelsTip.join('<br/>');
                    }
                },
                toolbox: {
                    show: false
                },
                series: [
                    {
                        name: '告警级别',
                        type: 'gauge',
                        center: ['50%', '50%'],
                        radius: [0, '90%'],
                        startAngle: 140,
                        endAngle: -140,
                        min: 0,
                        max: 100,
                        precision: 2,
                        splitNumber: splitNum,
                        axisLine: {
                            show: true,
                            lineStyle: {
                                color: function() {
                                    var colors = [];
                                    colors.push([0.005,"#008000"]);
                                    $.each(levels, function(i, level) {
                                        colors.push([level.limit/100, level.color]);
                                    });
                                    return colors;
                                }(),
                                width: 20
                            }
                        },
                        axisTick: {
                            show: true,
                            splitNumber: 5,
                            length :8,
                            lineStyle: {
                                color: '#eee',
                                width: 1,
                                type: 'solid'
                            }
                        },
                        axisLabel: {
                            show: true,
                            formatter: function(v) {
                                v = self.numFixed(v);
                                var splitVal = 1 / splitNum * 100,
                                    pos = parseInt(splitAreaNum/2);
                                var label = "";
                                $.each(levels, function(i, level) {
                                    if (v == self.numFixed((i * splitAreaNum + pos) * splitVal)) {
                                        label = level.name;
                                        return false;
                                    }
                                });
                                return label;
                            },
                            textStyle: {
                                color: '#333'
                            }
                        },
                        splitLine: {
                            show: true,
                            length: 20,
                            lineStyle: {
                                color: '#eee',
                                width: 2,
                                type: 'solid'
                            }
                        },
                        pointer: {
                            length : '80%',
                            width : 8,
                            color : 'auto'
                        },
                        title: {
                            show : true,
                            offsetCenter: ['-65%', -10],
                            textStyle: {
                                color: '#333',
                                fontSize : 16
                            }
                        },
                        detail: {
                            show : true,
                            backgroundColor: 'rgba(0,0,0,0)',
                            borderWidth: 0,
                            borderColor: '#ccc',
                            width: 100,
                            height: 40,
                            offsetCenter: ['-60%', 0],
                            formatter: function(val) {
                                var text = "";
                                val = val.toFixed(2);
                                $.each(levels, function(i, level) {
                                    if (val == 0) {
                                        text = "无";
                                        return false;
                                    } else if (val <= level.limit) {
                                        text = level.name;
                                        return false;
                                    }
                                });
                                return text;
                            },
                            textStyle: {
                                color: 'auto',
                                fontSize : 14
                            }
                        },
                        data: [{value: levelValue, name: "告警级别"}]
                    }
                ]
            };
            return options;
        },
        alarmLevels: function() {
            var self = this,
                levels = [], m = 0;
            var levelArray = {
                LOW: {
                    name: "一般",
                    color: "#f4da07",
                    order: 1
                },
                MODERATE: {
                    name: "重要",
                    color: "#fb9502",
                    order: 2
                },
                CRITICAL: {
                    name: "紧急",
                    color: "#bf1204",
                    order: 3
                }
            };
            for (var k in this.level) {
                levels[m] = {
                    key: k,
                    name: levelArray[k].name,
                    color: levelArray[k].color,
                    order: levelArray[k].order
                };
                m++;
            }
            levels.sort(function(a,b) {
                return a.order - b.order;
            });
            var i;
            for (i=0; i<levels.length; i++) {
                levels[i].limit = this.numFixed((i+1)/levels.length*100*(995/1000)) + 0.5;
            }
            //info = $.extend(true, {alarmSeverity: {LOW: 0, MODERATE: 0, CRITICAL: 0}}, info);
            var totalCount = 0;
            $.each(levels, function(i, level) {
                totalCount += (level.value = self.level[level.key]);
            });
            $.each(levels, function(i, level) {
                level.percent = self.numFixed((level.value / totalCount) * 100);
            });
            return levels;
        },
        alarmLevelVal: function(levels) {
            var levelValue = 0, i;
            for (i=levels.length-1; i>=0; i--) {
                if (levels[i].value) {
                    levelValue = levels[i-1] ? (levels[i-1].limit + (1 / levels.length * levels[i].percent)) : 1 / levels.length * levels[i].percent;
                    break;
                }
            }
            levelValue = this.numFixed(levelValue);
            return levelValue;
        },
        legendHtml: function(levels) {
            var html = "", levelsTmp = [].concat(levels).reverse();
            if (this.orient == 'horizontal') {
                $.each(levelsTmp, function(i, level) {
                    html += (
                        '<dl class="col-sm-6">' +
                            '<dt>' +
                                '<i class="fa fa-circle" style="color: '+level.color+'"></i> ' +
                                level.name + ' '  + level.value +
                            '</dt>' +
                            '<dd>（' + level.percent + '%）' +'</dd>' +
                        '</dl>'
                    );
                });
            } else {
                $.each(levelsTmp, function(i, level) {
                    html += (
                        '<dl class="col-sm-12" style="margin-left:38px">' +
                            '<dt>' +
                                '<i class="fa fa-circle" style="color: '+level.color+'"></i> ' +
                                level.name + ' '  + level.value +
                            '</dt>' +
                            '<dd>（' + level.percent + '%）' +'</dd>' +
                        '</dl>'
                    );
                });
            }
            return html;
        },
        numFixed: function(num, precision) {
            if (typeof num !== "number" || isNaN(num)) return 0;
            precision = typeof precision !== "number" ? 2 : precision;
            num = Number(num.toFixed(precision));
            return num;
        },
        level: {
            'LOW': 0,
            'MODERATE': 0,
            'CRITICAL': 0
        },
        orient: '',
        charts: [],
        $chart: $([]),
        $chartPane: $([])
    });
});