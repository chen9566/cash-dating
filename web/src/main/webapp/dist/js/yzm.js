// reformat by CJ
$(function () {
    var mobileInput = $('input[name=mobile]');
    //获取短信验证码
    // mobileInput.trigger('do-check');

    var validCode = true;
    $("#btn-mask").click(function () {
        var time = 50;
        var code = $(this);
        if (validCode) {
            mobileInput.trigger('do-check');
            if (!mobileInput.attr('checkResult') || mobileInput.attr('checkResult') == 'false') {
                return;
            }
            // if (!$.prototypesMode) {
            // 原型发个球
            // console.error('mobile', mobileInput.val());
            $.ajax($.uriPrefix + '/verificationCode?type=register&mobile=' + mobileInput.val(), {
                method: 'put',
                error: function (response) {
                    alert('发送失败');
                    // console.error('send failed', response.responseText);
                }
            });
            // }

            validCode = false;
            code.addClass("yzm-n2");
            var t = setInterval(function () {
                time--;
                code.html(time + "秒");
                if (time == 0) {
                    clearInterval(t);
                    code.html("重新获取");
                    validCode = true;
                    code.removeClass("msgs1");
                }
            }, 1000)
        }
    })
});