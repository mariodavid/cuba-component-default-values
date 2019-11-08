[![Build Status](https://travis-ci.com/mariodavid/cuba-component-default-values.svg?branch=master)](https://travis-ci.com/mariodavid/cuba-component-default-values)
[ ![Download](https://api.bintray.com/packages/mariodavid/cuba-components/cuba-component-default-values/images/download.svg) ](https://bintray.com/mariodavid/cuba-components/cuba-component-default-values/_latestVersion)
[![license](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0)

# CUBA Component - Default Values

This application component lets you configure default values for entities at runtime.

Table of Contents
=================

  * [Installation](#installation)
  * [Using the application component](#using-the-application-component)


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


