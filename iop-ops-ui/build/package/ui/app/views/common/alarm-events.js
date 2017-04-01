define(['App', 'jquery'], function(App, $) {
    return App.View({
        data: {
            alarmEvents: [
                {
                    "id":"1",
                    "name":"服务状态告警",
                    "description":"资源实例wy-wls指标[Weblogic.status]状态异常触发告警",
                    "severity":"CRITICAL",
                    "state":"ALARM",
                    "eventType":"SERVICE_STATUS_ALARM",
                    "updatedAt":"2016-07-14 19:09:15",
                    "timestamp":"2016-07-13 09:48:57"
                },
                {
                    "id":"2",
                    "name":"[wy-wls]weblogic",
                    "description":"资源实例weblogic指标[Weblogic_weblogic.status]状态异常触发告警",
                    "severity":"MODERATE",
                    "state":"ALARM",
                    "eventType":"COMPONENT_STATUS_ALARM",
                    "updatedAt":"2016-07-14 19:09:15",
                    "timestamp":"2016-07-13 09:47:54"
                },
                {
                    "id":"3",
                    "name":"服务状态告警",
                    "description":"资源实例wy-wls指标[Weblogic.status]状态异常触发告警",
                    "severity":"LOW",
                    "state":"ALARM",
                    "eventType":"SERVICE_STATUS_ALARM",
                    "updatedAt":"2016-07-14 19:09:15",
                    "timestamp":"2016-07-13 09:46:25"
                }
            ]
        },
        dataFilter: function(err, data) {
            return $.extend({}, data, {
                severity: {
                    CRITICAL: "critical",
                    MODERATE: "moderate",
                    LOW: "low"
                }
            });
        }
    });
});