package com.cloud.ops.toscamodel.impl;

import com.cloud.ops.toscamodel.*;
import com.cloud.ops.toscamodel.basictypes.impl.TypeList;
import com.cloud.ops.toscamodel.basictypes.impl.TypeString;
import com.cloud.ops.toscamodel.wf.WorkFlow;
import com.cloud.ops.toscamodel.wf.WorkFlowBuilder;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ToscaEnvironment implements IToscaEnvironment {

    public Object relationshipTemplate = null;
    private final TypeManager typeManager = new TypeManager(this);
    private TopologyContext topologyContext = null;
    private static final String relName = "normative_types.yaml";
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public ToscaEnvironment() {
        InputStream stream = this.getClass().getResourceAsStream(relName);
        readFile(new InputStreamReader(stream), true);
    }

    public ToscaEnvironment(String yamlFileName) {
        InputStream stream = this.getClass().getResourceAsStream(relName);
        readFile(new InputStreamReader(stream), true);
        try {
            readFile(yamlFileName, false);
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void readFile(Reader input, boolean hideTypes) {
        final Parser parser = new Parser(this, hideTypes);
        parser.Parse(input);
    }

    @Override
    public ToscaEnvironment readFile(String yamlFilePath, boolean hideTypes) throws FileNotFoundException {
        final Parser parser = new Parser(this, hideTypes);
        parser.Parse(new FileReader(yamlFilePath));
        return this;
    }

    @Override
    public void renameEntity(String entityName, String newEntityName) {
        typeManager.renameEntity(entityName, newEntityName);
    }

    @Override
    public void hideEntity(String entityName) {
        INamedEntity ret = getNamedEntity(entityName);
        if (ret instanceof NamedStruct)
            ((NamedStruct) ret).hidden = true;
        if (ret instanceof NamedNodeType)
            ((NamedNodeType) ret).hidden = true;
    }

    @Override
    public void unhideEntity(String entityName) {
        INamedEntity ret = getNamedEntity(entityName);
        if (ret instanceof NamedStruct)
            ((NamedStruct) ret).hidden = false;
        if (ret instanceof NamedNodeType)
            ((NamedNodeType) ret).hidden = false;
    }

    @Override
    public void writeFile(Writer output) {
        ToscaEmitter emitter = new ToscaEmitter();
        try {
            emitter.WriteDocument(output, this);
        } catch (IOException e) {
            throw new RuntimeException("zomg!");
        }

    }

    @Override
    public INamedEntity getNamedEntity(String entityName) {
        INamedEntity ret = null;
        if (ret == null)
            ret = (INamedEntity) typeManager.getNodeTemplate(entityName);
        if (ret == null)
            ret = (INamedEntity) typeManager.getNodeType(entityName);
        if (ret == null)
            ret = (INamedEntity) typeManager.getType(entityName);

        return ret;
    }


    @Override
    public Iterable<INodeTemplate> getNodeTemplatesOfType(INodeType rootType) {
        //return topology.getNodeTemplatesOfType(INodeType rootType);
        return typeManager.getNodeTemplatesOfType(rootType);
    }

    @Override
    public Iterable<INodeType> getNodeTypesDerivingFrom(INodeType rootType) {
        return typeManager.getNodeTypesDerivingFrom(rootType);
    }

    @Override
    public Iterable<ITypeStruct> getTypesDerivingFrom(ITypeStruct rootType) {

        return typeManager.getTypesDerivingFrom(rootType);
    }

    @Override
    public INamedEntity registerType(String entityName, IType t) {
        return typeManager.registerType(entityName, t);
    }

    @Override
    public INamedEntity registerNodeType(String entityName, INodeType t) {
        return typeManager.registerNodeType(entityName, t);
    }

    @Override
    public INamedEntity registerNodeTemplate(String entityName, INodeTemplate t) {
        return typeManager.registerNodeTemplate(entityName, t);
    }

    @Override
    public INamedEntity importWithSupertypes(INamedEntity entity) {
        INamedEntity res = getNamedEntity(entity.name());
        if (res != null)
            return res;
        if (entity instanceof INodeType) res = typeManager.importNodeType(entity);
        else if (entity instanceof INodeTemplate) res = typeManager.importNodeTemplate(entity);
        else if (entity instanceof ITypeStruct) res = typeManager.importStructType(entity);
        else {
            throw new NotImplementedException();
        }
        return res;
    }

    @Override
    public INodeTemplate newTemplate(INodeType type) {
        return new NodeTemplate((NamedNodeType) type, "", Collections.emptyMap(), Collections.emptyMap());
    }

    public TopologyContext getTopologyContext() {
        if (this.topologyContext == null) {
            INodeType rootNode = (INodeType) this.getNamedEntity("tosca.nodes.Root");
            Iterable<INodeTemplate> rootNodeTemplate = this.getNodeTemplatesOfType(rootNode);
            Map<String, NodeTemplateDto> nodeTemplateDtoMap = new HashMap<>();
            for (INodeTemplate nodeTemplate : rootNodeTemplate) {
                nodeTemplateDtoMap.put(nodeTemplate.toString(), NodeTemplateDto.convert(nodeTemplate));
            }
            this.topologyContext = TopologyContext.builder().nodeTemplateMap(nodeTemplateDtoMap).build();
        }
        return this.topologyContext;
    }

    @Override
    public WorkFlow getWorkFlow(String workFlowName) {
        return WorkFlowBuilder.buildWorkFlow(this.getTopologyContext(), workFlowName);
    }


    @Override
    public TopologyContext getTopologyWithWorkFlow(String workFlowName) {
        if (this.topologyContext == null) {
            this.getTopologyContext();
        }
        if (this.topologyContext.getWorkFlowMap() == null) {
            Map<String, WorkFlow> wfs = topologyContext.getWorkFlowMap();
            if (wfs == null) {
                wfs = Maps.newLinkedHashMap();
                topologyContext.setWorkFlowMap(wfs);
            }
            WorkFlow workFlow = WorkFlowBuilder.buildWorkFlow(this.topologyContext, workFlowName);
            topologyContext.getWorkFlowMap().put(workFlowName, workFlow);
        }
        return this.topologyContext;
    }

    @Override
    public void updateAttribute(TopologyContext context, String yamlFilePath) {
        context.getNodeTemplateMap().forEach((name, node) -> {
            this.updateNodeAttr(name, node.getAttributes());
        });
        try {
            this.writeFile(new FileWriter(yamlFilePath));
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void updateAttribute(String nodeName, Map<String, Object> attributes, String yamlFilePath) {
        try {
            updateNodeAttr(nodeName, attributes);
            this.writeFile(new FileWriter(yamlFilePath));
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    private void updateNodeAttr(String nodeName, Map<String, Object> attributes) {
        INodeType nodeTypes = (INodeType) this.getNamedEntity("tosca.nodes.Root");
        Iterable<INodeTemplate> nodes = this.getNodeTemplatesOfType(nodeTypes);
        for (INodeTemplate node : nodes) {
            if (nodeName.equals(node.toString())) {
                for (Map.Entry<String, Object> attribute : attributes.entrySet()) {
                    Object value = attribute.getValue();
                    if (value instanceof String) {
                        node.declaredAttributes().put(attribute.getKey(), TypeString.instance().instantiate(value));
                    } else if (value instanceof List) {
                        node.declaredAttributes().put(attribute.getKey(), TypeList.instance(TypeString.instance()).instantiate(value));
                    }
                }
            }
        }
    }
};
