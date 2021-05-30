import edu.berkeley.cs.jqf.fuzz.Fuzz;
import edu.berkeley.cs.jqf.fuzz.JQF;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static edu.ucla.cs.jqf.bigfuzz.BigFuzzDriver.PRINT_METHOD_NAMES;
import static edu.ucla.cs.jqf.bigfuzz.BigFuzzDriver.PRINT_MUTATION_DETAILS;

@RunWith(JQF.class)
public class SalaryAnalysisDriver {

    @Fuzz
    public void testSalaryAnalysis(String fileName) throws IOException {
        File inputFile = new File(fileName);
        if (PRINT_METHOD_NAMES) { System.out.println("[METHOD] SalaryAnalysisDriver::testSalaryAnalysis"); }
        SalaryAnalysis analysis = new SalaryAnalysis();
        analysis.SalaryAnalysis(inputFile.getPath());
    }

    public static void main(String[] args) throws IOException
    {

        String fileName = "./dataset/conf";
        if (PRINT_METHOD_NAMES) { System.out.println("[METHOD] SalaryAnalysisDriver::testSalaryAnalysis: "+fileName); }
        SalaryAnalysis analysis = new SalaryAnalysis();
        System.out.println(fileName);
        List<String> fileList = Files.readAllLines(Paths.get(fileName));
        System.out.println(fileList.size());
        analysis.SalaryAnalysis(fileList.get(0));
    }
}