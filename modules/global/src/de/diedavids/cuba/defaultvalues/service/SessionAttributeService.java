package de.diedavids.cuba.defaultvalues.service;

import java.util.List;

public interface SessionAttributeService {
    String NAME = "ddcdv_SessionAttributeService";

    List<String> getAvailableSessionAttributes();
}