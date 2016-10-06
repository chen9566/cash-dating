/**
 * 原型展示,甚至会出现一些错误,比如地址
 * Created by CJ on 10/4/16.
 */
if ($) {
    $.prototypesMode = true;
    $.testMode = true;
    $.unitTestMode= false;
    $.uriPrefix = '';
    $.localXMLURI = 'dist/local.json';
    // if ($.testMode) {
    //     $.ajaxSetup({
    //         async: false
    //     });
    // }
}