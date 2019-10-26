package de.diedavids.cuba.defaultvalues.screens;

import com.haulmont.cuba.gui.components.PickerField;
import java.util.Map;
import javax.inject.Inject;

public class ApplicationDefaultValuesScreen extends DefaultValuesScreen {

  @Inject
  protected PickerField contrattoField;
  @Inject
  protected PickerField annoField;
  @Inject
  protected PickerField strutturaRicettivaField;

  @Override
  public void init(Map<String, Object> params) {
    super.init(params);

    addToDefaultValues(contrattoField, DefaultValues.CONTRATTO);
    addToDefaultValues(strutturaRicettivaField, DefaultValues.STRUTTURA_RICETTIVA);
    addToDefaultValues(annoField, DefaultValues.ANNO);

  }
}