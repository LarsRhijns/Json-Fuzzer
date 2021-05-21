package edu.ucla.cs.bigfuzz.customarray.inapplicable.Property;

import edu.berkeley.cs.jqf.fuzz.Fuzz;
import edu.berkeley.cs.jqf.fuzz.JQF;
import edu.ucla.cs.bigfuzz.customarray.inapplicable.OutofJDU.Property;
import org.junit.runner.RunWith;

import java.io.IOException;
@RunWith(JQF.class)

public class PropertyDriver {

@Fuzz
    public void testProperty(String fileName) throws IOException {
        System.out.println("edu.ucla.cs.bigfuzz.customarray.inapplicable.Property.PropertyDriver::testProperty: "+fileName);
        Property analysis = new Property();
        analysis.Property(fileName);
    }
}
