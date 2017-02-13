/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
/*
 * This code was generated by https://github.com/google/apis-client-generator/
 * (build: 1969-12-31 23:59:59 UTC)
 * on 2017-02-12 at 07:01:06 UTC 
 * Modify at your own risk.
 */

package com.learncity.backend.persistence.genericLearnerProfileApi.model;

/**
 * Model definition for GenericLearnerProfile.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the genericLearnerProfileApi. For a detailed explanation
 * see:
 * <a href="https://developers.google.com/api-client-library/java/google-http-java-client/json">https://developers.google.com/api-client-library/java/google-http-java-client/json</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class GenericLearnerProfile extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Integer currentStatus;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String emailID;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String imagePath;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String name;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String password;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String phoneNo;

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Integer getCurrentStatus() {
    return currentStatus;
  }

  /**
   * @param currentStatus currentStatus or {@code null} for none
   */
  public GenericLearnerProfile setCurrentStatus(java.lang.Integer currentStatus) {
    this.currentStatus = currentStatus;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getEmailID() {
    return emailID;
  }

  /**
   * @param emailID emailID or {@code null} for none
   */
  public GenericLearnerProfile setEmailID(java.lang.String emailID) {
    this.emailID = emailID;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getImagePath() {
    return imagePath;
  }

  /**
   * @param imagePath imagePath or {@code null} for none
   */
  public GenericLearnerProfile setImagePath(java.lang.String imagePath) {
    this.imagePath = imagePath;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getName() {
    return name;
  }

  /**
   * @param name name or {@code null} for none
   */
  public GenericLearnerProfile setName(java.lang.String name) {
    this.name = name;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getPassword() {
    return password;
  }

  /**
   * @param password password or {@code null} for none
   */
  public GenericLearnerProfile setPassword(java.lang.String password) {
    this.password = password;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getPhoneNo() {
    return phoneNo;
  }

  /**
   * @param phoneNo phoneNo or {@code null} for none
   */
  public GenericLearnerProfile setPhoneNo(java.lang.String phoneNo) {
    this.phoneNo = phoneNo;
    return this;
  }

  @Override
  public GenericLearnerProfile set(String fieldName, Object value) {
    return (GenericLearnerProfile) super.set(fieldName, value);
  }

  @Override
  public GenericLearnerProfile clone() {
    return (GenericLearnerProfile) super.clone();
  }

}
