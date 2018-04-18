package com.cloud.ops.tosca;

import com.cloud.ops.tosca.exception.ToscaParseException;
import com.cloud.ops.tosca.exception.ToscaParseKeyException;
import com.cloud.ops.tosca.model.Topology;
import com.cloud.ops.tosca.model.definition.*;
import com.cloud.ops.tosca.model.template.*;
import com.cloud.ops.tosca.model.type.NodeType;
import com.google.common.collect.Maps;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.events.Event;
import org.yaml.snakeyaml.events.ScalarEvent;

import java.io.Reader;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

final class ToscaParser {
    private Map<String, NodeType> nodeTypes = Maps.newHashMap();
    private Map<String, LocationTemplate> locationTemplates = Maps.newHashMap();
    private Topology topology = new Topology();
    private static final Yaml yaml = new Yaml();

    Topology parse(Reader input) {
        Iterator<Event> it = yaml.parse(input).iterator();
        Event e = it.next();
        expect(e.is(Event.ID.StreamStart));

        parseDocument(it);
        return this.topology;
    }

    Map<String, LocationTemplate> parseLocationTemplates(Reader input) {
        Iterator<Event> it = yaml.parse(input).iterator();
        Event e = it.next();
        expect(e.is(Event.ID.StreamStart));

        parseDocument(it);
        return this.locationTemplates;
    }

    Map<String, NodeType> parseNodeTypes(Reader input) {
        Iterator<Event> it = yaml.parse(input).iterator();
        Event e = it.next();
        expect(e.is(Event.ID.StreamStart));

        parseDocument(it);
        return this.nodeTypes;
    }

    private void parseDocument(Iterator<Event> it) {
        Event e;
        while (it.hasNext()) {
            e = it.next();
            if (e.is(Event.ID.DocumentStart)) {
                parseDocument(e, it);
            } else {
                expect(e.is(Event.ID.StreamEnd));
                expect(!it.hasNext());
            }
        }
    }

    private void parseDocument(Event event, Iterator<Event> it) {
        expect(event.is(Event.ID.DocumentStart));
        Event e = it.next();
        parseMapping(e, it, (key, value) -> {
            switch (key) {
                case "tosca_definitions_version":
                    String s = getString(value);
                    expect(s.equals("tosca_simple_yaml_1_0_0"));
                    break;
                case "node_types":
                    parseMapping(value, it, (k, v) -> parseNodeType(v, it, k));
                    break;
                case "location_types":
                    skip(value, it);
                    break;
                case "location_templates":
                    parseMapping(value, it, (k, v) -> parseLocationTemplate(v, it, k));
                    break;
                case "topology_template":
                    parseMapping(value, it, (k, v) -> {
                        switch (k) {
                            case "inputs":
                                parseMapping(v, it, (k1, v1) -> topology.getInputs().put(k1, parseDefinition(v1, it)));
                                break;
                            case "node_templates":
                                parseMapping(v, it, (k1, v1) -> parseNodeTemplate(v1, it, k1));
                                break;
                            case "relationship_templates":
                                skip(v, it);
                                break;
                            default:
                                throw new ToscaParseKeyException(k);
                        }
                    });
                    break;
                default:
                    throw new ToscaParseKeyException(key);
            }
        });
        e = it.next();
        expect(e.is(Event.ID.DocumentEnd));
    }

    private void parseNodeType(Event event, Iterator<Event> it, String nodeTypeName) {
        final String[] parentTypeName = {null};
        final Map<String, IValue> attributes = new HashMap<>();
        parseMapping(event, it, (k, v) -> {
            switch (k) {
                case "derived_from":
                    expect(parentTypeName[0] == null);
                    parentTypeName[0] = getString(v);
                    break;
                case "attributes":
                    parseMapping(v, it, (key, value) -> attributes.put(key, parseDefinition(value, it)));
                    break;
                default:
                    throw new ToscaParseKeyException(k);
            }
        });
        this.nodeTypes.put(nodeTypeName, NodeType.builder().derivedFrom(parentTypeName[0]).attributes(attributes).build());
    }

    private void parseLocationTemplate(Event e, Iterator<Event> it, String locationName) {
        final String[] parentTypeName = {null};
        final Map<String, IValue> attributes = new HashMap<>();
        parseMapping(e, it, (key, value) -> {
            switch (key) {
                case "type":
                    expect(parentTypeName[0] == null);
                    parentTypeName[0] = getString(value);
                    break;
                case "attributes":
                    parseMapping(value, it, (k, v) -> attributes.put(k, parseValue(v, it)));
                    break;
                default:
                    throw new ToscaParseKeyException(key);
            }
        });
        this.locationTemplates.put(locationName, LocationTemplate.builder().name(locationName).type(parentTypeName[0]).attributes(attributes).build());
    }

    private void parseNodeTemplate(Event e, Iterator<Event> it, String nodeName) {
        final String[] parentTypeName = {null};
        final String[] description = {null};
        final Map<String, IValue> attributes = new HashMap<>();
        final Map<String, Artifact> artifacts = new HashMap<>();
        final Map<String, Interface> interfaces = new HashMap<>();
        final List<Requirement> requirements = new ArrayList<>();
        parseMapping(e, it, (key, value) -> {
            switch (key) {
                case "type":
                    expect(parentTypeName[0] == null);
                    parentTypeName[0] = getString(value);
                    break;
                case "description":
                    expect(description[0] == null);
                    description[0] = getString(value);
                    break;
                case "properties":
                    skip(value, it);
                    break;
                case "capabilities":
                    skip(value, it);
                    break;
                case "requirements":
                    parseSequence(value, it, event ->
                            parseMapping(e, it, (k, v)
                                    -> requirements.add(new Requirement(k, getString(v)))));
                    break;
                case "attributes":
                    parseMapping(value, it, (k, v) -> attributes.put(k, parseValue(v, it)));
                    break;
                case "artifacts":
                    parseArtifacts(value, it, artifacts);
                    break;
                case "interfaces":
                    parseInterfaces(value, it, interfaces);
                    break;
                default:
                    throw new ToscaParseKeyException(key);
            }
        });
        NodeTemplate nodeTemplate = NodeTemplate.builder()
                .name(nodeName)
                .type(parentTypeName[0])
                .attributes(attributes)
                .artifacts(artifacts)
                .interfaces(interfaces)
                .requirements(requirements)
                .build();
        this.topology.getNodeTemplates().put(nodeName, nodeTemplate);
    }

    private void parseArtifacts(Event e, Iterator<Event> it, Map<String, Artifact> artifactMap) {
        parseMapping(e, it, (key, value) -> {
            Artifact artifact = new Artifact();
            parseMapping(value, it, (k, v) -> {
                switch (k) {
                    case "file":
                        artifact.setFile(getString(v));
                        break;
                    case "type":
                        artifact.setType(getString(v));
                        break;
                    default:
                        throw new ToscaParseKeyException(k);
                }
            });
            artifactMap.put(key, artifact);
        });
    }

    private void parseInterfaces(Event e, Iterator<Event> it, Map<String, Interface> interfaces) {
        parseMapping(e, it, (key, value) -> {
            switch (key) {
                case "Standard":
                    break;
                case "Configure":
                    parseOperations(value, it, interfaces);
                    break;
                default:
                    throw new ToscaParseKeyException(key);
            }
        });
    }

    private void parseOperations(Event e, Iterator<Event> it, Map<String, Interface> interfaceMap) {
        parseMapping(e, it, (key, value) -> {
            Interface anInterface = new Interface();
            parseMapping(value, it, (k, v) -> {
                switch (k) {
                    case "implementation":
                        anInterface.setImplementation(getString(v));
                        break;
                    case "dependencies":
                        List<Dependency> dependencies = new ArrayList<>();
                        parseSequence(v, it, event -> parseMapping(event, it, (k1, v1) ->
                                dependencies.add(new Dependency(k1, parseFunction(v1, it)))));
                        anInterface.setDependencies(dependencies);
                        break;
                    case "inputs":
                        Map<String, FunctionValue> inputs = new HashMap<>();
                        parseMapping(v, it, (k2, v2) -> inputs.put(k2, parseFunction(v2, it)));
                        anInterface.setInputs(inputs);
                        break;
                    default:
                        throw new ToscaParseKeyException(k);
                }
            });
            interfaceMap.put(key, anInterface);
        });
    }

    private FunctionValue parseFunction(Event event, Iterator<Event> it) {
        expect(event.is(Event.ID.MappingStart));
        FunctionValue result = new FunctionValue();
        parseMapping(event, it, (k, v) -> {
            List<String> list = new ArrayList<>();
            result.setFunction(k);
            if (v.is(Event.ID.Scalar)) {
                list.add(((ScalarEvent) v).getValue());
            } else if (v.is(Event.ID.SequenceStart)) {
                parseSequence(v, it, v1 -> list.add(getString(v1)));
            }
            result.setParameters(list);
        });
        return result;
    }

    private PropertyDefinition parseDefinition(Event event, Iterator<Event> it) {
        final String[] type = {null};
        final String[] description = {null};
        parseMapping(event, it, (k, v) -> {
            switch (k) {
                case "type":
                    expect(type[0] == null);
                    type[0] = getString(v);
                    break;
                case "description":
                    expect(description[0] == null);
                    description[0] = getString(v);
                    break;
                default:
                    throw new ToscaParseKeyException(k);
            }
        });
        return PropertyDefinition.builder().type(type[0]).description(description[0]).build();
    }

    private IValue parseValue(Event e, Iterator<Event> it) {
        if (e.is(Event.ID.Scalar)) {
            return new ScalarPropertyValue(((ScalarEvent) e).getValue());
        } else if (e.is(Event.ID.SequenceStart)) {
            ListPropertyValue result = new ListPropertyValue();
            parseSequence(e, it, m -> result.getValue().add(getString(m)));
            return result;
        } else if (e.is(Event.ID.MappingStart)) {
            return parseFunction(e, it);
        }
        return null;
    }

    private void parseMapping(Event event, Iterator<Event> it, BiConsumer<String, Event> fn) {
        expect(event.is(Event.ID.MappingStart));
        while (it.hasNext()) {
            Event e = it.next();
            if (e.is(Event.ID.MappingEnd)) {
                return;
            } else if (e.is(Event.ID.Scalar) && e instanceof ScalarEvent && it.hasNext()) {
                String key = ((ScalarEvent) e).getValue();
                Event value = it.next();
                fn.accept(key.intern(), value);
            } else {
                throw new ToscaParseException();
            }
        }
        throw new ToscaParseException();
    }

    private void parseSequence(Event event, Iterator<Event> it, Consumer<Event> fn) {
        expect(event.is(Event.ID.SequenceStart));
        while (it.hasNext()) {
            Event e = it.next();
            if (e.is(Event.ID.SequenceEnd))
                return;
            else
                fn.accept(e);
        }
        throw new ToscaParseException();
    }

    private String getString(Event e) {
        expect(e.is(Event.ID.Scalar));
        expect(e instanceof ScalarEvent);
        return ((ScalarEvent) e).getValue();
    }

    private void expect(boolean guard) {
        if (!guard)
            throw new ToscaParseException();
    }

    private void skip(Event e, Iterator<Event> it) {
        if (e.is(Event.ID.SequenceStart)) {
            parseSequence(e, it, m -> skip(m, it));
        } else if (e.is(Event.ID.MappingStart)) {
            parseMapping(e, it, (k, v) -> skip(v, it));
        }
    }
}
