package no.hvl.dat250.h2020.group5.converters;

import no.hvl.dat250.h2020.group5.enums.PollVisibilityType;
import org.springframework.core.convert.converter.Converter;

//Source: https://www.baeldung.com/spring-enum-request-param
public class StringToVisibilityType implements Converter<String, PollVisibilityType> {
    @Override
    public PollVisibilityType convert(String source) {
        try{
            return PollVisibilityType.valueOf(source.toUpperCase());
        }catch (IllegalArgumentException e) {
            return null;
        }
    }
}
