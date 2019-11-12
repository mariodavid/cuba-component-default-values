package de.diedavids.cuba.defaultvalues.dynamicvalue;

import com.haulmont.chile.core.model.MetaProperty;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@Component("ddcdv_DynamicValueProviders")
public class DynamicValueProviders {

    @Inject
    List<DynamicValueProvider> dynamicValueProviders;

    public List<String> getProvidersFor(MetaProperty metaProperty) {

        return dynamicValueProviders.stream()
                .filter(dynamicValueProvider -> dynamicValueProvider.getReturnType().isAssignableFrom(metaProperty.getJavaType()))
                .map(DynamicValueProvider::getCode)
                .collect(Collectors.toList());
    }
}
