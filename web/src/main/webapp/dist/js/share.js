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
});
