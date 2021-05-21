package P5.StudentGrade.Spec_BigFuzz;

import edu.berkeley.cs.jqf.fuzz.Fuzz;
import edu.berkeley.cs.jqf.fuzz.JQF;
import org.junit.runner.RunWith;

import java.io.IOException;
@RunWith(JQF.class)

public class StudentGradesDriver {

@Fuzz
    public void testStudentGrades(String fileName) throws IOException {
        System.out.println("edu.ucla.cs.bigfuzz.customarray.applicable.StudentGrades.StudentGradesDriver::testStudentGrades: "+fileName);
        StudentGrades analysis = new StudentGrades();
        analysis.StudentGrades(fileName);
    }
}
