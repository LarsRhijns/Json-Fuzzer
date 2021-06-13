

import P4.FindSalary.Spec_BigFuzz.FindSalary;
import edu.berkeley.cs.jqf.fuzz.Fuzz;
import edu.berkeley.cs.jqf.fuzz.JQF;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@RunWith(JQF.class)

public class FindSalaryDriver {

    @Fuzz
    public void testFindSalary(String fileName) throws IOException {
        List<String> fileList = Files.readAllLines(Paths.get(fileName));
        FindSalary analysis = new FindSalary();
        analysis.FindSalary(fileList.get(0));
    }
}
