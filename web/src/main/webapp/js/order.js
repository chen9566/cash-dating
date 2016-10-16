/**
 * Created by CJ on 17/10/2016.
 */

$(function () {
    var cardSelect = $('select[name=card]');
    var touchOrderForm = $('#touchOrderForm');
    var cardSubmit = $('.cardSubmit');

    cardSubmit.click(function () {
        $('[name=cardId]', touchOrderForm)
            .val(cardSelect.val());
        touchOrderForm.submit();
    });

});