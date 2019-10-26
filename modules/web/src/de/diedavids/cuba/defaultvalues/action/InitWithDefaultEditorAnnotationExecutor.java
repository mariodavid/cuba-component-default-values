package de.diedavids.cuba.defaultvalues.action;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.gui.components.HasValue;
import com.haulmont.cuba.gui.components.Window.Editor;
import de.balvi.cuba.declarativecontrollers.web.annotationexecutor.editor.EditorFieldAnnotationExecutor;
import de.diedavids.cuba.defaultvalues.InitWithDefault;
import java.lang.annotation.Annotation;
import java.util.Map;
import javax.inject.Inject;
import org.springframework.stereotype.Component;

@Component("cesdv$InitWithDefaultEditorAnnotationExecutor")
public class InitWithDefaultEditorAnnotationExecutor implements
    EditorFieldAnnotationExecutor<InitWithDefault, HasValue> {


  @Inject
  protected UserSessionSource userSessionSource;

  @Inject
  protected DataManager dataManager;

  @SuppressWarnings("Instanceof")
  public boolean supports(Annotation annotation) {
    return annotation instanceof InitWithDefault;
  }

  @Override
  public void init(InitWithDefault annotation, Editor editor, HasValue target,
      Map<String, Object> params) {
  }

  @Override
  public void postInit(InitWithDefault annotation, Editor editor, HasValue target) {
    if (PersistenceHelper.isNew(editor.getItem())) {
      setDefaultValueToTarget(annotation, target);
    }
  }

  private void setDefaultValueToTarget(InitWithDefault annotation, HasValue target) {
    Entity defaultValue = userSessionSource.getUserSession().getAttribute(annotation.value());

    if (defaultValue != null) {
      target.setValue(dataManager.reload(defaultValue, annotation.view()));
    }
  }

}
