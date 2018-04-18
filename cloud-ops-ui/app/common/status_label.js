/**
 * 系统中的所有状态
 *
 * Created by jinzk on 2015/12/28.
 */
define(['jquery'], function($) {

    var handlerCore = function(status, label, cssClass, ext) {
        status = status || 'unknown';
        label = label || "未知";
        cssClass = cssClass || "label-status";
        if (typeof ext !== "undefined") {
            label = label + ' ( '+ext+' )';
        }
        return {status: status, label: label, cssClass: $.isArray(cssClass) ? cssClass.join(" ") : cssClass};
    };

    var statusHandler = {
        'default': function(status, ext) {
            return handlerCore(status, status, ["label-status"], ext);
        },
        'repository': function (status, ext) {
            var label, cssClass = ["label-status"];
            switch (status) {
                case 'COMPARE':
                    label = "正在比对";
                    break;
                case 'CLONING':
                    label = "正在克隆";
                    break;
                case 'BUILDING':
                    label = "正在构建";
                    break;
                case 'SAVING':
                    label = "正在保存";
                    break;
                case 'FINISH':
                    label = "打包完成";
                    break;
                case 'FAIL':
                    label = "打包失败";
                    break;
                case 'DEPLOYED':
                    label = "已部署";
                    break;
                default:
                    label = status;
                    break;
            }
            // css class
            switch (status) {
                case 'FINISH':
                case 'DEPLOYED':
                    cssClass.push("label-status-success");
                    break;
                case 'COMPARE':
                case 'CLONING':
                case 'BUILDING':
                case 'SAVING':
                    cssClass.push("label-status-info");
                    cssClass.push("anim-breath");
                    break;
                case 'FAIL':
                    cssClass.push("label-status-danger");
                    break;
                default:
                    break;
            }
            return handlerCore(status, label, cssClass, ext);
        },
        // 结果状态，如success、fail、failed、error、failure
        'result': function(status, ext) {
            var label, cssClass = ["label-status"];
            switch (status) {
                case 'SUCCESS':
                    label = "已成功";
                    cssClass.push("label-status-success");
                    break;
                case 'ERROR':
                case 'FAIL':
                case 'FAILED':
                case 'FAILURE':
                    label = "已失败";
                    cssClass.push("label-status-danger");
                    break;
                case 'WARNING':
                    label = status;
                    cssClass.push("label-status-warning");
                    break;
                default:
                    label = status;
                    break;
            }
            return handlerCore(status, label, cssClass, ext);
        },
        // 可用状态，如 enable 和 active、 disable 和 unactive
        'able': function(status, ext) {
            var label, cssClass = ["label-status"];
            // label
            switch (status) {
                case 'ENABLE':
                    label = "可用";
                    break;
                case 'DISABLE':
                    label = "不可用";
                    break;
                case 'ACTIVE':
                    label = "已启用";
                    break;
                case 'UNACTIVE':
                    label = "已停用";
                    break;
                default:
                    label = status;
                    break;
            }
            // css class
            switch (status) {
                case 'ENABLE':
                case 'ACTIVE':
                    cssClass.push("label-status-success");
                    break;
                case 'DISABLE':
                case 'UNACTIVE':
                    cssClass.push("label-status-danger");
                    break;
                case 'WARNING':
                    cssClass.push("label-status-warning");
                    break;
                default:
                    break;
            }
            return handlerCore(status, label, cssClass, ext);
        },
        // 用户
        "user": function(status, ext) {
            var label, cssClass = ["label-status"];
            switch (status) {
                case 'NORMAL':
                    label = "正常";
                    cssClass.push("label-status-success");
                    break;
                case 'LOCKED':
                    label = "已锁定";
                    cssClass.push("label-status-warning");
                    break;
                case 'DELETE':
                    label = "已删除";
                    cssClass.push("label-status-danger");
                    break;
                case 'RESET_PWD':
                    label = "密码重置";
                    cssClass.push("label-status-warning");
                    break;
                case 'DISABLED':
                    label = "已禁用";
                    break;
                default:
                    label = status;
                    break;
            }
            return handlerCore(status, label, cssClass, ext);
        },
    };

    var parseStatus = function(status) {
        var statusGroup = (status && typeof status === "string") ? status : "unknown";
        statusGroup = statusGroup.split(':');
        if (statusGroup.length > 1) {
            status = statusGroup[1].toUpperCase();
        } else {
            status = statusGroup[0].toUpperCase();
        }
        status = status.replace(/\-/g, "_");
        var handlerKey, ext;
        if (statusGroup.length == 2) {
            handlerKey = statusGroup[0].toLowerCase();
            if (typeof statusHandler[handlerKey] !== "function") {
                ext = statusGroup[1];
                handlerKey = "default";
            }
        } else if (statusGroup.length > 2) {
            handlerKey = statusGroup[0].toLowerCase();
            ext = statusGroup[2];
        }
        if (typeof statusHandler[handlerKey] !== "function") {
            handlerKey = "default";
        }
        return statusHandler[handlerKey].call(statusHandler[handlerKey], status, ext);
    };

    var statusLabel = function(status, label) {
        var statusObj = parseStatus(status);
        return '<label class="'+statusObj.cssClass+'" data-status="'+statusObj.status+'">'+(typeof label !== "undefined" && label != null ? label : statusObj.label)+'</label>';
    };

    var statusLabelTitle = function(status, label) {
        var statusObj = parseStatus(status);
        return '<label class="'+statusObj.cssClass+' label-status-icon" title="'+(typeof label !== "undefined" && label != null ? label : statusObj.label)+'" data-toggle="tooltip" data-status="'+statusObj.status+'"></label>';
    };

    return {
        compile: statusLabel,
        compile2Title: statusLabelTitle
    };

});