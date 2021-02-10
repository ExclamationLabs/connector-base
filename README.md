# connector-base
### Java Base Connector Framework for Identity Access Management (IAM) and Midpoint integration

This software is Copyright 2020 Exclamation Labs.  Licensed under the Apache License, Version 2.0.

This project serves as a base framework for various IAM connectors.  Its purpose
is to greatly reduce code repetition and complexity when writing new connectors
and minimize the interactions with the ConnId framework and have much of that
taken care of by a common API.

## Gradle and Build Process

Connector-base is a Gradle project based on Java 1.8.  The gradle wrapper is also included with this
 project.  To test and build the project, simply use

`./gradlew build`

If you need to fully clean the prior build first, then run -

`./gradlew clean build`

The build command will produce a small JAR, connector-base-{version}.jar.  
This jar contains the Base Connector
framework API/code classes.  You can add the following to your build.gradle to leverage
the Base Connector in your connector project.

Inside the build.gradle repositories { } section:

`    maven {
         url "https://artifactory.exclamationlabs.com/artifactory/libs-release-local"
     }
`

Inside build.gradle, listed with any other dependencies, replacing 1.0.3
 with the current version:
 
`implementation 'com.exclamationlabs.connid:connector-base:1.0.3-+'`

The critical dependencies of the Base Connector framework are described in build.gradle under
'Vital dependencies' block.  If your connector project uses the Base Connector and pulls it in
from Artifactory, these dependencies should carry into your project with the pom.xml present 
on Artifactory.  Any remaining dependencies
are dependent on your particular connector's needs ... for example if you have
a simple CSV connector, you should not need HTTP/RESTful or JWT support at runtime
and shouldn't need to include these dependencies in your connector project.

## Developer Guide

Also see:
- [Javadocs for Connector Base Framework](https://exclamationlabs.github.io/connector-base/)
- [Javadocs for ConnId Identity Connectors](https://connid.tirasa.net/apidocs/1.5/index.html)

The Base Connector consists of many interfaces and abstract base classes and is focused
on providing reusability as well as customization opportunities.   We use the term 'destination system' to describe any
 kind of a backend system or storage location that can hold IAM information.
 
Below is a summary of the key
class types in the Base Connector framework and some notes regarding 
their purpose and usage. 

### Framework Types

#### Models 

*Interface: `IdentityModel`*

The Base Connector framework needs to know what classes represent your user
and group data, so that data can be transmitted to your destination system. Your
object classes be a POJO or any type you like, but they must implement the
`IdentityModel` interface.

The Base Connector framework uses generics, and the model types must be
referenced by other types as the ones that pertain your connector implementation.

#### Connector

*Abstract classed: `BaseConnector`, `BaseFullAccessConnector`, 
`BaseReadOnlyConnector` and `BaseWriteOnlyConnector`*

The Connector is the prime class that MidPoint communicates with in order to interact
with your destination system.  Any connector based on the framework is expected to
do the following, at a minimum:

- Be annotated with org.identityconnectors.framework.spi.ConnectorClass. 
This annotation needs to contain the setup for your Configuration class and should
also point the display properties file for your connector.  Example:

`@ConnectorClass(displayNameKey = "my.connector.display", configurationClass = MyConfiguration.class)`

- Extend the BaseConnector.

- Have a default constructor that performs the following:
    - call `setDriver()` to set the Driver implementation for your connector.
    - call `setAdapters()` to set adapter implementations pertaining to each data type for your connector.
       
See StubConnector in the test source for an example.

Also note that, in most cases, you should subclass 
either `BaseFullAccessConnector`, `BaseReadOnlyConnector` or `BaseWriteOnlyConnector`, based on your
the amount of access to your destination system that you want to allow or that it will allow. 
       
#### Adapters

*Abstract class `BaseAdapter`*

Adapters serve as the controller between the Connector (which is invoked by MidPoint)
 and the Driver (which makes calls to your destination system).  As a result, the adapter
 is primarily concerned with routing calls and mapping data from your models to/from ConnId
 Attribute values.

You should have adapters that extend the abstract classes for the object types you wish to control.
Most often, you will want to provide access to users and groups.  The methods you need to 
override with be responsible for converting data from ConnId 
Attribute values to your model fields, and vice versa.

Any adapter needs to implement the following methods:
- `getType()`
- `getIdentityModelClass()`
- `getConnectorAttributes()`
- `constructModel()`
- `constructAttributes()`

See the javadoc for BaseAdapter for further information on the above methods. Also See StubGroupsAdapter and StubUsersAdapter in the test source for an example.

#### Attribute

You need to define enumeration classes for each object type that
the Base Connector framework can use to define the ConnId Attribute names that
your connector implementation will use.

See StubGroupAttribute and StubUserAttribute in the test source for an example.

#### Attributes and Object Assignment

In order to have an object type that can be assigned to another object type (ex. assigning a user to a group),
you will need to utilize ConnectorAttributeDataType.ASSIGNMENT_IDENTIFIER.  Using
this special type, you will then be able to define an attribute that holds a list of
string identifiers that the object belongs to (ex. groups that a user belongs to).

In order for assignment to materially work, you'll also need to do the following -
- Make sure your adapter `constructModel()` method reads the incoming id's
to be associated and reflects them in the IdentityModel (see test package
`StubUsersAdapter` `constructModel()` for an example):

```
        user.setGroupIds(readAssignments(attributes, GROUP_IDS));
        user.setClubIds(readAssignments(attributes, CLUB_IDS));
```

- Make sure your adapter `constructAttributes()` method populates attributes based
on the list of id's from the IdentityModel object (see test package 
`StubUsersAdapter` `constructAttributes()` for an example):

```
        attributes.add(AttributeBuilder.build(GROUP_IDS.name(), user.getGroupIds()));
        attributes.add(AttributeBuilder.build(CLUB_IDS.name(), user.getClubIds()));
```
- Make sure your invocator reads the list of id's from the object model and handles
the associations as needed on the destination system.

#### Configuration

*Abstract base class `BaseConnectorConfiguration`*

Your connector must specify a configuration object in order to make configuration
values available to your connector as well as the other objects involved.  Your
configuration class must override the abstract class and must also have the
`org.identityconnectors.framework.spi.ConfigurationClass` annotation.

Your configuration is also required to have two constructors.  A zero-argument 
constructor is used by MidPoint to instantiate your class.  A second argument,
receving a String for configurationName, is needed in order to provide flexible
testing for unit and integration tests you may write for your connector.  

If your configuration values need to be loaded from a file (as most MidPoint
connectors do), your configuration must also override the getConfigurationFilePath(), which 
should look something like the below example. `displayMessageKey` and
`helpMessageKey` should provide helpful text for your Connector since this
will be displayed in MidPoint.

```    @Override
     @ConfigurationProperty(
             displayMessageKey = "Some Configuration File Path",
             helpMessageKey = "File path for the Some Configuration File",
             required = true)
     public String getConfigurationFilePath() {
         return getMidPointConfigurationFilePath();
     } 
```
     
If your Connector does NOT load its configuration values from a file, 
override `getConfigurationFilePath()` and return null.  To load configuration
values from some other means, write code to do so and assemble the configuration
values in a Java Properties object.  Then call `setConnectorProperties()` with
your Properties object.

If you have configuration values that you wish to be manageable from the MidPoint
UI, then you need to create `get` and `set` methods for each of these values.  The
`get` method must also be annotated with @ConfigurationProperty.  See the example 
below, for having a configuration value named `thing` which is a String:

```
private String thing;
 
     @ConfigurationProperty(
             displayMessageKey = "Pretend thing",
             helpMessageKey = "Some pretend thing value to be configured",
             required = true)
     public String getThing() {
         return thing;
     }
  
     public void set(String input) {
         this.thing = flag;
     }
```
 
#### Authenticator

*Interface `Authenticator`*

An Authenticator is an object that might be needed by a connector implementation
 in order to obtain some kind of access token (String) required to
 authenticate to its destination system (usually via a Driver).  Authenticators
 often relate to JWT or OAuth2 procedures that need to be followed in order
 to obtain an access token to be used with RESTful endpoints. There are several
 concrete Authenticators along these lines provided in the 
 `com.exclamationlabs.connid.base.connector.authenticator`, one of which might 
 hopefully fit the needs of your destination system.  If not, you can subclass one of
 them, or completely write your own that implements the Authenticator interface.

#### Driver

*Interface `Driver`*

*Abstract base classes `BaseDriver` and `BaseRestDriver` (for RESTful destination systems)* 

The Driver is responsible for issuing calls to the destination system for creating, listing,
updating and removing data.  Often this data will relate to IAM users and groups on the destination
system.

If the destination system you are connecting is not RESTful, you should seek to subclass
BaseDriver directly, or possibly subclass a protocol-specific subclass of BaseDriver suited
to how your destination system communicates.

If the driver you are writing is one that calls to RESTful web service endpoints, you
should subclass `BaseRestDriver`.  Doing so will greatly cut down on the amount
of code you need to write and will help handle HTTP-based JSON request and 
response interactions with minimum code.

#### REST Fault Processor

If you utilize `BaseRestDriver`, you should also implement the 
`RestFaultProcessor` interface in order to recognize and handle any error response
information returned from your destination system.

#### Invocator

*Interface `DriverInvocator`*

Each Driver should generally have at least one Invocator.  You will need an invocator for
each object type that you wish to control on your destination system.

The role of the Invocator is to use the communication protocol prescribed by the Driver and
make the actual requests to read, create, update and delete data on the destination system, particular
to a specific data type.  If you wish to manage users and groups with your IAM connector, you
should have an Invocator implementation for users and another for groups.

The Invocator `create(), update(), delete(), getOne() and getAll()` methods should perform those
respective operations on the destination system, using the input provided via the API.

### Examples and starting points

The best project to borrow from or use as a skeleton are currently [connector-base-h2-example](https://github.com/ExclamationLabs/connector-base-h2-example)
and [Zoom Connector](https://github.com/ExclamationLabs/connector-base-zoom).

## Current Open source connectors using the Base Connector framework:

- [Zoom Connector](https://github.com/ExclamationLabs/connector-base-zoom)

## Related projects
- The [Test Support](https://github.com/ExclamationLabs/connector-base-test-support) project is a test harness that is required by the Base
Connector and other connectors can also make us of.  Note that `connector-base-test-support` is a dependency
of this project, `connector-base`.

- The [H2 Example](https://github.com/ExclamationLabs/connector-base-h2-example) project is an example
to show you a relatively simple implementation of an end-use connector and how
it can be built in Gradle to be utilized in MidPoint.  This project leverages a just-in-time
 in-memory H2 database to test its IAM operations.

