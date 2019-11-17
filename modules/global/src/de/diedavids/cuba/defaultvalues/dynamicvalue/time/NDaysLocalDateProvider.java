package de.diedavids.cuba.defaultvalues.dynamicvalue.time;

import com.haulmont.cuba.core.global.TimeSource;
import de.diedavids.cuba.defaultvalues.dynamicvalue.DynamicValueProvider;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.time.LocalDate;

public abstract class NDaysLocalDateProvider implements DynamicValueProvider<LocalDate> {

    @Inject
    protected TimeSource timeSource;

    @Override
    public Class<LocalDate> getReturnType() {
        return LocalDate.class;
    }

    protected LocalDate today() {
        return timeSource.now().toLocalDate();
    }
}
