package trace;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Program {
    @JSONField(name = "SUBPROGRAMS", ordinal = 1)
    private List<SubProgram> subPrograms = new ArrayList<>();

    @JSONField(name = "HBS", ordinal = 2)
    private List<HappenBefore> hbs = new ArrayList<>();

    public Program() {
        ;
    }

    public List<SubProgram> getSubPrograms() {
        return subPrograms;
    }

    public void setSubPrograms(List<SubProgram> subPrograms) {
        this.subPrograms = subPrograms;
    }

    public List<HappenBefore> getHbs() {
        return hbs;
    }

    public void setHbs(List<HappenBefore> hbs) {
        this.hbs = hbs;
    }

    public List<HappenBeforeGraph> generateHappenBeforeGraphs() {
        List<HappenBeforeGraph> graphs = new ArrayList<>();
        for (HappenBefore hb : hbs) {
            graphs.add(new HappenBeforeGraph(subPrograms, hb));
        }
        return graphs;
    }

    public void assignID() {
        int index = 0;
        for (int i = 0; i < subPrograms.size(); i++) {
            for (int j = 0; j < subPrograms.get(i).size(); j++) {
                subPrograms.get(i).get(j).setPairID(new ImmutablePair<>(i, j));
                subPrograms.get(i).get(j).setId(index);
                index++;
            }
        }
    }

    public String toString() {
        return JSON.toJSONString(this);
    }

    public static void main(String[] args) throws Exception {
        File filename = new File("test-lins.json");
        Long filelength = filename.length();
        byte[] filecontent = new byte[filelength.intValue()];
        FileInputStream in = new FileInputStream(filename);
        in.read(filecontent);
        String jsonfile = new String(filecontent, "UTF-8");
        //System.out.println(jsonfile);
        Program program = JSON.parseObject(jsonfile, Program.class);
        System.out.println(JSON.toJSONString(program));

        List<HappenBeforeGraph> list = program.generateHappenBeforeGraphs();
        for (HappenBeforeGraph g : list) {
            System.out.println("--------------start---------------");
            //g.print();
            Invocation.visibility.put("put", "COMPLETE");
            Invocation.visibility.put("remove", "COMPLETE");
            Invocation.visibility.put("contains", "BASIC");
            List<Linearization> lins = g.generateLins();
            for (Linearization l : lins) {
                System.out.println(l);
            }
            System.out.println("--------------end---------------");
        }
    }
}