package edu.ucla.cs.bigfuzz.customarray.ExternalUDF;

import edu.ucla.cs.bigfuzz.customarray.ExternalUDF.map2;
import edu.ucla.cs.bigfuzz.customarray.ExternalUDF.ExternalUDFCustomArray;
import edu.ucla.cs.bigfuzz.customarray.CustomArray;

import java.io.IOException;
import java.util.ArrayList;

public class ExternalUDF {
public void ExternalUDF(String inputFile) throws IOException {
ArrayList<String> results0 = CustomArray.read(inputFile);
ArrayList<map2> results1 = ExternalUDFCustomArray.Map1(results0);
ArrayList<map2> results2 = ExternalUDFCustomArray.Filter1(results1);
}}
