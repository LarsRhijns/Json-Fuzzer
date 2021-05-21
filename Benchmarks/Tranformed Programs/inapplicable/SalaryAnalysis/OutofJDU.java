package edu.ucla.cs.bigfuzz.customarray.inapplicable.OutofJDU;

import edu.ucla.cs.bigfuzz.customarray.inapplicable.OutofJDU.OutofJDUCustomArray;
import edu.ucla.cs.bigfuzz.customarray.CustomArray;
import edu.ucla.cs.bigfuzz.customarray.inapplicable.OutofJDU.map2;
import scala.Tuple3;

import java.io.IOException;
import java.util.ArrayList;

public class OutofJDU {
public void OutofJDU(String inputFile) throws IOException {
ArrayList<String> results0 = CustomArray.read(inputFile);
ArrayList<Tuple3> results1 = OutofJDUCustomArray.Map1(results0);
ArrayList<Tuple3> results2 = OutofJDUCustomArray.Filter1(results1);
ArrayList<map2> results3 = OutofJDUCustomArray.Map2(results2);
int pair=results3.size();
if(pair > 7)
    {
        System.out.println(pair);
        assert(pair != 8);
    }
}}
