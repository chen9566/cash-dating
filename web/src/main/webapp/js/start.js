/**
 * Created by CJ on 13/10/2016.
 */

Number.prototype.formatMoney = function (c, d, t) {
    var n = this,
        c = isNaN(c = Math.abs(c)) ? 2 : c,
        d = d == undefined ? "." : d,
        t = t == undefined ? "," : t,
        s = n < 0 ? "-" : "",
        i = String(parseInt(n = Math.abs(Number(n) || 0).toFixed(c))),
        j = (j = i.length) > 3 ? j % 3 : 0;
    return s + (j ? i.substr(0, j) + t : "") + i.substr(j).replace(/(\d{3})(?=\d)/g, "$1" + t) + (c ? d + Math.abs(n - i).toFixed(c).slice(2) : "");
};

$(function () {

    var amount = $('[name=amount]');
    var totalCost = $('#totalCost');
    var totalRate = $('#totalRate');
    var button = $('#_btn1');

    // button.click(function () {
    //     console.error(1);
    //     $('#orderForm').submit();
    // });

    var onChange = function () {
        var str = amount.val();
        if (!str || str.length == 0) {
            button.attr('disabled', 'disabled');
            button.addClass('black');
            button.removeClass('red');
            return;
        }
        var value = parseFloat(str);
        if (value <= 0) {
            button.attr('disabled', 'disabled');
            button.addClass('black');
            button.removeClass('red');
            return;
        }
        //最高可以刷的金额是 9,999,999.99

        button.removeAttr('disabled');
        button.removeClass('black');
        button.addClass('red');

        var money = value * (parseFloat(1) - $.bookRate);
        var myRate = value - money;
        // console.error(money, myRate);
        totalCost.text('到账金额 ' + money.formatMoney(2, '.', ','));
        totalRate.text('手续费 ' + myRate.formatMoney(2, '.', ','));
    };

    onChange();

    amount.change(onChange);
    amount.blur(onChange);
    amount.keyup(onChange);
});