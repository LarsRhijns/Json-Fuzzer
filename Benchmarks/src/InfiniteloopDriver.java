import P9.NumberSeries.Spec_BigFuzz.Infiniteloop;
import edu.berkeley.cs.jqf.fuzz.Fuzz;
import edu.berkeley.cs.jqf.fuzz.JQF;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@RunWith(JQF.class)

public class InfiniteloopDriver {

    @Fuzz
    public void testInfiniteloop(String fileName) throws IOException {
        List<String> fileList = Files.readAllLines(Paths.get(fileName));
        Infiniteloop analysis = new Infiniteloop();
        analysis.Infiniteloop(fileList.get(0));
    }
}
