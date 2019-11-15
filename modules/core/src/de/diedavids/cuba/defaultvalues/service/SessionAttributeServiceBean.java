package de.diedavids.cuba.defaultvalues.service;

import com.haulmont.chile.core.datatypes.DatatypeRegistry;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.security.entity.SessionAttribute;
import de.diedavids.cuba.defaultvalues.EntityAttributeDatatypes;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@Service(SessionAttributeService.NAME)
public class SessionAttributeServiceBean implements SessionAttributeService {

    @Inject
    protected DataManager dataManager;

    @Inject
    protected DatatypeRegistry datatypeRegistry;

    @Inject
    protected EntityAttributeDatatypes entityAttributeDatatypes;

    @Override
    public List<String> getAvailableSessionAttributes() {
        return dataManager.load(SessionAttribute.class)
                .list()
                .stream()
                .map(SessionAttribute::getName)
                .distinct()
                .collect(Collectors.toList());
    }
}