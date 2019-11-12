package de.diedavids.cuba.defaultvalues.dynamicvalue;

import com.haulmont.cuba.core.global.TimeSource;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.time.LocalDate;

@Component(TodayLocalDateProvider.CODE)
public class TodayLocalDateProvider implements DynamicValueProvider<LocalDate> {

    static final public String CODE = "ddcdv_TodayLocalDateProvider";

    @Inject
    protected TimeSource timeSource;

    @Override
    public String getCode() {
        return CODE;
    }

    @Override
    public Class<LocalDate> getReturnType() {
        return LocalDate.class;
    }

    @Override
    public LocalDate get() {
        return timeSource.now().toLocalDate();
    }
}
