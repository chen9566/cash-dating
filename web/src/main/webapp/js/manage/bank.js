/**
 * Created by CJ on 22/10/2016.
 */

$(function () {
    $('.background-input').change(function () {
        var val = $(this).val();
        var target = $(this).closest('section');
        // console.log('haha', val, target);
        target.css('background', val);
    });

    $('.background-submit').click(function () {
        var target = $(this).closest('section');
        $.ajax($.uriPrefix + '/manage/bank/' + target.attr('data-id') + '/background', {
            method: 'put',
            contentType: 'text/plain',
            data: $('.background-input', target).val()
        });
    })
});
