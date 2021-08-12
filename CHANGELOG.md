# connector-base
### Java Base Connector Framework for Identity Access Management (IAM) and Midpoint integration

This software is Copyright 2020 Exclamation Labs.  Licensed under the Apache License, Version 2.0.

This project serves as a base framework for various IAM connectors.  Its purpose
is to greatly reduce code repetition and complexity when writing new connectors
and minimize the interactions with the ConnId framework and have much of that
taken care of by a common API.

# Change Log

+ **1.1.5** - Add support for nativeName attribute info to schema building(08/12/2021)
+ **1.1.4** - Add retry logic support for BaseRestDriver(05/10/2021)
+ **1.1.3** - Added CHANGELOG.md for versions and removed Javadoc support from repo/gradle (05/03/2021)
+ **1.1.2** - Remove final keyword on BaseAdapter `get` method to support customization (04/26/2021)
+ **1.1.1** - Added BaseSoapDriver and SOAP support to base framework(04/16/2021)
+ **1.1.0** - Allow passage of IAM paging data (in `OperationOptions`) and other custom data to 
Driver and Invocator levels via `Map<String,Object>`(04/16/2021)
+ **1.0.7** - Allow support for retrieving HTTP response header data to BaseRestDriver (03/14/2021)
+ **1.0.6** - Added OAuth2 support for `Scope` for Client Credentials authentication type (03/08/2021)
+ **1.0.5** - Improved mock REST response for unit testing in `connector-base-test-support` (02/16/2021)
+ **1.0.4** - Fix attribute handling of Attribute passing to response handler (02/09/2021)
+ **1.0.3** - Many fixes for custom `ObjectClass` types support (02/09/2021)
+ **1.0.2** - Improved generics in base framework and added Invocator support and visibility into 
 BaseRestDriver execute methods. (01/27/2021)
+ **1.0.0** - Major refactor: Added support for custom `ObjectClass` types to base framework.  Removed hard definitions 
for users and groups types. (01/11/2021)
+ **0.8.0** - Pass through `OperationOptions` to Adapter level for paging/additional data support (11/19/2020)
+ **0.7.0** - Add retry support to BaseRestDriver for expired Authenticator token (07/28/2020)
+ **0.6.0** - Add custom properties Domain and Subdomain to configuration for `Sharefile` (07/22/2020)
+ **0.5.0** - Add additional headers support to configuration from Authenticator, additional work for `RingCentral` (07/15/2020)
+ **0.4.0** - Add additional headers support to configuration from Authenticator for (07/08/2020)
+ **0.3.0** - Added custom property support for `GotoMeeting` (07/07/2020)
+ **0.2.0** - Fixes for RefreshTokenAuthenticator for `WebEx`, clear JDK properties prior to OAuth2 execution for 
 correctness in Midpoint (07/07/2020)
+ **0.1.0** - Initial implementation of base framework (06/03/2020)