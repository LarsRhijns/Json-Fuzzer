

import P4.FindSalary.Spec_BigFuzz.FindSalary;
import edu.berkeley.cs.jqf.fuzz.Fuzz;
import edu.berkeley.cs.jqf.fuzz.JQF;
import org.junit.runner.RunWith;

import java.io.IOException;
@RunWith(JQF.class)

public class FindSalaryDriver {

@Fuzz
    public void testFindSalary(String fileName) throws IOException {
//        System.out.println("edu.ucla.cs.bigfuzz.customarray.applicable.P4.FindSalary.Spec_BigFuzz.FindSalary.FindSalaryDriver::testFindSalary: "+fileName);
        FindSalary analysis = new FindSalary();
        analysis.FindSalary(fileName);
    }
}
