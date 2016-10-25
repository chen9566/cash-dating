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
            button.removeClass('redremove');
            return;
        }
        var value = parseFloat(str);
        if (value <= 0) {
            button.attr('disabled', 'disabled');
            button.addClass('black');
            button.removeClass('redremove');
            return;
        }
        //最高可以刷的金额是 9,999,999.99

        button.removeAttr('disabled');
        button.removeClass('black');
        button.addClass('redremove');

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

    // 选择卡
    var all = $('.all-cards');

    $('[name=cardChanger]').click(function () {

        // all-cards
        if (all.is(':visible')) {
            all.slideUp();
        } else
            all.slideDown();
        return false;
    });

    var inputCard = $('input[name=card]');

    var selectedCard = $('.selectedCard');

    if (selectedCard.size() > 0) {
        inputCard.val(selectedCard.attr('data-id'));
    }

    var cardChooser = $('[name=cardChooser]');
    cardChooser.click(function () {
        var choseCard = $(this).closest('.card');
        selectedCard.attr('data-id', choseCard.attr('data-id'));
        inputCard.val(choseCard.attr('data-id'));
        selectedCard.css('background', choseCard.css('background'));
        $('img', selectedCard).attr('src', $('img', choseCard).attr('src'));
        // 连续copy2个span
        var choseSpans = $('span', choseCard);
        var selectedSpans = $('span', selectedCard);
        // console.log(choseSpans,choseSpans.get(0).setContent());
        var choseSpan1 = choseSpans.filter('.banktxt');
        var choseSpan2 = choseSpans.not('.banktxt');
        var selectedSpan1 = selectedSpans.filter('.banktxt');
        var selectedSpan2 = selectedSpans.not('.banktxt');

        // console.log(selectedSpan1,selectedSpan2);
        // console.log(choseSpan1,choseSpan1);
        selectedSpan1.text(choseSpan1.text());
        selectedSpan2.text(choseSpan2.text());

        all.slideUp();
        return false;
    });
});