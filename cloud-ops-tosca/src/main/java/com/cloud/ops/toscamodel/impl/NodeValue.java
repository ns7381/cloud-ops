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

import com.cloud.ops.toscamodel.INodeType;
import com.cloud.ops.toscamodel.IProperty;
import com.cloud.ops.toscamodel.ISchemaDefinition;
import com.cloud.ops.toscamodel.IValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pq on 20/04/2015.
 */
public abstract class NodeValue extends SchemaDefinition {
    final Map<String, IValue> attributes;
    final Map<String, IValue> allAttributes;
    final List<Map<String, Object>> requirements;
    final List<Map<String, Object>> allRequirements;
    final Map<String, Artifact> artifacts;
    final Map<String, Artifact> allArtifacts;

    public NodeValue(NodeType baseType, String description, Map<String, IProperty> properties, Map<String, ? extends Object> attributes) {
        super(baseType, description, properties);
        this.allAttributes = new HashMap<>();
        if(baseType!= null) {
            this.allAttributes.putAll(baseType.allAttributes());
            this.attributes = baseType.valueConvert(attributes);
        } else {
            this.attributes = new HashMap<>();
        }
        this.allAttributes.putAll(this.attributes);
        this.requirements = new ArrayList<>();
        this.allRequirements = new ArrayList<>();
        this.artifacts = new HashMap<>();
        this.allArtifacts = new HashMap<>();
    }

    public NodeValue(NodeType baseType, String description, Map<String, IProperty> properties,
                     Map<String, ? extends Object> attributes, List<Map<String, Object>> requirements,
                     Map<String, Artifact> artifacts) {
        super(baseType, description, properties);
        this.allAttributes = new HashMap<>();
        this.allArtifacts = new HashMap<>();
        this.allRequirements = new ArrayList<>();
        if (baseType != null) {
            this.allAttributes.putAll(baseType.allAttributes());
            this.attributes = baseType.valueConvert(attributes);
            this.allRequirements.addAll(baseType.allRequirements);
            this.requirements = requirements;
            this.artifacts = artifacts;
        } else {
            this.attributes = new HashMap<>();
            this.artifacts = new HashMap<>();
            this.requirements = new ArrayList<>();
        }
        this.allAttributes.putAll(this.attributes);
        this.allRequirements.addAll(this.requirements);
        this.allArtifacts.putAll(this.artifacts);
    }

    public Map<String, Artifact> declaredArtifacts() {
        return artifacts;
    }

    public Map<String, Artifact> allArtifacts() {
        return allArtifacts;
    }

    public Map<String, IValue> declaredAttributes() {
        return attributes;
    }

    public Map<String, IValue> allAttributes() {
        return allAttributes;
    }

    public List<Map<String, Object>> declaredRequirements() {
        return requirements;
    }

    public List<Map<String, Object>> allRequirements() {
        return allRequirements;
    }

    @Override
    public INodeType baseType() {

        return (INodeType) super.baseType();
    }

    @Override
    public boolean derivesFrom(ISchemaDefinition nodeType) {
        boolean equal = this.equals(nodeType);
        boolean derives = equal || (baseType != null && baseType().derivesFrom(nodeType));
        return derives;
    }
}
