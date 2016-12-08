/**
 * 项目贷款合同都需引入该JS
 * 并且在body加入id 以表示它的合同模板编号
 * Created by CJ on 08/12/2016.
 */
$(function () {

    var checkbox = $('input[class~=chk_1]');
    var button = $('input[type=submit]');

    checkbox.change(function () {
        if (checkbox.is(":checked")) {
            button.removeAttr('disabled');
            button.removeClass('black');
            button.addClass('redremove');
        } else {
            button.attr('disabled', 'disabled');
            button.addClass('black');
            button.removeClass('redremove');
        }
    });

    button.click(function () {
        var referrer = document.referrer;
        console.log(referrer);
        // 如果当前有fragment则先行移除
        var index = referrer.indexOf('#');
        if (index > 0) {
            referrer = referrer.substring(0, index);
        }
        console.log(referrer);
        window.location.href = referrer + '#' + $('body').attr('id');
        return false;
    });
});
