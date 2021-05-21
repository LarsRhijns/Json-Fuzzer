package edu.ucla.cs.bigfuzz.customarray.applicable.WordCountEXF;

import edu.berkeley.cs.jqf.fuzz.Fuzz;
import edu.berkeley.cs.jqf.fuzz.JQF;
import org.junit.runner.RunWith;

import java.io.IOException;
@RunWith(JQF.class)

public class WordCountNewDriver {

@Fuzz
    public void testWordCountNew(String fileName) throws IOException {
        System.out.println("edu.ucla.cs.bigfuzz.customarray.applicable.WordCountEXF.WordCountNewDriver::testWordCountNew: "+fileName);
        WordCountNew analysis = new WordCountNew();
        analysis.WordCountNew(fileName);
    }
}
