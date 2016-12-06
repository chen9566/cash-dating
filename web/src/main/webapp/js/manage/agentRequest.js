/**
 * Created by CJ on 19/10/2016.
 */

$(function () {
    var declineButton = $('#declineButton');
    var approveButton = $('#approveButton');
    var table = $('#table');

    var approveDialog = $('#approveDialog');
    var declineDialog = $('#declineDialog');
    var declineCommentInput = $('#declineCommentInput');
    var commentInput = $('#commentInput');
    var okButton = $('button[class~=btn-ok]');

    approveDialog.add(declineDialog).dialog({
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

    okButton.click(function () {
        var dialog;
        var type;
        if (approveDialog.dialog("isOpen")) {
            dialog = approveDialog;
            type = 'approve';
        } else if (declineDialog.dialog("isOpen")) {
            dialog = declineDialog;
            type = 'decline';
        }

        if (!dialog)
            return;

        dialog.dialog('close');

        var success = function () {
            table.bootstrapTable('refresh');
        };
        if (false && $.prototypesMode) {
            success();
        } else {
            $.ajax($.uriPrefix + '/manage/data/agentRequest/pending', {
                method: 'put',
                data: JSON.stringify({
                    comment: $('input', dialog).val(),
                    targets: dialog.ids,
                    type: type
                }),
                contentType: 'application/json;charset=UTF-8',
                error: function () {
                    alert('失败');
                },
                success: success
            });
        }

    });

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
            width: '60px',
            formatter: $.Manage.headImageRenderer
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
            title: '姓名',
            field: 'name',
            align: 'center',
            sortable: true
        }, {
            title: '手机',
            field: 'mobileNumber'
        }, {
            title: '申请时间',
            field: 'createdTime',
            sortable: true
        }, {
            title: '状态',
            field: 'processStatus',
            align: 'center',
            sortable: true
        }
    ], approveButton.add(declineButton));

    if (!$.prototypesMode && !$.auths.agent)
        approveButton.add(declineButton).hide();

    approveButton.click(function () {
        approveDialog.ids = idSupplier();
        approveDialog.dialog('open');
    });
    declineButton.click(function () {
        declineDialog.ids = idSupplier();
        declineDialog.dialog('open');
    });

});