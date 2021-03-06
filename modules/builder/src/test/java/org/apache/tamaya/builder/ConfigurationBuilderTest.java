/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tamaya.builder;

import org.apache.tamaya.ConfigException;
import org.apache.tamaya.Configuration;
import org.apache.tamaya.PropertyConverter;
import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.builder.util.mockito.NotMockedAnswer;
import org.apache.tamaya.builder.util.types.CustomTypeA;
import org.apache.tamaya.builder.util.types.CustomTypeB;
import org.apache.tamaya.spi.PropertySource;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import static org.apache.tamaya.builder.util.mockito.NotMockedAnswer.NOT_MOCKED_ANSWER;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class ConfigurationBuilderTest {

    @Test
    public void buildCanBuildEmptyConfiguration() {
        ConfigurationBuilder builder = new ConfigurationBuilder();

        Configuration config = builder.build();

        assertThat(config, notNullValue());
    }

    @Test(expected = IllegalStateException.class)
    public void buildCanBeCalledOnlyOnce() {
        ConfigurationBuilder builder = new ConfigurationBuilder();

        builder.build();
        builder.build();
    }

    /*********************************************************************
     * Tests for adding P r o p e r t y S o u r c e s
     */

    @Test(expected = NullPointerException.class)
    public void addPropertySourcesDoesNotAcceptNullValue() {
        ConfigurationBuilder builder = new ConfigurationBuilder();

        builder.addPropertySources((PropertySource[])null);
    }

    @Test(expected = IllegalStateException.class)
    public void propertySourceCanNotBeAddedAfterBuildingTheConfiguration() {
        PropertySource first = mock(PropertySource.class, NOT_MOCKED_ANSWER);

        doReturn("first").when(first).getName();

        ConfigurationBuilder builder = new ConfigurationBuilder().addPropertySources(first);

        builder.build();

        PropertySource second = mock(PropertySource.class, NOT_MOCKED_ANSWER);

        doReturn("second").when(first).getName();

        builder.addPropertySources(second);
    }

    @Test
    public void singleAddedPropertySourceIsUsed() {
        PropertySource source = mock(PropertySource.class, NOT_MOCKED_ANSWER);

        doReturn("one").when(source).getName();
        doReturn("a").when(source).get("keyOfA");

        ConfigurationBuilder builder = new ConfigurationBuilder().addPropertySources(source);

        Configuration config = builder.build();

        String valueOfA = config.get("keyOfA");

        assertThat(valueOfA, notNullValue());
        assertThat(valueOfA, equalTo("a"));
    }

    @Test
    public void twoAddedPropertySourcesAreUsed() {
        PropertySource sourceOne = mock(PropertySource.class, NOT_MOCKED_ANSWER);

        doReturn("one").when(sourceOne).getName();
        doReturn("b").when(sourceOne).get("keyOfA");
        doReturn(10).when(sourceOne).getOrdinal();

        PropertySource sourceTwo = mock(PropertySource.class, NOT_MOCKED_ANSWER);
        doReturn("two").when(sourceTwo).getName();
        doReturn("a").when(sourceTwo).get("keyOfA");
        doReturn(10).when(sourceTwo).getOrdinal();

        ConfigurationBuilder builder = new ConfigurationBuilder().addPropertySources(sourceOne)
                                                                 .addPropertySources(sourceTwo);

        Configuration config = builder.build();

        String valueOfA = config.get("keyOfA");

        assertThat(valueOfA, notNullValue());
        assertThat(valueOfA, equalTo("a"));
    }

    @Ignore
    @Test(expected = ConfigException.class)
    public void twoPropertySourcesSamePrioritySameKey() {
        PropertySource sourceOne = mock(PropertySource.class, NOT_MOCKED_ANSWER);

        doReturn("one").when(sourceOne).getName();
        doReturn("b").when(sourceOne).get("keyOfA");
        doReturn(20).when(sourceOne).getOrdinal();

        PropertySource sourceTwo = mock(PropertySource.class, NOT_MOCKED_ANSWER);
        doReturn("two").when(sourceTwo).getName();
        doReturn("a").when(sourceTwo).get("keyOfA");
        doReturn(20).when(sourceTwo).getOrdinal();

        ConfigurationBuilder builder = new ConfigurationBuilder().addPropertySources(sourceOne)
                                                                 .addPropertySources(sourceTwo);

        Configuration config = builder.build();

        config.get("keyOfA");
    }

    @Test
    public void twoPropertySourcesDiffPrioritySameKeyLowerAddedFirst() {
        PropertySource sourceOne = mock(PropertySource.class, NOT_MOCKED_ANSWER);

        doReturn("one").when(sourceOne).getName();
        doReturn("b").when(sourceOne).get("keyOfA");
        doReturn(10).when(sourceOne).getOrdinal();

        PropertySource sourceTwo = mock(PropertySource.class, NOT_MOCKED_ANSWER);
        doReturn("two").when(sourceTwo).getName();
        doReturn("a").when(sourceTwo).get("keyOfA");
        doReturn(20).when(sourceTwo).getOrdinal();

        ConfigurationBuilder builder = new ConfigurationBuilder().addPropertySources(sourceOne)
                                                                 .addPropertySources(sourceTwo);

        Configuration config = builder.build();

        String valueOfA = config.get("keyOfA");

        assertThat(valueOfA, notNullValue());
        assertThat(valueOfA, equalTo("a"));
    }

    @Test
    public void twoPropertySourcesDiffPrioritySameKeyHigherAddedFirst() {
        PropertySource sourceOne = mock(PropertySource.class, NOT_MOCKED_ANSWER);

        doReturn("one").when(sourceOne).getName();
        doReturn("b").when(sourceOne).get("keyOfA");
        doReturn(30).when(sourceOne).getOrdinal();

        PropertySource sourceTwo = mock(PropertySource.class, NOT_MOCKED_ANSWER);
        doReturn("two").when(sourceTwo).getName();
        doReturn("a").when(sourceTwo).get("keyOfA");
        doReturn(20).when(sourceTwo).getOrdinal();

        ConfigurationBuilder builder = new ConfigurationBuilder().addPropertySources(sourceOne, sourceTwo);

        Configuration config = builder.build();

        String valueOfA = config.get("keyOfA");

        assertThat(valueOfA, notNullValue());
        assertThat(valueOfA, equalTo("b"));
    }

    @Test
    public void consecutiveCallsToAddPropertySourceArePossible() {
        PropertySource sourceOne = mock(PropertySource.class, NOT_MOCKED_ANSWER);

        doReturn("one").when(sourceOne).getName();
        doReturn(null).when(sourceOne).get(anyString());
        doReturn("b").when(sourceOne).get("b");
        doReturn(30).when(sourceOne).getOrdinal();

        PropertySource sourceTwo = mock(PropertySource.class, NOT_MOCKED_ANSWER);
        doReturn("two").when(sourceTwo).getName();
        doReturn(null).when(sourceTwo).get(anyString());
        doReturn("a").when(sourceTwo).get("a");
        doReturn(30).when(sourceTwo).getOrdinal();

        ConfigurationBuilder builder = new ConfigurationBuilder().addPropertySources(sourceOne)
                                                                 .addPropertySources(sourceTwo);

        Configuration config = builder.build();

        assertThat(config.get("b"), equalTo("b"));
        assertThat(config.get("a"), equalTo("a"));
    }

    /**
     * ******************************************************************
     * Tests for adding P r o p e r t y C o n v e r t e r
     */

    @Test(expected = NullPointerException.class)
    public void canNotAddNullPropertyConverter() {
        ConfigurationBuilder builder = new ConfigurationBuilder();

        builder.addPropertyConverter(TypeLiteral.of(CustomTypeA.class), null);
    }

    @Test(expected = NullPointerException.class)
    public void canNotAddNullTypeLiteralButPropertyConverter() {
        ConfigurationBuilder builder = new ConfigurationBuilder();

        builder.addPropertyConverter((TypeLiteral<CustomTypeA>)null,
                                     prop -> new CustomTypeA(prop, prop));
    }

    @Test
    public void addedPropertyConverterWithTypeLiteralIsUsedByConfiguration() {
        PropertySource source = mock(PropertySource.class, NOT_MOCKED_ANSWER);

        doReturn("source").when(source).getName();
        doReturn("A").when(source).get("key");

        ConfigurationBuilder builder = new ConfigurationBuilder();

        builder.addPropertyConverter(TypeLiteral.of(CustomTypeA.class),
                                     prop -> new CustomTypeA(prop, prop))
               .addPropertySources(source);

        Configuration config = builder.build();

        Object resultRaw = config.get("key", CustomTypeA.class);

        assertThat(resultRaw, CoreMatchers.notNullValue());

        CustomTypeA result = (CustomTypeA)resultRaw;

        assertThat(result.getName(), equalTo("AA"));
    }

    @Test
    public void addedPropertyConverterWithClassIsUsedByConfiguration() {
        PropertySource source = mock(PropertySource.class, NOT_MOCKED_ANSWER);

        doReturn("source").when(source).getName();
        doReturn("A").when(source).get("key");

        ConfigurationBuilder builder = new ConfigurationBuilder();

        builder.addPropertyConverter(CustomTypeA.class,
                                     prop -> new CustomTypeA(prop, prop))
               .addPropertySources(source);

        Configuration config = builder.build();

        Object resultRaw = config.get("key", CustomTypeA.class);

        assertThat(resultRaw, CoreMatchers.notNullValue());

        CustomTypeA result = (CustomTypeA)resultRaw;

        assertThat(result.getName(), equalTo("AA"));
    }

    @Test
    public void canGetAndConvertPropertyViaOfMethod() {
        PropertySource source = mock(PropertySource.class, NOT_MOCKED_ANSWER);

        doReturn("source").when(source).getName();
        doReturn("A").when(source).get("key");

        ConfigurationBuilder builder = new ConfigurationBuilder();

        builder.addPropertySources(source);

        Configuration config = builder.build();

        Object resultRaw = config.get("key", CustomTypeB.class);

        assertThat(resultRaw, CoreMatchers.notNullValue());

        CustomTypeB result = (CustomTypeB)resultRaw;

        assertThat(result.getName(), equalTo("A"));
    }

    /*********************************************************************
     * Tests for adding P r o p e r t y F i l t e r
     */

    // @todo TAYAMA-60 Write more tests

    /*********************************************************************
     * Tests for adding P r o p e r t
     */

    // @todo TAYAMA-60 Write more tests

    /*********************************************************************
     * Tests for adding
     * P r o p e r t y V a l u e C o m b i n a t i o n P o l i c y
     */

    // @todo TAYAMA-60 Write more tests

    /*********************************************************************
     * Tests for enabling and disabling of automatic loading of
     * P r o p e r t y S o u r c e s
     */

    @Test
    public void enablingOfProvidedPropertySourceServiceProvidersIsOk() {
        ConfigurationBuilder builder = new ConfigurationBuilder();

        builder.disableProvidedPropertyConverters()
               .enableProvidedPropertyConverters();

        assertThat(builder.isPropertyConverterLoadingEnabled(), is(true));
    }

    @Test
    public void disablingOfProvidedPropertySourceServiceProvidersIsOk() {
        ConfigurationBuilder builder = new ConfigurationBuilder();

        builder.enableProvidedPropertyConverters()
               .disableProvidedPropertyConverters();

        assertThat(builder.isPropertyConverterLoadingEnabled(), is(false));
    }

}
