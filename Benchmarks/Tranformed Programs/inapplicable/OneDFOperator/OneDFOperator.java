package edu.ucla.cs.bigfuzz.customarray.inapplicable.OneDFOperator;

import edu.ucla.cs.bigfuzz.customarray.inapplicable.OneDFOperator.OneDFOperatorCustomArray;
import edu.ucla.cs.bigfuzz.customarray.CustomArray;

import java.io.IOException;
import java.util.ArrayList;

public class OneDFOperator {
public void OneDFOperator(String inputFile) throws IOException {
ArrayList<String> results0 = CustomArray.read(inputFile);
ArrayList<String> results1 = OneDFOperatorCustomArray.Map1(results0);
}}
