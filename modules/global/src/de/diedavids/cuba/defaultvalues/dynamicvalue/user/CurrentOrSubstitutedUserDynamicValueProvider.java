package de.diedavids.cuba.defaultvalues.dynamicvalue.user;

import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.security.entity.User;
import de.diedavids.cuba.defaultvalues.dynamicvalue.DynamicValueProvider;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component(CurrentOrSubstitutedUserDynamicValueProvider.CODE)
public class CurrentOrSubstitutedUserDynamicValueProvider implements DynamicValueProvider<User> {

    static final public String CODE = "ddcdv_CurrentOrSubstitutedUserDynamicValueProvider";

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
        return userSessionSource.getUserSession().getCurrentOrSubstitutedUser();
    }
}
