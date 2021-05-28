package P10.AgeAnalysis.Spec_BigFuzz;

import edu.berkeley.cs.jqf.fuzz.Fuzz;
import edu.berkeley.cs.jqf.fuzz.JQF;
import org.junit.runner.RunWith;

import java.io.IOException;
@RunWith(JQF.class)

public class TwoFlowsDriver {

@Fuzz
    public void testTwoFlows(String fileName) throws IOException {
//        System.out.println("edu.ucla.cs.bigfuzz.customarray.inapplicable.TwoFlows.TwoFlowsDriver::testTwoFlows: "+fileName);
        TwoFlows analysis = new TwoFlows();
        analysis.TwoFlows(fileName);
    }
}
