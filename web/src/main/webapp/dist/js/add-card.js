/**
 * 银行卡
 * Created by CJ on 11/10/2016.
 */

$(function () {
    // 给省份 城市设计联动
    // 动作必须是一对儿的 就是2者必须拥有同一个属性data-province-city-group data-address-group
    // province-selector
    // city-selector
    // 如果出现多个目标才需要配对 一个的话 就算了 哈
    var provinceSelector = $('.province-selector');
    var citySelector = $('.city-selector');
    var bankSelector = $('.bank-selector');
    var subBranchSelector = $('.subBranch-selector');
    // 还有一个是State 这里用不到 就懒惰了
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
                // if ($.unitTestMode) {
                //     console.error('Skip City auto select in UnitTest mode.');
                //     return;
                // }

                // console.error('start');
                $.locationDatabase = data;
                // console.error('got data',data);
                // console.log(data, $.locationDatabase);
                // 先给他们数据
                provinceSelector.empty();

                var provinceChanged = function () {
                    var provinceName = provinceSelector.val();
                    // console.log(this,provinceName);
                    citySelector.empty();
                    // 寻找合适的

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

    if (bankSelector.size() > 0 && citySelector.size() > 0 && subBranchSelector.size() > 0) {
        var anyChanged = function () {
            var city = citySelector.val();
            var bank = bankSelector.val();

            if (!city || !bank) {
                subBranchSelector.empty();
                return;
            }
            if (city.length == 0 || bank.length == 0) {
                subBranchSelector.empty();
                return;
            }

            var targetUri;
            if ($.prototypesMode)
                targetUri = 'mock/branches.json';
            else
                targetUri = $.uriPrefix + '/subBranchList';

            $.ajax(targetUri, {
                method: 'get',
                data: {
                    bankId: bank,
                    cityId: city
                },
                dataType: 'json',
                error: function (res, code) {
                    console.error(res.responseText, code);
                },
                success: function (data) {
                    subBranchSelector.empty();
                    for (var i = 0; i < data.length; i++) {
                        var val = data[i];
                        // console.error(val);
                        var name = val.name;
                        var code = val.id;
                        subBranchSelector.append('<option value="' + code + '" code="' + code + '">' + name + '</option>');
                    }
                }
            });
        };

        citySelector.change(anyChanged);
        bankSelector.change(anyChanged);


        anyChanged();
    }

});