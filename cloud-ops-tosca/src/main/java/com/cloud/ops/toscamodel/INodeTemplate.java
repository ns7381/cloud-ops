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

package com.cloud.ops.toscamodel;

import com.cloud.ops.toscamodel.impl.Artifact;

import java.util.List;
import java.util.Map;

/**
 * Created by pq on 16/04/2015.
 */
public interface INodeTemplate extends ISchemaDefinition {
    Map<String,Artifact> declaredArtifacts();
    Map<String,Artifact> allArtifacts();
    Map<String,IValue> declaredAttributes();
    Map<String,IValue> allAttributes();

    List<Map<String,Object>> declaredRequirements();
    List<Map<String,Object>> allRequirements();

    @Override
    INodeTemplate addProperty(String propName, IType propType, Object defaultValue);

    @Override
    INodeTemplate changeDescription(String newDescription);

    @Override
    INodeType baseType();
}
