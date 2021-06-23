import edu.berkeley.cs.jqf.fuzz.Fuzz;
import edu.berkeley.cs.jqf.fuzz.JQF;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static edu.tud.cs.jqf.bigfuzzplus.BigFuzzPlusDriver.PRINT_METHOD_NAMES;

@RunWith(JQF.class)
public class JsonFindSalaryDriver {
    @Fuzz
    public void testJsonFindSalary(String fileName) throws IOException {
        if (PRINT_METHOD_NAMES) {
            System.out.println("[METHOD] JsonFindSalaryDriver::testJsonFindSalary");
        }

        JsonFindSalary analysis = new JsonFindSalary();
        List<String> fileList = Files.readAllLines(Paths.get(fileName));
//        System.out.println(fileName);
//        System.out.println(fileList.size());
        analysis.JsonFindSalary(fileList.get(0));
    }
}
