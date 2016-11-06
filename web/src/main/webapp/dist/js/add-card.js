/**
 * 银行卡
 * Created by CJ on 11/10/2016.
 */

$(function () {
    var cardShower = $('#cardShower');
    var numberInput = $('input[name=number]');
    var nameInput = $('input[name=name]');
    var submitButton = $('input[type=submit]');
    var subBranchName = $('[name=subBranchName]');
    var subBranch = $('[name=subBranch]');

    function disableButton() {
        submitButton.attr('disabled', 'disabled');
        submitButton.addClass('black');
        submitButton.removeClass('redremove');
    }

    function enableButton() {
        submitButton.removeAttr('disabled');
        submitButton.removeClass('black');
        submitButton.addClass('redremove');
    }

    function checkButton() {
        var name = nameInput.val();
        if (name.length < 2 || name.length > 20) {
            return disableButton();
        }
        var number = numberInput.val();
        if (number.length < 16)
            return disableButton();
        if (subBranch.val().length == 0)
            return disableButton();

        enableButton();
    }

    nameInput.change(checkButton);
    nameInput.blur(checkButton);
    nameInput.keyup(checkButton);

    function numberChange() {
        var val = numberInput.val();
        var current = 0;
        var buffer = '';
        while (val.length > current) {
            var max = val.length - current;
            var len = Math.min(max, 4);

            buffer += val.substr(current, len);
            current += len;
            if (len == 4)
                buffer += ' ';
        }

        cardShower.text(buffer);

        checkButton();
    }

    numberInput.change(numberChange);
    numberInput.blur(numberChange);
    numberInput.keyup(numberChange);
    numberChange();

    // 给省份 城市设计联动
    // 动作必须是一对儿的 就是2者必须拥有同一个属性data-province-city-group data-address-group
    // province-selector
    // city-selector
    // 如果出现多个目标才需要配对 一个的话 就算了 哈
    var provinceSelector = $('.province-selector');
    var citySelector = $('.city-selector');
    var bankSelector = $('.bank-selector');

    var anyChanged = function () {
        var city = citySelector.val();
        var bank = bankSelector.val();
        // console.log('haha:', city, bank);

        if (!city || !bank) {
            // subBranchSelector.empty();
            return;
        }
        if (city.length == 0 || bank.length == 0) {
            // subBranchSelector.empty();
            return;
        }
        var subBranchRegion = $('#subBranchRegion');
        // 原始href
        var href = subBranchRegion.attr('data-href');
        subBranchRegion.attr('href', href + "?bankId=" + bank + "&cityId=" + city);

        // console.error(sessionStorage.getItem('branch-cityId'), city, sessionStorage.getItem('branch-bankId'), bank
        //     , sessionStorage.getItem('branch-id'), sessionStorage.getItem('branch-name'));

        if (sessionStorage.getItem('branch-cityId') == city && sessionStorage.getItem('branch-bankId') == bank) {
            subBranchName.val(sessionStorage.getItem('branch-name'));
            subBranch.val(sessionStorage.getItem('branch-id'));
        } else {
            // console.log('clea sessions');
            subBranchName.val('');
            subBranch.val('');
        }
        // var targetUri;
        // if ($.prototypesMode)
        //     targetUri = 'mock/branches.json';
        // else
        //     targetUri = $.uriPrefix + '/subBranchList';
        //
        // $.ajax(targetUri, {
        //     method: 'get',
        //     data: {
        //         bankId: bank,
        //         cityId: city
        //     },
        //     dataType: 'json',
        //     error: function (res, code) {
        //         console.error(res.responseText, code);
        //     },
        //     success: function (data) {
        //         subBranchSelector.empty();
        //         for (var i = 0; i < data.length; i++) {
        //             var val = data[i];
        //             // console.error(val);
        //             var name = val.name;
        //             var code = val.id;
        //             subBranchSelector.append('<option value="' + code + '" code="' + code + '">' + name + '</option>');
        //         }
        //     }
        // });
        checkButton();
    };

    // 如果之前选择了银行
    var storageBankId = sessionStorage.getItem('branch-bankId');
    if (storageBankId) {
        bankSelector.val(storageBankId);
        anyChanged();
    }

    function cityChange() {
        // console.log('city changed');
        sessionStorage.setItem('card-city', citySelector.val());
        anyChanged();
    }

    function afterDataReady() {
        if (bankSelector.size() > 0 && citySelector.size() > 0) {
            citySelector.change(cityChange);
            bankSelector.change(anyChanged);
            anyChanged();
        }
    }

    // var subBranchSelector = $('.subBranch-selector');
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
                    sessionStorage.setItem('card-province', provinceName);
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
                    // 添加完成了 找下有没有我
                    var storageCity = sessionStorage.getItem('card-city');
                    if (storageCity && $('option[value=' + storageCity + ']', citySelector).size() > 0) {
                        citySelector.val(storageCity);
                        // $('option[value=' + storageCity + ']', citySelector).click();
                        cityChange();
                    } else {
                        // 选中第一个
                        var firstValue = $('option', citySelector).get(0).getAttribute('value');
                        // console.log(firstValue);
                        citySelector.val(firstValue);
                        cityChange();
                        // $('option[value=' + firstValue + ']', citySelector).click();
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

                // 如果已经选择了的话
                var storageProvince = sessionStorage.getItem('card-province');
                if (storageProvince) {
                    provinceSelector.val(storageProvince);
                }

                provinceChanged(provinceSelector);

                afterDataReady();
            }
        });

    }

});
