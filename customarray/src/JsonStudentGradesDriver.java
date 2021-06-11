import edu.berkeley.cs.jqf.fuzz.Fuzz;
import edu.berkeley.cs.jqf.fuzz.JQF;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@RunWith(JQF.class)
public class JsonStudentGradesDriver {
    @Fuzz
    public void testJsonStudentGrades(String fileName) throws IOException {
        System.out.println("JsonStudentGrades::testJsonStudentGrades: "+fileName);
        JsonStudentGrades analysis = new JsonStudentGrades();
        System.out.println(fileName);
        List<String> fileList = Files.readAllLines(Paths.get(fileName));
        System.out.println(fileList.size());
        analysis.JsonStudentGrades(fileList.get(0));
    }
}
