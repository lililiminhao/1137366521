/**
 *  $("select").filterSelect();
 *  通过 $select.on("change", function() {console.log(this.value)}); 监听回调
 *  通过 $("select").val() 拿到最新的值
 *  通过 $("select").trigger("setEditSelectValue", 2); 设置选中的值为 2
 *  通过 $("select").trigger("optionChange", updateInputValue?) 触发 更新 option 的值
 */
$.fn.filterSelect = (function () {
    var isInit = false;
    function initCss() {
        isInit = true;
        var style = document.createElement("style");
        var csstext = '.model-select-box{display:inline-block;*display:inline; zoom:1;position:relative;-webkit-user-select:none; font-size:12px; color:#666;}\
                        \n.model-select-box ul, .model-select-box li{padding:0;margin:0;}\
                        \n.model-select-box .model-input{padding:0 22px 0 12px;min-width:160px; height:28px; font-size:12px;border: 1px solid #ced5e0; color: #666;}\
                        \n.model-select-box .model-input-ico{position:absolute;right:0;top:0;width:22px;height:100%;background:url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAwAAAAMCAYAAABWdVznAAAATElEQVQoU2NkIBEwkqiegTwNcXFx/4m1CW4DMZoWLVrEiOIkfJpAikGuwPADNk0wxVg1gASRNSErxqkBpgldMV4NuEKNvHggNg5A6gBo4xYmyyXcLAAAAABJRU5ErkJggg==) no-repeat 50% 50%;}\
                        \n.model-select-box .model-list{display:none;position:absolute;z-index:1;top:100%;left:0;right:0;max-width:100%;max-height:250px;overflow:auto;border-bottom:1px solid #ced5e0;}\
                        \n.model-select-box .model-list-item{cursor:default;padding:8px 5px;margin-top:-1px;background:#fff;border:1px solid #ced5e0;border-bottom:none;white-space:nowrap;overflow:hidden;text-overflow:ellipsis;}\
                        \n.model-select-box .model-list-item:hover{background:#ececec;}\
                        \n.model-select-box .model-list-item-active{background:#ececec;}';
        style = $("<style>" + csstext + "</style>")[0];
        var head = document.head || document.getElementsByTagName("head")[0];
        if (head.hasChildNodes()) {
            head.insertBefore(style, head.firstChild);
        } else {
            head.appendChild(style);
        }
        ;
    };
    return function (options) {
        !isInit && initCss();
        options = $.extend({
            blurClear: true,
            focusClear: true
        }, options || {});

        // 在 focus 和 blur 是还原为原始内容
        var isBlurClear = options.blurClear;
        var isFocusClear = options.focusClear;
        var $body = $("body");
        this.each(function (i, v) {
            var $sel = $(v),
                    oSelectBox = $('<div class="model-select-box"></div>');
            var mInput = $("<input type='text' class='model-input'/>");
            var $wrapper = $("<ul class='model-list'></ul>");
            oSelectBox = $sel.wrap(oSelectBox).hide().addClass("model-select").parent();
            oSelectBox.append(mInput).append("<span class='model-input-ico'></span>").append($wrapper);
            // 下拉层显示 + 隐藏
            var wrapper = {
                reset: function () {
                    this.get$list();
                    this.setIndex(this.$list.filter(".model-list-item-active"));
                    this.setActive(this.index);
                },
                show: function () {
                    $wrapper.show();
                    this.reset();
                },
                hide: function () {
                    $wrapper.hide();
                },
                next: function () {
                    return this.setActive(this.index + 1);
                },
                prev: function () {
                    return this.setActive(this.index - 1);
                },
                $list: null,
                get$list: function () {
                    this.$list = $wrapper.find(".model-list-item:visible");
                    return this.$list;
                },
                index: 0,
                $cur: null,
                setActive: function (i) {
                    // 找到第1个 li，并且赋值为 active
                    var $list = this.get$list(),
                            size = $list.size();
                    if (size <= 0) {
                        this.$cur = null;
                        return;
                    }
                    $list.filter(".model-list-item-active").removeClass("model-list-item-active");
                    if (i < 0) {
                        i = 0;
                    } else if (i >= size) {
                        i = size - 1;
                    }
                    this.index = i;
                    this.$cur = $list.eq(i).addClass("model-list-item-active");
                    this.fixScroll(this.$cur);
                    return this.$cur;
                },
                fixScroll: function ($elem) {
                    var height = $wrapper.height(),
                            top = $elem.position().top,
                            eHeight = $elem.outerHeight();
                    var scroll = $wrapper.scrollTop();
                    // 因为 li 的 实际　top，应该要加上 滚上 的距离
                    top += scroll;
                    if (scroll > top) {
                        $wrapper.scrollTop(top);
                    } else if (top + eHeight > scroll + height) {
                        $wrapper.scrollTop(top + eHeight - height);
                    }
                },
                setIndex: function ($li) {
                    if ($li && $li.size() > 0) {
                        this.index = this.get$list().index($li);
                        $li.addClass("model-list-item-active").siblings().removeClass("model-list-item-active");
                    } else {
                        this.index = 0;
                    }
                }
            };

            // input 的操作
            var operation = {
                clearInput: function () {
                    isFocusClear && (mInput.val(""));
                },
                // 文字更变了，更新 li
                textChange: function () {
                    var val = $.trim(mInput.val()).toLowerCase();
                    $wrapper.find(".model-list-item").each(function (i, v) {
                        var dataOp = $(this).attr("data-option");
                        if (dataOp.toLowerCase().indexOf(val) >= 0) {
                            $(v).show();
                        } else {
                            $(v).hide();
                        }
                        ;
                    });
                    wrapper.show();
                    if (!isBlurClear && (!isFocusClear || val)) {
                        mInput.attr("placeholder", val);
                    }
                },
                // 设值
                setValue: function ($li) {
                    if ($li && $li.size() > 0) {
                        var val = $.trim($li.html());
                        mInput.val(val).attr("placeholder", val);
                        wrapper.setIndex($li);
                        $sel.val($li.attr("data-option")).trigger("change");
                    } else {
                        mInput.val(function (i, v) {
                            return mInput.attr("placeholder");
                        });
                    }
                    ;
                    wrapper.hide();
                    this.offBody();
                },
                // 监听 body 被点击，如果不是点击到 $wrap or mInput，则隐藏
                onBody: function () {
                    var self = this;
                    setTimeout(function () {
                        self.offBody();
                        $body.on("click", self.bodyClick);
                    }, 10);
                },
                offBody: function () {
                    $body.off("click", this.bodyClick);
                },
                bodyClick: function (e) {
                    var target = e.target;
                    if (target != mInput[0] && target != $wrapper[0]) {
                        wrapper.hide();
                        operation.setValue();
                        operation.offBody();
                    }
                }
            };

            // 遍历 $sel 对象
            function resetOption(e, updateInputValue) {
                var html = "", val = "";
                $sel.find("option").each(function (i, v) {
                    if (v.selected && !val) {
                        val = $.trim(v.text);
                    }
                    ;
                    html += '<li class="model-list-item' + (v.selected ? " model-list-item-active" : "") + '" data-option="' + v.value + '">' + v.text + '</li>';
                });
                $wrapper.html(html);
                mInput.attr("placeholder", val);
                updateInputValue && mInput.val(val);
                wrapper.reset();
            }
            ;
            resetOption(null, true);
            $sel.on("optionChange", resetOption);
            $sel.on("setEditSelectValue", function (e, val) {
                var $all = $wrapper.find(".model-list-item"),
                        $item;
                for (var i = 0, max = $all.size(); i < max; i++) {
                    $item = $all.eq(i);
                    if ($item.attr("data-option") == val) {
                        operation.setValue($item);
                        return;
                    }
                }
            });
            // input 聚焦
            mInput.on("focus", function () {
                operation.clearInput();
                operation.textChange();
                operation.onBody();
            }).on("input propertychange", function (e) {
                operation.textChange();
            }).on("keydown", function (e) {
                // 上 38, 下 40， enter 13
                switch (e.keyCode) {
                    case 38:
                        wrapper.prev();
                        break;
                    case 40:
                        wrapper.next();
                        break;
                    case 13:
                        operation.setValue(wrapper.$cur);
                        break;
                }
            });

            oSelectBox.on("click", ".model-input-ico", function () {
                // 触发 focus 和 blur 事件 focus 是因为 input 有绑定
                // 而 blur，实际只是失去焦点而已，真正隐藏 wrapper 的是 $body 事件
                $wrapper.is(":visible") ? mInput.blur() : (operation.clearInput(), mInput.trigger("focus"));
            });
            // 选中
            $wrapper.on("click", ".model-list-item", function () {
                operation.setValue($(this));
                return false;
            });
            setTimeout(function () { //ie
                wrapper.hide();
            }, 1);
        });
        return this;
    };
})();
