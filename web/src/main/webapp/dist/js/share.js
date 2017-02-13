/**
 * 项目中可共享的js代码,它依赖jquery环境
 * Created by CJ on 10/6/16.
 */
$(function () {

    var myAlert = $('#myAlert');
    if (myAlert.length > 0) {
        myAlert.hide();
        function releaseMessage() {
            myAlert.fadeOut();
        }

        var lastTimeOut;
        $.alert = function (str) {
            myAlert.text(str);
            myAlert.fadeIn();
            if (lastTimeOut)
                clearTimeout(lastTimeOut);
            lastTimeOut = setTimeout(releaseMessage, 2000);
        };
    }

    // 给所有 clickHref 添加一个点击行为
    $('.clickHref').click(function () {
        // console.log('click on ', this);
        var url = $(this).attr('href');
        if (url) {
            window.location.href = url;
        }
    });

    if ($.weixinEnabled) {
        // images/sharebg.png
        //
        $('body').append('<div class="modal fade share" id="weixinShareModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" style="background:url('
            + $.uriPrefix + '/images/sharebg.png)"><img src="' + $.uriPrefix + '/images/sharebgok.png"  /></div>');
        // document.write('<div class="modal fade share" id="weixinShareModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" style="background:url('
        //     + $.uriPrefix + '/images/sharebg.png)"><img src="' + $.uriPrefix + '/images/sharebgok.png"  /></div>');

        $('.weixinShare').click(function () {
            $('#weixinShareModal').modal();
        });
    }

    /**
     * 停止LoadingOverlay，如果是在原型模式则会自动延时调用
     * @param callback 在停止以后将执行的代码
     */
    $.StopLoadingOverlay = function (callback) {
        if ($.prototypesMode) {
            // Hide it after 3 seconds
            setTimeout(function () {
                $.LoadingOverlay("hide");
                callback();
            }, 1000);
        } else {
            $.LoadingOverlay("hide");
            callback();
        }
    };

    $.DynamicAdjustObjects = {};
    /**
     * 动态执行调整
     * @param name 业务名称
     * @param selector 相关UI的selector
     * @param adjust 负责执行调整的function,只接受jQuery对象
     */
    $.DynamicAdjust = function (name, selector, adjust) {
        var toListener;
        if (!$.DynamicAdjustObjects[name]) {
            toListener = $(selector);
        } else {
            toListener = $(selector).not($.DynamicAdjustObjects[name]);
        }

        $.DynamicAdjustObjects[name] = $(selector);

        adjust(toListener);
    };
    $.DynamicAdjustClickHref = function () {
        $.DynamicAdjust('ClickHref', '.clickHref', function (toListener) {
            toListener.click(function () {
                // console.log('click on ', this);
                var url = $(this).attr('href');
                if (url) {
                    window.location.href = url;
                }
            });
        });
    }
});
