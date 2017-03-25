/**
 * 商城可公用的脚本
 * Created by CJ on 26/03/2017.
 */

$(function () {
    // 自动搜索
    // 目前我们搜索属性存在有 类目，搜索关键字，排序方式
    // 页面类型存在有 搜索结果页，首页
    var body = $('body');

    function search() {
        // 根据 body里的参数 形成一个搜索请求
        var search = body.attr('data-search');
        var category = body.attr('data-category');
        var hot = body.attr('data-hot');
        var special = body.attr('data-special');
        var order = body.attr('data-order');
        var url = $.searchUri + "?";
        if (search && search.length > 0)
            url = url + "search=" + search + '&';
        if (category && category.length > 0)
            url = url + "category=" + category + '&';
        if (hot && hot.length > 0)
            url = url + "hot=" + hot + '&';
        if (special && special.length > 0)
            url = url + "special=" + special + '&';
        if (order && order.length > 0)
            url = url + "order=" + order + '&';

        location.href = url;
    }

    $(".stxt").click(function () {
        body.attr('data-search', $('input[name=search]').val());
        search();
    });

    $('.newicon ul li a').click(function () {
        body.attr('data-category', $(this).attr('class'));
        search();
    });

});
