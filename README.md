# connector-base
### Java Base Connector Framework for Identity Access Managment (IAM) and Midpoint integration

This project serves as a base framework for various IAM connectors.  Its purpose
is to greatly reduce code repetition and complexity when writing new connectors
and minimize the interactions with the ConnId framework and have much of that
taken care of by a common API.

## Gradle and Build Process

Connector-base is a Gradle project based on Java 1.8.  To test and build the project, simply use

`gradle build`

If you need to fully clean the prior build first, then run -

`gradle clean build`

The build command will produce two small JARs, connector-base-{version}.jar
and connector-base-test-{version}.jar.  The first jar contains Connector Base
framework API/code classes.  The second jar contains test abstractions and utilities
to help Connectors keep their test classes more simple and concise.

These are both 'thin' jars and contain
no dependencies inside of them.  The actual project leveraging the Base Connector
framework will be responsible for referencing the dependencies it needs, in addition
to the dependencies that the Base Connector framework is relying upon.

The critical dependencies of the Base Connector framework are described in build.gradle under
'Vital dependencies' block.  The base framework will not work if your project
doesn't include these or a comparable version.  The remaining dependencies
are dependent on your particular connector's needs ... for example if you have
a simple CSV connector, you should not need HTTP/RESTful or JWT support at runtime
and shouldn't need to include these dependencies in your connector project.

The 'connector-h2-example' project (forthcoming) is intended as an example
to show you a relatively simple implementation of an end-use connector and how it
is can be built in Gradle to be utilized in MidPoint.
