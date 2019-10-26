package de.diedavids.cuba.defaultvalues.web.screens.defaultvalueconfiguration;

import com.haulmont.cuba.gui.screen.*;
import de.diedavids.cuba.defaultvalues.entity.DefaultValueConfiguration;

@UiController("ddcdv_DefaultValueConfiguration.browse")
@UiDescriptor("default-value-configuration-browse.xml")
@LookupComponent("defaultValueConfigurationsTable")
@LoadDataBeforeShow
public class DefaultValueConfigurationBrowse extends StandardLookup<DefaultValueConfiguration> {
}