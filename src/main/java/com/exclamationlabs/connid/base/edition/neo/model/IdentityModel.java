/*
    Copyright 2020 Exclamation Labs

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

package com.exclamationlabs.connid.base.edition.neo.model;

/**
 * Interface to describe model objects belonging to this base connector implementation.
 *
 * <p>To guarantee uniqueness and make items identifiable in the framework, all classes that
 * implement IdentityModel should not only implement methods as required by the interface, but also
 * do the following:
 *
 * <p>1) Override the toString() method. You can make a call to identityToString() method to
 * generate default id and name output for the IdentityModel, or implement your own method as
 * needed. 2) Override the equals() method in order to see if two IdentityModel objects are equal or
 * not. You can make a call to identityEquals() method to assist with implementing the equals
 * method, or write your own if needed. Commonly only the id would needed to compare if IAM would
 * deem two objects as equals. 3) Override the hashcode() method in order to provide a hash used to
 * uniquely identify this object. You can make a call to identityHashCode() method to assist with
 * implementing this logic, or write your own if needed. Commonly only the id would need to be
 * evaluated in order to provide a hashcode.
 */
public interface IdentityModel {}
