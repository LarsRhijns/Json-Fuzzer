
/*
 * Created by Melchior Oudemans for the bachelors research project at the TUDelft. Code has been created by extending on the BigFuzz framework in collaboration with 4 other students at the TU Delft.
 */

import edu.berkeley.cs.jqf.fuzz.Fuzz;
import edu.berkeley.cs.jqf.fuzz.JQF;
import org.junit.runner.RunWith;

import java.io.IOException;
@RunWith(JQF.class)

public class DFOperatorDriver {

@Fuzz
    public void testDFOperator(String fileName) throws Exception {
//        System.out.println("edu.ucla.cs.bigfuzz.customarray.inapplicable.DFOperator.DFOperatorDriver::testDFOperator: "+fileName);
        DFOperator analysis = new DFOperator();
        analysis.DFOperator(fileName);
    }
}
