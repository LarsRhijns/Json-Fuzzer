package P2.CommuteType.Spec_BigFuzz;

import P2.CommuteType.Spec_BigFuzz.CommuteTypeCustomArray;
import P2.CommuteType.Spec_BigFuzz.join6;
import P2.CommuteType.Spec_BigFuzz.map1;
import P2.CommuteType.Spec_BigFuzz.map3;
import P2.CommuteType.Spec_BigFuzz.map5;
import main.java.sharedClasses.CustomArray;

import java.io.IOException;
import java.util.ArrayList;

public class CommuteType {
public void CommuteType(String inputFile1,String inputFile2) throws IOException {
ArrayList<String> results0 = CustomArray.read(inputFile1);
ArrayList<map5> results1 = CommuteTypeCustomArray.Map1(results0);
ArrayList<String> results2 = CustomArray.read(inputFile2);
ArrayList<map3> results3 = CommuteTypeCustomArray.Map2(results2);
ArrayList<map3> results4 = CommuteTypeCustomArray.Filter1(results3);
ArrayList<join6> results5 = CommuteTypeCustomArray.Join1(results4,results1);
ArrayList<map1> results6 = CommuteTypeCustomArray.Map3(results5);
ArrayList<map1> results7 = CommuteTypeCustomArray.ReduceByKey1(results6);
}}
