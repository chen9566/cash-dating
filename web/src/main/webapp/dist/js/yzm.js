// reformat by CJ
$(function () {
    var mobileInput = $('input[type=tel]');
    //获取短信验证码
    var validCode = true;
    $("#btn-mask").click(function () {
        var time = 30;
        var code = $(this);
        if (validCode) {
            if (!$.yzmValidator.element(mobileInput)) {
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
            code.addClass("msgs1");
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