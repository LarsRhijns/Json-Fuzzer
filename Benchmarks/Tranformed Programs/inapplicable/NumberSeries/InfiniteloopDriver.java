package edu.ucla.cs.bigfuzz.customarray.inapplicable.SymbolicStateOutofBounds;

import edu.berkeley.cs.jqf.fuzz.Fuzz;
import edu.berkeley.cs.jqf.fuzz.JQF;
import edu.ucla.cs.bigfuzz.customarray.inapplicable.SymbolicStateOutofBounds.Infiniteloop;
import org.junit.runner.RunWith;

import java.io.IOException;
@RunWith(JQF.class)

public class InfiniteloopDriver {

@Fuzz
    public void testInfiniteloop(String fileName) throws IOException {
        System.out.println("edu.ucla.cs.bigfuzz.customarray.inapplicable.SymbolicStateOutofBounds.InfiniteloopDriver::testInfiniteloop: "+fileName);
        Infiniteloop analysis = new Infiniteloop();
        analysis.Infiniteloop(fileName);
    }
}
