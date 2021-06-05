import P5.StudentGrade.Spec_BigFuzz.StudentGrades;
import edu.berkeley.cs.jqf.fuzz.Fuzz;
import edu.berkeley.cs.jqf.fuzz.JQF;
import org.junit.runner.RunWith;

import java.io.IOException;
@RunWith(JQF.class)

public class StudentGradesDriver {

@Fuzz
    public void testStudentGrades(String fileName) throws IOException {
//        System.out.println("edu.ucla.cs.bigfuzz.customarray.applicable.P5.StudentGrade.Spec_BigFuzz.StudentGrades.StudentGradesDriver::testStudentGrades: "+fileName);
        StudentGrades analysis = new StudentGrades();
        analysis.StudentGrades(fileName);
    }
}
