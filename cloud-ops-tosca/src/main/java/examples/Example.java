package examples;

import com.cloud.ops.toscamodel.INodeTemplate;
import com.cloud.ops.toscamodel.INodeType;
import com.cloud.ops.toscamodel.IToscaEnvironment;
import com.cloud.ops.toscamodel.Tosca;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

/**
 * Created by pq on 27/04/2015.
 */
public class Example {

    public static void main(String[] params) {
        IToscaEnvironment aam = Tosca.newEnvironment();
        IToscaEnvironment discoverer = Tosca.newEnvironment();
        InputStream stream = Example.class.getResourceAsStream("../input/aam.yaml");
        aam.readFile(new InputStreamReader(stream));
        stream = Example.class.getResourceAsStream("../input/amazon_c1_xlarge.yaml");
        discoverer.readFile(new InputStreamReader(stream));
        stream = Example.class.getResourceAsStream("../input/platform_offerings_test.yaml");
        discoverer.readFile(new InputStreamReader(stream));
        //stream = Example.class.getResourceAsStream("\\input\\hp_cloud_serv.yaml");
        //discoverer.readFile(new InputStreamReader(stream),true);
        Matchmaker m = new Matchmaker(discoverer);
        Map<String, List<INodeType>> matches = m.Match(aam);
        System.out.println(matches);
        OptimizerExample o = new OptimizerExample();
        final List<IToscaEnvironment> plans = o.optimizeFullSearchCartesian(aam, matches);
        for (IToscaEnvironment plan : plans) {
            PrintWriter p = new PrintWriter(System.out);
            plan.writeFile(p);
            p.flush();
            //p.close();
        }


    }
}
