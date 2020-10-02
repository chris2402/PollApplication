package no.hvl.dat250.h2020.group5.converters;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class AlphaNumeric2Long implements AttributeConverter<String, Long> {

    final private int BASE36 = 36; 

    @Override
    public String convertToEntityAttribute(Long aLong) {
        return Long.toString(aLong, BASE36).toUpperCase();
    }

    @Override
    public Long convertToDatabaseColumn(String s) {
        return Long.valueOf(s.toLowerCase(), BASE36);
    }
}
