package me.jiangcai.dating.entity.converter;

import me.jiangcai.chanpay.model.Province;
import me.jiangcai.dating.service.PayResourceService;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * @author CJ
 */
@Converter(autoApply = true)
public class ProvinceConverter implements AttributeConverter<Province, String> {
    @Override
    public String convertToDatabaseColumn(Province attribute) {
        if (attribute == null)
            return null;
        return attribute.getId();
    }

    @Override
    public Province convertToEntityAttribute(String dbData) {
        if (dbData == null)
            return null;
        return PayResourceService.provinceById(dbData);
    }
}
