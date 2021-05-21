package edu.ucla.cs.bigfuzz.customarray.inapplicable.DFOperator;

import edu.berkeley.cs.jqf.fuzz.Fuzz;
import edu.berkeley.cs.jqf.fuzz.JQF;
import edu.ucla.cs.bigfuzz.customarray.inapplicable.DFOperator.DFOperator;
import org.junit.runner.RunWith;

import java.io.IOException;
@RunWith(JQF.class)

public class DFOperatorDriver {

@Fuzz
    public void testDFOperator(String fileName) throws IOException {
        System.out.println("edu.ucla.cs.bigfuzz.customarray.inapplicable.DFOperator.DFOperatorDriver::testDFOperator: "+fileName);
        DFOperator analysis = new DFOperator();
        analysis.DFOperator(fileName);
    }
}
