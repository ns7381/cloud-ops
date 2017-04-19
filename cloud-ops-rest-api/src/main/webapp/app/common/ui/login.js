define(['jquery', 'common/ui/modal', 'crypto-js', 'common/ui/pwdmasked', 'jq/form'], function($, Modal, CryptoJS) {

    var LoginHelper = {
        rememberKey: {
            username: "remember_username_ops",
            password: "remember_password_ops",
            remember: "remember_ops"
        },
        $form: $([]),
        $username: $([]),
        $password: $([]),
        $bRemember: $([]),
        $btnSubmit: $([]),
        btnSubmitText: "",
        modalHtml: function(App) {
            return (
                '<div class="signin-header">' +
                    '<div class="signin-title">' +
                        '<img class="signin-logo" alt="IOP Manager" src="' + App.getStyleUrl('img/logo.png') +'"/>' +
                    '</div>' +
                '</div>' +
                '<form class="form-horizontal form-signin" id="login-form" method="post" role="form" action="v1/login" autocomplete="off">' +
                    '<input class="hide">' +
                    '<div class="input-group">' +
                        '<span class="signin-icons signin-icon-input signin-icon-user">' +
                            '<i class="signin-icons signin-icon-br"></i>' +
                        '</span>' +
                        '<input class="form-control" id="username" name="username" type="text" placeholder="用户名"/>' +
                    '</div>' +
                    '<input class="hide" type="password">' +
                    '<div class="input-group form-control-pwd">' +
                        '<span class="signin-icons signin-icon-input signin-icon-pwd">' +
                            '<i class="signin-icons signin-icon-br"></i>' +
                        '</span>' +
                        '<input class="form-control" id="password" name="password" type="password" placeholder="密码"/>' +
                    '</div>' +
                    '<div class="checkbox">' +
                        '<label>' +
                            '<input type="checkbox" name="remember_me" value="1" /> 记住密码' +
                        '</label>' +
                    '</div>' +
                '</form>'
            );
        },
        submitForm: function(App, callback) {
            var self = this;

            var $username = LoginHelper.$username,
                $password = LoginHelper.$password,
                $bRemember = LoginHelper.$bRemember,
                $btnSubmit = LoginHelper.$btnSubmit,
                btnSubmitText = LoginHelper.btnSubmitText;

            if (!$username.val()) {
                LoginHelper.showErrorInfo("请输入登录账号！");
                $.isFunction(callback) && callback.call(this, true);
                return false;
            }

            if (!$password.val()) {
                LoginHelper.showErrorInfo("请输入登录密码！", $password);
                $.isFunction(callback) && callback.call(this, true);
                return false;
            }

            var data = {
                'name': $username.val(),
                'password': $password.val()
            };

            if ($btnSubmit.length) {
                $btnSubmit.text("正在登录...").prop('disabled', true);
            }
            LoginHelper.$form.ajaxSubmit({
                success: function(response, statusText, xhr, $form)  {
                    var encryptedPwd = LoginHelper.encryptPwd($password.val());
                    LoginHelper.setCookie.call(App, LoginHelper.rememberKey.username, data.name);
                    if ($bRemember.prop("checked")) {
                        LoginHelper.setCookie.call(App, LoginHelper.rememberKey.remember, '1');
                        LoginHelper.setCookie.call(App, LoginHelper.rememberKey.password, encryptedPwd);
                    } else {
                        LoginHelper.removeCookie.call(App, LoginHelper.rememberKey.remember);
                        LoginHelper.removeCookie.call(App, LoginHelper.rememberKey.password);
                    }
                    App.setLogin({name: data.name});
                    if (!LoginHelper.dialog) {
                        App.go(self.getParam('callback') || "/");
                    }
                    $btnSubmit.prop('disabled', false).html(btnSubmitText);
                    $.isFunction(callback) && callback.call(this, statusText);
                },
                error: function(response, statusText, error, $form)  {
                    if (statusText == "error" && error == "Forbidden") {
                        LoginHelper.showErrorInfo("用户名或密码错误");
                        $btnSubmit.prop('disabled', false).html(btnSubmitText);
                        $.isFunction(callback) && callback.call(this, statusText);
                    }
                }
            });
           /* self.ajax.postJSON({
                url: "v1/login",
                data: data,
                timeout: 5000
            }, function(err, resp, xhr) {
                if (err) {
                    LoginHelper.showErrorInfo("用户名或密码错误");
                } else {
                    // remember login info
                    var uid = resp.id,
                        uname = resp.name,
                        encryptedPwd = LoginHelper.encryptPwd($password.val());
                    LoginHelper.setCookie.call(App, LoginHelper.rememberKey.username, uname);
                    if ($bRemember.prop("checked")) {
                        LoginHelper.setCookie.call(App, LoginHelper.rememberKey.remember, '1');
                        LoginHelper.setCookie.call(App, LoginHelper.rememberKey.password, encryptedPwd);
                    } else {
                        LoginHelper.removeCookie.call(App, LoginHelper.rememberKey.remember);
                        LoginHelper.removeCookie.call(App, LoginHelper.rememberKey.password);
                    }
                    App.setLogin({id: uid, name: uname});
                    if (!LoginHelper.dialog) {
                        App.go(self.getParam('callback') || "/");
                    }
                }
                $btnSubmit.prop('disabled', false).html(btnSubmitText);
                $.isFunction(callback) && callback.apply(this, arguments);
            });*/
        },
        showErrorInfo: function(res, $tar) {
            var msg = "";
            if (typeof res === "string") {
                msg = res;
            } else {
                switch (res.inner_code) {
                    case 'username_is_null':
                        msg = "请输入登录账号！";
                        break;
                    case 'password_is_null':
                        msg = "请输入登录密码！";
                        break;
                    case 'invalid_username':
                    case 'password_is_incorrect':
                        msg = "用户名或密码错误！";
                        break;
                    case 'account_is_locked':
                        msg = "账号已锁定！";
                        break;
                    case 'account_is_disabled':
                        msg = "账号已禁用！";
                        break;
                    case 'no_admin':
                        msg = "非管理员用户，请以租户身份登录！";
                        break;
                    case 'no_vdc':
                        msg = "登录失败，未查询到当前用户关联的租户信息！";
                        break;
                    default:
                        msg = "系统错误！";
                }
            }
            msg = msg || "系统错误！";
            LoginHelper.errorTip($tar || LoginHelper.$username, msg);
        },
        errorTip: function($tar, msg) {
            if($tar instanceof $) {
                $tar.popover({
                    container: LoginHelper.$form,
                    className: "popover-danger",
                    placement: "left top",
                    content: '<i class="glyphicon glyphicon-exclamation-sign"></i> '+(msg||''),
                    trigger: 'manual',
                    html: true
                }).popover("show");
            }
        },
        setCookie: function(key, value, options) {
            return this.cookie.set(key, value, $.extend({expires: 1}, options));
        },
        getCookie: function(key) {
            return this.cookie.get(key);
        },
        removeCookie: function(key) {
            return this.cookie.remove(key);
        },
        encryptPwd: function(pwd) {
            return pwd ? CryptoJS.AES.encrypt(pwd, "inspur_dockerstack").toString() : "";
        },
        decryptPwd: function(encryptedPwd) {
            return encryptedPwd ? CryptoJS.AES.decrypt(encryptedPwd, "inspur_dockerstack").toString(CryptoJS.enc.Utf8) : "";
        }
    };

    var Login = {
        modal: function(App) {
            if (App.loginDlg) return this;
            var self = this;
            App.loginDlg = Modal.show({
                cssClass: 'modal-login',
                title: '请登录',
                message: LoginHelper.modalHtml.call(self, App),
                onshown: function(dialog) {
                    Login.ready.call(self, App, $("form", dialog.getModalBody()));
                },
                buttons: [
                    {
                        icon: 'fa fa-sign-in',
                        label: '登&ensp;录',
                        id: "btn-signin",
                        cssClass: 'btn-primary',
                        autospin: true,
                        hotkey: 13,
                        action: function(dialog) {
                            if (dialog.getData('logging')) return false;
                            dialog.setData('logging', true);
                            dialog.enableButtons(false);
                            LoginHelper.$form = $("#login-form", dialog.getModalBody());
                            LoginHelper.submitForm.call(self, App, function(statusText) {
                                if (statusText == "success") {
                                    dialog.setData('logged', true);
                                    dialog.close();
                                }
                                dialog.setData('logging', false);
                                dialog.enableButtons(true);
                                dialog.getButton('btn-signin').stopSpin();
                            });
                        }
                    },
                    {
                        label: '取消',
                        cssClass: 'btn-default',
                        action: function(dialog){
                            dialog.close();
                        }
                    }
                ],
                onhidden: function(dialog) {
                    dialog.setData('logging', false);
                    App.loginDlg = null;
                    if (!dialog.getData('logged')) {
                        App.go(App.getUrl('/login', {callback: App.getState('url')}));
                    } else {
                        App.go();
                    }
                }
            });
        },
        ready: function(App, $form) {
            var self = this;

            LoginHelper.$form = $form;

            setTimeout(function() {
                $('.form-control', $form).removeClass('noanimate');
            }, 300);

            var $username = $('[name="username"]', $form),
                $password = $('[name="password"]', $form),
                $bRemember = $('[name="remember_me"]', $form),
                $btnSubmit = $('#login-btn', $form);

            LoginHelper.$username = $username;
            LoginHelper.$password = $password;
            LoginHelper.$bRemember = $bRemember;
            LoginHelper.$btnSubmit = $btnSubmit;

            var rememberUsername = LoginHelper.getCookie.call(App, LoginHelper.rememberKey.username),
                rememberPassword = LoginHelper.getCookie.call(App, LoginHelper.rememberKey.password),
                rememberPwdDecrypted = LoginHelper.decryptPwd(rememberPassword),
                bRemember = LoginHelper.getCookie.call(App, LoginHelper.rememberKey.remember);

            if (bRemember) {
                $bRemember.prop('checked', true);
            } else {
                $bRemember.prop('checked', false);
            }

            $('input[type="checkbox"]', $form).iCheck({
                checkboxClass: "icheckbox-primary"
            });

            rememberUsername && $username.val(rememberUsername);
            rememberPassword && $password.val(rememberPwdDecrypted);

            $password.pwdMasked();

            var btnSubmitText = $btnSubmit.html();

            LoginHelper.btnSubmitText = btnSubmitText;

            self.bind('keydown', $username, function(e) {
                if (e && e.keyCode == 13) { // enter 键
                    LoginHelper.submitForm.call(self, App);
                }
            });

            self.bind('keydown', $password, function(e) {
                if (e && e.keyCode == 13) { // enter 键
                    LoginHelper.submitForm.call(self, App);
                }
            });

            self.bind('change', $username, function(e) {
                var $this = $(e.target);
                if ($this.val() !== rememberUsername) {
                    $password.val("");
                } else {
                    $password.val(rememberPwdDecrypted);
                }
            });

            self.bind('focusin', $username, function() {
                $password.popover('destroy');
            });

            self.bind('focusin', $password, function() {
                $username.popover('destroy');
            });

            if ($btnSubmit.length) {
                self.bind('click', $btnSubmit, function() {
                    LoginHelper.submitForm.call(self, App);
                });
            }
        }
    };
    return Login;
});