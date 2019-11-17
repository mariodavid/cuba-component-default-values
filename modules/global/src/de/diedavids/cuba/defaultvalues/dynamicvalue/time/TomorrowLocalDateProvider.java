package de.diedavids.cuba.defaultvalues.dynamicvalue.time;

import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component(TomorrowLocalDateProvider.CODE)
public class TomorrowLocalDateProvider extends NDaysLocalDateProvider {

    static final public String CODE = "ddcdv_TomorrowLocalDateProvider";

    public String getCode() {
        return CODE;
    }

    @Override
    public LocalDate get() {
        return today().plusDays(1);
    }
}
