/**
 * Created by CJ on 19/10/2016.
 */

$(function () {
    var table = $('#table');

    var idSupplier = $.initTable(table, 'id', function () {
        return $(window).height() - $('.header').outerHeight(true);
    }, [
        {
            field: 'state',
            checkbox: true,
            align: 'center'
        }, {
            title: '城市',
            field: 'city',
            align: 'center',
            sortable: true
        }, {
            title: '昵称',
            field: 'nickname',
            align: 'center',
            sortable: true
        }, {
            title: '手机',
            field: 'mobileNumber'
        }, {
            title: '加入时间',
            field: 'joinTime',
            sortable: true
        }
    ], null);


});