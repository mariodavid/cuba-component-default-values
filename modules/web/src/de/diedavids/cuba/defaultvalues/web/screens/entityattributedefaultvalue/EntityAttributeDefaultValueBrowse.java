package de.diedavids.cuba.defaultvalues.web.screens.entityattributedefaultvalue;

import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.MessageTools;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.MetadataTools;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.screen.*;
import de.diedavids.cuba.metadataextensions.entity.MetaClassEntity;

import javax.inject.Inject;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@LoadDataBeforeShow
@UiController("ddcdv_EntityAttributeDefaultValue.browse")
@UiDescriptor("entity-attribute-default-value-browse.xml")
public class EntityAttributeDefaultValueBrowse extends Screen {

    @Inject
    protected MetadataTools metadataTools;
    @Inject
    protected MessageTools messageTools;
    @Inject
    protected Metadata metadata;
    @Inject
    protected ScreenBuilders screenBuilders;
    @Inject
    protected Table<MetaClassEntity> metaClassEntitiesTable;

    @Install(to = "metaClassEntityDl", target = Target.DATA_LOADER)
    protected List<MetaClassEntity> loadEntities(LoadContext<MetaClassEntity> loadContext) {

        return metadataTools.getAllPersistentMetaClasses()
                .stream()
                .map(this::createMetaClassEntity)
                .sorted(Comparator.comparing(MetaClassEntity::getDescription))
                .collect(Collectors.toList());

    }

    private MetaClassEntity createMetaClassEntity(MetaClass metaClass) {
        MetaClassEntity metaClassEntity = metadata.create(MetaClassEntity.class);
        metaClassEntity.setName(metaClass.getName());
        metaClassEntity.setDescription(messageTools.getEntityCaption(metaClass));
        return metaClassEntity;
    }

    @Subscribe("metaClassEntitiesTable.edit")
    protected void onMetaClassEntitiesTableEdit(Action.ActionPerformedEvent event) {
        screenBuilders.editor(metaClassEntitiesTable)
                .withScreenClass(EntityAttributeDefaultValueEdit.class)
                .show();
    }

}