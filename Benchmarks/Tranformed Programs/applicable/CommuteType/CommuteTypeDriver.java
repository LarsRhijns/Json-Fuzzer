package edu.ucla.cs.bigfuzz.customarray.applicable.CommuteType;

import edu.berkeley.cs.jqf.fuzz.Fuzz;
import edu.berkeley.cs.jqf.fuzz.JQF;
import edu.ucla.cs.bigfuzz.customarray.applicable.CommuteType.CommuteType;
import org.junit.runner.RunWith;

import java.io.IOException;
@RunWith(JQF.class)

public class CommuteTypeDriver {

@Fuzz
    public void testCommuteType(String fileName1,String fileName2) throws IOException {
        System.out.println("edu.ucla.cs.bigfuzz.customarray.applicable.CommuteType.CommuteTypeDriver::testCommuteType: "+fileName1+";"+fileName2);
        CommuteType analysis = new CommuteType();
        analysis.CommuteType(fileName1,fileName2);
    }

    public static void main(String[] args) throws IOException
    {

        CommuteType commuteType = new CommuteType();
        commuteType.CommuteType("trips.csv","zipcode.csv");
    }
}
