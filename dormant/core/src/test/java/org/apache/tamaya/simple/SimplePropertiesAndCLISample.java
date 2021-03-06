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
package org.apache.tamaya.simple;

import java.util.HashMap;
import java.util.Map;

import org.apache.tamaya.Configuration;
import org.apache.tamaya.core.ConfigurationFunctions;
import org.apache.tamaya.core.properties.PropertySourceBuilder;
import org.junit.Test;

/**
 * Created by Anatole on 24.02.14.
 */
public class SimplePropertiesAndCLISample {

    @Test
    public void testSystemPropertyResolution() {
        System.out.println(Configuration.evaluateValue("${sys:java.version}"));
    }

    @Test
    public void testProgrammatixPropertySet() {
        System.out.println(PropertySourceBuilder.of("testdata").addPaths("testdata", "classpath:testdata.properties").build());
    }

    @Test
    public void testProgrammaticConfig() {
//        ConfigurationFormat format = ConfigurationFormats.getPropertiesFormat();
        Map<String, String> cfgMap = new HashMap<>();
        cfgMap.put("param1", "value1");
        cfgMap.put("a", "Adrian"); // overrides Anatole
        Configuration config = Configuration.from(PropertySourceBuilder.of("myTestConfig").addPaths(
                "classpath:testdata.properties").addPaths("classpath:cfg/testdata.xml")
                .addArgs(new String[]{"-arg1", "--fullarg", "fullValue", "-myflag"}).addMap(cfgMap)
                .build());
        System.out.println(config.query(ConfigurationFunctions.getAreas()));
        System.out.println("---");
        System.out.println(config.query(ConfigurationFunctions.getAreas(s -> s.startsWith("another"))));
        System.out.println("---");
        System.out.println(config.query(ConfigurationFunctions.getTransitiveAreas()));
        System.out.println("---");
        System.out.println(config.query(ConfigurationFunctions.getTransitiveAreas(s -> s.startsWith("another"))));
        System.out.println("---");
        System.out.println(config);
        System.out.print("--- b=");
        System.out.println(config.get("b"));
//        System.out.println("--- only a,b,c)");
//        System.out.println(PropertySourceBuilder.of(config).filter((f) -> f.equals("a") || f.equals("b") || f.equals("c")).build());
    }

}
