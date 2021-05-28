import edu.berkeley.cs.jqf.fuzz.Fuzz;
import edu.berkeley.cs.jqf.fuzz.JQF;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@RunWith(JQF.class)
public class JsonSalaryAnalysisDriver {

    @Fuzz
    public void testJsonSalaryAnalysis(String fileName) throws IOException {
        System.out.println("JsonSalaryAnalysisDriver::testJsonSalaryAnalysis: "+fileName);
        JsonSalaryAnalysis analysis = new JsonSalaryAnalysis();
        System.out.println(fileName);
        List<String> fileList = Files.readAllLines(Paths.get(fileName));
        System.out.println(fileList.size());
        analysis.JsonSalaryAnalysis(fileList.get(0));
    }
}
