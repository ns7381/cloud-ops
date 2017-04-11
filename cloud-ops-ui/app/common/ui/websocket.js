define(['jquery'], function ($) {
    var WS = {};
    var curWwwPath = window.document.location.href,
        pathName = window.location.hash,
        pos = curWwwPath.indexOf(pathName),
        rootPath = curWwwPath.substring(0, pos);
    WS.rootPath = rootPath;
    WS.open = function (path, callback) {
        var websocket;
        websocket = new WebSocket("ws://" + WS.rootPath.substr(7) + "messages?routing-key=" + path);
        websocket.onopen = function (evnt) {
            console.log("websocket connect success: " + evnt.data);
        };
        websocket.onmessage = function (event) {
            typeof callback === "function" && callback.call(null, event);
        };
        websocket.onerror = function (evnt) {
        };
        websocket.onclose = function (evnt) {
        };
    };
    return WS;
});