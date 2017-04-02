define(['App', 'common/ui/resetpwd', 'bs/collapse', 'jq/nicescroll'], function(App, ResetPwd) {
    return App.Layout({
        binds: [
            [
                'resize', 'window', function(e) {
                    this.resize(e.target);
                }
            ],
            [
                'viewchange', 'window', function(e, curState, prevState) {
                    this.activeSideBar(curState);
                }
            ],
            [
                'click', '[href="#/resetpwd"]', function() {
                    var that = this;
                    ResetPwd.show(function() {
                        that.removeCookie(that.rememberKey.remember);
                        that.removeCookie(that.rememberKey.password);
                        App.go();
                    });
                    return false;
                }
            ]
        ],
		data: App.remote('/api/v1/clusters/json'),
     	dataFilter: function(err, result) {
	        if (err) {
	            // this.onError(err, function(err) {
	            //     Modal.error('获取集群失败。原因：'+err.message);
	            // });
	            result = [];
	        }
        	return {result: result};
        },
        ready: function() {
			var result = this.getData('result');
           // alert(result)
			if(result == null||result==""){
				this.$('#container-li').hide();
				this.$('#image-li').hide();
				this.$('#volume-li').hide();
				this.$('#apigateway-li').hide();
			}
            var $aside = this.$('aside:first'),
                $sideBar = $('#side-bar', $aside);

            $sideBar.metisMenu();

            var $pageMain = this.$('#page-main'),
                $pageContent = $('.page-content:first', $pageMain);

            this.set('$aside', $aside);
            this.set('$sideBar', $sideBar);
            this.set('$pageMain', $pageMain);
            this.set('$pageContent', $pageContent);

            this.resize();
            this.activeSideBar(App.getState());
        },
        destroy: function() {
            var mainScrollbar = this.$pageMain.getNiceScroll(),
                sideScrollbar = this.$sideBar.getNiceScroll();
            if (mainScrollbar && mainScrollbar.length) {
                mainScrollbar.remove();
            }
            if (sideScrollbar && sideScrollbar.length) {
                sideScrollbar.remove();
            }
        },
        resize: function(window) {
            this.adjustMainHeight(window);
            this.adjustSideScrollbar(window);
            this.adjustMainScrollbar();
            this.adjustContentLeft();
            this.adjustContentHeight();
        },
        adjustMainHeight: function(win) {
            var $win = win ? $(win) : this.$win,
                winH = $win.height(),
                mainOffset = this.$pageMain.offset() || {},
                mainH = winH - (mainOffset.top || 0);
            mainH = mainH <=0 ? 1 : mainH;
            this.$pageMain.css('height', mainH + 'px');
        },
        adjustContentLeft: function() {
            var $headerNav2 = this.$('#header-nav > nav[index="2"] > ul');
            if (!this.$aside.length) {
                $headerNav2.css('margin-left', "0");
                this.$pageContent.css('margin-left', "0");
            } else {
                var asideW = this.$aside.outerWidth(true) || 0,
                    asideMinW = parseFloat(this.$aside.css('min-width')) || 1,
                    contentMgL = parseFloat(this.$pageContent.css('margin-left')) || 1;
                // set margin
                if (contentMgL < asideMinW) {
                    $headerNav2.css('margin-left', asideMinW+"px");
                    this.$pageContent.css('margin-left', asideMinW+"px");
                } else {
                    $headerNav2.css('margin-left', asideW+"px");
                    this.$pageContent.css('margin-left', asideW+"px");
                }
            }
        },
        adjustContentHeight: function() {
            var contentMinH = this.$pageMain.height(),
                $contentParent = this.$pageContent.parent(),
                contentBorderH = $contentParent.outerHeight() - $contentParent.innerHeight();
            contentMinH -= contentBorderH;
            contentMinH = contentMinH <=0 ? 1 : contentMinH;
            this.$pageContent.css('min-height',contentMinH);
        },
        adjustMainScrollbar: function() {
            var scrollbar = this.$pageMain.getNiceScroll();
            if (!scrollbar || !scrollbar.length) {
                // 初始化滚动条
                this.$pageMain.niceScroll({
                    smoothscroll: false,
                    horizrailenabled: false,
                    cursoropacitymin: 0.65,
                    cursoropacitymax: 0.75,
                    cursorcolor: "#999",
                    cursorwidth: "8px",
                    cursorborder: "0 none",
                    cursorborderradius: "3px 0 0 3px",
                    railpadding: {
                        top: 5,
                        bottom: 5
                    }
                });
            } else {
                scrollbar.resize();
            }
        },
        adjustSideScrollbar: function(win) {
            // resize sidebar
            var $win = win ? $(win) : this.$win,
                winH = $win.height(),
                sideBarOffset = this.$sideBar.offset() || {},
                sideBarH = winH - (sideBarOffset.top || 0);
            this.$sideBar.css('height', sideBarH + "px");
            // update scrollbar
            var scrollbar = this.$sideBar.getNiceScroll();
            if (!scrollbar || !scrollbar.length) {
                this.$sideBar.niceScroll({
                    smoothscroll: false,
                    horizrailenabled: false,
                    cursoropacitymin: 1,
                    cursoropacitymax: 1,
                    cursorcolor: "#37b3e0",
                    cursorwidth: "4px",
                    cursorborder: "0 none",
                    cursorborderradius: "0"
                });
            } else {
                scrollbar.resize();
            }
        },
        activeSideBar: function(state) {
            state = state || {};

            var path = state.path;

            var $curActive = this.$sideBar.find("li.active,a.active"),
                $curActiveWrapper = this.$sideBar.find("ul.in");

            var $a = this.$sideBar.find('a[href^="#'+path+'"]:first'), posLastSlash;

            while (!$a.length && path) {
                posLastSlash = path.lastIndexOf('/');
                path = path.substring(0, posLastSlash);
                if (path) {
                    $a = this.$sideBar.find('a[href^="#'+path+'"]:first');
                }
            }

            if (!$a.length) {
                if ($curActive.length || $curActiveWrapper.length)
                    return this;
                var $nav = this.$sideBar,
                    $li = $nav.children('li:first'),
                    level = 1;
                while ($nav.length > 0 && level < 100) {
                    $a = $li.children('a:first');
                    $nav = $li.children('.nav:first');
                    level++;
                }
            } else {
                $curActive.removeClass("active");
                $curActiveWrapper.removeClass("in");
            }

            var $openNav = $a.parents('.nav:first');

            if ($openNav.length && $openNav[0] !== this.$sideBar[0]) {
                $a.addClass('active');
                level = 1;
                while ($openNav.length > 0 && level < 100) {
                    $openNav = $openNav.addClass("in").parents('li:first').addClass('active').parents('.nav:first');
                    $openNav.is(this.$sideBar.selector) && ($openNav = $([]));
                }
            } else {
                $a.parents('li:first').addClass('active');
            }

            return this;
        },
        $win: $(window),
        $aside: $([]),
        $sideBar: $([]),
        $pageMain: $([]),
        $pageContent: $([])
    });
});