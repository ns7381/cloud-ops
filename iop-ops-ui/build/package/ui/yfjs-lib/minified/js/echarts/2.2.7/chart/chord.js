define("echarts2/chart/chord",["require","./base","zrender2/shape/Text","zrender2/shape/Line","zrender2/shape/Sector","../util/shape/Ribbon","../util/shape/Icon","zrender2/shape/BezierCurve","../config","../util/ecData","zrender2/tool/util","zrender2/tool/vector","../data/Graph","../layout/Chord","../chart"],function(a){"use strict";function b(a,b,d,e,f){c.call(this,a,b,d,e,f),this.scaleLineLength=4,this.scaleUnitAngle=4,this.refresh(e)}var c=a("./base"),d=a("zrender2/shape/Text"),e=a("zrender2/shape/Line"),f=a("zrender2/shape/Sector"),g=a("../util/shape/Ribbon"),h=a("../util/shape/Icon"),i=a("zrender2/shape/BezierCurve"),j=a("../config");j.chord={zlevel:0,z:2,clickable:!0,radius:["65%","75%"],center:["50%","50%"],padding:2,sort:"none",sortSub:"none",startAngle:90,clockWise:!0,ribbonType:!0,minRadius:10,maxRadius:20,symbol:"circle",showScale:!1,showScaleText:!1,itemStyle:{normal:{borderWidth:0,borderColor:"#000",label:{show:!0,rotate:!1,distance:5},chordStyle:{width:1,color:"black",borderWidth:1,borderColor:"#999",opacity:.5}},emphasis:{borderWidth:0,borderColor:"#000",chordStyle:{width:1,color:"black",borderWidth:1,borderColor:"#999"}}}};var k=a("../util/ecData"),l=a("zrender2/tool/util"),m=a("zrender2/tool/vector"),n=a("../data/Graph"),o=a("../layout/Chord");return b.prototype={type:j.CHART_TYPE_CHORD,_init:function(){var a=this.series;this.selectedMap={};for(var b={},c={},d=0,e=a.length;d<e;d++)if(a[d].type===this.type){var f=this.isSelected(a[d].name);this.selectedMap[a[d].name]=f,f&&this.buildMark(d),this.reformOption(a[d]),b[a[d].name]=a[d]}for(var d=0,e=a.length;d<e;d++)if(a[d].type===this.type)if(a[d].insertToSerie){var g=b[a[d].insertToSerie];a[d]._referenceSerie=g}else c[a[d].name]=[a[d]];for(var d=0,e=a.length;d<e;d++)if(a[d].type===this.type&&a[d].insertToSerie){for(var h=a[d]._referenceSerie;h&&h._referenceSerie;)h=h._referenceSerie;c[h.name]&&this.selectedMap[a[d].name]&&c[h.name].push(a[d])}for(var i in c)this._buildChords(c[i]);this.addShapeList()},_getNodeCategory:function(a,b){return a.categories&&a.categories[b.category||0]},_getNodeQueryTarget:function(a,b){var c=this._getNodeCategory(a,b);return[b,c,a]},_getEdgeQueryTarget:function(a,b,c){return c=c||"normal",[b.itemStyle&&b.itemStyle[c],a.itemStyle[c].chordStyle]},_buildChords:function(a){for(var b=[],c=a[0],d=function(a){return a.layout.size>0},e=function(a){return function(b){return a.getEdge(b.node2,b.node1)}},f=0;f<a.length;f++){var g=a[f];if(this.selectedMap[g.name]){var h;g.matrix?h=this._getSerieGraphFromDataMatrix(g,c):g.links&&(h=this._getSerieGraphFromNodeLinks(g,c)),h.filterNode(d,this),g.ribbonType&&h.filterEdge(e(h)),b.push(h),h.__serie=g}}if(b.length){var i=b[0];if(!c.ribbonType){var j=c.minRadius,k=c.maxRadius,l=1/0,m=-(1/0);i.eachNode(function(a){m=Math.max(a.layout.size,m),l=Math.min(a.layout.size,l)});var n=(k-j)/(m-l);i.eachNode(function(a){var b=this._getNodeQueryTarget(c,a),d=this.query(b,"symbolSize");m===l?a.layout.size=d||l:a.layout.size=d||(a.layout.size-l)*n+j},this)}var p=new o;p.clockWise=c.clockWise,p.startAngle=c.startAngle*Math.PI/180,p.clockWise||(p.startAngle=-p.startAngle),p.padding=c.padding*Math.PI/180,p.sort=c.sort,p.sortSub=c.sortSub,p.directed=c.ribbonType,p.run(b);var q=this.query(c,"itemStyle.normal.label.show");if(c.ribbonType){this._buildSectors(c,0,i,c,b),q&&this._buildLabels(c,0,i,c,b);for(var f=0,r=0;f<a.length;f++)this.selectedMap[a[f].name]&&this._buildRibbons(a,f,b[r++],c);c.showScale&&this._buildScales(c,0,i)}else{this._buildNodeIcons(c,0,i,c,b),q&&this._buildLabels(c,0,i,c,b);for(var f=0,r=0;f<a.length;f++)this.selectedMap[a[f].name]&&this._buildEdgeCurves(a,f,b[r++],c,i)}this._initHoverHandler(a,b)}},_getSerieGraphFromDataMatrix:function(a,b){for(var c=[],d=0,e=[],f=0;f<a.matrix.length;f++)e[f]=a.matrix[f].slice();for(var g=a.data||a.nodes,f=0;f<g.length;f++){var h={},i=g[f];i.rawIndex=f;for(var j in i)"name"===j?h.id=i.name:h[j]=i[j];var k=this._getNodeCategory(b,i),l=k?k.name:i.name;if(this.selectedMap[l]=this.isSelected(l),this.selectedMap[l])c.push(h),d++;else{e.splice(d,1);for(var m=0;m<e.length;m++)e[m].splice(d,1)}}var o=n.fromMatrix(c,e,!0);return o.eachNode(function(a,b){a.layout={size:a.data.outValue},a.rawIndex=a.data.rawIndex}),o.eachEdge(function(a){a.layout={weight:a.data.weight}}),o},_getSerieGraphFromNodeLinks:function(a,b){for(var c=new n(!0),d=a.data||a.nodes,e=0,f=d.length;e<f;e++){var g=d[e];if(g&&!g.ignore){var h=this._getNodeCategory(b,g),i=h?h.name:g.name;if(this.selectedMap[i]=this.isSelected(i),this.selectedMap[i]){var j=c.addNode(g.name,g);j.rawIndex=e}}}for(var e=0,f=a.links.length;e<f;e++){var k=a.links[e],l=k.source,m=k.target;"number"==typeof l&&(l=d[l],l&&(l=l.name)),"number"==typeof m&&(m=d[m],m&&(m=m.name));var o=c.addEdge(l,m,k);o&&(o.rawIndex=e)}return c.eachNode(function(a){var c=a.data.value;if(null==c)if(c=0,b.ribbonType)for(var d=0;d<a.outEdges.length;d++)c+=a.outEdges[d].data.weight||0;else for(var d=0;d<a.edges.length;d++)c+=a.edges[d].data.weight||0;a.layout={size:c}}),c.eachEdge(function(a){a.layout={weight:null==a.data.weight?1:a.data.weight}}),c},_initHoverHandler:function(a,b){var c=a[0],d=b[0],e=this;d.eachNode(function(a){a.shape.onmouseover=function(){d.eachNode(function(a){a.shape.style.opacity=.1,a.labelShape&&(a.labelShape.style.opacity=.1,a.labelShape.modSelf()),a.shape.modSelf()});for(var c=0;c<b.length;c++)for(var f=0;f<b[c].edges.length;f++){var g=b[c].edges[f],h=e._getEdgeQueryTarget(b[c].__serie,g.data);g.shape.style.opacity=.1*e.deepQuery(h,"opacity"),g.shape.modSelf()}a.shape.style.opacity=1,a.labelShape&&(a.labelShape.style.opacity=1);for(var c=0;c<b.length;c++){var i=b[c].getNodeById(a.id);if(i)for(var f=0;f<i.outEdges.length;f++){var g=i.outEdges[f],h=e._getEdgeQueryTarget(b[c].__serie,g.data);g.shape.style.opacity=e.deepQuery(h,"opacity");var j=b[0].getNodeById(g.node2.id);j&&(j.shape&&(j.shape.style.opacity=1),j.labelShape&&(j.labelShape.style.opacity=1))}}e.zr.refreshNextFrame()},a.shape.onmouseout=function(){d.eachNode(function(a){a.shape.style.opacity=1,a.labelShape&&(a.labelShape.style.opacity=1,a.labelShape.modSelf()),a.shape.modSelf()});for(var a=0;a<b.length;a++)for(var f=0;f<b[a].edges.length;f++){var g=b[a].edges[f],h=[g.data,c];g.shape.style.opacity=e.deepQuery(h,"itemStyle.normal.chordStyle.opacity"),g.shape.modSelf()}e.zr.refreshNextFrame()}})},_buildSectors:function(a,b,c,d){var e=this.parseCenter(this.zr,d.center),g=this.parseRadius(this.zr,d.radius),h=d.clockWise,i=h?1:-1;c.eachNode(function(c){var j=this._getNodeCategory(d,c.data),l=j?this.getColor(j.name):this.getColor(c.id),m=c.layout.startAngle/Math.PI*180*i,n=c.layout.endAngle/Math.PI*180*i,o=new f({zlevel:a.zlevel,z:a.z,style:{x:e[0],y:e[1],r0:g[0],r:g[1],startAngle:m,endAngle:n,brushType:"fill",opacity:1,color:l,clockWise:h},clickable:d.clickable,highlightStyle:{brushType:"fill"}});o.style.lineWidth=this.deepQuery([c.data,d],"itemStyle.normal.borderWidth"),o.highlightStyle.lineWidth=this.deepQuery([c.data,d],"itemStyle.emphasis.borderWidth"),o.style.strokeColor=this.deepQuery([c.data,d],"itemStyle.normal.borderColor"),o.highlightStyle.strokeColor=this.deepQuery([c.data,d],"itemStyle.emphasis.borderColor"),o.style.lineWidth>0&&(o.style.brushType="both"),o.highlightStyle.lineWidth>0&&(o.highlightStyle.brushType="both"),k.pack(o,a,b,c.data,c.rawIndex,c.id,c.category),this.shapeList.push(o),c.shape=o},this)},_buildNodeIcons:function(a,b,c,d){var e=this.parseCenter(this.zr,d.center),f=this.parseRadius(this.zr,d.radius),g=f[1];c.eachNode(function(c){var f=c.layout.startAngle,i=c.layout.endAngle,j=(f+i)/2,l=g*Math.cos(j),m=g*Math.sin(j),n=this._getNodeQueryTarget(d,c.data),o=this._getNodeCategory(d,c.data),p=this.deepQuery(n,"itemStyle.normal.color");p||(p=o?this.getColor(o.name):this.getColor(c.id));var q=new h({zlevel:a.zlevel,z:a.z+1,style:{x:-c.layout.size,y:-c.layout.size,width:2*c.layout.size,height:2*c.layout.size,iconType:this.deepQuery(n,"symbol"),color:p,brushType:"both",lineWidth:this.deepQuery(n,"itemStyle.normal.borderWidth"),strokeColor:this.deepQuery(n,"itemStyle.normal.borderColor")},highlightStyle:{color:this.deepQuery(n,"itemStyle.emphasis.color"),lineWidth:this.deepQuery(n,"itemStyle.emphasis.borderWidth"),strokeColor:this.deepQuery(n,"itemStyle.emphasis.borderColor")},clickable:d.clickable,position:[l+e[0],m+e[1]]});k.pack(q,a,b,c.data,c.rawIndex,c.id,c.category),this.shapeList.push(q),c.shape=q},this)},_buildLabels:function(a,b,c,e){var f=this.query(e,"itemStyle.normal.label.rotate"),g=this.query(e,"itemStyle.normal.label.distance"),h=this.parseCenter(this.zr,e.center),i=this.parseRadius(this.zr,e.radius),j=e.clockWise,k=j?1:-1;c.eachNode(function(b){var c=b.layout.startAngle/Math.PI*180*k,j=b.layout.endAngle/Math.PI*180*k,l=(c*-k+j*-k)/2;l%=360,l<0&&(l+=360);var n=l<=90||l>=270;l=l*Math.PI/180;var o=[Math.cos(l),-Math.sin(l)],p=0;p=e.ribbonType?e.showScaleText?35+g:g:g+b.layout.size;var q=m.scale([],o,i[1]+p);m.add(q,q,h);var r={zlevel:a.zlevel,z:a.z+1,hoverable:!1,style:{text:null==b.data.label?b.id:b.data.label,textAlign:n?"left":"right"}};f?(r.rotation=n?l:Math.PI+l,n?r.style.x=i[1]+p:r.style.x=-i[1]-p,r.style.y=0,r.position=h.slice()):(r.style.x=q[0],r.style.y=q[1]),r.style.color=this.deepQuery([b.data,e],"itemStyle.normal.label.textStyle.color")||"#000000",r.style.textFont=this.getFont(this.deepQuery([b.data,e],"itemStyle.normal.label.textStyle")),r=new d(r),this.shapeList.push(r),b.labelShape=r},this)},_buildRibbons:function(a,b,c,d){var e=a[b],f=this.parseCenter(this.zr,d.center),h=this.parseRadius(this.zr,d.radius);c.eachEdge(function(i,j){var l,m=c.getEdge(i.node2,i.node1);if(m&&!i.shape){if(m.shape)return void(i.shape=m.shape);var n=i.layout.startAngle/Math.PI*180,o=i.layout.endAngle/Math.PI*180,p=m.layout.startAngle/Math.PI*180,q=m.layout.endAngle/Math.PI*180;l=1===a.length?i.layout.weight<=m.layout.weight?this.getColor(i.node1.id):this.getColor(i.node2.id):this.getColor(e.name);var r,s,t=this._getEdgeQueryTarget(e,i.data),u=this._getEdgeQueryTarget(e,i.data,"emphasis"),v=new g({zlevel:e.zlevel,z:e.z,style:{x:f[0],y:f[1],r:h[0],source0:n,source1:o,target0:p,target1:q,brushType:"both",opacity:this.deepQuery(t,"opacity"),color:l,lineWidth:this.deepQuery(t,"borderWidth"),strokeColor:this.deepQuery(t,"borderColor"),clockWise:d.clockWise},clickable:d.clickable,highlightStyle:{brushType:"both",opacity:this.deepQuery(u,"opacity"),lineWidth:this.deepQuery(u,"borderWidth"),strokeColor:this.deepQuery(u,"borderColor")}});i.layout.weight<=m.layout.weight?(r=m.node1,s=m.node2):(r=i.node1,s=i.node2),k.pack(v,e,b,i.data,null==i.rawIndex?j:i.rawIndex,i.data.name||r.id+"-"+s.id,r.id,s.id),this.shapeList.push(v),i.shape=v}},this)},_buildEdgeCurves:function(a,b,c,d,e){var f=a[b],g=this.parseCenter(this.zr,d.center);c.eachEdge(function(a,c){var d=e.getNodeById(a.node1.id),h=e.getNodeById(a.node2.id),j=d.shape,l=h.shape,m=this._getEdgeQueryTarget(f,a.data),n=this._getEdgeQueryTarget(f,a.data,"emphasis"),o=new i({zlevel:f.zlevel,z:f.z,style:{xStart:j.position[0],yStart:j.position[1],xEnd:l.position[0],yEnd:l.position[1],cpX1:g[0],cpY1:g[1],lineWidth:this.deepQuery(m,"width"),strokeColor:this.deepQuery(m,"color"),opacity:this.deepQuery(m,"opacity")},highlightStyle:{lineWidth:this.deepQuery(n,"width"),strokeColor:this.deepQuery(n,"color"),opacity:this.deepQuery(n,"opacity")}});k.pack(o,f,b,a.data,null==a.rawIndex?c:a.rawIndex,a.data.name||a.node1.id+"-"+a.node2.id,a.node1.id,a.node2.id),this.shapeList.push(o),a.shape=o},this)},_buildScales:function(a,b,c){var f,g,h=a.clockWise,i=this.parseCenter(this.zr,a.center),j=this.parseRadius(this.zr,a.radius),k=h?1:-1,l=0,n=-(1/0);a.showScaleText&&(c.eachNode(function(a){var b=a.data.value;b>n&&(n=b),l+=b}),n>1e10?(f="b",g=1e-9):n>1e7?(f="m",g=1e-6):n>1e4?(f="k",g=.001):(f="",g=1));var o=l/(360-a.padding);c.eachNode(function(b){for(var c=b.layout.startAngle/Math.PI*180,l=b.layout.endAngle/Math.PI*180,n=c;;){if(h&&n>l||!h&&n<l)break;var p=n/180*Math.PI,q=[Math.cos(p),Math.sin(p)],r=m.scale([],q,j[1]+1);m.add(r,r,i);var s=m.scale([],q,j[1]+this.scaleLineLength);m.add(s,s,i);var t=new e({zlevel:a.zlevel,z:a.z-1,hoverable:!1,style:{xStart:r[0],yStart:r[1],xEnd:s[0],yEnd:s[1],lineCap:"round",brushType:"stroke",strokeColor:"#666",lineWidth:1}});this.shapeList.push(t),n+=k*this.scaleUnitAngle}if(a.showScaleText)for(var u=c,v=5*o*this.scaleUnitAngle,w=0;;){if(h&&u>l||!h&&u<l)break;var p=u;p%=360,p<0&&(p+=360);var x=p<=90||p>=270,y=new d({zlevel:a.zlevel,z:a.z-1,hoverable:!1,style:{x:x?j[1]+this.scaleLineLength+4:-j[1]-this.scaleLineLength-4,y:0,text:Math.round(10*w)/10+f,textAlign:x?"left":"right"},position:i.slice(),rotation:x?[-p/180*Math.PI,0,0]:[-(p+180)/180*Math.PI,0,0]});this.shapeList.push(y),w+=v*g,u+=k*this.scaleUnitAngle*5}},this)},refresh:function(a){if(a&&(this.option=a,this.series=a.series),this.legend=this.component.legend,this.legend)this.getColor=function(a){return this.legend.getColor(a)},this.isSelected=function(a){return this.legend.isSelected(a)};else{var b={},c=0;this.getColor=function(a){return b[a]?b[a]:(b[a]||(b[a]=this.zr.getColor(c++)),b[a])},this.isSelected=function(){return!0}}this.backupShapeList(),this._init()},reformOption:function(a){var b=l.merge;a=b(b(a||{},this.ecTheme.chord),j.chord),a.itemStyle.normal.label.textStyle=this.getTextStyle(a.itemStyle.normal.label.textStyle),this.z=a.z,this.zlevel=a.zlevel}},l.inherits(b,c),a("../chart").define("chord",b),b}),define("echarts2/util/shape/Ribbon",["require","zrender2/shape/Base","zrender2/shape/util/PathProxy","zrender2/tool/util","zrender2/tool/area"],function(a){function b(a){c.call(this,a),this._pathProxy=new d}var c=a("zrender2/shape/Base"),d=a("zrender2/shape/util/PathProxy"),e=a("zrender2/tool/util"),f=a("zrender2/tool/area");return b.prototype={type:"ribbon",buildPath:function(a,b){var c=b.clockWise||!1,d=this._pathProxy;d.begin(a);var e=b.x,f=b.y,g=b.r,h=b.source0/180*Math.PI,i=b.source1/180*Math.PI,j=b.target0/180*Math.PI,k=b.target1/180*Math.PI,l=e+Math.cos(h)*g,m=f+Math.sin(h)*g,n=e+Math.cos(i)*g,o=f+Math.sin(i)*g,p=e+Math.cos(j)*g,q=f+Math.sin(j)*g,r=e+Math.cos(k)*g,s=f+Math.sin(k)*g;d.moveTo(l,m),d.arc(e,f,b.r,h,i,!c),d.bezierCurveTo(.7*(e-n)+n,.7*(f-o)+o,.7*(e-p)+p,.7*(f-q)+q,p,q),b.source0===b.target0&&b.source1===b.target1||(d.arc(e,f,b.r,j,k,!c),d.bezierCurveTo(.7*(e-r)+r,.7*(f-s)+s,.7*(e-l)+l,.7*(f-m)+m,l,m))},getRect:function(a){return a.__rect?a.__rect:(this._pathProxy.isEmpty()||this.buildPath(null,a),this._pathProxy.fastBoundingRect())},isCover:function(a,b){var c=this.getRect(this.style);if(a>=c.x&&a<=c.x+c.width&&b>=c.y&&b<=c.y+c.height)return f.isInsidePath(this._pathProxy.pathCommands,0,"fill",a,b)}},e.inherits(b,c),b}),define("echarts2/layout/Chord",["require"],function(a){var b=function(a){a=a||{},this.sort=a.sort||null,this.sortSub=a.sortSub||null,this.padding=.05,this.startAngle=a.startAngle||0,this.clockWise=null!=a.clockWise&&a.clockWise,this.center=a.center||[0,0],this.directed=!0};b.prototype.run=function(a){a instanceof Array||(a=[a]);var b=a.length;if(b){for(var e=a[0],f=e.nodes.length,g=[],h=0,i=0;i<f;i++){var j=e.nodes[i],k={size:0,subGroups:[],node:j};g.push(k);for(var l=0,m=0;m<a.length;m++){var n=a[m],o=n.getNodeById(j.id);if(o){k.size+=o.layout.size;for(var p=this.directed?o.outEdges:o.edges,q=0;q<p.length;q++){var r=p[q],s=r.layout.weight;k.subGroups.push({weight:s,edge:r,graph:n}),l+=s}}}h+=k.size;for(var t=k.size/l,q=0;q<k.subGroups.length;q++)k.subGroups[q].weight*=t;"ascending"===this.sortSub?k.subGroups.sort(c):"descending"===this.sort&&(k.subGroups.sort(c),k.subGroups.reverse())}"ascending"===this.sort?g.sort(d):"descending"===this.sort&&(g.sort(d),g.reverse());for(var t=(2*Math.PI-this.padding*f)/h,u=this.startAngle,v=this.clockWise?1:-1,i=0;i<f;i++){var k=g[i];k.node.layout.startAngle=u,k.node.layout.endAngle=u+v*k.size*t,k.node.layout.subGroups=[];for(var q=0;q<k.subGroups.length;q++){var w=k.subGroups[q];w.edge.layout.startAngle=u,u+=v*w.weight*t,w.edge.layout.endAngle=u}u=k.node.layout.endAngle+v*this.padding}}};var c=function(a,b){return a.weight-b.weight},d=function(a,b){return a.size-b.size};return b});