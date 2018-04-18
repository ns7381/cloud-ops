define(['App', 'common/ui/login'], function(App, Login) {
    return App.View({
        layout: null,
        styles: 'login.css',
        ready: function() {
            var $form = this.$("#login-form");
            Login.ready.call(this, App, $form);
        }
    });
});