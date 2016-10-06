/**
 * Created by CJ on 10/4/16.
 */
if ($) {
    $.prototypesMode = false;
    $.testMode = _TestMode_;
    $.unitTestMode= _UnitTestMode_;
    $.uriPrefix = '_UriPrefix_';
    $.localXMLURI = $.uriPrefix + '/dist/local.json';
    if ($.testMode) {
        $.ajaxSetup({
            async: false
        });
    }
}