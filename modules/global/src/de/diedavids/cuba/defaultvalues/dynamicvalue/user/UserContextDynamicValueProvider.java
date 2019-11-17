package de.diedavids.cuba.defaultvalues.dynamicvalue.user;

import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.cuba.security.global.UserSession;
import de.diedavids.cuba.defaultvalues.dynamicvalue.DynamicValueProvider;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

public abstract class UserContextDynamicValueProvider implements DynamicValueProvider<User> {

    @Inject
    protected UserSessionSource userSessionSource;


    @Override
    public Class<User> getReturnType() {
        return User.class;
    }

    protected UserSession getUserSession() {
        return userSessionSource.getUserSession();
    }
}
