package de.diedavids.cuba.defaultvalues.metadata

import com.haulmont.chile.core.model.MetaClass
import com.haulmont.chile.core.model.MetaProperty
import com.haulmont.cuba.core.app.dynamicattributes.DynamicAttributes
import com.haulmont.cuba.core.entity.CategoryAttribute
import com.haulmont.cuba.core.entity.Entity
import com.haulmont.cuba.core.global.*
import com.haulmont.cuba.security.entity.EntityOp
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

import javax.inject.Inject

@Component(MetadataDataProvider.NAME)
@CompileStatic
class MetadataSelector implements MetadataDataProvider {

    @Inject
    Metadata metadata

    @Inject
    Messages messages

    @Inject
    Security security

    @Inject
    DynamicAttributes dynamicAttributes


    @Override
    Map<String, MetaClass> getEntitiesLookupFieldOptions() {
        Map<String, MetaClass> options = new TreeMap<>()

        for (MetaClass metaClass : metadataTools.allPersistentMetaClasses) {
            if (readPermitted(metaClass)) {
                Class javaClass = metaClass.javaClass
                if (Entity.isAssignableFrom(javaClass)) {
                    options.put(messageTools.getEntityCaption(metaClass) + ' (' + metaClass.name + ')', metaClass)
                }
            }
        }

        options
    }

    @Override
    List<MetaProperty> getBusinessMetaProperties(MetaClass entityMetaClass) {

        return entityMetaClass.properties.findAll { MetaProperty property ->
            metadataTools.isPersistent(property) && !metadataTools.isSystem(property)
        } as List<MetaProperty>
    }

    @Override
    Map<String, String> getAllAttributesLookupFieldOptions(MetaClass entityMetaClass) {
        getLookupMetaProperties(entityMetaClass.properties)
    }

    Map<String, String> getDynamicAttributesLookupFieldOptions(MetaClass entityMetaClass) {

        Collection<CategoryAttribute> dynamicAttributesForImportConfiguration = dynamicAttributes.getAttributesForMetaClass(entityMetaClass)

        dynamicAttributesForImportConfiguration.collectEntries {
            ["${it.name} (${it.code})".toString(), it.name]
        }
    }
    Map<String, String> getDirectAttributesLookupFieldOptions(MetaClass entityMetaClass) {

        def directMetaProperties = metaPropertiesOfType(entityMetaClass, MetaProperty.Type.DATATYPE) + metaPropertiesOfType(entityMetaClass, MetaProperty.Type.ENUM)
        getLookupMetaProperties(directMetaProperties)
    }

    Map<String, String> getLookupMetaProperties(Collection<MetaProperty> metaProperties) {
        metaProperties.collectEntries {
            ["${messageTools.getPropertyCaption(it)} (${it.name})".toString(), it.name]
        }
    }

    Map<String, String> getAssociationAttributes(MetaClass entityMetaClass) {

        def associationMetaProperties = metaPropertiesOfType(entityMetaClass, MetaProperty.Type.ASSOCIATION)

        getLookupMetaProperties(associationMetaProperties)
    }

    private Collection<MetaProperty> metaPropertiesOfType(MetaClass entityMetaClass, MetaProperty.Type metaPropertyType) {
        entityMetaClass.properties.findAll {
            it.type == metaPropertyType
        }
    }

    protected boolean readPermitted(MetaClass metaClass) {
        entityOpPermitted(metaClass, EntityOp.READ)
    }

    protected boolean entityOpPermitted(MetaClass metaClass, EntityOp entityOp) {
        security.isEntityOpPermitted(metaClass, entityOp)
    }


    private MessageTools getMessageTools() {
        messages.tools
    }

    private MetadataTools getMetadataTools() {
        metadata.tools
    }

}