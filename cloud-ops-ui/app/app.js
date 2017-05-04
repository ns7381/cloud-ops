define(['yfjs/spa', 'common/status_label', 'jquery', 'common/ui/modal', 'common/ui/login','crypto-js', 'bs/tab', 'bs/popover'], function(App, StatusLabel, $, Modal, Login, CryptoJS) {
    App.create({
        index: "/environment",
        baseUrl: {
            resource: "/assets",
            style: "/assets/styles"
        },
        cssPrefix: {
            view: "page_"
        },
        binds: [
            [
                'viewchange', 'window', function() {
                    var app = this;
                    // remove tooltip
                    $('.tooltip').remove();
                    // close modal & ignore login modal
                    if (Modal.dialogs != null) {
                        var dialog;
                        for (var id in Modal.dialogs) {
                            dialog = Modal.dialogs[id];
                            if (dialog !== app.loginDlg) {
                                dialog.dispose();
                            }
                        }
                    }
                }
            ]
        ],
        Widget: {
            beforeReady: function() {
                this.rootContext.readyContent(this);
            }
        },
        View: {
            layout: "default"
        },
        cookie: {
            getLogin: function() {
                return this.get('user');
            },
            getLoginId: function() {
                var user = this.getLogin();
                return user ? user.id : user;
            },
            getLoginName: function() {
                var user = this.getLogin();
                return user ? user.name : user;
            }
        },
        filter: {
            name: 'login',
            access: function() {
                var filter = this.getFilter('login');
                return filter.exclude(this.getPath()) || this.hasLogin();
                // return true;
            },
            do: function() {
                var curUrl = this.getState('url'), params;
                if (/^\/login/.test(curUrl)) {
                    params = this.getParams();
                } else if (curUrl) {
                    params = {callback: curUrl};
                }
                this.go(this.getUrl('/login', params));
            },
            includes: ['*'],
            excludes: ['/login']
        },
        template: {
            helpers: {
                'getLoginName': function() {
                    return this.rootContext.cookie.getLoginName();
                },
                'statusLabel': function(status, ext) {
                    return this.rootContext.statusLabel.apply(this.rootContext, arguments);
                },
                'statusLabelTitle': function(status, ext) {
                    return this.rootContext.statusLabelTitle.apply(this.rootContext, arguments);
                },
                'shortContent': function(content, len, ext) {
                    return this.rootContext.shortContent.apply(this.rootContext, arguments);
                },
                'formatBytes': function(bytes) {
                    return this.rootContext.formatBytes.apply(this.rootContext, arguments);
                },
                'formatBytesStrict': function(bytes) {
                    return this.rootContext.formatBytesStrict.apply(this.rootContext, arguments);
                },
                'pwdHtml': function(text, withoutControl) {
                    return this.rootContext.pwdHtml(text, withoutControl);
                },
                'pwdText': function(text) {
                    return this.rootContext.pwdText(text);
                },
                'uiSelect': function(data) {
                    var inHtml = this.rootContext.uiSelect(data);
                    return inHtml;
                },
                'uiSelectList': function(data) {
                    if(!$.isPlainObject(data)) {
                        data = $.extend({}, {list: data}, {wrapper: false});
                    } else {
                        data = $.extend({}, data, {wrapper: false});
                    }
                    var inHtml = this.rootContext.uiSelect(data);
                    return inHtml;
                }
            }
        },
        ready: function() {
            var self = this;
            // modal position
            Modal.configDefaultOptions(
                ['processing', 'info', 'warning', 'error', 'success'],
                {
                    position: "right 50"
                }
            );
            // tab show
            $.fn.tabShow = function(index, callback) {
                if (typeof index === "function") {
                    callback = index;
                    index = null;
                }
                index = $.trim(index);
                var tabIndex = null;
                if (index.length) {
                    tabIndex = parseInt(index);
                    if (isNaN(tabIndex)) {
                        if (!/^#/.test(index)) {
                            tabIndex = '#' + index;
                        } else {
                            tabIndex = index;
                        }
                    }
                }
                this.each(function() {
                    var $tab = $(this),
                        $container = $tab.parents('[id^="app-view"][data-view]'),
                        $tabNav = $tab.find('.nav-tabs,.nav-pills'),
                        $tabContent = $('.tab-content', $tab);

                    if (tabIndex != null) {
                        $tabNav.data('__bs_tab_cache__', tabIndex);
                    }

                    if (typeof callback === "function") {
                        $tab.data('callback', callback);
                    } else {
                        callback = $tab.data('callback');
                    }

                    var tabSelector = 'a[data-toggle="tab"],a[data-toggle="pill"]',
                        $tabs = $(tabSelector, $tabNav);

                    if ($container.length) {
                        $tabs.off('shown.ui.tab').on('shown.ui.tab', tabShown);
                    } else {
                        $tabs.off('shown.bs.tab').on('shown.bs.tab', tabShown);
                    }

                    var hrefCur, $curTab;

                    if (tabIndex != null) {
                        hrefCur = tabIndex;
                    }

                    if (typeof hrefCur === "number") {
                        $curTab = $tabs.eq(hrefCur);
                    } else if (hrefCur != null) {
                        $curTab = $tabs.filter('[href$="' + hrefCur + '"]');
                    }

                    if (!$curTab || !$curTab.length) {
                        $curTab = $tabNav.children('.active').children(tabSelector);
                        if ($curTab.length) {
                            $curTab.parent().removeClass("active");
                        }
                    }

                    if (!$curTab || !$curTab.length) {
                        $curTab = $tabs.eq(0);
                    }

                    $curTab.tab('show');

                    function tabShown(e) {
                        var relativeId = $(this).attr('href'),
                            $tabPane = $(relativeId, $tabContent);
                        if ($tabPane.length && typeof callback === "function") {
                            try {
                                var args = [relativeId.replace(/^#+/, ''), $tabPane, $tab];
                                if (typeof e.targetKey !== "undefined") {
                                    args.push(e.targetKey);
                                }
                                callback.apply(this, args);
                            } catch(e) {
                                self.trigger('CallbackError',
                                    self.makeError('callback', ['calledTabCallback', e.message], e)
                                );
                            }
                        }
                    }
                });
                return this;
            };
            // bind pwd mask
            this.bind('click', '.pwd-masked-static + .btn-pwd-masked', this.passToggle);
            return this;
        },
        readyContent: function(widget) {
            var self = this;

            if (!self.prevState() || window.history.length <=1) {
                widget.$('.btn-back').each(function() {
                    var $this = $(this);
                    if (!$this.attr('href')) {
                        $this.attr('disabled', true);
                    }
                });
                widget.$('.breadcrumb').find('a').each(function() {
                    var $this = $(this);
                    if (!$this.attr('href')) {
                        $this.attr('disabled', true);
                    }
                });
            }

            // bind tab shown, fix to bootstrap tab
            var tabSelector = 'a[data-toggle="tab"],a[data-toggle="pill"]',
                tabNavSelector = '.nav-tabs,.nav-pills';

            var $tabNavs = widget.$(tabNavSelector),
                tabCacheKey = '__bs_tab_cache__',
                tabFixedKey = '__bs_tab_fixed__';

            // ready tab
            if ($tabNavs.length) {
                $tabNavs.each(function () {
                    var $nav = $(this);

                    if ($nav.data(tabFixedKey)) {
                        return false;
                    }

                    var navIndex = $nav.index($tabNavs),
                        $tabs = $(tabSelector, $nav);

                    var hrefCache = $nav.data(tabCacheKey);

                    if (hrefCache == null) {
                        var tabCache = widget.getCache('tab') || {};
                        hrefCache = tabCache[navIndex];
                    }

                    var $curTab;

                    if (hrefCache != null) {
                        if (typeof hrefCache === "number") {
                            $curTab = $tabs.eq(hrefCache);
                        } else if (hrefCache != null) {
                            $curTab = $tabs.filter('[href$="' + hrefCache + '"]');
                        }
                        if ($curTab && $curTab.length) {
                            $nav.children(".active").removeClass("active");
                            $nav.siblings(".tab-content").children(".active").removeClass("active");
                        }
                    }

                    if (!$curTab || !$curTab.length) {
                        $curTab = $nav.children('.active').children(tabSelector);
                    }

                    if (!$curTab || !$curTab.length) {
                        $curTab = $tabs.eq(0);
                    }

                    if ($curTab && $curTab.length) {
                        $curTab.parent().addClass("active");
                        $nav.siblings(".tab-content").children($curTab.attr('href')).addClass("active");
                    }

                    $nav.data(tabFixedKey, true);
                });
            }

            widget.bind("shown.bs.tab", $(tabSelector, $tabNavs), function(e) {
                var widget = this;
                var $tab = $(e.currentTarget),
                    href = $tab.attr('href'),
                    tabCache = widget.getCache('tab') || {};
                var $tabNav, navIndex;
                if ($tab.is('a[data-toggle="pill"]')) {
                    $tabNav = $tab.parents('.nav-pills:first');
                } else {
                    $tabNav = $tab.parents('.nav-tabs:first');
                }
                navIndex = $tabNav.index($tabNavs);
                if (navIndex >= 0) {
                    tabCache[navIndex] = href;
                    widget.setCache('tab', tabCache);
                }
                $tab.trigger($.Event('shown.ui.tab', {
                    context: widget,
                    targetKey: navIndex,
                    relatedTarget: e.relatedTarget,
                    target: e.target
                }));
                return false;
            });

            widget.bind("click", $(tabSelector, $tabNavs), function(e) {
                self.prevState('isBack', false);
            });

            // 返回按钮
            widget.bind('click', '.breadcrumb+.btn-back,.breadcrumb+.breadcrumb-btn>.btn-back', function(e) {
                e.preventDefault();
                self.setState('isBack', true);
                self.goback(e, this);
                return false;
            });

            // 面包屑导航链接
            widget.bind('click', '.breadcrumb>li>a', function(e) {
                e.preventDefault();
                self.setState('isBack', true);
                self.gobreadcrumb(e, this);
                return false;
            });

            widget.$('input').each(function() {
                var $this = $(this);
                var placeholder = $this.attr('placeholder');
                if (/^\/login\/?/.test(self.getState('url'))) {
                    return true;
                } else {
                    $this.on('focus', function() {
                        $this.attr('placeholder', '');
                    }).on('blur', function() {
                        $this.attr('placeholder', placeholder);
                    });
                }
            });

            // 分类过滤事件
            widget.bind("click", ".category-filter-item", function(e) {
                var $this = $(e.currentTarget);
                if ($this.hasClass("active")) return false;
                $this.siblings().removeClass("active").end().addClass("active");
                $this.parents(".category-filter-box").trigger($.Event("changed", { relatedTarget: $this[0] }));
            });
        },
        ajax: {
            respFilter: function(err, resp, xhr) {
                var res = Array.prototype.slice.call(arguments);
                if (resp && resp.errors) {
                    var err = {};
                    err.message = resp.errors;
                    res[0] = err;
                }
                return res;
            }
        },
        errorFilter: function() {
            var errLogin = this.getError(this.rootContext.errLogin);
            if (errLogin.length) {
                this.assignError(errLogin, {level: 'app'});
                this.addError(errLogin);
            }
        },
        onError: function() {
            var loginErrs = this.getError(this.errLogin);
            if (loginErrs.length) {
                this.clearLogin();
                this.removeError(loginErrs);
                Login.modal.call(this, this);
                return this;
            }
            var otherErrs = this.getError();
            if (otherErrs.length) {
                $.each(otherErrs, function(i, err) {
                    Modal.error(err.message);
                });
            }
        },
        errLogin: {type: 'ajax', status: 403},
        hasLogin: function() {
            return !!this.cookie.getLoginName();
        },
        setLogin: function(user) {
            this.cookie.set('user', user);
            return this;
        },
        clearLogin: function() {
            this.cookie.remove('user');
            return this;
        },
        loginDlg: null,
        goback: function(e, widget) {
            this.go(-1);
            return this;
        },
        gobreadcrumb: function(e, widget) {
            widget = widget || this;
            var $this = $(e.currentTarget),
                href = $this.attr('href') || '';
            var posHash = href.lastIndexOf("#"), path;
            if (~posHash) {
                path = href.substring(posHash + 1);
            } else {
                var $breadcrumb = $this.parents(".breadcrumb:first"),
                    $list = $breadcrumb.children("li:not(.active):has(a)"),
                    len = $list.length,
                    index = $list.index($this.parents("li:first"));
                path = index - len;
            }
            if (typeof path == "number") {
                this.go(path);
            } else {
                this.go(widget.getUrl(path));
            }
            return this;
        },
        passToggle: function(e) {
            var $this = $(e.currentTarget),
                $static = $this.siblings(".pwd-masked-static:first");
            var isOpen = $this.hasClass("open"),
                orgPwd = this.Base64.decode($static.data('original-pwd'));
            if (isOpen) {
                // 加密显示密码
                orgPwd = orgPwd || $.trim($static.text());
                $static.html(this.pwdHtml(orgPwd, true));
            } else if (orgPwd) {
                // 明文显示密码
                $static.text(orgPwd);
            }
            $this.toggleClass("open");
            return false;
        },
        /**
         * 加亮显示文本内容
         * @param text {String}
         *          加亮文本
         * @param start {Number}
         *          起始位置。如果是负数，则该参数规定的是从字符串的尾部开始算起的位置。也就是说，-1 指字符串的最后一个字符，-2 指倒数第二个字符，以此类推。
         * @param end {Number}
         *          结束位置。若未指定此参数，则要提取的子串包括 start 到原字符串结尾的字符串。如果该参数是负数，那么它规定的是从字符串的尾部开始算起的位置。
         * @returns {String}
         *          加亮后的包含html标签的字符串
         */
        highlight: function(text, start, end) {
            if (!text || typeof text !== "string") return "";
            var stext = '<span class="text-highlight">[', etext = ']</span>';
            start = parseInt(start);
            isNaN(start) && (start = 0);
            end = parseInt(end);
            (isNaN(end) || end > text.length) && (end = text.length);
            return (text.slice(0, start) + stext + text.slice(start, end) + etext + text.slice(end, text.length));
        },
        shortContent: function(content, len, ext) {
            content = content || "";
            len = len || 50;
            ext = ext || "…";
            var l = -1;
            for (var i=0; i<content.length; i++) {
                var ch = content.charAt(i);
                if (/^[\x00-\xff]$/.test(ch)) {
                    l += 1;
                } else {
                    l += 0.5;
                }
                if (l >= len) {
                    break;
                }
            }
            if (l < 0) l = 0;
            return content.substr(0, l) + ext;
        },
        statusLabel: function(status, label) {
            return StatusLabel.compile(status, label);
        },
        statusLabelTitle: function(status, label) {
            return StatusLabel.compile2Title(status, label);
        },
        parseStatus: function(status) {
            return StatusLabel.parseStatus(status);
        },
        Base64: {
            encode: function(a) {
                var wordArray = CryptoJS.enc.Utf8.parse(a);
                return CryptoJS.enc.Base64.stringify(wordArray);
            },
            decode: function(a) {
                var wordArray = CryptoJS.enc.Base64.parse(a);
                return CryptoJS.enc.Utf8.stringify(wordArray);
            }
        },
        /**
         * 加密文本显示，用于html元素中
         * @param text
         * @returns {*}
         */
        pwdHtml: function(text, withoutControl) {
            if (!text) return "";
            text = $.trim(text + "");
            withoutControl = withoutControl || false;
            var masked = text.substr(0, 10).replace(/./g, "&bull;");
            return (
                withoutControl ? masked :
                '<span class="pwd-masked-static" data-original-pwd="'+this.Base64.encode(text)+'">' + masked + '</span>' +
                '<a class="btn-icon btn-pwd-masked"></a>'
            );
        },
        /**
         * 加密文本显示，用于text文本中
         * @param text
         * @returns {*}
         */
        pwdText: function(text) {
            if (!text) return "";
            text = text + "";
            return text.substr(0, 10).replace(/./g, "*");
        },
        /**
         * 容量单位
         */
        BytesUnit: ['B', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'],
        /**
         * 基于 1000 格式化 “数据传输速度” 和 “硬盘存储空间” 等概念性的容量单位
         * @param bytes {Number|String}
         *          要转换的容量数据，可为数字或字符串，字符串时可附缀容量单位
         * @param toUnit {String}
         *          要转换成的单位，有效值为 BytesUnit 中的数组项，大小写不限
         * @returns {String}
         *          格式化后的字符串
         */
        formatBytes: function(bytes, toUnit) {
            return this.formatBytesByRadixUnit(bytes, 1000, toUnit);
        },
        /**
         * 基于 1024 格式化 “内存” 或者 “CPU高速缓存容量” 等实际存储类的容量单位
         * @param bytes {Number|String}
         *          要转换的容量数据，可为数字或字符串，字符串时可附缀容量单位
         * @param toUnit {String}
         *          要转换成的单位，有效值为 BytesUnit 中的数组项，大小写不限
         * @returns {String}
         *          格式化后的字符串
         */
        formatBytesStrict: function(bytes, toUnit) {
            return this.formatBytesByRadixUnit(bytes, 1024, toUnit);
        },
        /**
         * 按照指定的基数 radix 和 单位 toUnit 格式化容量单位
         * @param bytes {Number|String}
         *          要转换的容量数据，可为数字或字符串，字符串时可附缀容量单位
         * @param radix {Number}
         *          转换基数，常见为 1000 或 1024
         * @param toUnit {String}
         *          要转换成的单位，有效值为 BytesUnit 中的数组项，非法单位参数将自由转换结果单位。大小写不限
         * @returns {String}
         *          格式化后的字符串
         */
        formatBytesByRadixUnit: function(bytes, radix, toUnit) {
            var _NAN_ = "N/A";
            if (typeof bytes === "undefined") {
                return _NAN_;
            }
            if (typeof radix === "string" && !toUnit) {
                toUnit = radix;
                radix = 1000;
            } else {
                radix = parseInt(radix) || 1000;
            }
            if (!~$.inArray(toUnit, this.BytesUnit)) {
                toUnit = "";
            }
            var suffixIndex = 0, suffix = toUnit;
            if (typeof bytes !== "number") {
                var bytesTmp = parseFloat(bytes);
                if (isNaN(bytesTmp)) {
                    return _NAN_;
                }
                var regexp = new RegExp("^-?(\\d*(?:\\.\\d+)?)\\s*("+this.BytesUnit.join("|")+")$", "i"),
                    matches;
                if (matches = bytes.match(regexp)) {
                    if (suffix = matches[2]) {
                        suffix = suffix.toUpperCase();
                        suffixIndex = $.inArray(suffix, this.BytesUnit);
                    }
                    if (suffixIndex < 0) {
                        suffixIndex = 0;
                    }
                    bytesTmp *= Math.pow(radix, suffixIndex);
                }
                bytes = bytesTmp;
            }
            if (!bytes) {
                return '0' + (suffix ? " " + suffix : "");
            }
            var round = function(num, precision) {
                if (typeof num !== "number" || typeof precision !== "number") {
                    return Number.NaN;
                }
                try {
                    if (Math.abs(num) >= Math.pow(10, 8) || Math.abs(num) < Math.pow(10, -precision)) {
                        return num.toExponential(precision);
                    }
                    return Math.round(num * Math.pow(10, precision)) / Math.pow(10, precision);
                } catch (e) {
                    return Number.NaN;
                }
            };
            var toSuffixIndex = toUnit ? $.inArray(toUnit, this.BytesUnit) : -1,
                index = toSuffixIndex < 0 ? this.BytesUnit.length - 1 : toSuffixIndex,
                boundary = Math.pow(radix, index);
            if (toSuffixIndex < 0) {
                // 自动计算单位
                while (bytes < boundary && index > 0) {
                    boundary /= radix;
                    index --;
                }
            }
            var rounded = round(bytes / boundary, 2);
            if (isNaN(rounded)) {
                return _NAN_;
            }
            return rounded + " " + this.BytesUnit[index];
        },
        uuid: function() {
            var chars = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'.split(''),
                uuid = new Array(36), rnd=0, r;
            for (var i = 0; i < 36; i++) {
                if (i==8 || i==13 ||  i==18 || i==23) {
                    uuid[i] = '-';
                } else if (i==14) {
                    uuid[i] = '4';
                } else {
                    if (rnd <= 0x02) rnd = 0x2000000 + (Math.random()*0x1000000)|0;
                    r = rnd & 0xf;
                    rnd = rnd >> 4;
                    uuid[i] = chars[(i == 19) ? (r & 0x3) | 0x8 : r];
                }
            }
            return uuid.join('');
        },
        uiSelect: function(options, renderTo) {
            var defaults = {wrapper: true, className: "form-control"};
            if(!$.isPlainObject(options)) {
                options = $.extend({}, {list: options}, defaults);
            } else {
                options = $.extend({}, defaults, options);
            }
            var inHtml = this.template.render('ui-select', options);
            try {
                if(renderTo) {
                    if(!(renderTo instanceof $)) {
                        renderTo = $(renderTo);
                    }
                    renderTo.append(inHtml);
                }
            } catch (e) { }
            return inHtml;
        },
        parseBytesText: function(text) {
            var res;
            if (typeof text === "string" && text) {
                res = [];
                for (var i = 0, len = text.length; i < len; ++i) {
                    var c = text.charCodeAt(i);
                    //在每个字符的两个字节之中，只保留后一个字节，将前一个字节扔掉。原因是浏览器解读字符的时候，会把字符自动解读成Unicode的0xF700-0xF7ff区段
                    var byte = c & 0xff;
                    res.push(byte);
                }
                if (res.length) {
                    res = String.fromCharCode.apply(String, res);
                } else {
                    res = text;
                }
            } else {
                res = text;
            }
            return res;
        }
    });
});