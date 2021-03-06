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
package org.apache.tamaya.format;

import org.apache.tamaya.spi.PropertySource;
import org.apache.tamaya.spi.PropertySourceProvider;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of a {@link org.apache.tamaya.spi.PropertySourceProvider} that reads configuration from
 * a given resource and in a given format.
 */
public abstract class BaseSimpleFormatPropertySourceProvider implements PropertySourceProvider {
    /**
     * The logger used.
     */
    private static final Logger LOG = Logger.getLogger(BaseSimpleFormatPropertySourceProvider.class.getName());
    /**
     * The config formats supported, not null.
     */
    private ConfigurationFormat configFormat;
    /**
     * The resource to be read, not null.
     */
    private URL resource;

    /**
     * Creates a new instance.
     *
     * @param format   the formas to be used, not null.
     * @param resource the resource to be read, not null.
     */
    public BaseSimpleFormatPropertySourceProvider(
            ConfigurationFormat format,
            URL resource) {
        this.configFormat = Objects.requireNonNull(format);
        this.resource = Objects.requireNonNull(resource);
    }

    /**
     * Method to create a {@link org.apache.tamaya.spi.PropertySource} based on the given entries read.
     *
     * @param entryTypeName the entry type of the entries read, not null.
     * @param entries       the entries read by the {@link ConfigurationFormat}
     * @param formatUsed    the format instance used to read the entries.
     * @return the {@link org.apache.tamaya.spi.PropertySource} instance ready to be registered.
     * @see ConfigurationFormat#getEntryTypes()
     */
    protected abstract PropertySource getPropertySource(String entryTypeName, Map<String, String> entries,
                                                        ConfigurationFormat formatUsed);

    /**
     * This method does dynamically resolve the paths using the current ClassLoader set. If no ClassLoader was
     * explcitly set during creation the current Thread context ClassLoader is used. If none of the supported
     * formats is able to parse a resource a WARNING log is written.
     *
     * @return the PropertySources successfully read
     */
    @Override
    public Collection<PropertySource> getPropertySources() {
        List<PropertySource> propertySources = new ArrayList<>();
        try {
            Map<String, Map<String, String>> entries = configFormat.readConfiguration(resource);
            for (Map.Entry<String, Map<String, String>> en : entries.entrySet()) {
                PropertySource ps = getPropertySource(en.getKey(), en.getValue(), configFormat);
                if (ps != null) {
                    propertySources.add(ps);
                } else {
                    LOG.info(() -> "Config Entries read ignored by PropertySourceFactory: format=" + configFormat +
                            ", entryType=" + en.getKey());
                }
            }
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Failed to add resource based config: " + resource, e);
        }
        return propertySources;
    }

}
