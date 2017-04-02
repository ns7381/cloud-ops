/**
 * 密码设置弹出框
 *
 * Created by jinzk on 2015/12/4.
 */
define(['App', 'jquery', './modal', './validator', './pwdmasked'], function(App, $, Modal) {
    var Cookie = App.cookie,
        JSON = App.json;
    return {
        show: function(success, failure) {
            if (!Cookie.getLoginId()) {
                typeof failure === "function" && failure();
                return false;
            }
            var loginTag = "_dockerstack";
            return Modal.show({
                cssClass: 'modal-resetpwd',
                title: '密码设置',
                message: function() {
                    return (
                        '<form class="form-horizontal" id="form-resetpwd" role="form">' +
                            '<div class="form-group form-required">' +
                                '<label class="control-label col-sm-3" for="password'+loginTag+'">原密码：</label>' +
                                '<div class="col-sm-8">' +
                                    '<input class="form-control" name="password'+loginTag+'" id="password'+loginTag+'" type="password" data-toggle="pwd-masked">' +
                                '</div>' +
                            '</div>' +
                            '<div class="form-group form-required ">' +
                                '<label class="control-label col-sm-3" for="newPassword'+loginTag+'">新密码：</label>' +
                                '<div class="col-sm-8">' +
                                    '<input class="form-control" name="newPassword'+loginTag+'" id="newPassword'+loginTag+'" type="password" data-toggle="pwd-masked">' +
                                '</div>' +
                            '</div>' +
                            '<div class="form-group form-required">' +
                                '<label class="control-label col-sm-3" for="confirmPassword'+loginTag+'">确认新密码：</label>' +
                                '<div class="col-sm-8">' +
                                    '<input class="form-control" name="confirmPassword'+loginTag+'" id="confirmPassword'+loginTag+'" type="password" data-toggle="pwd-masked">' +
                                '</div>' +
                            '</div>' +
                        '</form>'
                    );
                }(),
                onshow: function(dialog) {
                    var $dialog = dialog.getModalDialog(),
                        $form = $('#form-resetpwd', $dialog);
                    var validateOpts = {
                        errorContainer: $dialog,
                        errorPlacement:"left",
                        rules: {},
                        messages: {}
                    };
                    validateOpts.rules['password'+loginTag] = {
                        required: true,
                        minlength: 6,
                        maxlength: 15
                    };
                    validateOpts.messages['password'+loginTag] = {
                        required: "请填写原密码"
                    };
                    validateOpts.rules['newPassword'+loginTag] = {
                        required: true,
                        minlength: 6,
                        maxlength: 15,
                        notEqualTo: "#password"+loginTag
                    };
                    validateOpts.messages['newPassword'+loginTag] = {
                        required: "请填写新密码",
                        notEqualTo: "新密码不能与原密码相同"
                    };
                    validateOpts.rules['confirmPassword'+loginTag] = {
                        required: true,
                        minlength: 6,
                        maxlength: 15,
                        equalTo: "#newPassword"+loginTag
                    };
                    validateOpts.messages['confirmPassword'+loginTag] = {
                        required: "请确认新密码"
                    };
                    $form.validate(validateOpts);
                    // 重新登录
                    $dialog.on("click", ".btn-login", function() {
                        dialog.setData('login', true);
                        dialog.close();
                    });

                    $('[data-toggle="pwd-masked"]', $dialog).pwdMasked();
                },
                buttons: [{
                    icon: 'fa fa-check-circle',
                    label: '确&ensp;定',
                    id: "btn-resetpwd",
                    cssClass: 'btn-primary',
                    autospin: true,
                    hotkey: 13,
                    action: function(dialog) {
                        dialog.enableButtons(false);
                        dialog.getButton('btn-resetpwd').spin();
                        var $form = $('#form-resetpwd', dialog.getModalBody()),
                            submitError = function(message) {
                                dialog.enableButtons(true);
                                dialog.getButton('btn-resetpwd').stopSpin();
                                var $alert = $(".alert", $form);
                                if (message) {
                                    if (!$alert.length) {
                                        $alert = $('<div class="alert alert-danger"></div>').prependTo($form);
                                    }
                                    $alert.html('<i class="fa fa-exclamation-circle"></i>&ensp;' + message);
                                } else if ($alert.length) {
                                    $alert.remove();
                                }
                                dialog.setPosition();
                                return false;
                            },
                            submitSuccess = function() {
                                dialog.setData('success', true);
                                dialog.enableButtons(true);
                                dialog.getButton('btn-resetpwd').stopSpin();
                                dialog.close();
                            };
                        if (!$form.valid()) {
                            return submitError();
                        }
                        var uid = Cookie.getLoginId();
                        if (!uid) {
                            dialog.setData('login', true);
                            return submitError("获取用户信息失败！可能是登录已失效。<a class='btn-login link-more' href='javascript:;'>点此重新登录</a>");
                        }
                        var $password = $('[name="password'+loginTag+'"]', $form),
                            $newPassword = $('[name="newPassword'+loginTag+'"]', $form);
                        var postData = {
                            "user": {
                                "id": uid,
                                "password": $password.val(),
                                "newPassword": $newPassword.val()
                            }
                        };
                        App.ajax.postJSON({
                            url: "/identity/v2.0/users/password",
                            data: postData,
                            success: function(res) {
                                if (res == 1) {
                                    submitSuccess();
                                } else if (res == -1) {
                                    submitError("原密码输入错误！");
                                } else {
                                    submitError(res && res.error_desc ? res.error_desc : "密码修改失败！");
                                }
                            },
                            error: function(xhr, errorText) {
                                submitError(errorText);
                            }
                        });
                    }
                }, {
                    label: '取消',
                    cssClass: 'btn-default',
                    action: function(dialog){
                        dialog.close();
                    }
                }],
                onhidden: function(dialog) {
                    dialog.enableButtons(true);
                    dialog.getButton('btn-resetpwd').stopSpin();
                    if (dialog.getData('success')) {
                        typeof success === "function" && success.call(success, dialog);
                    } else {
                        typeof failure === "function" && failure.call(failure, dialog);
                    }
                }
            });
        }
    };
});