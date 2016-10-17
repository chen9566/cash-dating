/**
 * Created by CJ on 17/10/2016.
 */

$(function () {
    var cardSelect = $('select[name=card]');
    var touchOrderForm = $('#touchOrderForm');
    var orderIdInput = $('input[name=id]', touchOrderForm);
    var cardIdInput = $('input[name=cardId]', touchOrderForm);
    var cardSubmit = $('.cardSubmit');

    cardSubmit.click(function () {
        cardIdInput
            .val(cardSelect.val());
        touchOrderForm.submit();
    });

    $('.flow-retry').click(function () {
        var target = $(this).closest('div.orderFlow').attr('data-id');
        orderIdInput.val(target);
        cardIdInput.val('');
        touchOrderForm.submit();
        return false;
    });

    $('.flow-change').click(function () {
        var target = $(this).closest('div.orderFlow').attr('data-id');
        orderIdInput.val(target);
        $('#cardsContainer').modal();
        return false;
    });

});