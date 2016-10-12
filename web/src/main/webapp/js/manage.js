/**
 * 管理后台专用
 * Created by CJ on 12/10/2016.
 */
$(function () {

    // header
    var header = $('.header');

    header.empty();
    header.append('<h1>款爷</h1>');
    var menus = '<ul class="feature">';
    menus = menus + '<li>用户</li>';
    menus = menus + '<li>设置</li>';
    menus = menus + '<li>订单</li>';
    menus = menus + '<li>汇总</li>';
    menus = menus + '</ul>';
    header.append(menus);
    header.append('<h2>' + document.title + '</h2>');
    
});