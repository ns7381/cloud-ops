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

import com.cloud.ops.toscamodel.impl.TopologyContext;
import com.cloud.ops.toscamodel.wf.WorkFlow;

import java.io.FileNotFoundException;
import java.io.Writer;
import java.util.Map;

/**
 * Created by pq on 16/04/2015.
 */
public interface IToscaEnvironment {
    INamedEntity getNamedEntity(String entityName);

    INamedEntity registerType(String entityName, IType t);

    INamedEntity registerNodeType(String entityName, INodeType t);

    INamedEntity registerNodeTemplate(String entityName, INodeTemplate t);

    /***
     *  Imports a named entity from another environment, with all its supertypes and used data types.
     *  When an entity with the same name is present it is assumed to be consistent and used without importing a new one
     * @param entity the named entity to be imported
     */
    INamedEntity importWithSupertypes(INamedEntity entity);

    INodeTemplate newTemplate(INodeType type);

    Iterable<INodeTemplate> getNodeTemplatesOfType(INodeType rootType);

    TopologyContext getTopologyContext();

    WorkFlow getWorkFlow(String workFlowName);

    TopologyContext getTopologyWithWorkFlow(String workFlowName);

    Iterable<INodeType> getNodeTypesDerivingFrom(INodeType rootType);

    Iterable<ITypeStruct> getTypesDerivingFrom(ITypeStruct rootType);

    void readFile(String yamlFilePath, boolean hideTypes) throws FileNotFoundException;

    default void readFile(String yamlFilePath) throws FileNotFoundException {
        readFile(yamlFilePath, false);
    }

    void renameEntity(String entityName, String newEntityName);

    void hideEntity(String entityName);

    void unhideEntity(String entityName);

    void writeFile(Writer output);

    void updateAttribute(String nodeId, Map<String, Object> attributes);
}
