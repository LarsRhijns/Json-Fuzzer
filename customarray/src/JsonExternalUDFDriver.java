import edu.berkeley.cs.jqf.fuzz.Fuzz;
import edu.berkeley.cs.jqf.fuzz.JQF;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static edu.tud.cs.jqf.bigfuzzplus.BigFuzzPlusDriver.PRINT_METHOD_NAMES;

@RunWith(JQF.class)
public class JsonExternalUDFDriver {
    @Fuzz
    public void testJsonExternalUDF(String fileName) throws IOException {
        if (PRINT_METHOD_NAMES) {
            System.out.println("[METHOD] JsonExternalUDFDriver::testJsonExternalUDF");
        }

        JsonExternalUDF analysis = new JsonExternalUDF();
        List<String> fileList = Files.readAllLines(Paths.get(fileName));
//        System.out.println(fileName);
//        System.out.println(fileList.size());
        analysis.JsonExternalUDF(fileList.get(0));
    }
}
