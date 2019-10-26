package de.diedavids.cuba.defaultvalues.metadata;

import com.haulmont.chile.core.model.MetaClass;

import java.util.Map;

public interface MetadataDataProvider {

    String NAME = "ddcmu_MetadataDataProvider";


    Map<String, String> getAllAttributesLookupFieldOptions(MetaClass entityMetaClass);

    Map<String, MetaClass> getEntitiesLookupFieldOptions();
}
