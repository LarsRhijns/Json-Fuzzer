package P7.InsideCircle.Spec_BigFuzz;

import P7.InsideCircle.Spec_BigFuzz.ExternalUDFCustomArray;
import P7.InsideCircle.Spec_BigFuzz.map2;
import main.java.sharedClasses.CustomArray;


import java.io.IOException;
import java.util.ArrayList;

public class ExternalUDF {
public void ExternalUDF(String inputFile) throws IOException {
ArrayList<String> results0 = CustomArray.read(inputFile);
ArrayList<map2> results1 = ExternalUDFCustomArray.Map1(results0);
ArrayList<map2> results2 = ExternalUDFCustomArray.Filter1(results1);
}}
