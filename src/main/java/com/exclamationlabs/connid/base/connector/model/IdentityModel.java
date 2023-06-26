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

package com.exclamationlabs.connid.base.connector.model;

import org.apache.commons.lang3.StringUtils;

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
public interface IdentityModel {

  /**
   * Implement this method to return the id value applicable to the concrete model object.
   *
   * @return String version of Id value recognized by model
   */
  String getIdentityIdValue();

  /**
   * Implement this method to return the name applicable to the concrete model object.
   *
   * @return String version of the name recognized by model
   */
  String getIdentityNameValue();

  default String getValueBySearchableAttributeName(String attributeName) {
    return null;
  }

  /**
   * Utility method to help IdentityModel implementations produce a default toString()
   * representation of the object. This implementation simply returns the id and name value of the
   * IdentityModel object.
   *
   * @return String containing id and name value of the IdentityModel object, separated by a ';'
   *     character.
   *     <p>See test class com.exclamationlabs.connid.base.connector.stub.model.StubUser toString()
   *     method for an example usage of identityToString.
   */
  default String identityToString() {
    return getIdentityIdValue() + ";" + getIdentityNameValue();
  }

  /**
   * Utility method to help IdentityModel implementations produce an equals method for the object.
   * This equals method behavior makes sure the provided object class of the IdentityModel matches,
   * and the id values are equal.
   *
   * <p>See test class com.exclamationlabs.connid.base.connector.stub.model.StubUser equals() method
   * for an example usage of identityEquals.
   *
   * @param identityClass Concrete class of the IdentityModel to be used for equality comparison.
   * @param compareSource IdentityModel object of the instance needing to perform an equals
   *     comparison.
   * @param compareTo Object passed in to be checked for equality.
   * @return True if objects are equal, false if not.
   */
  default boolean identityEquals(
      Class<? extends IdentityModel> identityClass, IdentityModel compareSource, Object compareTo) {
    return identityClass.isInstance(compareTo)
        && StringUtils.equals(
            ((IdentityModel) compareTo).getIdentityIdValue(), compareSource.getIdentityIdValue());
  }

  /**
   * Utility method to help IdentityModel implementations produce a hashcode method for the object.
   * This hashcode behavior generates a hash based solely on the id value.
   *
   * <p>See test class com.exclamationlabs.connid.base.connector.stub.model.StubUser hashCode()
   * method for an example usage of identityHashCode.
   *
   * @return Integer denoting distinct hash for this IdentityModel, based on id value.
   */
  default int identityHashCode() {
    return getIdentityIdValue().hashCode();
  }
}
