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
    var inputAmount = $('#amount');
    var inputTermDays = $('#termDays');
    var inputYearRate = $('#yearRate');
    var form = $('form', approveDialog);
    form.validate();

    $('input[name=filterType]').change(function () {
        var target = $(this).val();
        table.bootstrapTable('refresh', {
            url: table.attr('data-url' + target)
        });
    });

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
        var commentEle;
        if (approveDialog.dialog("isOpen")) {
            dialog = approveDialog;
            type = 'approve';
            commentEle = commentInput;
        } else if (declineDialog.dialog("isOpen")) {
            dialog = declineDialog;
            type = 'decline';
            commentEle = declineCommentInput;
        }

        if (!dialog)
            return false;

        if (type == 'approve') {
            // 检查3个输入栏
            if (!inputAmount.valid()) {
                return false;
            }
            if (!inputTermDays.valid()) {
                return false;
            }
            if (!inputYearRate.valid()) {
                return false;
            }
        }

        dialog.dialog('close');

        var success = function () {
            table.bootstrapTable('refresh');
        };
        if (false && $.prototypesMode) {
            success();
        } else {
            $.ajax($.uriPrefix + '/manage/data/projectLoan', {
                method: 'put',
                data: JSON.stringify({
                    comment: commentEle.val(),
                    amount: inputAmount.val(),
                    termDays: inputTermDays.val(),
                    yearRate: inputYearRate.val(),
                    targets: dialog.ids,
                    type: type
                }),
                contentType: 'application/json;charset=UTF-8',
                error: function (res) {
                    alert(res.responseText);
                },
                success: success
            });
        }

        return false;
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
            field: 'city.city.name',
            align: 'center',
            sortable: true
        }, {
            title: '姓名',
            field: 'name',
            align: 'center',
            sortable: true
        }, {
            title: '申请金额',
            field: 'applyAmount',
            align: 'right',
            sortable: true
        }, {
            title: '借款期限(天)',
            field: 'applyTermDays',
            align: 'right',
            sortable: true
        }, {
            title: '工作单位',
            field: 'employer',
            sortable: true
        }, {
            title: '家庭收入(万)',
            field: 'familyIncome',
            align: 'right',
            sortable: true
        }, {
            title: '个人收入(万)',
            field: 'personalIncome',
            align: 'right',
            sortable: true
        }, {
            title: '年龄',
            field: 'age',
            sortable: true
        }, {
            title: '申请时间',
            field: 'createdTime',
            sortable: true
        }, {
            title: '状态',
            field: 'processStatus',
            align: 'center',
            sortable: true
        }, {
            title: '备注',
            field: 'comment',
            align: 'center',
            sortable: true
        }
    ], approveButton.add(declineButton));

    // if (!$.prototypesMode && !$.auths.agent)
    //     approveButton.add(declineButton).hide();

    approveButton.click(function () {
        approveDialog.ids = idSupplier();
        // 恢复默认数据 再加上当前的数据
        // 获取第一个数据
        var data = table.bootstrapTable('getRowByUniqueId', approveDialog.ids[0]);
        // console.log(data);
        inputAmount.val(data.applyAmount);
        inputTermDays.val(inputTermDays.attr('data-origin'));
        inputYearRate.val(inputYearRate.attr('data-origin'));
        commentInput.val('');
        approveDialog.dialog('open');
    });
    declineButton.click(function () {
        declineDialog.ids = idSupplier();
        declineDialog.dialog('open');
    });

});