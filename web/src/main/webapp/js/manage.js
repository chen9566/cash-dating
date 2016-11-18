/**
 * 管理后台专用
 * Created by CJ on 12/10/2016.
 */
$(function () {

    // header
    var header = $('.header');

    header.empty();
    header.append('<h1>款爷后台管理</h1>');
    var menus = '<ul class="feature">';
    var hrefUser = 'user.html';
    if (!$.prototypesMode)
        hrefUser = $.uriPrefix + '/manage/user';
    menus = menus + '<li><a href="' + hrefUser + '">用户</a></li>';

    var hrefLoanRequest = 'loanRequest.html';
    if (!$.prototypesMode)
        hrefLoanRequest = $.uriPrefix + '/manage/loanRequest';
    if ($.prototypesMode || $.auths.agent)
        menus = menus + '<li><a href="' + hrefLoanRequest + '">审批借款</a></li>';

    var hrefAgentRequest = 'agentRequest.html';
    if (!$.prototypesMode)
        hrefAgentRequest = $.uriPrefix + '/manage/agentRequest';
    if ($.prototypesMode || $.auths.agent)
        menus = menus + '<li><a href="' + hrefAgentRequest + '">合伙人请求</a></li>';
    var agentRequest = 'agent.html';
    if (!$.prototypesMode)
        agentRequest = $.uriPrefix + '/manage/agent';
    if ($.prototypesMode || $.auths.agent)
        menus = menus + '<li><a href="' + agentRequest + '">合伙人</a></li>';
    var notify = 'notify.html';
    if (!$.prototypesMode)
        notify = $.uriPrefix + '/manage/notify';
    if ($.prototypesMode || $.auths.edit)
        menus = menus + '<li><a href="' + notify + '">通知</a></li>';
    var bank = 'bank.html';
    if (!$.prototypesMode)
        bank = $.uriPrefix + '/manage/bank';
    if ($.prototypesMode || $.auths.edit)
        menus = menus + '<li><a href="' + bank + '">银行</a></li>';
    // if ($.prototypesMode || $.auths.finance)
    //     menus = menus + '<li>设置</li>';
    // if ($.prototypesMode || $.auths.order)
    //     menus = menus + '<li>订单</li>';
    // if ($.prototypesMode || $.auths.finance)
    //     menus = menus + '<li>汇总</li>';
    menus = menus + '</ul>';
    header.append(menus);
    header.append('<h2>' + document.title + '</h2>');

    // console.log(jQuery.fn.bootstrapTable.defaults);
    // table部分

    /**
     *
     * @param ele 要变成table的组件(jquery)
     * @param idField id字段
     * @param heightSupplier 获取高度的方法
     * @param columns 字段
     * @param buttons 操作按钮
     * @returns {getIdSelections} 可以获取选择状态的fun
     */
    $.initTable = function (ele, idField, heightSupplier, columns, buttons) {
        $.selections = [];

        function getIdSelections() {
            return $.map(ele.bootstrapTable('getSelections'), function (row) {
                return row[idField]
            });
        }

        ele.bootstrapTable({
            idField: idField,
            uniqueId: idField,
            pagination: true,
            sidePagination: 'server',
            responseHandler: function (res) {
                $.each(res.rows, function (i, row) {
                    row.state = $.inArray(row[idField], $.selections) !== -1;
                });
                return res;
            },
            pageList: [10, 25, 50],
            search: true,
            showPaginationSwitch: true,
            minimumCountColumns: 2,
            showColumns: true,
            showRefresh: true,
            showExport: true,
            columns: columns,
            height: heightSupplier()
        });

        ele.on('check.bs.table uncheck.bs.table ' +
            'check-all.bs.table uncheck-all.bs.table', function () {
            if (buttons)
                buttons.prop('disabled', !ele.bootstrapTable('getSelections').length);
            // save your data, here just save the current page
            $.selections = getIdSelections();
            // push or splice the selections if you want to save all data selections
        });

        $(window).resize(function () {
            ele.bootstrapTable('resetView', {
                height: heightSupplier()
            });
        });

        return getIdSelections;
    }
});