import edu.berkeley.cs.jqf.fuzz.Fuzz;
import edu.berkeley.cs.jqf.fuzz.JQF;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static edu.tud.cs.jqf.bigfuzzplus.BigFuzzPlusDriver.PRINT_METHOD_NAMES;

@RunWith(JQF.class)
public class DFOperatorDriver {

    @Fuzz
    public void testDFOperator(String fileName) throws IOException {
        File inputFile = new File(fileName);
        if (PRINT_METHOD_NAMES) { System.out.println("[METHOD] DFOperatorDriver::testDFOperator"); }
        DFOperator.DFOperator(inputFile.getPath());
    }
}
