package de.diedavids.cuba.defaultvalues.dynamicvalue.user;

import com.haulmont.cuba.security.entity.User;
import org.springframework.stereotype.Component;

@Component(CurrentUserDynamicValueProvider.CODE)
public class CurrentUserDynamicValueProvider extends UserContextDynamicValueProvider {

    static final public String CODE = "ddcdv_CurrentUserDynamicValueProvider";

    @Override
    public String getCode() {
        return CODE;
    }

    @Override
    public User get() {
        return getUserSession().getUser();
    }
}
