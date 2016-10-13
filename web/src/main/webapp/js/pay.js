/**
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
});