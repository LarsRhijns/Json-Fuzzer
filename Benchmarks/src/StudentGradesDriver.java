import P5.StudentGrade.Spec_BigFuzz.StudentGrades;
import edu.berkeley.cs.jqf.fuzz.Fuzz;
import edu.berkeley.cs.jqf.fuzz.JQF;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@RunWith(JQF.class)

public class StudentGradesDriver {

    @Fuzz
    public void testStudentGrades(String fileName) throws IOException {
        List<String> fileList = Files.readAllLines(Paths.get(fileName));
        StudentGrades analysis = new StudentGrades();
        analysis.StudentGrades(fileList.get(0));
    }
}
