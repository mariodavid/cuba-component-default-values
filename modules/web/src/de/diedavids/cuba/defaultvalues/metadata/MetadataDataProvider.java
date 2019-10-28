package de.diedavids.cuba.defaultvalues.metadata;

import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.chile.core.model.MetaProperty;

import java.util.List;
import java.util.Map;

public interface MetadataDataProvider {

    String NAME = "ddcmu_MetadataDataProvider";


    Map<String, String> getAllAttributesLookupFieldOptions(MetaClass entityMetaClass);

    Map<String, MetaClass> getEntitiesLookupFieldOptions();

    List<MetaProperty> getBusinessMetaProperties(MetaClass metaClass);
}
