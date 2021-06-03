import P12.PropertyInvestment.Spec_BigFuzz.Property;
import edu.berkeley.cs.jqf.fuzz.Fuzz;
import edu.berkeley.cs.jqf.fuzz.JQF;
import org.junit.runner.RunWith;

import java.io.IOException;
@RunWith(JQF.class)

public class PropertyDriver {

@Fuzz
    public void testProperty(String fileName) throws IOException {
//        System.out.println("edu.ucla.cs.bigfuzz.customarray.inapplicable.P12.PropertyInvestment.Spec_BigFuzz.Property.PropertyDriver::testProperty: "+fileName);
        Property analysis = new Property();
        analysis.Property(fileName);
    }
}
