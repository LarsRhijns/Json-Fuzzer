import P7.InsideCircle.Spec_BigFuzz.ExternalUDF;
import edu.berkeley.cs.jqf.fuzz.Fuzz;
import edu.berkeley.cs.jqf.fuzz.JQF;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@RunWith(JQF.class)

public class ExternalUDFDriver {

    @Fuzz
    public void testExternalUDF(String fileName) throws IOException {
        List<String> fileList = Files.readAllLines(Paths.get(fileName));
        ExternalUDF analysis = new ExternalUDF();
        analysis.ExternalUDF(fileList.get(0));
    }
}
