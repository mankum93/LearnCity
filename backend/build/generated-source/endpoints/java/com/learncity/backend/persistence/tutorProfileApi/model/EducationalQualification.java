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
 * (build: 2017-01-11 18:31:16 UTC)
 * on 2017-02-04 at 18:49:13 UTC 
 * Modify at your own risk.
 */

package com.learncity.backend.persistence.tutorProfileApi.model;

/**
 * Model definition for EducationalQualification.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the tutorProfileApi. For a detailed explanation see:
 * <a href="https://developers.google.com/api-client-library/java/google-http-java-client/json">https://developers.google.com/api-client-library/java/google-http-java-client/json</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class EducationalQualification extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private Duration duration;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String institution;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String qualificationName;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Integer yearOfPassing;

  /**
   * @return value or {@code null} for none
   */
  public Duration getDuration() {
    return duration;
  }

  /**
   * @param duration duration or {@code null} for none
   */
  public EducationalQualification setDuration(Duration duration) {
    this.duration = duration;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getInstitution() {
    return institution;
  }

  /**
   * @param institution institution or {@code null} for none
   */
  public EducationalQualification setInstitution(java.lang.String institution) {
    this.institution = institution;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getQualificationName() {
    return qualificationName;
  }

  /**
   * @param qualificationName qualificationName or {@code null} for none
   */
  public EducationalQualification setQualificationName(java.lang.String qualificationName) {
    this.qualificationName = qualificationName;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Integer getYearOfPassing() {
    return yearOfPassing;
  }

  /**
   * @param yearOfPassing yearOfPassing or {@code null} for none
   */
  public EducationalQualification setYearOfPassing(java.lang.Integer yearOfPassing) {
    this.yearOfPassing = yearOfPassing;
    return this;
  }

  @Override
  public EducationalQualification set(String fieldName, Object value) {
    return (EducationalQualification) super.set(fieldName, value);
  }

  @Override
  public EducationalQualification clone() {
    return (EducationalQualification) super.clone();
  }

}
