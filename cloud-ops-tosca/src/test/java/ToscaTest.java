import com.cloud.ops.tosca.Tosca;
import com.cloud.ops.tosca.model.Topology;
import com.cloud.ops.tosca.model.type.NodeType;
import org.junit.Test;

import java.io.*;
import java.util.Map;

/**
 * Created by Nathan on 2017/4/7.
 */
public class ToscaTest {
    private String fileName = "tomcat-mysql-app/topology.yaml";

    @Test
    public void testGetNodeTypes() throws FileNotFoundException {
        Map<String, NodeType> nodeTypes = Tosca.getNodeTypes();
        System.out.println(nodeTypes);
    }

    @Test
    public void testParse() throws FileNotFoundException {
        Topology read = Tosca.read(new InputStreamReader(this.getClass().getResourceAsStream(fileName)));
        System.out.println(read);
    }

    @Test
    public void testWrite() throws IOException {
        Tosca.write(Tosca.read(new InputStreamReader(this.getClass().getResourceAsStream(fileName))), new PrintWriter(new OutputStreamWriter(System.out)));
    }
}
