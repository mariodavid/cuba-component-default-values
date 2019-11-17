[![Build Status](https://travis-ci.com/mariodavid/cuba-component-default-values.svg?branch=master)](https://travis-ci.com/mariodavid/cuba-component-default-values)
[ ![Download](https://api.bintray.com/packages/mariodavid/cuba-components/cuba-component-default-values/images/download.svg) ](https://bintray.com/mariodavid/cuba-components/cuba-component-default-values/_latestVersion)
[![license](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0)

# CUBA Component - Default Values

This application component lets you configure default values for entities at runtime.


![Customer Editor with default values](https://github.com/mariodavid/cuba-component-default-values/blob/master/img/6-customer-edit.png)


Table of Contents
=================

  * [Installation](#installation)
  * [Using the application component](#using-the-application-component)
  * [Default Value Configuration](#default-value-configuration)
     * [Default Value Types](#default-value-types)
        * [Static Value](#static-value)
        * [Dynamic Value](#dynamic-value)
        * [Session Attribute](#session-attribute)
        * [Script](#script)

## Installation

1. `default-values` is available in the [CUBA marketplace](https://www.cuba-platform.com/marketplace/default-values)
2. Select a version of the add-on which is compatible with the platform version used in your project:

| Platform Version | Add-on Version |
| ---------------- | -------------- |
| 7.1.x            | 0.1.x          |


The latest version is: [ ![Download](https://api.bintray.com/packages/mariodavid/cuba-components/cuba-component-default-values/images/download.svg) ](https://bintray.com/mariodavid/cuba-components/cuba-component-default-values/_latestVersion)

Add custom application component to your project:

* Artifact group: `de.diedavids.cuba.defaultvalues`
* Artifact name: `defaultvalues-global`
* Version: *add-on version*

```groovy
dependencies {
  appComponent("de.diedavids.cuba.defaultvalues:defaultvalues-global:*addon-version*")
}
```


### CHANGELOG

Information on changes that happen through the different versions of the application component can be found in the [CHANGELOG](https://github.com/mariodavid/cuba-component-default-values/blob/master/CHANGELOG.md).
The Changelog also contains information about breaking changes and tips on how to resolve them.

## Using the application component

### Example usage
To see this application component in action, check out this example: [cuba-example-using-default-values](https://github.com/mariodavid/cuba-example-using-default-values).
It contains all examples of all the screenshots.

### Creation of Entities with configured Default Values

![Customer Editor with default values](/img/6-customer-edit.png)

![Order Editor with default values](/img/7-order-edit.png)




## Default Value Configuration

Default Values can be configured for entities via `Administration > Entity Default Values`. It contains a list
of all Entities that are registered in the application.

![List of all Default Value Entities](/img/1-default-value-entity-browse.png)

Selecting one Entity opens the Details screen that shows the all attributes of the entity that fulfill this criteria:

* attribute is persistent
* attribute is not annotated with `@SystemLevel` or are part of System level Interfaces (like `createTs` e.g.)
* attribute is not a 1:N or M:N association


### Default Value Types


There are multiple types of default values that can be configured for an entity attribute.

![default value type selection](/img/4-default-value-entity-edit-new.png)

### Static Value

A static value is used when a globally pre-configured value should be used for an entity attribute as the default value.

![default value - static value edit](/img/3-default-value-entity-edit-attribute-static-value-edit.png)

Examples:

* _always_ set customer type to `REGULAR` 
* _always_ set the Group association of a User to `Employees` Group
 

### Dynamic Value

Dynamic values compared to static values are evaluated at the time when the instance is created. Those values are dependent
on the environment.

Examples:

* _current date_ for the attribute orderDate of an order
* _current user_ for a association from a customer to the User via the attribute `accountManager`
* _locale of the current user_ for an attribute with a locale type


![default value - dynamic value](/img/5-default-value-entity-edit-new-dynamic-value.png)

There are different dynamic value options for different datatypes. The application component itself defines the following
dynamic value options:

* User
  * current user
  * current / substituted user
* Date
  * Today
* LocalDate
  * Yesterday
  * Today
  * Tomorrow
  
In case the datatype of the entity attribute has no options for dynamic values, it is not possible to select this option.

#### Custom Dynamic Values

It is also possible to create application specific dynamic value options. The application component defines an interface
[DynamicValueProvider<T>](https://github.com/mariodavid/cuba-component-default-values/blob/master/modules/global/src/de/diedavids/cuba/defaultvalues/dynamicvalue/DynamicValueProvider.java)
that has to be implemented by a custom dynamic value.

The new dynamic value option needs to be a Spring bean in the global module of the application. 

An example can be found in the example application: [CustomerTypeFavoriteValueProvider](https://github.com/mariodavid/cuba-example-using-default-values/blob/master/modules/global/src/com/company/ceudv/CustomerTypeFavoriteValueProvider.java)

```
package com.company.ceudv;

import com.company.ceudv.entity.CustomerType;
import de.diedavids.cuba.defaultvalues.dynamicvalue.DynamicValueProvider;
import org.springframework.stereotype.Component;

@Component("ceudv_CustomerTypeFavoriteValueProvider")
public class CustomerTypeFavoriteValueProvider implements DynamicValueProvider<CustomerType> {

    @Override
    public String getCode() {
        return "customerTypeFavorite";
    }

    @Override
    public Class<CustomerType> getReturnType() {
        return CustomerType.class;
    }

    @Override
    public CustomerType get() {
        return CustomerType.favorite();
    }
}
```

The interface requires the following methods to be implemented:

* `getCode` - return a unique code / name of this dynamic value provider
* `getReturnType` - the type that this dynamic value is applicable for
* `get` - execution of the logic to return the default value


Additionally a translation value has be set in the main message pack of the web module:

```
dynamicValueProvider.customerTypeFavorite = Favorite Customer Type
```

* `dynamicValueProvider.` - prefix for translation of dynamic value providers
* `customerTypeFavorite` - the code of the dynamic value provider


With that, the dynamic value appears in the default value configuration UI and can be selected for all entity
attributes that have the datatype `CustomerType`. 

### Session Attribute

A session attribute can also be placed into a entity attribute as a default value.

Examples:

* Preferred City for a new Customer based on the Access group the current user is in
* Default Group for a new User defined by the current local admin in multi tenant application
 

Session attributes are a good option, if user specific options should be leveraged when assigning a default value.

The session attribute concept of CUBA integrates the whole user group hierarchy into the value that is placed
into the session.

More information on session attributes can be found in the [CUBA docs: Session attributes](https://doc.cuba-platform.com/manual-latest/session_attr.html).

NOTE: in the configuration UI the datatypes of the session attributes are not checked against the datatype
of the entity attribute. If the datatypes do not match, the value will not be bound.


Another usage of a session attribute based default value is when a session attribute is programmatically assigned before
the new entity is created. 

For example it is possible to use this variant if a user globally for its current
user session (until next logout) defines that (s)he now works on the asian area of the business. This would e.g.
set the default country to `China` in the session. With a configured session attribute default value 
for a new customers country is now `China`.


### Script

The last option is a script based default value. This option allows to define a groovy script, that programmatically
determines the default value at the time when a new entity instance is created.

This option gives the most flexibility of all options and with a script all above mentioned options can also
be achieved.

That being said, it requires the ability to write code in order to leverage that possibility.

The following attribute are available in the script:
 
* `dataManager` - DataManager instance for Database access from CUBA (com.haulmont.cuba.core.global.DataManager)
* `beanLocator` - Provides access to all managed beans from CUBA (com.haulmont.cuba.core.global.BeanLocator)
* `timeSource` - Global time source interface from CUBA (com.haulmont.cuba.core.global.TimeSource) 


NOTE: The return type has to match the datatype of the entity attribute. 
If no value is returned, the Groovy evaluator will treat that execution as false


