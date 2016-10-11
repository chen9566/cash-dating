package me.jiangcai.dating.entity.converter;

import me.jiangcai.chanpay.model.City;
import me.jiangcai.dating.service.PayResourceService;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * @author CJ
 */
@Converter(autoApply = true)
public class CityConverter implements AttributeConverter<City, String> {
    @Override
    public String convertToDatabaseColumn(City attribute) {
        if (attribute == null)
            return null;
        return attribute.getId();
    }

    @Override
    public City convertToEntityAttribute(String dbData) {
        if (dbData == null)
            return null;
        return PayResourceService.cityById(dbData);
    }
}
