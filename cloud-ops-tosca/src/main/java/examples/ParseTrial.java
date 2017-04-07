package examples;

import com.cloud.ops.toscamodel.INodeTemplate;
import com.cloud.ops.toscamodel.INodeType;
import com.cloud.ops.toscamodel.IToscaEnvironment;
import com.cloud.ops.toscamodel.Tosca;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/* FileReader */

/**
 * Created by Mattia Buccarella
 **/
public class ParseTrial {

    static void FatalError(String errorMessage) {
        System.out.println("FATAL ERROR: " + errorMessage);
        System.out.println("The application will now terminate.");
        System.out.println();
        System.exit(2);
    }

    static void printUsage() {
        System.out.println("Usage java ParseTrial <file name>");
        System.out.println();
        System.exit(1);
    }

    public static void main(String[] args) {
        if (args.length < 1)
            printUsage();

		/* opening file */
        String yamlFileName = args[0];
        FileReader fr = null;
        try {
            fr = new FileReader(yamlFileName);
        } catch (FileNotFoundException fnfex) {
            System.out.println("Cannot find specified file: " + yamlFileName);
            printUsage();
            return;
        }

		/* parsing */
        IToscaEnvironment tyaml = Tosca.newEnvironment();
        tyaml.readFile(fr, false);
		
		/* closing */
        try {
            fr.close();
        } catch (IOException ioex) {
            FatalError(ioex.getMessage());
        }
		
		/* printing stuff */
        for (INodeTemplate nodeType : tyaml.getNodeTemplatesOfType((INodeType) tyaml.getNamedEntity("tosca.nodes.Root")))
            System.out.println(nodeType.allAttributes().toString());


        return;
    }

}