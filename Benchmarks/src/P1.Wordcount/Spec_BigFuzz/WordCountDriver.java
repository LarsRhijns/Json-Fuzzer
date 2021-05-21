package P1.Wordcount.Spec_BigFuzz;

import edu.berkeley.cs.jqf.fuzz.Fuzz;
import edu.berkeley.cs.jqf.fuzz.JQF;
import org.junit.runner.RunWith;

@RunWith(JQF.class)

public class WordCountDriver {

@Fuzz
    public void testWordCount(String fileName) throws Exception {
        System.out.println("edu.ucla.cs.bigfuzz.customarray.applicable.WordCount.WordCountDriver::testWordCount: "+fileName);
        WordCount analysis = new WordCount();
        analysis.WordCount(fileName);
    }
}
