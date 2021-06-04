


import P11.IncomAggregation.Spec_BigFuzz.DFOperator;
import edu.berkeley.cs.jqf.fuzz.Fuzz;
import edu.berkeley.cs.jqf.fuzz.JQF;
import org.junit.runner.RunWith;

@RunWith(JQF.class)

public class DFOperatorDriver {

@Fuzz
    public void testDFOperator(String fileName) throws Exception {
//        System.out.println("edu.ucla.cs.bigfuzz.customarray.inapplicable.P11.IncomAggregation.Spec_BigFuzz.DFOperator.DFOperatorDriver::testDFOperator: "+fileName);
        DFOperator analysis = new DFOperator();
        analysis.DFOperator(fileName);
    }
}
