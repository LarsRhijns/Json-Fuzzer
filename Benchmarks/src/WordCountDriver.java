import P1.Wordcount.Spec_BigFuzz.WordCount;
import edu.berkeley.cs.jqf.fuzz.Fuzz;
import edu.berkeley.cs.jqf.fuzz.JQF;
import org.junit.runner.RunWith;

@RunWith(JQF.class)

public class WordCountDriver {

@Fuzz
    public void testWordCount(String fileName) throws Exception {
//        System.out.println("edu.ucla.cs.bigfuzz.customarray.applicable.P1.Wordcount.Spec_BigFuzz.WordCount.WordCountDriver::testWordCount: "+fileName);
        WordCount analysis = new WordCount();
        analysis.WordCount(fileName);
    }
}
