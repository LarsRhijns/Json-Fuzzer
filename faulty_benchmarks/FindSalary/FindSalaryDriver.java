package edu.ucla.cs.bigfuzz.customarray.applicable.FindSalary;

import edu.berkeley.cs.jqf.fuzz.Fuzz;
import edu.berkeley.cs.jqf.fuzz.JQF;
import edu.ucla.cs.bigfuzz.customarray.applicable.FindSalary.FindSalary;
import org.junit.runner.RunWith;

import java.io.IOException;
@RunWith(JQF.class)

public class FindSalaryDriver {

@Fuzz
    public void testFindSalary(String fileName) throws IOException {
        System.out.println("edu.ucla.cs.bigfuzz.customarray.applicable.FindSalary.FindSalaryDriver::testFindSalary: "+fileName);
        FindSalary analysis = new FindSalary();
        analysis.FindSalary(fileName);
    }
}
