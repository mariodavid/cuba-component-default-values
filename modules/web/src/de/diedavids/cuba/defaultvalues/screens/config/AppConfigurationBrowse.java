/*
 * Copyright (c) 2008-2016 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package de.diedavids.cuba.defaultvalues.screens.config;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.config.AppPropertyEntity;
import com.haulmont.cuba.gui.WindowManager.OpenType;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.Table.SortDirection;
import com.haulmont.cuba.gui.components.actions.RefreshAction;
import com.haulmont.cuba.gui.settings.Settings;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller of the {@code appconfig-browse.xml} screen
 */
public class AppConfigurationBrowse extends AbstractWindow {

    @Inject
    private AppConfigurationDatasource paramsDs;

    @Named("paramsTable.editValue")
    private Action editValueAction;

    @Named("paramsTable.refresh")
    private RefreshAction refreshAction;

    @Inject
    private TreeTable<AppPropertyEntity> paramsTable;

    @Inject
    private TextField<String> searchField;

    @Inject
    private Button exportBtn;

    @Inject
    private HBoxLayout hintBox;

    private AppPropertyEntity lastSelected;

    @Override
    public void init(Map<String, Object> params) {
        paramsDs.addItemChangeListener(e -> {
            boolean enabled = e.getItem() != null && !e.getItem().getCategory();
            editValueAction.setEnabled(enabled);
            exportBtn.setEnabled(enabled);
        });
        paramsTable.setItemClickAction(editValueAction);

        paramsTable.sort("name", SortDirection.ASCENDING);

        searchField.addValueChangeListener(e -> {
            paramsDs.refresh(ParamsMap.of("name", e.getValue()));

            if (StringUtils.isNotEmpty(e.getValue())) {
                paramsTable.expandAll();
            }
        });

        refreshAction.setBeforeRefreshHandler(() ->
                lastSelected = paramsTable.getSingleSelected()
        );
        refreshAction.setAfterRefreshHandler(() -> {
            if (StringUtils.isNotEmpty(searchField.getValue())) {
                paramsTable.expandAll();
            }

            if (lastSelected != null) {
                for (AppPropertyEntity entity : paramsDs.getItems()) {
                    if (entity.getName().equals(lastSelected.getName())) {
                        paramsTable.expand(entity.getId());
                        paramsTable.setSelected(entity);
                    }
                }
            }
        });
    }

    public void editValue() {
        Window editor = openWindow("appPropertyEditor", OpenType.DIALOG,
                ParamsMap.of("item", paramsDs.getItem()));
        editor.addCloseWithCommitListener(() -> {
            List<AppPropertyEntity> entities = paramsDs.loadAppPropertyEntities();
            for (AppPropertyEntity entity : entities) {
                if (entity.getName().equals(paramsDs.getItem().getName())) {
                    paramsDs.getItem().setCurrentValue(entity.getCurrentValue());
                    paramsDs.getItem().setUpdateTs(entity.getUpdateTs());
                    paramsDs.getItem().setUpdatedBy(entity.getUpdatedBy());
                    break;
                }
            }
        });
    }

    public void exportAsSql() {
        List<AppPropertyEntity> exported = paramsTable.getSelected().stream()
                .filter(appPropertyEntity -> !appPropertyEntity.getCategory())
                .collect(Collectors.toList());
        if (!exported.isEmpty()) {
            openWindow("appPropertiesExport", OpenType.DIALOG, ParamsMap.of("exported", exported));
        }
    }

    public void closeHint() {
        hintBox.setVisible(false);
        getSettings().get(hintBox.getId()).addAttribute("visible", "false");
    }

    @Override
    public void applySettings(Settings settings) {
        super.applySettings(settings);
        String visible = settings.get(hintBox.getId()).attributeValue("visible");
        if (visible != null)
            hintBox.setVisible(Boolean.parseBoolean(visible));
    }
}