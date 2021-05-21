import edu.berkeley.cs.jqf.fuzz.Fuzz;
import edu.berkeley.cs.jqf.fuzz.JQF;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static edu.tud.cs.jgf.bigfuzzplus.BigFuzzPlusDriver.PRINT_METHOD_NAMES;
import static edu.tud.cs.jgf.bigfuzzplus.BigFuzzPlusDriver.PRINT_MUTATION_DETAILS;

@RunWith(JQF.class)
public class SalaryAnalysisDriver {

    @Fuzz
    public void testSalaryAnalysis(String fileName) throws IOException {
//        System.out.println(fileName);
//        byte[] bs = fileName.getBytes();
//        System.out.println(Arrays.toString(bs));
//        byte[] bytes = fileName.getBytes();
        if (PRINT_METHOD_NAMES) { System.out.println("SalaryAnalysisDriver::testSalaryAnalysis: "+fileName); }
        SalaryAnalysis analysis = new SalaryAnalysis();
        List<String> fileList = Files.readAllLines(Paths.get(fileName));
        if (PRINT_MUTATION_DETAILS) {
            System.out.println("fileList size: " + fileList.size());
        }
        analysis.SalaryAnalysis(fileList.get(0));
    }

    public static void main(String[] args) throws IOException
    {

        String fileName = "./dataset/conf";
        if (PRINT_METHOD_NAMES) { System.out.println("SalaryAnalysisDriver::testSalaryAnalysis: "+fileName); }
        SalaryAnalysis analysis = new SalaryAnalysis();
        System.out.println(fileName);
        List<String> fileList = Files.readAllLines(Paths.get(fileName));
        System.out.println(fileList.size());
        analysis.SalaryAnalysis(fileList.get(0));
    }
}