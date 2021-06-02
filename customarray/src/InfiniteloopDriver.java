
import edu.berkeley.cs.jqf.fuzz.Fuzz;
import edu.berkeley.cs.jqf.fuzz.JQF;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

import static edu.tud.cs.jqf.bigfuzzplus.BigFuzzPlusDriver.PRINT_METHOD_NAMES;

@RunWith(JQF.class)
public class InfiniteloopDriver {

    @Fuzz
    public void testInfiniteloop(String fileName) throws IOException {
        File inputFile = new File(fileName);
        if (PRINT_METHOD_NAMES) { System.out.println("[METHOD] InfiniteloopDriver::testInfiniteloop"); }
        Infiniteloop analysis = new Infiniteloop();
        analysis.Infiniteloop(inputFile.getPath());
    }

    public static void main(String[] args) throws IOException {
        String fileName = "dataset/salary1.csv";
        Infiniteloop analysis = new Infiniteloop();
        analysis.Infiniteloop(fileName);
    }
}
