package P12.PropertyInvestment.Spec_BigFuzz;

import scala.Tuple4;
import main.java.sharedClasses.CustomArray;


import java.io.IOException;
import java.util.ArrayList;

public class Property {
public void Property(String inputFile) throws IOException {
ArrayList<String> results0 = CustomArray.read(inputFile);
ArrayList<Tuple4> results1 = PropertyCustomArray.Map1(results0);
ArrayList<Tuple4> results2 = PropertyCustomArray.Map2(results1);
}}
