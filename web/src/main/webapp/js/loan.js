/**
 * 借款用的
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

    var myRates = {
        // 1 2 3 18 24
        setup: {
            rate: 0.0144,
            manage: 0.0025,
            it: 0.0035,
            account: 0.001,
            period: 3
        },
        f1: {
            rate: 0.0144,
            manage: 0.0025,
            it: 0.0035,
            account: 0.001,
            period: 3
        },
        f2: {
            rate: 0.0144,
            manage: 0.0025,
            it: 0.0035,
            account: 0.001,
            period: 3
        },
        f3: {
            rate: 0.0144,
            manage: 0.0025,
            it: 0.0035,
            account: 0.001,
            period: 3
        },
        f6: {
            rate: 0.0142,
            manage: 0.0025,
            it: 0.0035,
            account: 0.001,
            period: 6
        },
        f9: {
            rate: 0.014,
            manage: 0.0025,
            it: 0.0035,
            account: 0.001,
            period: 9
        },
        f12: {
            rate: 0.0139,
            manage: 0.0025,
            it: 0.0035,
            account: 0,
            period: 12
        },
        f18: {
            rate: 0.0139,
            manage: 0.0025,
            it: 0.0035,
            account: 0,
            period: 12
        },
        f24: {
            rate: 0.0139,
            manage: 0.0025,
            it: 0.0035,
            account: 0,
            period: 12
        }
    };

    var amount = $('[name=amount]');
    var spanMonthLo = $('#spanMonthLo');
    var checked = $('input[type=checkbox]');
    var period = $('select[name=period]');
    var button = $('#_btn1');

    var onChange = function () {
        var str = amount.val();
        if (!str || str.length == 0) {
            button.attr('disabled', 'disabled');
            button.addClass('black');
            button.removeClass('redremove');
            spanMonthLo.text('¥0.00');
            return;
        }
        var value = parseFloat(str);
        if (isNaN(value)) {
            button.attr('disabled', 'disabled');
            button.addClass('black');
            button.removeClass('redremove');
            spanMonthLo.text('¥0.00');
            return;
        }
        if (value <= 0 || value < $.minLoanAmount || value > $.maxLoanAmount) {
            button.attr('disabled', 'disabled');
            button.addClass('black');
            button.removeClass('redremove');
            spanMonthLo.text('¥0.00');
            return;
        }
        //先计算利息
        var rate = myRates['f' + period.val()] || myRates.setup;
        var p1 = value / rate.period;
        var p2 = value * rate.rate;
        var p3 = value * rate.manage;
        var p4 = value * rate.it;
        var p5 = value * rate.account;
        var bf = Math.max(Math.ceil(value / 10000) * 25, 30) / rate.period;

        spanMonthLo.text('¥' + (p1 + p2 + p3 + p4 + p5 + bf).formatMoney(2, '.', ','));

        if (checked.is(":checked")) {
            button.removeAttr('disabled');
            button.removeClass('black');
            button.addClass('redremove');
        } else {
            button.attr('disabled', 'disabled');
            button.addClass('black');
            button.removeClass('redremove');
        }

    };

    onChange();

    amount.change(onChange);
    amount.blur(onChange);
    amount.keyup(onChange);

    checked.change(onChange);

    period.change(onChange);
    period.blur(onChange);
    period.keyup(onChange);
});