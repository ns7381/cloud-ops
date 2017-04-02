define(['App'], function(App) {
    return App.View({
        layout: null,
        template: null,
        ready: function() {
            App.clearLogin();
            App.go(App.getUrl('/login', {callback: App.prevState('url')}));
        }
    });
});