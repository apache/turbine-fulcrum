package org.apache.fulcrum.json.jackson.filters;

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

import org.apache.fulcrum.json.jackson.Jackson2MapperService;
import org.apache.fulcrum.json.jackson.Jackson2MapperService.CustomModule;

import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 * Used by {@link Jackson2MapperService#addAdapter(String, Class, Object)} to provide a wrapper module as a helper class
 * for the inner class {@link CustomModule}.
 * @author gkallidis
 *
 * @param <T>
 */
public class CustomModuleWrapper<T> {
    StdSerializer<T> ser;
    StdDeserializer<T> deSer;

    public CustomModuleWrapper(StdSerializer<T> ser, StdDeserializer<T> deSer) {
        this.ser = ser;
        this.deSer = deSer;
    }

    public StdSerializer<T> getSer() {
        return ser;
    }

    public void setSer(StdSerializer<T> ser) {
        this.ser = ser;
    }

    public StdDeserializer<T> getDeSer() {
        return deSer;
    }

    public void setDeSer(StdDeserializer<T> deSer) {
        this.deSer = deSer;
    }

}
