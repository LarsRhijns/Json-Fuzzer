package edu.ucla.cs.bigfuzz.customarray.inapplicable.OneDFOperator;

import edu.berkeley.cs.jqf.fuzz.Fuzz;
import edu.berkeley.cs.jqf.fuzz.JQF;
import edu.ucla.cs.bigfuzz.customarray.inapplicable.OneDFOperator.OneDFOperator;
import org.junit.runner.RunWith;

import java.io.IOException;
@RunWith(JQF.class)

public class OneDFOperatorDriver {

@Fuzz
    public void testOneDFOperator(String fileName) throws IOException {
        System.out.println("edu.ucla.cs.bigfuzz.customarray.inapplicable.OneDFOperator.OneDFOperatorDriver::testOneDFOperator: "+fileName);
        OneDFOperator analysis = new OneDFOperator();
        analysis.OneDFOperator(fileName);
    }
}
