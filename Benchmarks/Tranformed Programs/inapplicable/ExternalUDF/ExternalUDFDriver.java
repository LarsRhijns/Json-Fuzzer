package edu.ucla.cs.bigfuzz.customarray.ExternalUDF;

import edu.berkeley.cs.jqf.fuzz.Fuzz;
import edu.berkeley.cs.jqf.fuzz.JQF;
import org.junit.runner.RunWith;

import java.io.IOException;
@RunWith(JQF.class)

public class ExternalUDFDriver {

@Fuzz
    public void testExternalUDF(String fileName) throws IOException {
        System.out.println("edu.ucla.cs.bigfuzz.customarray.ExternalUDF.ExternalUDFDriver::testExternalUDF: "+fileName);
        ExternalUDF analysis = new ExternalUDF();
        analysis.ExternalUDF(fileName);
    }
}
