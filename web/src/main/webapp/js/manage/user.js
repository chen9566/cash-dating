/**
 * Created by CJ on 19/10/2016.
 */

$(function () {
    var grantDialog = $('#grantDialog');
    var manageStatusInput = $('#manageStatusInput');

    manageStatusInput.selectmenu();

    grantDialog.dialog({
        autoOpen: false,
        show: {
            effect: "blind",
            duration: 1000
        },
        hide: {
            effect: "explode",
            duration: 1000
        }
    });

    manageStatusInput.selectmenu({
        select: function (event, ui) {
            if (grantDialog.dialog("isOpen")) {
                // console.log(ui.item.value);
                grantDialog.dialog('close');
                // 执行一个请求 来做这个事儿
                // refresh
                var success = function () {
                    table.bootstrapTable('refresh');
                    // alert('完成');
                };
                if ($.prototypesMode) {
                    success();
                } else {
                    $.ajax($.uriPrefix + '/manage/grant/' + grantDialog.id, {
                        method: 'put',
                        data: ui.item.value,
                        dataType: 'json',
                        error: function () {
                            alert('更改失败');
                        },
                        success: success
                    });
                }
            }

        }
    });

    var grant = $('#grant');
    var remove = $('#remove');
    var table = $('#table');

    var idSupplier = $.initTable(table, 'id', function () {
        return $(window).height() - $('.header').outerHeight(true);
    }, [
        {
            field: 'state',
            checkbox: true,
            align: 'center'
        }, {
            field: 'headImageUrl',
            align: 'center',
            title: '头像',
            width: '152px',
            formatter: function (url, row, index) {
                return '<img src="' + url + '" width="150px" height="150px"/>';
            }
        }, {
            title: '昵称',
            field: 'nickname',
            align: 'center',
            sortable: true
        }, {
            title: '合伙人',
            field: 'agent',
            align: 'center',
            sortable: true
        }, {
            title: '发展人',
            field: 'guide',
            align: 'center',
            sortable: true
        }, {
            title: '城市',
            field: 'city',
            align: 'center',
            sortable: true
        }, {
            title: '性别',
            field: 'gender',
            align: 'center',
            sortable: true
        }, {
            title: '手机',
            field: 'mobileNumber'
        }, {
            title: '管理权',
            field: 'manageStatus',
            align: 'center',
            sortable: true
        }
    ], remove.add(grant));

    remove.hide();

    if (!$.prototypesMode && !$.auths.grant)
        grant.hide();

    grant.click(function () {
        var ids = idSupplier();
        if (ids.length > 1) {
            alert('同时只可以修改一个人的管理权');
            return;
        }

        var id = ids[0];

        var row = table.bootstrapTable('getRowByUniqueId', id);

        // console.error(row, row.manageStatus);
        if (row.manageStatus) {
            $('option', manageStatusInput).each(function () {
                if ($(this).text() == row.manageStatus) {
                    manageStatusInput.val($(this).attr('value'));
                }
            });
        } else {
            manageStatusInput.val('');
        }
        manageStatusInput.selectmenu('refresh');

        grantDialog.id = id;

        grantDialog.dialog('open');
    })

});