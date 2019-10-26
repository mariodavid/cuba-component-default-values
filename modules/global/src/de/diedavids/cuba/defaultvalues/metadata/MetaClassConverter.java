package de.diedavids.cuba.defaultvalues.metadata;

import com.google.common.base.Strings;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.*;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class MetaClassConverter implements AttributeConverter<MetaClass, String> {

    @Override
    public String convertToDatabaseColumn(MetaClass value) {

        if (value == null) {
            return "";
        }

        return value.getName();

    }

    @Override
    public MetaClass convertToEntityAttribute(String value) {

        if (Strings.isNullOrEmpty(value)) {
            return null;
        }

        Metadata metadata = getMetadata();

        return metadata.getClass(value);
    }


    private Metadata getMetadata() {
        return AppBeans.get(Metadata.NAME);
    }

}
