import main.java.sharedClasses.CustomArray;
import scala.Tuple3;

import SalaryAnalysis.*;
import java.io.IOException;
import java.util.ArrayList;

public class SalaryAnalysis {
public void OutofJDU(String inputFile) throws IOException {
ArrayList<String> results0 = CustomArray.read(inputFile);
ArrayList<Tuple3> results1 = SalaryAnalysisCustomArray.Map1(results0);
ArrayList<Tuple3> results2 = SalaryAnalysisCustomArray.Filter1(results1);
ArrayList<map2> results3 = SalaryAnalysisCustomArray.Map2(results2);
int pair=results3.size();
if(pair > 7)
    {
        System.out.println(pair);
        assert(pair != 8);
    }
}}
