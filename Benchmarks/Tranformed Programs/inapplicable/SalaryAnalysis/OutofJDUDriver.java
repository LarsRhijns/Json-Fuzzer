package edu.ucla.cs.bigfuzz.customarray.inapplicable.OutofJDU;

import edu.berkeley.cs.jqf.fuzz.Fuzz;
import edu.berkeley.cs.jqf.fuzz.JQF;
import edu.ucla.cs.bigfuzz.customarray.inapplicable.OutofJDU.OutofJDU;
import org.junit.runner.RunWith;

import java.io.IOException;
@RunWith(JQF.class)

public class OutofJDUDriver {

@Fuzz
    public void testOutofJDU(String fileName) throws IOException {
        System.out.println("edu.ucla.cs.bigfuzz.customarray.inapplicable.OutofJDU.OutofJDUDriver::testOutofJDU: "+fileName);
        OutofJDU analysis = new OutofJDU();
        analysis.OutofJDU(fileName);
    }
}
