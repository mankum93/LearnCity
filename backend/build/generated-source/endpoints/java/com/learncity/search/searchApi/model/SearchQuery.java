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
 * on 2017-02-03 at 10:52:07 UTC 
 * Modify at your own risk.
 */

package com.learncity.search.searchApi.model;

/**
 * Model definition for SearchQuery.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the Search API. For a detailed explanation see:
 * <a href="https://developers.google.com/api-client-library/java/google-http-java-client/json">https://developers.google.com/api-client-library/java/google-http-java-client/json</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class SearchQuery extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String cursor;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Integer limit;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String qualification;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String subject;

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getCursor() {
    return cursor;
  }

  /**
   * @param cursor cursor or {@code null} for none
   */
  public SearchQuery setCursor(java.lang.String cursor) {
    this.cursor = cursor;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Integer getLimit() {
    return limit;
  }

  /**
   * @param limit limit or {@code null} for none
   */
  public SearchQuery setLimit(java.lang.Integer limit) {
    this.limit = limit;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getQualification() {
    return qualification;
  }

  /**
   * @param qualification qualification or {@code null} for none
   */
  public SearchQuery setQualification(java.lang.String qualification) {
    this.qualification = qualification;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getSubject() {
    return subject;
  }

  /**
   * @param subject subject or {@code null} for none
   */
  public SearchQuery setSubject(java.lang.String subject) {
    this.subject = subject;
    return this;
  }

  @Override
  public SearchQuery set(String fieldName, Object value) {
    return (SearchQuery) super.set(fieldName, value);
  }

  @Override
  public SearchQuery clone() {
    return (SearchQuery) super.clone();
  }

}
