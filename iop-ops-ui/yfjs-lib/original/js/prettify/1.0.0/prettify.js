!
function() {
	var q = null;
	window.PR_SHOULD_USE_CONTINUATION = !0;
	(function() {
		function S(a) {
			function d(e) {
				var b = e.charCodeAt(0);
				if (b !== 92) return b;
				var a = e.charAt(1);
				return (b = r[a]) ? b : "0" <= a && a <= "7" ? parseInt(e.substring(1), 8) : a === "u" || a === "x" ? parseInt(e.substring(2), 16) : e.charCodeAt(1)
			}
			function g(e) {
				if (e < 32) return (e < 16 ? "\\x0" : "\\x") + e.toString(16);
				e = String.fromCharCode(e);
				return e === "\\" || e === "-" || e === "]" || e === "^" ? "\\" + e : e
			}
			function b(e) {
				var b = e.substring(1, e.length - 1).match(/\\u[\dA-Fa-f]{4}|\\x[\dA-Fa-f]{2}|\\[0-3][0-7]{0,2}|\\[0-7]{1,2}|\\[\S\s]|[^\\]/g),
					e = [],
					a = b[0] === "^",
					c = ["["];
				a && c.push("^");
				for (var a = a ? 1 : 0, f = b.length; a < f; ++a) {
					var h = b[a];
					if (/\\[bdsw]/i.test(h)) c.push(h);
					else {
						var h = d(h),
							l;
						a + 2 < f && "-" === b[a + 1] ? (l = d(b[a + 2]), a += 2) : l = h;
						e.push([h, l]);
						l < 65 || h > 122 || (l < 65 || h > 90 || e.push([Math.max(65, h) | 32, Math.min(l, 90) | 32]), l < 97 || h > 122 || e.push([Math.max(97, h) & -33, Math.min(l, 122) & -33]))
					}
				}
				e.sort(function(e, a) {
					return e[0] - a[0] || a[1] - e[1]
				});
				b = [];
				f = [];
				for (a = 0; a < e.length; ++a) h = e[a], h[0] <= f[1] + 1 ? f[1] = Math.max(f[1], h[1]) : b.push(f = h);
				for (a = 0; a < b.length; ++a) h = b[a], c.push(g(h[0])), h[1] > h[0] && (h[1] + 1 > h[0] && c.push("-"), c.push(g(h[1])));
				c.push("]");
				return c.join("")
			}
			function s(e) {
				for (var a = e.source.match(/\[(?:[^\\\]]|\\[\S\s])*]|\\u[\dA-Fa-f]{4}|\\x[\dA-Fa-f]{2}|\\\d+|\\[^\dux]|\(\?[!:=]|[()^]|[^()[\\^]+/g), c = a.length, d = [], f = 0, h = 0; f < c; ++f) {
					var l = a[f];
					l === "(" ? ++h : "\\" === l.charAt(0) && (l = +l.substring(1)) && (l <= h ? d[l] = -1 : a[f] = g(l))
				}
				for (f = 1; f < d.length; ++f) - 1 === d[f] && (d[f] = ++x);
				for (h = f = 0; f < c; ++f) l = a[f], l === "(" ? (++h, d[h] || (a[f] = "(?:")) : "\\" === l.charAt(0) && (l = +l.substring(1)) && l <= h && (a[f] = "\\" + d[l]);
				for (f = 0; f < c; ++f)"^" === a[f] && "^" !== a[f + 1] && (a[f] = "");
				if (e.ignoreCase && m) for (f = 0; f < c; ++f) l = a[f], e = l.charAt(0), l.length >= 2 && e === "[" ? a[f] = b(l) : e !== "\\" && (a[f] = l.replace(/[A-Za-z]/g, function(a) {
					a = a.charCodeAt(0);
					return "[" + String.fromCharCode(a & -33, a | 32) + "]"
				}));
				return a.join("")
			}
			for (var x = 0, m = !1, j = !1, k = 0, c = a.length; k < c; ++k) {
				var i = a[k];
				if (i.ignoreCase) j = !0;
				else if (/[a-z]/i.test(i.source.replace(/\\u[\da-f]{4}|\\x[\da-f]{2}|\\[^UXux]/gi, ""))) {
					m = !0;
					j = !1;
					break
				}
			}
			for (var r = {
				b: 8,
				t: 9,
				n: 10,
				v: 11,
				f: 12,
				r: 13
			}, n = [], k = 0, c = a.length; k < c; ++k) {
				i = a[k];
				if (i.global || i.multiline) throw Error("" + i);
				n.push("(?:" + s(i) + ")")
			}
			return RegExp(n.join("|"), j ? "gi" : "g")
		}
		function T(a, d) {
			function g(a) {
				var c = a.nodeType;
				if (c == 1) {
					if (!b.test(a.className)) {
						for (c = a.firstChild; c; c = c.nextSibling) g(c);
						c = a.nodeName.toLowerCase();
						if ("br" === c || "li" === c) s[j] = "\n", m[j << 1] = x++, m[j++ << 1 | 1] = a
					}
				} else if (c == 3 || c == 4) c = a.nodeValue, c.length && (c = d ? c.replace(/\r\n?/g, "\n") : c.replace(/[\t\n\r ]+/g, " "), s[j] = c, m[j << 1] = x, x += c.length, m[j++ << 1 | 1] = a)
			}
			var b = /(?:^|\s)nocode(?:\s|$)/,
				s = [],
				x = 0,
				m = [],
				j = 0;
			g(a);
			return {
				a: s.join("").replace(/\n$/, ""),
				d: m
			}
		}
		function H(a, d, g, b) {
			d && (a = {
				a: d,
				e: a
			}, g(a), b.push.apply(b, a.g))
		}
		function U(a) {
			for (var d = void 0, g = a.firstChild; g; g = g.nextSibling) var b = g.nodeType,
				d = b === 1 ? d ? a : g : b === 3 ? V.test(g.nodeValue) ? a : d : d;
			return d === a ? void 0 : d
		}
		function C(a, d) {
			function g(a) {
				for (var j = a.e, k = [j, "pln"], c = 0, i = a.a.match(s) || [], r = {}, n = 0, e = i.length; n < e; ++n) {
					var z = i[n],
						w = r[z],
						t = void 0,
						f;
					if (typeof w === "string") f = !1;
					else {
						var h = b[z.charAt(0)];
						if (h) t = z.match(h[1]), w = h[0];
						else {
							for (f = 0; f < x; ++f) if (h = d[f], t = z.match(h[1])) {
								w = h[0];
								break
							}
							t || (w = "pln")
						}
						if ((f = w.length >= 5 && "lang-" === w.substring(0, 5)) && !(t && typeof t[1] === "string")) f = !1, w = "src";
						f || (r[z] = w)
					}
					h = c;
					c += z.length;
					if (f) {
						f = t[1];
						var l = z.indexOf(f),
							B = l + f.length;
						t[2] && (B = z.length - t[2].length, l = B - f.length);
						w = w.substring(5);
						H(j + h, z.substring(0, l), g, k);
						H(j + h + l, f, I(w, f), k);
						H(j + h + B, z.substring(B), g, k)
					} else k.push(j + h, w)
				}
				a.g = k
			}
			var b = {},
				s;
			(function() {
				for (var g = a.concat(d), j = [], k = {}, c = 0, i = g.length; c < i; ++c) {
					var r = g[c],
						n = r[3];
					if (n) for (var e = n.length; --e >= 0;) b[n.charAt(e)] = r;
					r = r[1];
					n = "" + r;
					k.hasOwnProperty(n) || (j.push(r), k[n] = q)
				}
				j.push(/[\S\s]/);
				s = S(j)
			})();
			var x = d.length;
			return g
		}
		function v(a) {
			var d = [],
				g = [];
			a.tripleQuotedStrings ? d.push(["str", /^(?:'''(?:[^'\\]|\\[\S\s]|''?(?=[^']))*(?:'''|$)|"""(?:[^"\\]|\\[\S\s]|""?(?=[^"]))*(?:"""|$)|'(?:[^'\\]|\\[\S\s])*(?:'|$)|"(?:[^"\\]|\\[\S\s])*(?:"|$))/, q, "'\""]) : a.multiLineStrings ? d.push(["str", /^(?:'(?:[^'\\]|\\[\S\s])*(?:'|$)|"(?:[^"\\]|\\[\S\s])*(?:"|$)|`(?:[^\\`]|\\[\S\s])*(?:`|$))/, q, "'\"`"]) : d.push(["str", /^(?:'(?:[^\n\r'\\]|\\.)*(?:'|$)|"(?:[^\n\r"\\]|\\.)*(?:"|$))/, q, "\"'"]);
			a.verbatimStrings && g.push(["str", /^@"(?:[^"]|"")*(?:"|$)/, q]);
			var b = a.hashComments;
			b && (a.cStyleComments ? (b > 1 ? d.push(["com", /^#(?:##(?:[^#]|#(?!##))*(?:###|$)|.*)/, q, "#"]) : d.push(["com", /^#(?:(?:define|e(?:l|nd)if|else|error|ifn?def|include|line|pragma|undef|warning)\b|[^\n\r]*)/, q, "#"]), g.push(["str", /^<(?:(?:(?:\.\.\/)*|\/?)(?:[\w-]+(?:\/[\w-]+)+)?[\w-]+\.h(?:h|pp|\+\+)?|[a-z]\w*)>/, q])) : d.push(["com", /^#[^\n\r]*/, q, "#"]));
			a.cStyleComments && (g.push(["com", /^\/\/[^\n\r]*/, q]), g.push(["com", /^\/\*[\S\s]*?(?:\*\/|$)/, q]));
			if (b = a.regexLiterals) {
				var s = (b = b > 1 ? "" : "\n\r") ? "." : "[\\S\\s]";
				g.push(["lang-regex", RegExp("^(?:^^\\.?|[+-]|[!=]=?=?|\\#|%=?|&&?=?|\\(|\\*=?|[+\\-]=|->|\\/=?|::?|<<?=?|>>?>?=?|,|;|\\?|@|\\[|~|{|\\^\\^?=?|\\|\\|?=?|break|case|continue|delete|do|else|finally|instanceof|return|throw|try|typeof)\\s*(" + ("/(?=[^/*" + b + "])(?:[^/\\x5B\\x5C" + b + "]|\\x5C" + s + "|\\x5B(?:[^\\x5C\\x5D" + b + "]|\\x5C" + s + ")*(?:\\x5D|$))+/") + ")")])
			}(b = a.types) && g.push(["typ", b]);
			b = ("" + a.keywords).replace(/^ | $/g, "");
			b.length && g.push(["kwd", RegExp("^(?:" + b.replace(/[\s,]+/g, "|") + ")\\b"), q]);
			d.push(["pln", /^\s+/, q, " \r\n\t\u00a0"]);
			b = "^.[^\\s\\w.$@'\"`/\\\\]*";
			a.regexLiterals && (b += "(?!s*/)");
			g.push(["lit", /^@[$_a-z][\w$@]*/i, q], ["typ", /^(?:[@_]?[A-Z]+[a-z][\w$@]*|\w+_t\b)/, q], ["pln", /^[$_a-z][\w$@]*/i, q], ["lit", /^(?:0x[\da-f]+|(?:\d(?:_\d+)*\d*(?:\.\d*)?|\.\d\+)(?:e[+-]?\d+)?)[a-z]*/i, q, "0123456789"], ["pln", /^\\[\S\s]?/, q], ["pun", RegExp(b), q]);
			return C(d, g)
		}
		function J(a, d, g) {
			function b(a) {
				var c = a.nodeType;
				if (c == 1 && !x.test(a.className)) if ("br" === a.nodeName) s(a), a.parentNode && a.parentNode.removeChild(a);
				else for (a = a.firstChild; a; a = a.nextSibling) b(a);
				else if ((c == 3 || c == 4) && g) {
					var d = a.nodeValue,
						i = d.match(m);
					if (i) c = d.substring(0, i.index), a.nodeValue = c, (d = d.substring(i.index + i[0].length)) && a.parentNode.insertBefore(j.createTextNode(d), a.nextSibling), s(a), c || a.parentNode.removeChild(a)
				}
			}
			function s(a) {
				function b(a, c) {
					var d = c ? a.cloneNode(!1) : a,
						e = a.parentNode;
					if (e) {
						var e = b(e, 1),
							g = a.nextSibling;
						e.appendChild(d);
						for (var i = g; i; i = g) g = i.nextSibling, e.appendChild(i)
					}
					return d
				}
				for (; !a.nextSibling;) if (a = a.parentNode, !a) return;
				for (var a = b(a.nextSibling, 0), d;
				(d = a.parentNode) && d.nodeType === 1;) a = d;
				c.push(a)
			}
			for (var x = /(?:^|\s)nocode(?:\s|$)/, m = /\r\n?|\n/, j = a.ownerDocument, k = j.createElement("li"); a.firstChild;) k.appendChild(a.firstChild);
			for (var c = [k], i = 0; i < c.length; ++i) b(c[i]);
			d === (d | 0) && c[0].setAttribute("value", d);
			var r = j.createElement("ol");
			r.className = "linenums";
			for (var d = Math.max(0, d - 1 | 0) || 0, i = 0, n = c.length; i < n; ++i) k = c[i], k.className = "L" + (i + d) % 10, k.firstChild || k.appendChild(j.createTextNode("\u00a0")), r.appendChild(k);
			a.appendChild(r)
		}
		function p(a, d) {
			for (var g = d.length; --g >= 0;) {
				var b = d[g];
				F.hasOwnProperty(b) ? D.console && console.warn("cannot override language handler %s", b) : F[b] = a
			}
		}
		function I(a, d) {
			if (!a || !F.hasOwnProperty(a)) a = /^\s*</.test(d) ? "default-markup" : "default-code";
			return F[a]
		}
		function K(a) {
			var d = a.h;
			try {
				var g = T(a.c, a.i),
					b = g.a;
				a.a = b;
				a.d = g.d;
				a.e = 0;
				I(d, b)(a);
				var s = /\bMSIE\s(\d+)/.exec(navigator.userAgent),
					s = s && +s[1] <= 8,
					d = /\n/g,
					x = a.a,
					m = x.length,
					g = 0,
					j = a.d,
					k = j.length,
					b = 0,
					c = a.g,
					i = c.length,
					r = 0;
				c[i] = m;
				var n, e;
				for (e = n = 0; e < i;) c[e] !== c[e + 2] ? (c[n++] = c[e++], c[n++] = c[e++]) : e += 2;
				i = n;
				for (e = n = 0; e < i;) {
					for (var p = c[e], w = c[e + 1], t = e + 2; t + 2 <= i && c[t + 1] === w;) t += 2;
					c[n++] = p;
					c[n++] = w;
					e = t
				}
				c.length = n;
				var f = a.c,
					h;
				if (f) h = f.style.display, f.style.display = "none";
				try {
					for (; b < k;) {
						var l = j[b + 2] || m,
							B = c[r + 2] || m,
							t = Math.min(l, B),
							A = j[b + 1],
							G;
						if (A.nodeType !== 1 && (G = x.substring(g, t))) {
							s && (G = G.replace(d, "\r"));
							A.nodeValue = G;
							var L = A.ownerDocument,
								o = L.createElement("span");
							o.className = c[r + 1];
							var v = A.parentNode;
							v.replaceChild(o, A);
							o.appendChild(A);
							g < l && (j[b + 1] = A = L.createTextNode(x.substring(t, l)), v.insertBefore(A, o.nextSibling))
						}
						g = t;
						g >= l && (b += 2);
						g >= B && (r += 2)
					}
				} finally {
					if (f) f.style.display = h
				}
			} catch (u) {
				D.console && console.log(u && u.stack || u)
			}
		}
		var D = window,
			y = ["break,continue,do,else,for,if,return,while"],
			E = [
				[y, "auto,case,char,const,default,double,enum,extern,float,goto,inline,int,long,register,short,signed,sizeof,static,struct,switch,typedef,union,unsigned,void,volatile"], "catch,class,delete,false,import,new,operator,private,protected,public,this,throw,true,try,typeof"],
			M = [E, "alignof,align_union,asm,axiom,bool,concept,concept_map,const_cast,constexpr,decltype,delegate,dynamic_cast,explicit,export,friend,generic,late_check,mutable,namespace,nullptr,property,reinterpret_cast,static_assert,static_cast,template,typeid,typename,using,virtual,where"],
			N = [E, "abstract,assert,boolean,byte,extends,final,finally,implements,import,instanceof,interface,null,native,package,strictfp,super,synchronized,throws,transient"],
			O = [N, "as,base,by,checked,decimal,delegate,descending,dynamic,event,fixed,foreach,from,group,implicit,in,internal,into,is,let,lock,object,out,override,orderby,params,partial,readonly,ref,sbyte,sealed,stackalloc,string,select,uint,ulong,unchecked,unsafe,ushort,var,virtual,where"],
			E = [E, "debugger,eval,export,function,get,null,set,undefined,var,with,Infinity,NaN"],
			P = [y, "and,as,assert,class,def,del,elif,except,exec,finally,from,global,import,in,is,lambda,nonlocal,not,or,pass,print,raise,try,with,yield,False,True,None"],
			Q = [y, "alias,and,begin,case,class,def,defined,elsif,end,ensure,false,in,module,next,nil,not,or,redo,rescue,retry,self,super,then,true,undef,unless,until,when,yield,BEGIN,END"],
			W = [y, "as,assert,const,copy,drop,enum,extern,fail,false,fn,impl,let,log,loop,match,mod,move,mut,priv,pub,pure,ref,self,static,struct,true,trait,type,unsafe,use"],
			y = [y, "case,done,elif,esac,eval,fi,function,in,local,set,then,until"],
			R = /^(DIR|FILE|vector|(de|priority_)?queue|list|stack|(const_)?iterator|(multi)?(set|map)|bitset|u?(int|float)\d*)\b/,
			V = /\S/,
			X = v({
				keywords: [M, O, E, "caller,delete,die,do,dump,elsif,eval,exit,foreach,for,goto,if,import,last,local,my,next,no,our,print,package,redo,require,sub,undef,unless,until,use,wantarray,while,BEGIN,END", P, Q, y],
				hashComments: !0,
				cStyleComments: !0,
				multiLineStrings: !0,
				regexLiterals: !0
			}),
			F = {};
		p(X, ["default-code"]);
		p(C([], [
			["pln", /^[^<?]+/],
			["dec", /^<!\w[^>]*(?:>|$)/],
			["com", /^<\!--[\S\s]*?(?:--\>|$)/],
			["lang-", /^<\?([\S\s]+?)(?:\?>|$)/],
			["lang-", /^<%([\S\s]+?)(?:%>|$)/],
			["pun", /^(?:<[%?]|[%?]>)/],
			["lang-", /^<xmp\b[^>]*>([\S\s]+?)<\/xmp\b[^>]*>/i],
			["lang-js", /^<script\b[^>]*>([\S\s]*?)(<\/script\b[^>]*>)/i],
			["lang-css", /^<style\b[^>]*>([\S\s]*?)(<\/style\b[^>]*>)/i],
			["lang-in.tag", /^(<\/?[a-z][^<>]*>)/i]
		]), ["default-markup", "htm", "html", "mxml", "xhtml", "xml", "xsl"]);
		p(C([
			["pln", /^\s+/, q, " \t\r\n"],
			["atv", /^(?:"[^"]*"?|'[^']*'?)/, q, "\"'"]
		], [
			["tag", /^^<\/?[a-z](?:[\w-.:]*\w)?|\/?>$/i],
			["atn", /^(?!style[\s=]|on)[a-z](?:[\w:-]*\w)?/i],
			["lang-uq.val", /^=\s*([^\s"'>]*(?:[^\s"'/>]|\/(?=\s)))/],
			["pun", /^[/<->]+/],
			["lang-js", /^on\w+\s*=\s*"([^"]+)"/i],
			["lang-js", /^on\w+\s*=\s*'([^']+)'/i],
			["lang-js", /^on\w+\s*=\s*([^\s"'>]+)/i],
			["lang-css", /^style\s*=\s*"([^"]+)"/i],
			["lang-css", /^style\s*=\s*'([^']+)'/i],
			["lang-css", /^style\s*=\s*([^\s"'>]+)/i]
		]), ["in.tag"]);
		p(C([], [
			["atv", /^[\S\s]+/]
		]), ["uq.val"]);
		p(v({
			keywords: M,
			hashComments: !0,
			cStyleComments: !0,
			types: R
		}), ["c", "cc", "cpp", "cxx", "cyc", "m"]);
		p(v({
			keywords: "null,true,false"
		}), ["json"]);
		p(v({
			keywords: O,
			hashComments: !0,
			cStyleComments: !0,
			verbatimStrings: !0,
			types: R
		}), ["cs"]);
		p(v({
			keywords: N,
			cStyleComments: !0
		}), ["java"]);
		p(v({
			keywords: y,
			hashComments: !0,
			multiLineStrings: !0
		}), ["bash", "bsh", "csh", "sh"]);
		p(v({
			keywords: P,
			hashComments: !0,
			multiLineStrings: !0,
			tripleQuotedStrings: !0
		}), ["cv", "py", "python"]);
		p(v({
			keywords: "caller,delete,die,do,dump,elsif,eval,exit,foreach,for,goto,if,import,last,local,my,next,no,our,print,package,redo,require,sub,undef,unless,until,use,wantarray,while,BEGIN,END",
			hashComments: !0,
			multiLineStrings: !0,
			regexLiterals: 2
		}), ["perl", "pl", "pm"]);
		p(v({
			keywords: Q,
			hashComments: !0,
			multiLineStrings: !0,
			regexLiterals: !0
		}), ["rb", "ruby"]);
		p(v({
			keywords: E,
			cStyleComments: !0,
			regexLiterals: !0
		}), ["javascript", "js"]);
		p(v({
			keywords: "all,and,by,catch,class,else,extends,false,finally,for,if,in,is,isnt,loop,new,no,not,null,of,off,on,or,return,super,then,throw,true,try,unless,until,when,while,yes",
			hashComments: 3,
			cStyleComments: !0,
			multilineStrings: !0,
			tripleQuotedStrings: !0,
			regexLiterals: !0
		}), ["coffee"]);
		p(v({
			keywords: W,
			cStyleComments: !0,
			multilineStrings: !0
		}), ["rc", "rs", "rust"]);
		p(C([], [
			["str", /^[\S\s]+/]
		]), ["regex"]);
		var Y = D.PR = {
			createSimpleLexer: C,
			registerLangHandler: p,
			sourceDecorator: v,
			PR_ATTRIB_NAME: "atn",
			PR_ATTRIB_VALUE: "atv",
			PR_COMMENT: "com",
			PR_DECLARATION: "dec",
			PR_KEYWORD: "kwd",
			PR_LITERAL: "lit",
			PR_NOCODE: "nocode",
			PR_PLAIN: "pln",
			PR_PUNCTUATION: "pun",
			PR_SOURCE: "src",
			PR_STRING: "str",
			PR_TAG: "tag",
			PR_TYPE: "typ",
			prettyPrintOne: D.prettyPrintOne = function(a, d, g) {
				var b = document.createElement("div");
				b.innerHTML = "<pre>" + a + "</pre>";
				b = b.firstChild;
				g && J(b, g, !0);
				K({
					h: d,
					j: g,
					c: b,
					i: 1
				});
				return b.innerHTML
			},
			prettyPrint: D.prettyPrint = function(a, d) {
				function g() {
					for (var b = D.PR_SHOULD_USE_CONTINUATION ? c.now() + 250 : Infinity; i < p.length && c.now() < b; i++) {
						for (var d = p[i], j = h, k = d; k = k.previousSibling;) {
							var m = k.nodeType,
								o = (m === 7 || m === 8) && k.nodeValue;
							if (o ? !/^\??prettify\b/.test(o) : m !== 3 || /\S/.test(k.nodeValue)) break;
							if (o) {
								j = {};
								o.replace(/\b(\w+)=([\w%+\-.:]+)/g, function(a, b, c) {
									j[b] = c
								});
								break
							}
						}
						k = d.className;
						if ((j !== h || e.test(k)) && !v.test(k)) {
							m = !1;
							for (o = d.parentNode; o; o = o.parentNode) if (f.test(o.tagName) && o.className && e.test(o.className)) {
								m = !0;
								break
							}
							if (!m) {
								d.className += " prettyprinted";
								m = j.lang;
								if (!m) {
									var m = k.match(n),
										y;
									if (!m && (y = U(d)) && t.test(y.tagName)) m = y.className.match(n);
									m && (m = m[1])
								}
								if (w.test(d.tagName)) o = 1;
								else var o = d.currentStyle,
									u = s.defaultView,
									o = (o = o ? o.whiteSpace : u && u.getComputedStyle ? u.getComputedStyle(d, q).getPropertyValue("white-space") : 0) && "pre" === o.substring(0, 3);
								u = j.linenums;
								if (!(u = u === "true" || +u)) u = (u = k.match(/\blinenums\b(?::(\d+))?/)) ? u[1] && u[1].length ? +u[1] : !0 : !1;
								u && J(d, u, o);
								r = {
									h: m,
									c: d,
									j: u,
									i: o
								};
								K(r)
							}
						}
					}
					i < p.length ? setTimeout(g, 250) : "function" === typeof a && a()
				}
				for (var b = d || document.body, s = b.ownerDocument || document, b = [b.getElementsByTagName("pre"), b.getElementsByTagName("code"), b.getElementsByTagName("xmp")], p = [], m = 0; m < b.length; ++m) for (var j = 0, k = b[m].length; j < k; ++j) p.push(b[m][j]);
				var b = q,
					c = Date;
				c.now || (c = {
					now: function() {
						return +new Date
					}
				});
				var i = 0,
					r, n = /\blang(?:uage)?-([\w.]+)(?!\S)/,
					e = /\bprettyprint\b/,
					v = /\bprettyprinted\b/,
					w = /pre|xmp/i,
					t = /^code$/i,
					f = /^(?:pre|code|xmp)$/i,
					h = {};
				g()
			}
		};
		/*typeof define === "function" && define.amd && define("google-code-prettify", [], function() {
			return Y
		})*/
		typeof define === "function" && define.amd && define([], function() {
			return Y
		})
	})();
}()