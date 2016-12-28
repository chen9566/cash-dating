/**
 * Created by CJ on 19/10/2016.
 */

$(function () {
    $('#imageRegion').accordion();
    var declineButton = $('#declineButton');
    var approveButton = $('#approveButton');
    var messageButton = $('#messageButton');
    var detailButton = $('#detailButton');
    var table = $('#table');

    var approveDialog = $('#approveDialog');
    var declineDialog = $('#declineDialog');
    var detailDialog = $('#detailDialog');
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
        table.bootstrapTable('refreshOptions', {
            url: table.attr('data-url' + target)
        });
        table.bootstrapTable('refresh', {
            url: table.attr('data-url' + target)
        });
    });

    approveDialog.add(declineDialog).add(detailDialog).dialog({
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
            title: '身份证',
            field: 'number',
            align: 'center',
            sortable: true
        }, {
            title: '手机',
            field: 'mobileNumber',
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
            sortable: true,
            formatter: function (status, row) {
                //此处分析状态
                if (status == '等待供应商') {
                    $.ajax($.uriPrefix + '/manage/data/projectLoan/query/' + row.id, {
                        method: 'put',
                        error: function (res) {
                            alert(res.responseText)
                        }
                    })
                }
                return status;
            }
        }, {
            title: '签章',
            field: 'signed',
            align: 'center',
            formatter: $.Manage.enableRenderer
        }, {
            title: '备注',
            field: 'comment',
            align: 'center',
            sortable: true
        }
    ], approveButton.add(declineButton).add(messageButton).add(detailButton));

    // if (!$.prototypesMode && !$.auths.agent)
    //     approveButton.add(declineButton).hide();

    var termChanged = function () {
        // console.log('changed', inputTermDays.val());
        var rate = $.yearRates[inputTermDays.val()];
        // console.log(rate);
        inputYearRate.val(rate);
    };

    approveButton.click(function () {
        approveDialog.ids = idSupplier();
        // 恢复默认数据 再加上当前的数据
        // 获取第一个数据
        var data = table.bootstrapTable('getRowByUniqueId', approveDialog.ids[0]);
        // console.log(data);
        inputAmount.val(data.applyAmount);
        commentInput.val('');

        var url;
        if ($.prototypesMode) {
            // 随机获取1-3
            var targetNum;
            var randomX = Math.random() * 3;
            if (randomX > 2) {
                targetNum = 3;
            } else if (randomX > 1) {
                targetNum = 2;
            } else
                targetNum = 1;
            url = '../mock/projectLoanRequestNextTerm' + targetNum + ".json";
        } else {
            url = $.uriPrefix + '/manage/projectLoanRequestNextTerm';
        }
        $.ajax(url, {
            method: 'get',
            async: false,
            dataType: 'json',
            success: function (re) {
                // console.log(re);
                inputTermDays.val(re);
                termChanged();
                // inputYearRate.val(inputYearRate.attr('data-origin'));
                approveDialog.dialog('open');
            }
        });

    });
    declineButton.click(function () {
        declineDialog.ids = idSupplier();
        declineDialog.dialog('open');
    });

    detailButton.click(function () {
        var data = table.bootstrapTable('getRowByUniqueId', idSupplier()[0]);
        $('.detailName', detailDialog).text(data.name);
        $('.detailAmount', detailDialog).text(data.applyAmount);
        $('.detailTermDays', detailDialog).text(data.applyTermDays);
        $('.detailTime', detailDialog).text(data.createdTime);
        $('[name=frontID]', detailDialog).attr('src', data.frontIDUrl);
        $('[name=backID]', detailDialog).attr('src', data.backIDUrl);
        $('[name=handID]', detailDialog).attr('src', data.handIDUrl);
        detailDialog.dialog("option", "width", 500);
        detailDialog.dialog('open');
    });

    // 签章和其他
    messageButton.click(function () {
        $.each(idSupplier(), function (index, id) {
            var data = table.bootstrapTable('getRowByUniqueId', id);
            // console.log(data);
            if (data.processStatus == '签章和其他') {
                $.ajax($.uriPrefix + '/manage/data/projectLoan/sendNotify/' + data.id, {
                    method: 'put',
                    success: function () {
                        alert('通知已发送');
                    }
                    // error: function (res) {
                    //     alert(res.responseText)
                    // }
                })
            }
        })
    });

    inputTermDays.change(termChanged);

});