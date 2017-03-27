/**
 * 注册页面专用脚本
 * Created by CJ on 19/10/2016.
 */
$(function () {

    var myAlert = $('#myAlert');
    myAlert.hide();

    var mobileInput = $('input[name=mobile]');

    mobileInput.on('do-check', function () {
        $(this).attr('checkResult', mobileCheck($(this).val()));
    });
    // mobileInput.trigger('do-check');

    function releaseMessage() {
        myAlert.fadeOut();
    }

    var lastTimeOut;

    function message(str) {
        if (window.registerMessageAlert != null) {
            registerMessageAlert(str);
            return;
        }

        myAlert.text(str);
        myAlert.fadeIn();
        if (lastTimeOut)
            clearTimeout(lastTimeOut);

        lastTimeOut = setTimeout(releaseMessage, 2000);
    }

    function mobileCheck(mobile) {
        if (!mobile || mobile.length < 1) {
            message('手机号码不能为空');
            return false;
        }
        if (mobile.length != 11) {
            message('必须输入11位的手机号码');
            return false;
        }
        return true;
    }

    function formCheck(mobile, yzm) {
        if (!mobileCheck(mobile))
            return false;
        if (!yzm || yzm.length < 1) {
            message('验证码不能为空');
            return false;
        }
        if (yzm.length != 4) {
            message('请输入验证码');
            return false;
        }

        return true;
    }

    $('button[class~=btn-danger]').click(function () {
        var mobileValue = mobileInput.val();
        var verificationValue = $('input[name=verificationCode]').val();
        return formCheck(mobileValue, verificationValue);
    });

});
