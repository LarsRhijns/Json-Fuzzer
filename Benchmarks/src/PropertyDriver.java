import P12.PropertyInvestment.Spec_BigFuzz.Property;
import edu.berkeley.cs.jqf.fuzz.Fuzz;
import edu.berkeley.cs.jqf.fuzz.JQF;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@RunWith(JQF.class)

public class PropertyDriver {

    @Fuzz
    public void testProperty(String fileName) throws IOException {
        List<String> fileList = Files.readAllLines(Paths.get(fileName));
        Property analysis = new Property();
        analysis.Property(fileList.get(0));
    }
}
