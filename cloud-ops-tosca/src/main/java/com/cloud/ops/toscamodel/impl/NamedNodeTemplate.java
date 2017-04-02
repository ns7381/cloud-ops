/*
 * Copyright 2015 Universita' di Pisa
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cloud.ops.toscamodel.impl;

import com.cloud.ops.toscamodel.INamedEntity;

/**
 * Created by pq on 20/04/2015.
 */
public class NamedNodeTemplate extends NodeTemplate implements INamedEntity {
    private String name;

    public NamedNodeTemplate(String name,NodeTemplate unnamedVersion) {
        super((NodeType)unnamedVersion.baseType,unnamedVersion.description, unnamedVersion.declaredProperties(), unnamedVersion.attributes);
        this.name = name;
    }

    @Override
    public String name() {
        return name;
    }


    @Override
    public String toString() {
        return name();
    }

    public void rename(String newEntityName) {
        name = newEntityName;
    }
}