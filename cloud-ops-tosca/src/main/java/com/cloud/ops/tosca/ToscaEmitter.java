package com.cloud.ops.tosca;

import com.cloud.ops.tosca.exception.ToscaEmitterException;
import com.cloud.ops.tosca.model.Topology;
import com.cloud.ops.tosca.model.definition.*;
import com.cloud.ops.tosca.model.template.*;
import com.google.common.collect.ImmutableMap;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.emitter.Emitter;
import org.yaml.snakeyaml.events.*;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ToscaEmitter {
    private final DumperOptions options = new DumperOptions();

    void write(Writer target, Topology topology) throws IOException {
        StringWriter sw = new StringWriter();
        Emitter e = new Emitter(sw, options);
        e.emit(new StreamStartEvent(null, null));
        emitDocument(e, topology);
        e.emit(new StreamEndEvent(null, null));
        target.write(sw.toString().replaceAll("! ", "").replaceAll("\"", ""));
        target.flush();
    }

    private void emitDocument(Emitter e, Topology topology) throws IOException {
        e.emit(new DocumentStartEvent(null, null, false, options.getVersion(), options.getTags()));
        startMap(e);
        writeScalarValue(e, "tosca_definitions_version");
        writeScalarValue(e, "tosca_simple_yaml_1_0_0");
        writeScalarValue(e, "topology_template");
        startMap(e);
        if (!topology.getInputs().isEmpty()) {
            writeScalarValue(e, "inputs");
            startMap(e);
            topology.getInputs().forEach((name, input) -> {
                try {
                    writeScalarValue(e, name);
                    writeInput(e, input);
                } catch (IOException e1) {
                    throw new ToscaEmitterException(e1);
                }
            });
            endMap(e);
        }
        writeScalarValue(e, "node_templates");
        startMap(e);
        topology.getNodeTemplates().forEach((name, nodeTemplate) -> {
            try {
                writeScalarValue(e, name);
                writeNodeTemplate(e, nodeTemplate);
            } catch (IOException e1) {
                throw new ToscaEmitterException(e1);
            }
        });
        endMap(e);
        endMap(e);
        endMap(e);
        e.emit(new DocumentEndEvent(null, null, options.isExplicitEnd()));
    }

    private void writeNodeTemplate(Emitter e, NodeTemplate nodeTemplate) throws IOException {
        startMap(e);
        if (nodeTemplate.getType() != null) {
            writeScalarValue(e, "type");
            writeScalarValue(e, nodeTemplate.getType());
        }
        if (nodeTemplate.getAttributes() != null && !nodeTemplate.getAttributes().isEmpty()) {
            writeScalarValue(e, "attributes");
            writeMap(e, convert(nodeTemplate.getAttributes()));
        }
        if (nodeTemplate.getRequirements() != null && !nodeTemplate.getRequirements().isEmpty()) {
            writeScalarValue(e, "requirements");
            for (Requirement map : nodeTemplate.getRequirements()) {
                startSequence(e);
                writeRequirement(e, map);
                endSequence(e);
            }
        }
        if (nodeTemplate.getArtifacts() != null && !nodeTemplate.getArtifacts().isEmpty()) {
            writeScalarValue(e, "artifacts");
            startMap(e);
            for (Map.Entry<String, Artifact> entry : nodeTemplate.getArtifacts().entrySet()) {
                writeScalarValue(e, entry.getKey());
                startMap(e);
                if (entry.getValue().getFile() != null) {
                    writeScalarValue(e, "file");
                    writeScalarValue(e, entry.getValue().getFile());
                }
                if (entry.getValue().getType() != null) {
                    writeScalarValue(e, "type");
                    writeScalarValue(e, entry.getValue().getType());
                }
                endMap(e);
            }
            endMap(e);
        }
        if (nodeTemplate.getInterfaces() != null && !nodeTemplate.getInterfaces().isEmpty()) {
            writeScalarValue(e, "interfaces");
            startMap(e);
            writeScalarValue(e, "Configure");
            startMap(e);
            for (Map.Entry<String, Interface> entry : nodeTemplate.getInterfaces().entrySet()) {
                writeScalarValue(e, entry.getKey());
                startMap(e);
                Interface value = entry.getValue();
                if (value.getImplementation() != null) {
                    writeScalarValue(e, "implementation");
                    writeScalarValue(e, value.getImplementation());
                }
                if (value.getInputs() != null && !value.getInputs().isEmpty()) {
                    writeScalarValue(e, "inputs");
                    writeMap(e, convertFunction(value.getInputs()));
                }
                if (value.getDependencies() != null && !value.getDependencies().isEmpty()) {
                    writeScalarValue(e, "dependencies");
                    startSequence(e);
                    for (Dependency map : value.getDependencies()) {
                        writeMap(e, convertDependency(map));
                    }
                    endSequence(e);
                }
                endMap(e);
            }
            endMap(e);
            endMap(e);
        }
        endMap(e);
    }

    private void writeScalarValue(Emitter e, Object scalar) throws IOException {
        e.emit(new ScalarEvent(null, null, new ImplicitTuple(true, false), scalar.toString(), null, null, '\0'));
    }

    private void writeInput(Emitter e, PropertyDefinition propertyDefinition) throws IOException {
        startMap(e);
        writeScalarValue(e, "type");
        writeScalarValue(e, propertyDefinition.getType());
        writeScalarValue(e, "description");
        writeScalarValue(e, propertyDefinition.getDescription());
        endMap(e);
    }

    private void writeRequirement(Emitter e, Requirement requirement) throws IOException {
        startMap(e);
        writeScalarValue(e, requirement.getType());
        writeScalarValue(e, requirement.getValue());
        endMap(e);
    }

    private void startMap(Emitter e) throws IOException {
        e.emit(new MappingStartEvent(null, null, true, null, null, false));
    }

    private void endMap(Emitter e) throws IOException {
        e.emit(new MappingEndEvent(null, null));
    }

    private void writeMap(Emitter e, Map<String, Object> map) throws IOException {
        startMap(e);
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            writeScalarValue(e, entry.getKey());
            if (entry.getValue() instanceof Map) {
                writeScalarValue(e, entry.getValue().toString().replace("=", ": "));
            } else {
                writeScalarValue(e, entry.getValue());
            }
        }
        endMap(e);
    }

    private void endSequence(Emitter e) throws IOException {
        e.emit(new SequenceEndEvent(null, null));
    }

    private void startSequence(Emitter e) throws IOException {
        e.emit(new SequenceStartEvent(null, null, true, null, null, false));
    }

    private Map<String, Object> convertFunction(Map<String, FunctionValue> functionValueMap) {
        Map<String, Object> map = new HashMap<>();
        functionValueMap.forEach((k, v) -> map.put(k, ImmutableMap.of(v.getFunction(), v.getParameters())));
        return map;
    }

    private Map<String, Object> convert(Map<String, IValue> valueMap) {
        Map<String, Object> map = new HashMap<>();
        valueMap.forEach((k, v) -> {
            if (v instanceof ScalarPropertyValue) {
                map.put(k, ((ScalarPropertyValue) v).getValue());
            } else if (v instanceof ListPropertyValue) {
                map.put(k, ((ListPropertyValue) v).getValue());
            } else if (v instanceof FunctionValue) {
                List<String> parameters = ((FunctionValue) v).getParameters();
                if (parameters != null && parameters.size() == 1) {
                    map.put(k, ImmutableMap.of(((FunctionValue) v).getFunction(), parameters.get(0)));
                } else if (parameters != null && parameters.size() == 2) {
                    map.put(k, ImmutableMap.of(((FunctionValue) v).getFunction(), parameters));
                }
            }
        });
        return map;
    }

    private Map<String, Object> convertDependency(Dependency dependency) {
        Map<String, Object> map = new HashMap<>();
        map.put(dependency.getName(), ImmutableMap.of(dependency.getValue().getFunction(), dependency.getValue().getParameters()));
        return map;
    }
}
