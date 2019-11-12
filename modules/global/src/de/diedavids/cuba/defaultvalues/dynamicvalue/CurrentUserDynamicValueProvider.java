package de.diedavids.cuba.defaultvalues.dynamicvalue;

import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.security.entity.User;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component(CurrentUserDynamicValueProvider.CODE)
public class CurrentUserDynamicValueProvider implements DynamicValueProvider<User> {

    static final public String CODE = "ddcdv_CurrentUserDynamicValueProvider";

    @Inject
    protected UserSessionSource userSessionSource;


    @Override
    public String getCode() {
        return CODE;
    }

    @Override
    public Class<User> getReturnType() {
        return User.class;
    }

    @Override
    public User get() {
        return userSessionSource.getUserSession().getUser();
    }
}
