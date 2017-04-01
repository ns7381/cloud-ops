!function(a){"object"==typeof exports&&"object"==typeof module?a(require("../../lib/codemirror"),require("../htmlmixed/htmlmixed"),require("../../addon/mode/overlay")):"function"==typeof define&&define.amd?define(["../../lib/codemirror","../htmlmixed/htmlmixed","../../addon/mode/overlay"],a):a(CodeMirror)}(function(a){"use strict";a.defineMode("django:inner",function(){function a(a,b){if(a.match("{{"))return b.tokenize=c,"tag";if(a.match("{%"))return b.tokenize=d,"tag";if(a.match("{#"))return b.tokenize=e,"comment";for(;null!=a.next()&&!a.match("{{",!1)&&!a.match("{%",!1););return null}function b(a,b){return function(c,d){if(!d.escapeNext&&c.eat(a))d.tokenize=b;else{d.escapeNext&&(d.escapeNext=!1);var e=c.next();"\\"==e&&(d.escapeNext=!0)}return"string"}}function c(c,d){if(d.waitDot){if(d.waitDot=!1,"."!=c.peek())return"null";if(c.match(/\.\W+/))return"error";if(c.eat("."))return d.waitProperty=!0,"null";throw Error("Unexpected error while waiting for property.")}if(d.waitPipe){if(d.waitPipe=!1,"|"!=c.peek())return"null";if(c.match(/\.\W+/))return"error";if(c.eat("|"))return d.waitFilter=!0,"null";throw Error("Unexpected error while waiting for filter.")}return d.waitProperty&&(d.waitProperty=!1,c.match(/\b(\w+)\b/))?(d.waitDot=!0,d.waitPipe=!0,"property"):d.waitFilter&&(d.waitFilter=!1,c.match(h))?"variable-2":c.eatSpace()?(d.waitProperty=!1,"null"):c.match(/\b\d+(\.\d+)?\b/)?"number":c.match("'")?(d.tokenize=b("'",d.tokenize),"string"):c.match('"')?(d.tokenize=b('"',d.tokenize),"string"):c.match(/\b(\w+)\b/)&&!d.foundVariable?(d.waitDot=!0,d.waitPipe=!0,"variable"):c.match("}}")?(d.waitProperty=null,d.waitFilter=null,d.waitDot=null,d.waitPipe=null,d.tokenize=a,"tag"):(c.next(),"null")}function d(c,d){if(d.waitDot){if(d.waitDot=!1,"."!=c.peek())return"null";if(c.match(/\.\W+/))return"error";if(c.eat("."))return d.waitProperty=!0,"null";throw Error("Unexpected error while waiting for property.")}if(d.waitPipe){if(d.waitPipe=!1,"|"!=c.peek())return"null";if(c.match(/\.\W+/))return"error";if(c.eat("|"))return d.waitFilter=!0,"null";throw Error("Unexpected error while waiting for filter.")}if(d.waitProperty&&(d.waitProperty=!1,c.match(/\b(\w+)\b/)))return d.waitDot=!0,d.waitPipe=!0,"property";if(d.waitFilter&&(d.waitFilter=!1,c.match(h)))return"variable-2";if(c.eatSpace())return d.waitProperty=!1,"null";if(c.match(/\b\d+(\.\d+)?\b/))return"number";if(c.match("'"))return d.tokenize=b("'",d.tokenize),"string";if(c.match('"'))return d.tokenize=b('"',d.tokenize),"string";if(c.match(i))return"operator";var e=c.match(g);return e?("comment"==e[0]&&(d.blockCommentTag=!0),"keyword"):c.match(/\b(\w+)\b/)?(d.waitDot=!0,d.waitPipe=!0,"variable"):c.match("%}")?(d.waitProperty=null,d.waitFilter=null,d.waitDot=null,d.waitPipe=null,d.blockCommentTag?(d.blockCommentTag=!1,d.tokenize=f):d.tokenize=a,"tag"):(c.next(),"null")}function e(b,c){return b.match("#}")&&(c.tokenize=a),"comment"}function f(a,b){return a.match(/\{%\s*endcomment\s*%\}/,!1)?(b.tokenize=d,a.match("{%"),"tag"):(a.next(),"comment")}var g=["block","endblock","for","endfor","true","false","filter","endfilter","loop","none","self","super","if","elif","endif","as","else","import","with","endwith","without","context","ifequal","endifequal","ifnotequal","endifnotequal","extends","include","load","comment","endcomment","empty","url","static","trans","blocktrans","endblocktrans","now","regroup","lorem","ifchanged","endifchanged","firstof","debug","cycle","csrf_token","autoescape","endautoescape","spaceless","endspaceless","ssi","templatetag","verbatim","endverbatim","widthratio"],h=["add","addslashes","capfirst","center","cut","date","default","default_if_none","dictsort","dictsortreversed","divisibleby","escape","escapejs","filesizeformat","first","floatformat","force_escape","get_digit","iriencode","join","last","length","length_is","linebreaks","linebreaksbr","linenumbers","ljust","lower","make_list","phone2numeric","pluralize","pprint","random","removetags","rjust","safe","safeseq","slice","slugify","stringformat","striptags","time","timesince","timeuntil","title","truncatechars","truncatechars_html","truncatewords","truncatewords_html","unordered_list","upper","urlencode","urlize","urlizetrunc","wordcount","wordwrap","yesno"],i=["==","!=","<",">","<=",">=","in","not","or","and"];return g=new RegExp("^\\b("+g.join("|")+")\\b"),h=new RegExp("^\\b("+h.join("|")+")\\b"),i=new RegExp("^\\b("+i.join("|")+")\\b"),{startState:function(){return{tokenize:a}},token:function(a,b){return b.tokenize(a,b)},blockCommentStart:"{% comment %}",blockCommentEnd:"{% endcomment %}"}}),a.defineMode("django",function(b){var c=a.getMode(b,"text/html"),d=a.getMode(b,"django:inner");return a.overlayMode(c,d)}),a.defineMIME("text/x-django","django")});