package history;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import org.apache.commons.lang3.tuple.ImmutablePair;
import validation.OperationTypes;
import arbitration.Linearization;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class Program {
    @JSONField(name = "SUBPROGRAMS", ordinal = 1)
    private List<SubProgram> subPrograms = new ArrayList<>();

    @JSONField(name = "HB", ordinal = 2)
    private HappenBefore hb = new HappenBefore();

    public Program() {
        ;
    }

    public Program(List<SubProgram> subPrograms, HappenBefore hb) {
        this.subPrograms = subPrograms;
        this.hb = hb;
    }

    public List<SubProgram> getSubPrograms() {
        return subPrograms;
    }

    public void setSubPrograms(List<SubProgram> subPrograms) {
        this.subPrograms = subPrograms;
    }

    public HappenBeforeGraph generateHappenBeforeGraph() {
        return new HappenBeforeGraph(subPrograms, hb);
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

    public static HappenBeforeGraph load(String fileName) throws Exception {
        File filename = new File(fileName);
        Long filelength = filename.length();
        byte[] filecontent = new byte[filelength.intValue()];
        FileInputStream in = new FileInputStream(filename);
        in.read(filecontent);
        String jsonfile = new String(filecontent, "UTF-8");
        Program program = JSON.parseObject(jsonfile, Program.class);

        return program.generateHappenBeforeGraph();
    }

    public static void main(String[] args) throws Exception {
        File filename = new File("ralin1.json");
        Long filelength = filename.length();
        byte[] filecontent = new byte[filelength.intValue()];
        FileInputStream in = new FileInputStream(filename);
        in.read(filecontent);
        String jsonfile = new String(filecontent, "UTF-8");
        //System.out.println(jsonfile);
        Program program = JSON.parseObject(jsonfile, Program.class);
        System.out.println(JSON.toJSONString(program));
    }
}