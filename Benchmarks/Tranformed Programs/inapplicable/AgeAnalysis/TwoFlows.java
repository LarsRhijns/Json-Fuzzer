package edu.ucla.cs.bigfuzz.customarray.inapplicable.TwoFlows;

import edu.ucla.cs.bigfuzz.customarray.inapplicable.TwoFlows.TwoFlowsCustomArray;
import edu.ucla.cs.bigfuzz.customarray.CustomArray;
import edu.ucla.cs.bigfuzz.customarray.inapplicable.TwoFlows.map2;
import scala.Tuple3;

import java.io.IOException;
import java.util.ArrayList;

public class TwoFlows {
public void TwoFlows(String inputFile) throws IOException {
ArrayList<String> results0 = CustomArray.read(inputFile);
ArrayList<Tuple3> results1 = TwoFlowsCustomArray.Map1(results0);
ArrayList<Tuple3> results2 = TwoFlowsCustomArray.Filter1(results1);
ArrayList<map2> results3 = TwoFlowsCustomArray.Map2(results2);
}}
