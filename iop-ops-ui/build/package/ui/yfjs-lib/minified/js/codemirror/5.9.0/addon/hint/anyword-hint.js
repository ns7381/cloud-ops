!function(a){"object"==typeof exports&&"object"==typeof module?a(require("../../lib/codemirror")):"function"==typeof define&&define.amd?define(["../../lib/codemirror"],a):a(CodeMirror)}(function(a){"use strict";var b=/[\w$]+/,c=500;a.registerHelper("hint","anyword",function(d,e){for(var f=e&&e.word||b,g=e&&e.range||c,h=d.getCursor(),i=d.getLine(h.line),j=h.ch,k=j;k&&f.test(i.charAt(k-1));)--k;for(var l=k!=j&&i.slice(k,j),m=e&&e.list||[],n={},o=new RegExp(f.source,"g"),p=-1;p<=1;p+=2)for(var q=h.line,r=Math.min(Math.max(q+p*g,d.firstLine()),d.lastLine())+p;q!=r;q+=p)for(var s,t=d.getLine(q);s=o.exec(t);)q==h.line&&s[0]===l||l&&0!=s[0].lastIndexOf(l,0)||Object.prototype.hasOwnProperty.call(n,s[0])||(n[s[0]]=!0,m.push(s[0]));return{list:m,from:a.Pos(h.line,k),to:a.Pos(h.line,j)}})});