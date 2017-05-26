import com.cloud.ops.toscamodel.INodeTemplate;
import com.cloud.ops.toscamodel.INodeType;
import com.cloud.ops.toscamodel.IToscaEnvironment;
import com.cloud.ops.toscamodel.Tosca;
import com.cloud.ops.toscamodel.impl.ToscaEmitter;
import com.cloud.ops.toscamodel.impl.ToscaEnvironment;
import org.junit.Ignore;
import org.junit.Test;

import java.io.*;

/**
 * Created by Nathan on 2017/4/7.
 */
@Ignore
public class ToscaEnvironmentTest {
    @Test
    public void testParse() throws FileNotFoundException {
        IToscaEnvironment tyaml = Tosca.newEnvironment();
//        tyaml.readFile(new InputStreamReader(this.getClass().getResourceAsStream("test.yaml")), false);
        tyaml.readFile("test123.yaml");
        INodeType rootNode = (INodeType) tyaml.getNamedEntity("tosca.nodes.Compute");
        Iterable<INodeTemplate> rootNodeTemplate = tyaml.getNodeTemplatesOfType(rootNode);
        for (INodeTemplate nodeType : rootNodeTemplate)
            System.out.println(nodeType);
    }
    @Test
    public void testWrite() throws IOException {
        IToscaEnvironment environment = Tosca.newEnvironment();
//        environment.readFile(new InputStreamReader(this.getClass().getResourceAsStream("test.yaml")), false);
        environment.readFile("test123.yaml");
        environment.writeFile(new FileWriter("test1234.yaml"));
    }
    @Test
    public void testWriteSequense() throws FileNotFoundException {
        ToscaEmitter emitter = new ToscaEmitter();
        try {
            emitter.WriteDocument(new PrintWriter(System.out), new ToscaEnvironment());
        } catch (IOException e) {
            throw new RuntimeException("zomg!");
        }
    }
}
