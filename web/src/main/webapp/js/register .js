/**
 * Created by CJ on 19/10/2016.
 */

$(function () {

    var myAlert = $('#myAlert');
    myAlert.hide();

    $('.ok-request').click(function () {
        history.back();
    });

    var requestForm = $("#requestForm");

    function afterSuccess() {
        //
        $('#myModal').modal();
    }

    function releaseMessage() {
        myAlert.fadeOut();
    }

    var lastTimeOut;

    function message(str) {
        myAlert.text(str);
        myAlert.fadeIn();
        if (lastTimeOut)
            clearTimeout(lastTimeOut);

        lastTimeOut = setTimeout(releaseMessage, 2000);
    }

    function formCheck(mobile, yzm) {
        
        if (!mobile || mobile.length < 1) {
            message('手机号码不能为空');
            return false;
        }
        if (mobile.length != 11) {
            message('必须输入11位的手机号码');
            return false;
        }
		if (!yzm || yzm.length < 1) {
            message('不能为空');
            return false;
        }
        if (!name || name.length < 2) {
            message('至少得2个字');
            return false;
        }
        if (name.length != 4) {
            message('请输入验证吗');
            return false;
        }

        return true;
    }

    $('button[class~=btn-danger]').click(function () {
        var verificationValue = $('input[name=verificationCode]').val();
        var mobileValue = $('input[name=mobile]').val();

        if (formCheck(verificationValue, mobileValue)) {
            // 只有通过以后才继续
            if ($.prototypesMode) {
                afterSuccess();
            } else {
                $.ajax($.uriPrefix + '/agent', {
                    method: 'post',
                    contentType: 'application/json; charset-UTF-8',
                    data: JSON.stringify({
                        name: verificationValue,
                        mobile: mobileValue
                    }),
                    success: afterSuccess,
                    error: function (res) {
                        //                        console.log(arguments);
                        alert(res.responseText);
                    }
                });
            }
        }
        return false;
    });
});
