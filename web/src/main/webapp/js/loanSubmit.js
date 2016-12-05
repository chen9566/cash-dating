/**
 * 用于提交借款详细数据的脚本
 * Created by CJ on 10/11/2016.
 */

$(function () {
    var moreRegion = $('#moreRegion');
    var moreCover = $('#moreCover');

    moreRegion.hide();

    $('a', moreCover).click(function () {
        moreCover.slideUp();
        moreRegion.slideDown();
    });

    var loanForm = $('#loanForm');
    var button = $('[type=submit]', loanForm);
    loanForm.validate({
        messages: {
            name: {
                required: '姓名必须输入',
                minlength: '姓名必须多于2个字符'
            },
            number: {
                required: '身份证必须输入',
                minlength: '请输入正确的身份证号码'
            }
        },
        errorPlacement: function (error, element) {
            $.alert(error.text());
        }
        // ,
        // success:function (what) {
        //     console.log('success',arguments);
        // }
        // showErrors: function (errorMap, errorList) {
        //     console.log(errorMap, errorList);
        // }
    });
    var inputs = $('input', loanForm);

    function inputChanges() {
        if (loanForm.valid()) {
            button.removeAttr('disabled');
            button.removeClass('black');
            button.addClass('redremove');
        } else {
            button.attr('disabled', 'disabled');
            button.addClass('black');
            button.removeClass('redremove');
        }
    }

    inputs.change(inputChanges);
    inputs.blur(inputChanges);
    inputs.keyup(inputChanges);


    var provinceSelector = $('.province-selector');
    var citySelector = $('.city-selector');

    if (provinceSelector.size() > 0) {
        // 得去下载文件了
        var targetUri;
        if ($.prototypesMode)
            targetUri = 'mock/provinces.json';
        else
            targetUri = $.uriPrefix + '/provinceList';

        $.ajax(targetUri, {
            method: 'get',
            dataType: 'json',
            error: function (res, code) {
                console.error(res.responseText, code);
            },
            success: function (data) {
                $.locationDatabase = data;
                provinceSelector.empty();

                var provinceChanged = function () {
                    var provinceName = provinceSelector.val();
                    citySelector.empty();

                    for (var i = 0; i < $.locationDatabase.length; i++) {
                        var val = $.locationDatabase[i];
                        // console.error(val);
                        var name = val.name;
                        var code = val.id;
                        if (code == provinceName) {
                            for (var j = 0; j < val.cityList.length; j++) {
                                var otherVal = val.cityList[j];
                                var otherName = otherVal.name;
                                var otherCode = otherVal.id;
                                citySelector.append('<option value="' + otherCode + '" code="' + otherCode + '">' + otherName + '</option>');
                            }
                        }
                    }

                };

                provinceSelector.change(provinceChanged);

                // console.error('start for ',$.locationDatabase.length);

                for (var i = 0; i < $.locationDatabase.length; i++) {
                    var val = $.locationDatabase[i];
                    // console.error(val);
                    var name = val.name;
                    var code = val.id;
                    provinceSelector.append('<option value="' + code + '" code="' + code + '">' + name + '</option>');
                }

                provinceChanged(provinceSelector);
            }
        });

    }

});