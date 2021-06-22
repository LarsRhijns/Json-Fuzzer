import P8.StringSelf.Spec_BigFuzz.OneDFOperator;
import edu.berkeley.cs.jqf.fuzz.Fuzz;
import edu.berkeley.cs.jqf.fuzz.JQF;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@RunWith(JQF.class)

public class OneDFOperatorDriver {

    @Fuzz
    public void testOneDFOperator(String fileName) throws IOException {
        List<String> fileList = Files.readAllLines(Paths.get(fileName));
        OneDFOperator analysis = new OneDFOperator();
        analysis.OneDFOperator(fileList.get(0));
    }
}
