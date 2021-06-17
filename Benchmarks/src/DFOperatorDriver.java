import P11.IncomAggregation.Spec_BigFuzz.DFOperator;
import edu.berkeley.cs.jqf.fuzz.Fuzz;
import edu.berkeley.cs.jqf.fuzz.JQF;
import org.junit.runner.RunWith;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@RunWith(JQF.class)

public class DFOperatorDriver {

    @Fuzz
    public void testDFOperator(String fileName) throws Exception {
        List<String> fileList = Files.readAllLines(Paths.get(fileName));
        DFOperator analysis = new DFOperator();
        analysis.DFOperator(fileList.get(0));
    }
}
