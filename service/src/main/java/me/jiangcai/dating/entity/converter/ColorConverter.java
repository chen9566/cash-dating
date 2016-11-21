package me.jiangcai.dating.entity.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.awt.*;

/**
 * @author CJ
 */
@Converter(autoApply = true)
public class ColorConverter implements AttributeConverter<Color, Integer> {
    @Override
    public Integer convertToDatabaseColumn(Color attribute) {
        if (attribute == null)
            return null;
        return attribute.getRGB();
    }

    @Override
    public Color convertToEntityAttribute(Integer dbData) {
        if (dbData == null)
            return null;
        return new Color(dbData, true);
    }
}
