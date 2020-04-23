package validation;

import com.alibaba.fastjson.JSON;
import trace.HappenBeforeGraph;
import trace.Linearization;
import trace.Program;
import visibility.VisibilityPredicate;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

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
        for (Linearization lin : lins) {
            System.out.println(lin.toString());
        }
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
