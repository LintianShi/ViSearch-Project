package validation;

import com.alibaba.fastjson.JSON;
import trace.HappenBeforeGraph;
import trace.Linearization;
import trace.Node;
import trace.Program;
import visibility.*;

import java.beans.Visibility;
import java.io.File;
import java.io.FileInputStream;
import java.util.*;

public class VisibilityValidation {
    private Program program;
    private List<HappenBeforeGraph> happenBeforeGraphs;

    public void loadTrace(String filename) {
        File file = new File(filename);
        Long filelength = file.length();
        byte[] filecontent = new byte[filelength.intValue()];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(filecontent);
            String jsonfile = new String(filecontent, "UTF-8");
            program = JSON.parseObject(jsonfile, Program.class);
            happenBeforeGraphs = program.generateHappenBeforeGraphs();
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
    }

    private List<Linearization> generateLinearazations() {
        List<Linearization> lins = new ArrayList<>();
        for (HappenBeforeGraph g : happenBeforeGraphs) {
            lins.addAll(g.generateLins());
        }
        return lins;
    }

    public void check() {
        List<Linearization> lins = generateLinearazations();
        Linearization lin = lins.get(5);
        System.out.println(lin.toString());
        List<LinVisibility> linVisibilities = lin.generateAllNodeVisibility();
        System.out.println(linVisibilities.size());
        Specification specification = new Specification();
        specification.setSpecification("put", "COMPLETE");
        //specification.setSpecification("contains", "WEAK");
        //specification.setSpecification("contains", "BASIC");
        specification.setSpecification("contains", "MONOTONIC");
        int sum = 0;
        for (LinVisibility l : linVisibilities) {
            if (filter(lin, l, specification)) {
                sum++;
                System.out.println(l);
            }
        }
        System.out.println(sum);
    }

    public boolean filter(Linearization linearization, LinVisibility linVisibility, Specification specification) {
        for (int i = 0; i < linearization.size(); i++) {
            Linearization prefixLin = linearization.prefix(i);
            Set<Node> vis = linVisibility.getNodeVisibility(linearization.get(i));
            String spec = specification.getSpecification(linearization.get(i).getInvocation().getMethodName());
            VisibilityPredicate predicate;
            if (spec.equals("COMPLETE")) {
                predicate = new CompleteVisibilityPredicate();
            } else if (spec.equals("CAUSAL")) {
                predicate = new CausalVisibilityPredicate();
            } else if (spec.equals("PEER")) {
                predicate = new PeerVisibilityPredicate();
            } else if (spec.equals("MONOTONIC")) {
                predicate = new MonotonicVisibilityPredicate();
            } else if (spec.equals("BASIC")) {
                predicate = new BasicVisibilityPredicate();
            } else if (spec.equals("WEAK")) {
                predicate = new WeakVisibilityPredicate();
            } else {
                predicate = new CompleteVisibilityPredicate();
            }

            if (!predicate.check(vis, prefixLin, linVisibility)) {
                return false;
            }
        }
        return true;
    }

    public void printProgram() {
        System.out.println(program.toString());
    }

    public static void main(String[] args) {
        VisibilityValidation vv = new VisibilityValidation();
        vv.loadTrace("test1.json");
        vv.check();
    }
}
