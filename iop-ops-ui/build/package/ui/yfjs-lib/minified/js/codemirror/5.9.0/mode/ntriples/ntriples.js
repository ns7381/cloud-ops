!function(a){"object"==typeof exports&&"object"==typeof module?a(require("../../lib/codemirror")):"function"==typeof define&&define.amd?define(["../../lib/codemirror"],a):a(CodeMirror)}(function(a){"use strict";a.defineMode("ntriples",function(){function a(a,c){var d,e=a.location;d=e==b.PRE_SUBJECT&&"<"==c?b.WRITING_SUB_URI:e==b.PRE_SUBJECT&&"_"==c?b.WRITING_BNODE_URI:e==b.PRE_PRED&&"<"==c?b.WRITING_PRED_URI:e==b.PRE_OBJ&&"<"==c?b.WRITING_OBJ_URI:e==b.PRE_OBJ&&"_"==c?b.WRITING_OBJ_BNODE:e==b.PRE_OBJ&&'"'==c?b.WRITING_OBJ_LITERAL:e==b.WRITING_SUB_URI&&">"==c?b.PRE_PRED:e==b.WRITING_BNODE_URI&&" "==c?b.PRE_PRED:e==b.WRITING_PRED_URI&&">"==c?b.PRE_OBJ:e==b.WRITING_OBJ_URI&&">"==c?b.POST_OBJ:e==b.WRITING_OBJ_BNODE&&" "==c?b.POST_OBJ:e==b.WRITING_OBJ_LITERAL&&'"'==c?b.POST_OBJ:e==b.WRITING_LIT_LANG&&" "==c?b.POST_OBJ:e==b.WRITING_LIT_TYPE&&">"==c?b.POST_OBJ:e==b.WRITING_OBJ_LITERAL&&"@"==c?b.WRITING_LIT_LANG:e==b.WRITING_OBJ_LITERAL&&"^"==c?b.WRITING_LIT_TYPE:" "!=c||e!=b.PRE_SUBJECT&&e!=b.PRE_PRED&&e!=b.PRE_OBJ&&e!=b.POST_OBJ?e==b.POST_OBJ&&"."==c?b.PRE_SUBJECT:b.ERROR:e,a.location=d}var b={PRE_SUBJECT:0,WRITING_SUB_URI:1,WRITING_BNODE_URI:2,PRE_PRED:3,WRITING_PRED_URI:4,PRE_OBJ:5,WRITING_OBJ_URI:6,WRITING_OBJ_BNODE:7,WRITING_OBJ_LITERAL:8,WRITING_LIT_LANG:9,WRITING_LIT_TYPE:10,POST_OBJ:11,ERROR:12};return{startState:function(){return{location:b.PRE_SUBJECT,uris:[],anchors:[],bnodes:[],langs:[],types:[]}},token:function(b,c){var d=b.next();if("<"==d){a(c,d);var e="";return b.eatWhile(function(a){return"#"!=a&&">"!=a&&(e+=a,!0)}),c.uris.push(e),b.match("#",!1)?"variable":(b.next(),a(c,">"),"variable")}if("#"==d){var f="";return b.eatWhile(function(a){return">"!=a&&" "!=a&&(f+=a,!0)}),c.anchors.push(f),"variable-2"}if(">"==d)return a(c,">"),"variable";if("_"==d){a(c,d);var g="";return b.eatWhile(function(a){return" "!=a&&(g+=a,!0)}),c.bnodes.push(g),b.next(),a(c," "),"builtin"}if('"'==d)return a(c,d),b.eatWhile(function(a){return'"'!=a}),b.next(),"@"!=b.peek()&&"^"!=b.peek()&&a(c,'"'),"string";if("@"==d){a(c,"@");var h="";return b.eatWhile(function(a){return" "!=a&&(h+=a,!0)}),c.langs.push(h),b.next(),a(c," "),"string-2"}if("^"==d){b.next(),a(c,"^");var i="";return b.eatWhile(function(a){return">"!=a&&(i+=a,!0)}),c.types.push(i),b.next(),a(c,">"),"variable"}" "==d&&a(c,d),"."==d&&a(c,d)}}}),a.defineMIME("text/n-triples","ntriples")});