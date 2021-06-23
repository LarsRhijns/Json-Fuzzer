
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
public class WordCountNewDriver {

    @Fuzz
    public void testWordCountNew(String fileName) throws IOException {
        List<String> fileList = Files.readAllLines(Paths.get(fileName));
        if (PRINT_METHOD_NAMES) { System.out.println("[METHOD] WordCountNewDriver::testWordCountNew"); }
        WordCountNew analysis = new WordCountNew();
        analysis.WordCountNew(fileList.get(0));
    }
}
