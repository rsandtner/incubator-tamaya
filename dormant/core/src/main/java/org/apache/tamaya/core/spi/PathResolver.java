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
package org.apache.tamaya.core.spi;

import org.apache.tamaya.core.resource.Resource;

import java.util.Collection;

/**
 * Created by Anatole on 16.06.2014.
 */
public interface PathResolver{

    /**
     * Get the (unique) resolver prefix.
     *
     * @return the resolver prefix, never null.
     */
    public String getResolverId();

    /**
     * Resolve the given expression.
     *
     * @param expressions expressions, never null.
     * @return the resolved URIs, never null.
     */
    public Collection<Resource> resolve(ClassLoader classLoader, Collection<String> expressions);

}
