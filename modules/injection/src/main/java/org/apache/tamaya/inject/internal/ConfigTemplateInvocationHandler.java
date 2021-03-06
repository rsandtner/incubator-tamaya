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
package org.apache.tamaya.inject.internal;

import org.apache.tamaya.Configuration;
import org.apache.tamaya.TypeLiteral;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Invocation handler that handles request against a configuration template.
 */
public final class ConfigTemplateInvocationHandler implements InvocationHandler {

    /*
    TODO
    the given method (in case of a template) can use different caching strategies:
    1) no caching (always evaluate the values completely) - slow.
    2) instance caching (a cache per instance).
    3) classloader caching...
    4) global shared cache.
     */


    /**
     * The configured type.
     */
    private ConfiguredType type;

    /**
     * Creates a new handler instance.
     * @param type           the target type, not null.
     * @param configurations overriding configurations to be used for evaluating the values for injection into {@code instance}, not null.
     *                       If no such config is passed, the default configurationa provided by the current
     *                       registered providers are used.
     */
    public ConfigTemplateInvocationHandler(Class<?> type, Configuration... configurations) {
        Objects.requireNonNull(configurations);

        this.type = new ConfiguredType(Objects.requireNonNull(type));
        if (!type.isInterface()) {
            throw new IllegalArgumentException("Can only proxy interfaces as configuration templates.");
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if ("toString".equals(method.getName())) {
            return "Configured Proxy -> " + this.type.getType().getName();
        }
        String configValue = InjectionUtils.getConfigValue(method);
        return InjectionUtils.adaptValue(method,  TypeLiteral.of(method.getReturnType()), configValue);
    }
}
