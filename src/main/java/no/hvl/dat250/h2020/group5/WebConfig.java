package no.hvl.dat250.h2020.group5;

import no.hvl.dat250.h2020.group5.converters.StringToAnswerType;
import no.hvl.dat250.h2020.group5.converters.StringToVisibilityType;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToVisibilityType());
        registry.addConverter(new StringToAnswerType());
    }
}
