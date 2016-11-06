/**
 * 支行选择
 * Created by CJ on 07/11/2016.
 */
var getUrlParameter = function getUrlParameter(sParam) {
    var sPageURL = decodeURIComponent(window.location.search.substring(1)),
        sURLVariables = sPageURL.split('&'),
        sParameterName,
        i;

    for (i = 0; i < sURLVariables.length; i++) {
        sParameterName = sURLVariables[i].split('=');

        if (sParameterName[0] === sParam) {
            return sParameterName[1] === undefined ? true : sParameterName[1];
        }
    }
};

var cityId = getUrlParameter('cityId');
var bankId = getUrlParameter('bankId');

if (sessionStorage.getItem('branch-cityId') == cityId && sessionStorage.getItem('branch-bankId') == bankId) {
    // 如果跟上次打开的是一样的
} else {
    sessionStorage.setItem('branch-cityId', cityId);
    sessionStorage.setItem('branch-bankId', bankId);
    sessionStorage.removeItem('branch-id');
    sessionStorage.removeItem('branch-name');
}

$(function () {
    // 所有li
    var storageId = sessionStorage.getItem('branch-id');
    var storageName = sessionStorage.getItem('branch-name');
    if (storageId != null && storageName != null) {
        $('li[data-id=' + storageId + ']').addClass('curr');
    }

    var all = $('li');
    all.click(function () {
        var me = $(this);
        sessionStorage.setItem('branch-id', me.attr('data-id'));
        sessionStorage.setItem('branch-name', me.text());
        history.back();
    });

    function searchChange() {
        var me = $(this);
        var str = me.val();

        all.each(function (index, data) {
            if (-1 == $(data).text().indexOf(str)) {
                $(data).hide();
            } else {
                $(data).show();
            }
        });
    }

    $('[name=search]').keyup(searchChange);
});

