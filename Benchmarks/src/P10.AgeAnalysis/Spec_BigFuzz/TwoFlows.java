package P10.AgeAnalysis.Spec_BigFuzz;

import scala.Tuple3;
import main.java.sharedClasses.CustomArray;


import java.io.IOException;
import java.util.ArrayList;

public class TwoFlows {
public void TwoFlows(String inputFile) throws IOException {
ArrayList<String> results0 = CustomArray.read(inputFile);
ArrayList<Tuple3> results1 = TwoFlowsCustomArray.Map1(results0);
ArrayList<Tuple3> results2 = TwoFlowsCustomArray.Filter1(results1);
ArrayList<map2> results3 = TwoFlowsCustomArray.Map2(results2);
}}
