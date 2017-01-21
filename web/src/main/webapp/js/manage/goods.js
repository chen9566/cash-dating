/**
 * Created by CJ on 21/01/2017.
 */

$(function () {
    var table = $('#table');
    var stockAddButton = $('#stockAddButton');
    var enableButton = $('#enableButton');
    var disableButton = $('#disableButton');
    var stockAddRegion = $('#stockAddRegion');
    var goodsId = $('[name=goodsId]', stockAddRegion);
    var expiredDate = $('[name=expiredDate]', stockAddRegion);

    expiredDate.datepicker();
    expiredDate.datepicker("option", "dateFormat", 'yy-m-d');

    var idSupplier = $.initTable(table, 'id', function () {
        return $(window).height() - $('.header').outerHeight(true);
    }, [
        {
            field: 'state',
            checkbox: true,
            align: 'center'
        }, {
            title: '类型',
            field: 'type',
            align: 'center'
//            }, {
//                title: '&nbsp;',
//                field: 'imageUrl',
//                align: 'center',
//                formatter: $.Manage.headImageRenderer
        }, {
            title: '已上架',
            field: 'enable',
            formatter: $.Manage.enableRenderer,
            align: 'center'
        }, {
            title: '品牌',
            field: 'brand',
            align: 'center',
            sortable: true
        }, {
            title: '名称',
            field: 'name',
            align: 'center',
            sortable: true
        }, {
            title: '描述',
            field: 'description',
            align: 'center',
            sortable: true
        }, {
            title: '售价',
            field: 'price',
            align: 'right',
            sortable: true
        }, {
            title: '原价',
            field: 'subPrice',
            sortable: true
        }, {
            title: '库存',
            field: 'stock',
            sortable: true
        }
    ], stockAddButton.add(enableButton).add(disableButton), function (buttons, currentSelections) {
        stockAddButton.prop('disabled', currentSelections[0].type != '卡券类');
        enableButton.prop('disabled', currentSelections[0].enable);
        disableButton.prop('disabled', !currentSelections[0].enable);
    });

    stockAddButton.click(function () {
        var data = table.bootstrapTable('getRowByUniqueId', idSupplier());
        goodsId.val(data.id);
        $('#stockAddRegionTitle').text(data.name);
        stockAddRegion.modal();
    });

    $('.enableChange').click(function () {
        // 设置enable状态
        $.ajax($.uriPrefix + '/manage/goods/' + idSupplier() + '/enable', {
            method: 'put',
            contentType: 'text/plain',
            data: $(this).attr('data-value'),
            success: function () {
                table.bootstrapTable('refresh');
            }, error: function () {
                table.bootstrapTable('refresh');
            }
        });
    });

});