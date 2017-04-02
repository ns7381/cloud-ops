!function(a){"object"==typeof exports&&"object"==typeof module?a(require("../../lib/codemirror"),require("./searchcursor"),require("../dialog/dialog")):"function"==typeof define&&define.amd?define(["../../lib/codemirror","./searchcursor","../dialog/dialog"],a):a(CodeMirror)}(function(a){"use strict";function b(a,b){return"string"==typeof a?a=new RegExp(a.replace(/[\-\[\]\/\{\}\(\)\*\+\?\.\\\^\$\|]/g,"\\$&"),b?"gi":"g"):a.global||(a=new RegExp(a.source,a.ignoreCase?"gi":"g")),{token:function(b){a.lastIndex=b.pos;var c=a.exec(b.string);return c&&c.index==b.pos?(b.pos+=c[0].length||1,"searching"):void(c?b.pos=c.index:b.skipToEnd())}}}function c(){this.posFrom=this.posTo=this.lastQuery=this.query=null,this.overlay=null}function d(a){return a.state.search||(a.state.search=new c)}function e(a){return"string"==typeof a&&a==a.toLowerCase()}function f(a,b,c){return a.getSearchCursor(b,c,e(b))}function g(a,b,c,d){a.openDialog(b,d,{value:c,selectValueOnOpen:!0,closeOnEnter:!1,onClose:function(){o(a)}})}function h(a,b,c,d,e){a.openDialog?a.openDialog(b,e,{value:d,selectValueOnOpen:!0}):e(prompt(c,d))}function i(a,b,c,d){a.openConfirm?a.openConfirm(b,d):confirm(c)&&d[0]()}function j(a){return a.replace(/\\(.)/g,function(a,b){return"n"==b?"\n":"r"==b?"\r":b})}function k(a){var b=a.match(/^\/(.*)\/([a-z]*)$/);if(b)try{a=new RegExp(b[1],b[2].indexOf("i")==-1?"":"i")}catch(a){}else a=j(a);return("string"==typeof a?""==a:a.test(""))&&(a=/x^/),a}function l(a,c,d){c.queryText=d,c.query=k(d),a.removeOverlay(c.overlay,e(c.query)),c.overlay=b(c.query,e(c.query)),a.addOverlay(c.overlay),a.showMatchesOnScrollbar&&(c.annotate&&(c.annotate.clear(),c.annotate=null),c.annotate=a.showMatchesOnScrollbar(c.query,e(c.query)))}function m(b,c,e){var f=d(b);if(f.query)return n(b,c);var i=b.getSelection()||f.lastQuery;if(e&&b.openDialog){var j=null;g(b,r,i,function(c,d){a.e_stop(d),c&&(c!=f.queryText&&l(b,f,c),j&&(j.style.opacity=1),n(b,d.shiftKey,function(a,c){var d;c.line<3&&document.querySelector&&(d=b.display.wrapper.querySelector(".CodeMirror-dialog"))&&d.getBoundingClientRect().bottom-4>b.cursorCoords(c,"window").top&&((j=d).style.opacity=.4)}))})}else h(b,r,"Search for:",i,function(a){a&&!f.query&&b.operation(function(){l(b,f,a),f.posFrom=f.posTo=b.getCursor(),n(b,c)})})}function n(b,c,e){b.operation(function(){var g=d(b),h=f(b,g.query,c?g.posFrom:g.posTo);(h.find(c)||(h=f(b,g.query,c?a.Pos(b.lastLine()):a.Pos(b.firstLine(),0)),h.find(c)))&&(b.setSelection(h.from(),h.to()),b.scrollIntoView({from:h.from(),to:h.to()},20),g.posFrom=h.from(),g.posTo=h.to(),e&&e(h.from(),h.to()))})}function o(a){a.operation(function(){var b=d(a);b.lastQuery=b.query,b.query&&(b.query=b.queryText=null,a.removeOverlay(b.overlay),b.annotate&&(b.annotate.clear(),b.annotate=null))})}function p(a,b,c){a.operation(function(){for(var d=f(a,b);d.findNext();)if("string"!=typeof b){var e=a.getRange(d.from(),d.to()).match(b);d.replace(c.replace(/\$(\d)/g,function(a,b){return e[b]}))}else d.replace(c)})}function q(a,b){if(!a.getOption("readOnly")){var c=a.getSelection()||d(a).lastQuery,e=b?"Replace all:":"Replace:";h(a,e+s,e,c,function(c){c&&(c=k(c),h(a,t,"Replace with:","",function(d){if(d=j(d),b)p(a,c,d);else{o(a);var e=f(a,c,a.getCursor()),g=function(){var b,j=e.from();!(b=e.findNext())&&(e=f(a,c),!(b=e.findNext())||j&&e.from().line==j.line&&e.from().ch==j.ch)||(a.setSelection(e.from(),e.to()),a.scrollIntoView({from:e.from(),to:e.to()}),i(a,u,"Replace?",[function(){h(b)},g,function(){p(a,c,d)}]))},h=function(a){e.replace("string"==typeof c?d:d.replace(/\$(\d)/g,function(b,c){return a[c]})),g()};g()}}))})}}var r='Search: <input type="text" style="width: 10em" class="CodeMirror-search-field"/> <span style="color: #888" class="CodeMirror-search-hint">(Use /re/ syntax for regexp search)</span>',s=' <input type="text" style="width: 10em" class="CodeMirror-search-field"/> <span style="color: #888" class="CodeMirror-search-hint">(Use /re/ syntax for regexp search)</span>',t='With: <input type="text" style="width: 10em" class="CodeMirror-search-field"/>',u="Replace? <button>Yes</button> <button>No</button> <button>All</button> <button>Stop</button>";a.commands.find=function(a){o(a),m(a)},a.commands.findPersistent=function(a){o(a),m(a,!1,!0)},a.commands.findNext=m,a.commands.findPrev=function(a){m(a,!0)},a.commands.clearSearch=o,a.commands.replace=q,a.commands.replaceAll=function(a){q(a,!0)}});