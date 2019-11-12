package de.diedavids.cuba.defaultvalues.dynamicvalue;

import java.util.function.Supplier;


/**
 * a dynamic value provider that provides an instance of a type T that will be dynamically created
 *
 * Examples:
 * - current user
 * - today
 * - current locale
 *
 * @param <T> the type of the dynamic value
 */
public interface DynamicValueProvider<T> extends Supplier<T> {


    /**
     * returns a canonical name of the provider
     * @return the name
     */
    String getCode();

    /**
     * returns the type of that dynamic value that it provides
     * @return class of the type of the provider
     */
    Class<T> getReturnType();


}
