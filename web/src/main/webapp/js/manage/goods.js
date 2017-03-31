/**
 * Created by CJ on 21/01/2017.
 */

$(function () {
    var table = $('#table');
    var stockAddButton = $('#stockAddButton');
    var enableButton = $('#enableButton');
    var disableButton = $('#disableButton');
    var stockAddRegion = $('#stockAddRegion');
    var imageButton = $('#imageButton');
    var goodsImagesRegion = $('#goodsImagesRegion');
    var editButton = $('#editButton');
    var goodsEditRegion = $('#goodsEditRegion');
    // var goodsId = $('[name=goodsId]', stockAddRegion);
    var goodsId = $('input[name=goodsId]');
    var expiredDate = $('[name=expiredDate]', stockAddRegion);
    var addGoodsButton = $('#addGoodsButton');
    var addGoodsRegion = $('#addGoodsRegion');

    function initImageRegion() {
        var manualUploader = new qq.FineUploader({
            element: document.getElementById('items-uploader'),
            template: 'qq-template-manual-trigger',
            request: {
                // endpoint: $.galleryItemsUrl,
                // method: top.$.prototypesMode ? 'GET' : 'POST'
                method: 'POST'
            },
            thumbnails: {
                placeholders: {
                    waitingPath: '//cdn.kuanyes.com/jquery-fine-uploader/5.10.0/placeholders/waiting-generic.png',
                    notAvailablePath: '//cdn.kuanyes.com/jquery-fine-uploader/5.10.0/placeholders/not_available-generic.png'
                }
            },
            validation: {
                allowedExtensions: ['jpeg', 'jpg', 'png', 'bmp'],
                itemLimit: 20,
                sizeLimit: 3 * 1024 * 1024
            },
            session: {
                // endpoint: $.galleryItemsUrl
            },
            deleteFile: {
                enabled: true,
                // endpoint: $.galleryItemsUrl
            },
            autoUpload: false
        });

        $('.btn-primary', goodsImagesRegion).click(function () {
            manualUploader.uploadStoredFiles();
        });

        // qq(document.getElementById("trigger-upload")).attach("click", function () {
        //     console.log('upload');
        //     manualUploader.uploadStoredFiles();
        // });

        imageButton.click(function () {
            // 找到id 自然就找到图片的url
            var id = idSupplier();
            var url = $.prototypesMode ? '../../mock/images.json' : imageButton.attr('src') + id;
            manualUploader.reset();
            $.ajax(url, {
                dataType: 'json',
                success: function (data) {
                    manualUploader.addInitialFiles(data);
                    manualUploader.setEndpoint(url);
                    manualUploader.setDeleteFileEndpoint(url);
                    // console.log('update to ', url);
                    goodsImagesRegion.modal();
                }
            });

        });
    }

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
            title: '简述',
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
        }, {
            title: '爆款',
            field: 'hot',
            formatter: $.Manage.enableRenderer,
            align: 'center'
        }, {
            title: '新品',
            field: 'freshly',
            formatter: $.Manage.enableRenderer,
            align: 'center'
        }, {
            title: '特卖',
            field: 'special',
            formatter: $.Manage.enableRenderer,
            align: 'center'
        }
    ], stockAddButton.add(enableButton).add(disableButton).add(editButton).add(imageButton), function (buttons, currentSelections) {
        stockAddButton.prop('disabled', currentSelections[0].type != '卡券类');
        // enableButton.prop('disabled', currentSelections[0].enable);
        // disableButton.prop('disabled', !currentSelections[0].enable);
    });

    initImageRegion();

    stockAddButton.click(function () {
        var data = table.bootstrapTable('getRowByUniqueId', idSupplier());
        goodsId.val(data.id);
        $('#stockAddRegionTitle').text(data.name);
        stockAddRegion.modal();
    });

    editButton.click(function () {
        var data = table.bootstrapTable('getRowByUniqueId', idSupplier());
        goodsId.val(data.id);
        $('#goodsEditRegionTitle').text(data.name);
        // 填好表单
        // 弄一个属性组 然后自己就可以处理了
        var properties = ['name', 'brand', 'description', 'price', 'subPrice', 'richDetail', 'weight', 'hot'
            , 'freshly', 'special'];

        function updateDomValue(name, value, showIt) {
            var target = $('[name=' + name + ']', goodsEditRegion);

            if (target.attr('type') == 'checkbox') {
                target.prop('checked', value);
            } else
                target.val(value);

            if (showIt) {
                target.closest('.specialGoodsProperty').show();
            }
        }

        $.each(properties, function (index, value) {
            updateDomValue(value, data[value]);
        });
        // 隐藏所有的特殊属性
        $('.specialGoodsProperty', goodsEditRegion).hide();
        // 打开特殊属性地址
        var url = data.morePropertiesUrl;
        if (url) {
            $.ajax(url, {
                method: 'get',
                dataType: 'json',
                success: function (moreData) {
                    $.each(moreData, function (propertyName, propertyValue) {
                        updateDomValue(propertyName, propertyValue, true);
                    });
                    goodsEditRegion.modal();
                }
            });
        } else
            goodsEditRegion.modal();
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

    addGoodsButton.click(function () {
        addGoodsRegion.modal();
    })

});