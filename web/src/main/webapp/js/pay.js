/**
 * 付款时才选择支付方式
 * Created by CJ on 13/10/2016.
 */
$(function () {

    var uri;
    if ($.prototypesMode || !$.targetOrderId)
        uri = 'mock/false.json';
    else
        uri = $.uriPrefix + "/orderCompleted/" + $.targetOrderId;

    var success = function (data) {
        if (data) {
            window.location.reload();
        } else {
            //继续刷
            //等待一会儿
            setTimeout('$._loginCheck()', 2500);
        }
    };

    $._loginCheck = function () {
        $.ajax(uri, {
            method: 'get',
            dataType: 'json',
            success: success,
            error: function (msg) {
                console.log(msg.responseText);
            }
        });
    };

    setTimeout('$._loginCheck()', 2000);

    function currentBackground() {
        return $(".showbg");
    }

    $('.payChannel').click(function () {
        var name = $(this).attr('data-id');
        if (name == 'alipay') {
            document.title = '支付宝支付';
        } else {
            document.title = '微信支付';
        }
        // if (name == 'alipay') {
        //     window.location.href = $('#goAlipay').attr('href');
        //     return;
        // }
        var url;
        if ($.prototypesMode) {
            url = 'mock/qr.txt';
        } else {
            url = $.uriPrefix + "/orderQRURL";
        }
        $.ajax(url, {
            method: 'get',
            data: {
                id: $.targetOrderId,
                channel: name
            },
            error: function (res) {
                alert(res.responseText);
            },
            success: function (data) {
                $('#qrCode').attr('src', data);
                $(".am-show").removeClass('am-modal-active');
                currentBackground().remove();
                $('.' + name).show();
            }
        })
    });

    $(".am-show").addClass("am-modal-active");
    if (currentBackground().length > 0) {
        currentBackground().addClass("showbg-active");
    } else {
        $("body").append('<div class="showbg"></div>');
        currentBackground().click(function () {
            // 不可取消
            // $(".am-show").removeClass('am-modal-active');
            // currentBackground().remove();
        });
        currentBackground().addClass("showbg-active");
    }

    $("#alipayTip").click(function (event) {
        $(".helpshow1").css({"display": "block"});
    });
    $(".helpshow1").click(function (event) {
        $(".helpshow2").css({"display": "block"});
        $(".helpshow1").css({"display": "none"});
    });
    $(".helpshow2").click(function (event) {
        $(".helpshow2").css({"display": "none"});
    });
});