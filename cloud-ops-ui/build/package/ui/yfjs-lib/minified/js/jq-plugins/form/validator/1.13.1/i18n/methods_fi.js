!function(a){"function"==typeof define&&define.amd?define(["jquery","../jquery.validate"],a):a(jQuery)}(function(a){a.extend(a.validator.methods,{date:function(a,b){return this.optional(b)||/^\d{1,2}\.\d{1,2}\.\d{4}$/.test(a)},number:function(a,b){return this.optional(b)||/^-?(?:\d+)(?:,\d+)?$/.test(a)}})});