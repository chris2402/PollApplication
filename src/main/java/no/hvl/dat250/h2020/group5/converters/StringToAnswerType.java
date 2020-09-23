package no.hvl.dat250.h2020.group5.converters;

import no.hvl.dat250.h2020.group5.enums.AnswerType;
import org.springframework.core.convert.converter.Converter;

//Source: https://www.baeldung.com/spring-enum-request-param
public class StringToAnswerType implements Converter<String, AnswerType> {
    @Override
    public AnswerType convert(String source) {
        try{
            return AnswerType.valueOf(source.toUpperCase());
        }catch (IllegalArgumentException e) {
            return null;
        }
    }
}
