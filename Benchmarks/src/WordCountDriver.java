import P1.Wordcount.Spec_BigFuzz.WordCount;
import edu.berkeley.cs.jqf.fuzz.Fuzz;
import edu.berkeley.cs.jqf.fuzz.JQF;
import org.junit.runner.RunWith;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@RunWith(JQF.class)

public class WordCountDriver {

    @Fuzz
    public void testWordCount(String fileName) throws Exception {
        List<String> fileList = Files.readAllLines(Paths.get(fileName));
        WordCount analysis = new WordCount();
        analysis.WordCount(fileList.get(0));
    }
}
