/**
 * Created by CJ on 19/10/2016.
 */

$(function () {

    var myAlert = $('#myAlert');
    myAlert.hide();

    $('button[class~=btn-primary]').click(function () {
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

    function formCheck(name, mobile) {
        if (!name || name.length < 1) {
            message('姓名不能为空');
            return false;
        }
        if (!name || name.length < 2) {
            message('姓名至少得2个字');
            return false;
        }
        if (name.length > 20) {
            message('姓名太长了');
            return false;
        }

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

    $('button[class~=btn-danger]').click(function () {
        var nameValue = $('input[name=name]').val();
        var mobileValue = $('input[name=mobile]').val();

        if (formCheck(nameValue, mobileValue)) {
            // 只有通过以后才继续
            if ($.prototypesMode) {
                afterSuccess();
            } else {
                $.ajax($.uriPrefix + '/agent', {
                    method: 'post',
                    contentType: 'application/json; charset-UTF-8',
                    data: JSON.stringify({
                        name: nameValue,
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
