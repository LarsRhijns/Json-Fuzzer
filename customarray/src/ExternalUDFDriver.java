

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
public class ExternalUDFDriver {

    @Fuzz
    public void testExternalUDF(String fileName) throws IOException {
        File inputFile = new File(fileName);
        if (PRINT_METHOD_NAMES) { System.out.println("[METHOD] ExternalUDFDriver::testExternalUDF"); }
        ExternalUDF analysis = new ExternalUDF();
        analysis.ExternalUDF(inputFile.getPath());
    }

    public static void main(String[] args) throws IOException {
        ExternalUDF analysis = new ExternalUDF();
        analysis.ExternalUDF("dataset/salary1.csv");
    }
}
