!function(a){"object"==typeof exports&&"object"==typeof module?a(require("../../lib/codemirror")):"function"==typeof define&&define.amd?define(["../../lib/codemirror"],a):a(CodeMirror)}(function(a){"use strict";a.defineMode("pig",function(a,b){function c(a,b,c){return b.tokenize=c,c(a,b)}function d(a,b){for(var c,d=!1;c=a.next();){if("/"==c&&d){b.tokenize=f;break}d="*"==c}return"comment"}function e(a){return function(b,c){for(var d,e=!1,g=!1;null!=(d=b.next());){if(d==a&&!e){g=!0;break}e=!e&&"\\"==d}return(g||!e&&!j)&&(c.tokenize=f),"error"}}function f(a,b){var f=a.next();return'"'==f||"'"==f?c(a,b,e(f)):/[\[\]{}\(\),;\.]/.test(f)?null:/\d/.test(f)?(a.eatWhile(/[\w\.]/),"number"):"/"==f?a.eat("*")?c(a,b,d):(a.eatWhile(k),"operator"):"-"==f?a.eat("-")?(a.skipToEnd(),"comment"):(a.eatWhile(k),"operator"):k.test(f)?(a.eatWhile(k),"operator"):(a.eatWhile(/[\w\$_]/),g&&g.propertyIsEnumerable(a.current().toUpperCase())&&!a.eat(")")&&!a.eat(".")?"keyword":h&&h.propertyIsEnumerable(a.current().toUpperCase())?"variable-2":i&&i.propertyIsEnumerable(a.current().toUpperCase())?"variable-3":"variable")}var g=b.keywords,h=b.builtins,i=b.types,j=b.multiLineStrings,k=/[*+\-%<>=&?:\/!|]/;return{startState:function(){return{tokenize:f,startOfLine:!0}},token:function(a,b){if(a.eatSpace())return null;var c=b.tokenize(a,b);return c}}}),function(){function b(a){for(var b={},c=a.split(" "),d=0;d<c.length;++d)b[c[d]]=!0;return b}var c="ABS ACOS ARITY ASIN ATAN AVG BAGSIZE BINSTORAGE BLOOM BUILDBLOOM CBRT CEIL CONCAT COR COS COSH COUNT COUNT_STAR COV CONSTANTSIZE CUBEDIMENSIONS DIFF DISTINCT DOUBLEABS DOUBLEAVG DOUBLEBASE DOUBLEMAX DOUBLEMIN DOUBLEROUND DOUBLESUM EXP FLOOR FLOATABS FLOATAVG FLOATMAX FLOATMIN FLOATROUND FLOATSUM GENERICINVOKER INDEXOF INTABS INTAVG INTMAX INTMIN INTSUM INVOKEFORDOUBLE INVOKEFORFLOAT INVOKEFORINT INVOKEFORLONG INVOKEFORSTRING INVOKER ISEMPTY JSONLOADER JSONMETADATA JSONSTORAGE LAST_INDEX_OF LCFIRST LOG LOG10 LOWER LONGABS LONGAVG LONGMAX LONGMIN LONGSUM MAX MIN MAPSIZE MONITOREDUDF NONDETERMINISTIC OUTPUTSCHEMA  PIGSTORAGE PIGSTREAMING RANDOM REGEX_EXTRACT REGEX_EXTRACT_ALL REPLACE ROUND SIN SINH SIZE SQRT STRSPLIT SUBSTRING SUM STRINGCONCAT STRINGMAX STRINGMIN STRINGSIZE TAN TANH TOBAG TOKENIZE TOMAP TOP TOTUPLE TRIM TEXTLOADER TUPLESIZE UCFIRST UPPER UTF8STORAGECONVERTER ",d="VOID IMPORT RETURNS DEFINE LOAD FILTER FOREACH ORDER CUBE DISTINCT COGROUP JOIN CROSS UNION SPLIT INTO IF OTHERWISE ALL AS BY USING INNER OUTER ONSCHEMA PARALLEL PARTITION GROUP AND OR NOT GENERATE FLATTEN ASC DESC IS STREAM THROUGH STORE MAPREDUCE SHIP CACHE INPUT OUTPUT STDERROR STDIN STDOUT LIMIT SAMPLE LEFT RIGHT FULL EQ GT LT GTE LTE NEQ MATCHES TRUE FALSE DUMP",e="BOOLEAN INT LONG FLOAT DOUBLE CHARARRAY BYTEARRAY BAG TUPLE MAP ";a.defineMIME("text/x-pig",{name:"pig",builtins:b(c),keywords:b(d),types:b(e)}),a.registerHelper("hintWords","pig",(c+e+d).split(" "))}()});