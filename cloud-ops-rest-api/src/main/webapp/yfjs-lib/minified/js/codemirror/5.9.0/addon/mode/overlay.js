!function(a){"object"==typeof exports&&"object"==typeof module?a(require("../../lib/codemirror")):"function"==typeof define&&define.amd?define(["../../lib/codemirror"],a):a(CodeMirror)}(function(a){"use strict";a.overlayMode=function(b,c,d){return{startState:function(){return{base:a.startState(b),overlay:a.startState(c),basePos:0,baseCur:null,overlayPos:0,overlayCur:null,streamSeen:null}},copyState:function(d){return{base:a.copyState(b,d.base),overlay:a.copyState(c,d.overlay),basePos:d.basePos,baseCur:null,overlayPos:d.overlayPos,overlayCur:null}},token:function(a,e){return(a!=e.streamSeen||Math.min(e.basePos,e.overlayPos)<a.start)&&(e.streamSeen=a,e.basePos=e.overlayPos=a.start),a.start==e.basePos&&(e.baseCur=b.token(a,e.base),e.basePos=a.pos),a.start==e.overlayPos&&(a.pos=a.start,e.overlayCur=c.token(a,e.overlay),e.overlayPos=a.pos),a.pos=Math.min(e.basePos,e.overlayPos),null==e.overlayCur?e.baseCur:null!=e.baseCur&&e.overlay.combineTokens||d&&null==e.overlay.combineTokens?e.baseCur+" "+e.overlayCur:e.overlayCur},indent:b.indent&&function(a,c){return b.indent(a.base,c)},electricChars:b.electricChars,innerMode:function(a){return{state:a.base,mode:b}},blankLine:function(a){b.blankLine&&b.blankLine(a.base),c.blankLine&&c.blankLine(a.overlay)}}}});