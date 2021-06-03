package P4.FindSalary.Spec_BigFuzz;

import P4.FindSalary.Spec_BigFuzz.FindSalaryCustomArray;
import main.java.sharedClasses.CustomArray;


import java.io.IOException;
import java.util.ArrayList;

public class FindSalary {
public void FindSalary(String inputFile) throws IOException {
ArrayList<String> results0 = CustomArray.read(inputFile);
ArrayList<String> results1 = FindSalaryCustomArray.Map1(results0);
ArrayList<Integer> results2 = FindSalaryCustomArray.Map2(results1);
ArrayList<Integer> results3 = FindSalaryCustomArray.Filter1(results2);
ArrayList<Integer> results4 = FindSalaryCustomArray.Reduce1(results3);
}}
