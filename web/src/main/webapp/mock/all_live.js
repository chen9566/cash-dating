/**
 * Created by CJ on 10/4/16.
 */
if ($) {
    $.prototypesMode = false;
    $.testMode = _TestMode_;
    $.uriPrefix = '_UriPrefix_';
    $.localXMLURI = $.uriPrefix + '/dist/local.xml';
    if ($.testMode) {
        $.ajaxSetup({
            async: false
        });
    }
}