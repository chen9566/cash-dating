/**
 * Created by CJ on 22/10/2016.
 */


$(function () {
    var listeners;

    $('.yjlist').jscroll({
        loadingHtml: '<img src="images/loading.gif" alt="Loading" /> 载入...',
        nextSelector: 'a.next:last',
        callback: function () {
            // 不重复
            var toListener;
            if (!listeners) {
                toListener = $('.levelSelect');
            } else {
                toListener = $('.levelSelect').not(listeners);
            }

            listeners = $('.levelSelect');

            toListener.change(function () {
                var ul = $(this).closest('ul');
                var newValue = $(this).val();
                if ($.prototypesMode) {
                    console.log('change to ' + newValue + ' for :' + ul.attr('data-id'));
                } else {
                    $.ajax($.uriPrefix + '/memberRate/' + ul.attr('data-id'), {
                        method: 'put',
                        contentType: 'text/plain',
                        data: newValue
                    });
                }
            })
        }
    });
    //
    // console.log($('.levelSelect'));
    //
    // $('.levelSelect').find('option').click(function () {
    //     console.error($(this));
    // });
});

