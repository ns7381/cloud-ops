import com.cloud.ops.toscamodel.INodeTemplate;
import com.cloud.ops.toscamodel.INodeType;
import com.cloud.ops.toscamodel.IToscaEnvironment;
import com.cloud.ops.toscamodel.Tosca;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;

/**
 * Created by Nathan on 2017/4/7.
 */
public class ToscaEnvironmentTest {
    @Test
    public void testParse() throws FileNotFoundException {
        IToscaEnvironment tyaml = Tosca.newEnvironment();
        tyaml.readFile(new InputStreamReader(this.getClass().getResourceAsStream("test.yaml")), false);
        INodeType rootNode = (INodeType) tyaml.getNamedEntity("tosca.nodes.Compute");
        Iterable<INodeTemplate> rootNodeTemplate = tyaml.getNodeTemplatesOfType(rootNode);
        for (INodeTemplate nodeType : rootNodeTemplate)
            System.out.println(nodeType);
    }
}
