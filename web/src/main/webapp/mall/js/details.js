/**
 * 商品详情页
 * Created by CJ on 01/04/2017.
 */

$(function () {
    "use strict";

    $(".close").click(function (event) {
        $(".show-t").css({"display": "none"});
        $(".wap-show").css({"display": "none"});
    });

    var _body = $('body');
    var urlCreate = _body.attr('data-url-create');
    var urlShow = _body.attr('data-url-show');
    var urlLogin = _body.attr('data-url-login');

    $(".buy a").click(function () {

        console.log("click me");
        function showPayQR(data) {
            console.log("order info :", data);
            $.currentOrderId = data;
            $('img[name=payImage]').attr('src', urlShow + "?id=" + data);
            $(".show-t").css({"display": "block"});
            $(".wap-show").css({"display": "block"});
        }

        if ($.currentOrderId) {
            showPayQR($.currentOrderId);
            return;
        }
        // 首先检测是否已登录
        // 构造购买url
        var url = urlCreate;
        var count = $('[name=count]').val();
        // count
        if ($.prototypesMode) {
            url = url + "?id=" + _body.attr('data-id') + "&count=" + count;
        } else {
            url = url + _body.attr('data-id') + "/" + count;
        }
        $.ajax(url, {
            method: 'get',
            dataType: 'text',
            success: showPayQR,
            error: function () {
                console.log('error json');
                location.href = urlLogin;
            }
        });
        // if ($('body').attr('data-login') == 'true') {
        //     $(".show-t").css({"display": "block"});
        //     $(".wap-show").css({"display": "block"});
        // } else {
        //     location.href = $('[name=loginLink]').attr('href');
        // }
    });
});