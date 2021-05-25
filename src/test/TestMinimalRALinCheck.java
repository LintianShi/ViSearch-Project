package test;

import datatype.*;
import history.*;
import org.apache.commons.lang3.tuple.Pair;
import traceprocessing.RedisProcessor;
import validation.MinimalVisSearch;
import arbitration.LinVisibility;
import arbitration.Linearization;
import validation.OperationTypes;
import validation.SearchConfiguration;

import java.io.File;
import java.io.FileWriter;

public class TestMinimalRALinCheck {
    public static void minimalExtensionRaLinCheck(String output, HappenBeforeGraph happenBeforeGraph, AbstractDataType adt) {
        SearchConfiguration configuration = new SearchConfiguration(1);
        MinimalVisSearch vfs = new MinimalVisSearch(configuration);
        vfs.init(happenBeforeGraph);
        vfs.checkConsistency(adt);
        Pair<Linearization, LinVisibility> result = vfs.getResult();
        try {
            FileWriter fileWriter = new FileWriter(new File(output));
            fileWriter.write("====================Linearization====================\n");
            //System.out.println("====================Linearization====================");
            for (HBGNode node : result.getLeft()) {
                fileWriter.write(node.getInvocation().getRetValue() + ", ");
            }
            fileWriter.write("\n");

            fileWriter.write("====================Visibility====================\n");
            for (HBGNode node : result.getLeft()) {
                fileWriter.write("#" + node.getInvocation().getRetValue() + " => { ");
                for (HBGNode visNode : result.getLeft().prefix(node)) {
                    if (result.getRight().getNodeVisibility(node).contains(visNode)) {
                        fileWriter.write(visNode.getInvocation().getRetValue() + ", ");
                    }
                }
                fileWriter.write(" }\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        RedisProcessor rp = new RedisProcessor();
        rp.load("trace");
        HappenBeforeGraph happenBeforeGraph = rp.generateProgram(new RRpq()).generateHappenBeforeGraph();

        TestMinimalRALinCheck.minimalExtensionRaLinCheck("result.txt", happenBeforeGraph, new RRpq());
    }
}
