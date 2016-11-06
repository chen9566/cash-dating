/**
 * 项目中可共享的js代码,它依赖jquery环境
 * Created by CJ on 10/6/16.
 */
$(function () {

    // 给所有 clickHref 添加一个点击行为
    $('.clickHref').click(function () {
        // console.log('click on ', this);
        var url = $(this).attr('href');
        if (url) {
            window.location.href = url;
        }
    });

    if ($.weixinEnabled) {
        // images/sharebg.png
        //
        $('body').append('<div class="modal fade share" id="weixinShareModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" style="background:url('
            + $.uriPrefix + '/images/sharebg.png)"><img src="' + $.uriPrefix + '/images/sharebgok.png"  /></div>');
        // document.write('<div class="modal fade share" id="weixinShareModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" style="background:url('
        //     + $.uriPrefix + '/images/sharebg.png)"><img src="' + $.uriPrefix + '/images/sharebgok.png"  /></div>');

        $('.weixinShare').click(function () {
            $('#weixinShareModal').modal();
        });
    }
});
