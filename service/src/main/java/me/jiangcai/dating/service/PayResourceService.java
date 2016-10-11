package me.jiangcai.dating.service;

import me.jiangcai.chanpay.Dictionary;
import me.jiangcai.chanpay.model.City;
import me.jiangcai.chanpay.model.Province;
import me.jiangcai.chanpay.model.SubBranch;

import java.util.Collection;
import java.util.List;

/**
 * 支付相关的资源服务
 *
 * @author CJ
 */
public interface PayResourceService {

    Collection<Province> provinceCollection = Dictionary.findAll(Province.class);

    /**
     * @return 所有省份
     */
    static Collection<Province> listProvince() {
        return provinceCollection;
    }

    /**
     * @param id
     * @return 查找省份, 如果没有就用null
     */
    static Province provinceById(String id) {
        return provinceCollection.stream()
                .filter(province -> id.equals(province.getId()))
                .findAny()
                .orElse(null);
    }

    static City cityById(String id) {
        return provinceCollection.stream()
                .map(Province::getCityList)
                .filter(list -> list.stream()
                        .filter(city -> id.equals(city.getId()))
                        .count() > 0)
                .map(list -> list.stream()
                        .filter(city -> id.equals(city.getId()))
                        .findAny()
                        .orElse(null)
                )
                .findAny()
                .orElse(null);
    }

    /**
     * @param cityId 城市编号
     * @param bankId 银行编号
     * @return 查询所有的分支
     */
    List<SubBranch> listSubBranches(String cityId, String bankId);

}
