package com.cloud.ops.tosca;

import com.cloud.ops.tosca.exception.ToscaException;
import com.cloud.ops.tosca.model.Topology;
import com.cloud.ops.tosca.model.template.LocationTemplate;
import com.cloud.ops.tosca.model.type.NodeType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.*;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Tosca {
    private static Map<String, NodeType> nodeTypes;
    private static final String NORMATIVE_TYPE_YML = "normative_types.yaml";

    public static Map<String, NodeType> getNodeTypes() {
        if (nodeTypes == null) {
            nodeTypes = new ToscaParser().parseNodeTypes(
                    new InputStreamReader(Tosca.class.getResourceAsStream(NORMATIVE_TYPE_YML)));
        }
        return nodeTypes;
    }

    public static Map<String, LocationTemplate> getLocationTemplates(String fileName) {
        try {
            return new ToscaParser().parseLocationTemplates(new FileReader(fileName));
        } catch (FileNotFoundException e) {
            throw new ToscaException("yml file not find.", e);
        }
    }

    public static Topology read(String fileName) {
        try {
            return new ToscaParser().parse(new FileReader(fileName));
        } catch (FileNotFoundException e) {
            throw new ToscaException("yml file not find.", e);
        }
    }

    public static Topology read(Reader input) {
        return new ToscaParser().parse(input);
    }

    public static void write(Topology topology, Writer output) {
        ToscaEmitter emitter = new ToscaEmitter();
        try {
            emitter.write(output, topology);
        } catch (IOException e) {
            throw new ToscaException();
        }
    }

    public static void write(Topology topology, String fileName) {
        ToscaEmitter emitter = new ToscaEmitter();
        try {
            emitter.write(new FileWriter(fileName), topology);
        } catch (IOException e) {
            throw new ToscaException();
        }
    }
}
