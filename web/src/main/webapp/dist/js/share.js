/**
 * 项目中可共享的js代码,它依赖jquery环境
 * Created by CJ on 10/6/16.
 */
$(function () {

    // 给所有 clickHref 添加一个点击行为
    $('.clickHref').click(function () {
        // console.log('click on ', this);
        var url = $(this).attr('href');
        if (url) {
            window.location.href = url;
        }
    });

    // 给省份 城市设计联动
    // 动作必须是一对儿的 就是2者必须拥有同一个属性data-province-city-group data-address-group
    // province-selector
    // city-selector
    // 如果出现多个目标才需要配对 一个的话 就算了 哈
    var provinceSelector = $('.province-selector');
    var citySelector = $('.city-selector');
    // 还有一个是State 这里用不到 就懒惰了
    if (provinceSelector.size() > 0) {
        // 得去下载文件了
        $.ajax($.localXMLURI, {
            method: 'get',
            data: 'json',
            error: function (res, code) {
                console.error(res.responseText, code);
            },
            success: function (data) {
                if ($.unitTestMode) {
                    console.error('Skip City auto select in UnitTest mode.');
                    return;
                }

                // console.error('start');
                $.locationDatabase = data;
                // console.error('got data',data);
                // console.log(data, $.locationDatabase);
                // 先给他们数据
                provinceSelector.empty();

                provinceSelector.change(function () {
                    var provinceName = $(this).val();
                    // console.log(this,provinceName);
                    citySelector.empty();
                    // 寻找合适的

                    for (var i = 0; i < $.locationDatabase.length; i++) {
                        var val = $.locationDatabase[i];
                        // console.error(val);
                        var name = val.name;
                        var code = val.code;
                        if (name == provinceName) {
                            for (var j = 0; j < val.cities.length; j++) {
                                var otherVal = val.cities[j];
                                var otherName = otherVal.name;
                                var otherCode = otherVal.code;
                                citySelector.append('<option value="' + otherName + '" code="' + otherCode + '">' + otherName + '</option>');
                            }
                        }
                    }
                });

                // console.error('start for ',$.locationDatabase.length);

                for (var i = 0; i < $.locationDatabase.length; i++) {
                    var val = $.locationDatabase[i];
                    // console.error(val);
                    var name = val.name;
                    var code = val.code;
                    provinceSelector.append('<option value="' + name + '" code="' + code + '">' + name + '</option>');
                }


            }
        });

    }
});
