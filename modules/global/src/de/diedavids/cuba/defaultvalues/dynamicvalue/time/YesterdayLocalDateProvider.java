package de.diedavids.cuba.defaultvalues.dynamicvalue.time;

import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component(YesterdayLocalDateProvider.CODE)
public class YesterdayLocalDateProvider extends NDaysLocalDateProvider {

    static final public String CODE = "ddcdv_YesterdayLocalDateProvider";

    public String getCode() {
        return CODE;
    }

    @Override
    public LocalDate get() {
        return today().minusDays(1);
    }
}
