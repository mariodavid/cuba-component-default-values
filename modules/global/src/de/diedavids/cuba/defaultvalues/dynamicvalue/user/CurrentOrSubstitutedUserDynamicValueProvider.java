package de.diedavids.cuba.defaultvalues.dynamicvalue.user;

import com.haulmont.cuba.security.entity.User;
import org.springframework.stereotype.Component;

@Component(CurrentOrSubstitutedUserDynamicValueProvider.CODE)
public class CurrentOrSubstitutedUserDynamicValueProvider extends UserContextDynamicValueProvider {

    static final public String CODE = "ddcdv_CurrentOrSubstitutedUserDynamicValueProvider";

    @Override
    public String getCode() {
        return CODE;
    }

    @Override
    public User get() {
        return getUserSession().getCurrentOrSubstitutedUser();
    }
}
