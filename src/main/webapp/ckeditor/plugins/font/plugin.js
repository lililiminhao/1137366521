﻿/*
 Copyright (c) 2003-2013, CKSource - Frederico Knabben. All rights reserved.
 For licensing, see LICENSE.md or http://ckeditor.com/license
 */
        (function() {
            function g(a, b, g, h, j, n, k, o) {
                for (var p = a.config, l = new CKEDITOR.style(k), c = j.split(";"), j = [], f = {}, d = 0; d < c.length; d++) {
                    var e = c[d];
                    if (e) {
                        var e = e.split("/"), m = {}, i = c[d] = e[0];
                        m[g] = j[d] = e[1] || i;
                        f[i] = new CKEDITOR.style(k, m);
                        f[i]._.definition.name = i;
                    } else
                        c.splice(d--, 1);
                }
                a.ui.addRichCombo(b, {label: h.label, title: h.panelTitle, toolbar: "styles," + o, allowedContent: l, requiredContent: l, panel: {css: [CKEDITOR.skin.getPath("editor")].concat(p.contentsCss), multiSelect: !1, attributes: {"aria-label": h.panelTitle}},
                    init: function() {
                        this.startGroup(h.panelTitle);
                        for (var a = 0; a < c.length; a++) {
                            var b = c[a];
                            this.add(b, f[b].buildPreview(), b);
                        }
                    }, onClick: function(b) {
                        a.focus();
                        a.fire("saveSnapshot");
                        var c = f[b];
                        a[this.getValue() == b ? "removeStyle" : "applyStyle"](c);
                        a.fire("saveSnapshot");
                    }, onRender: function() {
                        a.on("selectionChange", function(a) {
                            for (var b = this.getValue(), a = a.data.path.elements, c = 0, d; c < a.length; c++) {
                                d = a[c];
                                for (var e in f)
                                    if (f[e].checkElementMatch(d, !0)) {
                                        e != b && this.setValue(e);
                                        return;
                                    }
                            }
                            this.setValue("", n);
                        }, this);
                    }});
            }
            CKEDITOR.plugins.add("font", {requires: "richcombo", lang: "af,ar,bg,bn,bs,ca,cs,cy,da,de,el,en,en-au,en-ca,en-gb,eo,es,et,eu,fa,fi,fo,fr,fr-ca,gl,gu,he,hi,hr,hu,id,is,it,ja,ka,km,ko,ku,lt,lv,mk,mn,ms,nb,nl,no,pl,pt,pt-br,ro,ru,si,sk,sl,sq,sr,sr-latn,sv,th,tr,ug,uk,vi,zh,zh-cn", init: function(a) {
                    var b = a.config;
                    g(a, "Font", "family", a.lang.font, b.font_names, b.font_defaultLabel, b.font_style, 30);
                    g(a, "FontSize", "size", a.lang.font.fontSize, b.fontSize_sizes, b.fontSize_defaultLabel, b.fontSize_style, 40)
                }})
        })();
CKEDITOR.config.font_names = "Arial/Arial, Helvetica, sans-serif;Comic Sans MS/Comic Sans MS, cursive;Courier New/Courier New, Courier, monospace;Georgia/Georgia, serif;Lucida Sans Unicode/Lucida Sans Unicode, Lucida Grande, sans-serif;Tahoma/Tahoma, Geneva, sans-serif;Times New Roman/Times New Roman, Times, serif;Trebuchet MS/Trebuchet MS, Helvetica, sans-serif;Verdana/Verdana, Geneva, sans-serif";
CKEDITOR.config.font_defaultLabel = "";
CKEDITOR.config.font_style = {element: "span", styles: {"font-family": "#(family)"}, overrides: [{element: "font", attributes: {face: null}}]};
CKEDITOR.config.fontSize_sizes = "8/8px;9/9px;10/10px;11/11px;12/12px;14/14px;16/16px;18/18px;20/20px;22/22px;24/24px;26/26px;28/28px;36/36px;48/48px;72/72px";
CKEDITOR.config.fontSize_defaultLabel = "";
CKEDITOR.config.fontSize_style = {element: "span", styles: {"font-size": "#(size)"}, overrides: [{element: "font", attributes: {size: null}}]};