import P10.AgeAnalysis.Spec_BigFuzz.TwoFlows;
import edu.berkeley.cs.jqf.fuzz.Fuzz;
import edu.berkeley.cs.jqf.fuzz.JQF;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@RunWith(JQF.class)

public class TwoFlowsDriver {

    @Fuzz
    public void testTwoFlows(String fileName) throws IOException {
        List<String> fileList = Files.readAllLines(Paths.get(fileName));
        TwoFlows analysis = new TwoFlows();
        analysis.TwoFlows(fileList.get(0));
    }
}
