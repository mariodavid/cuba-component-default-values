package de.diedavids.cuba.defaultvalues.dynamicvalue.time;

import com.haulmont.cuba.core.global.TimeSource;
import de.diedavids.cuba.defaultvalues.dynamicvalue.DynamicValueProvider;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Date;

@Component(TodayDateProvider.CODE)
public class TodayDateProvider implements DynamicValueProvider<Date> {

    static final public String CODE = "ddcdv_TodayDateProvider";

    @Inject
    protected TimeSource timeSource;

    @Override
    public String getCode() {
        return CODE;
    }

    @Override
    public Class<Date> getReturnType() {
        return Date.class;
    }

    @Override
    public Date get() {
        return timeSource.currentTimestamp();
    }
}
