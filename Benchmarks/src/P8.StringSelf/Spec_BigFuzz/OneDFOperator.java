package P8.StringSelf.Spec_BigFuzz;

import P8.StringSelf.Spec_BigFuzz.OneDFOperatorCustomArray;
import main.java.sharedClasses.CustomArray;


import java.io.IOException;
import java.util.ArrayList;

public class OneDFOperator {
public void OneDFOperator(String inputFile) throws IOException {
ArrayList<String> results0 = CustomArray.read(inputFile);
ArrayList<String> results1 = OneDFOperatorCustomArray.Map1(results0);
}}
