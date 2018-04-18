import com.cloud.ops.tosca.Tosca;
import com.cloud.ops.tosca.model.template.NodeTemplate;
import com.cloud.ops.tosca.model.workflow.WorkFlow;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * @author ningsheng
 * @version 1.0
 * @date 2017/8/19
 */
public class TopologyTest {
    private String fileName = "tomcat-mysql-app/topology.yaml";
    private String osFileName = "tomcat-mysql-openstack/topology.yaml";

    @Test
    public void testGetWorkFlow() throws IOException {
        WorkFlow patch_deploy = Tosca.read(new InputStreamReader(this.getClass().getResourceAsStream(fileName))).getWorkFlow("tomcat","patch_deploy");
        System.out.println(patch_deploy);
    }
    @Test
    public void testGetOpenstackWorkFlow() throws IOException {
        WorkFlow patch_deploy = Tosca.read(new InputStreamReader(this.getClass().getResourceAsStream(osFileName))).getWorkFlow("mysql","create");
        System.out.println(patch_deploy);
    }

    @Test
    public void testGetNodeTemplatesOfType() throws IOException {
        List<NodeTemplate> patch_deploy = Tosca.read(new InputStreamReader(this.getClass().getResourceAsStream(fileName))).getNodeTemplatesOfType("tosca.nodes.Compute");
        System.out.println(patch_deploy);
    }

    @Test
    public void testGetComputeType() throws IOException {
        String patch_deploy = Tosca.read(new InputStreamReader(this.getClass().getResourceAsStream(fileName))).getComputeType("tosca.nodes.Compute");
        System.out.println(patch_deploy);
    }
}
