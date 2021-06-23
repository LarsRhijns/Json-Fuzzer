import P3.ExternalFunction.Spec_BigFuzz.WordCountNew;
import edu.berkeley.cs.jqf.fuzz.Fuzz;
import edu.berkeley.cs.jqf.fuzz.JQF;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@RunWith(JQF.class)

public class WordCountNewDriver {

    @Fuzz
    public void testWordCountNew(String fileName) throws IOException {
        List<String> fileList = Files.readAllLines(Paths.get(fileName));
        WordCountNew analysis = new WordCountNew();
        analysis.WordCountNew(fileList.get(0));
    }
}
