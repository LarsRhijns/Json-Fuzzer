/*
 * Created by Melchior Oudemans for the bachelors research project at the TUDelft. Code has been created by extending on the BigFuzz framework in collaboration with 4 other students at the TU Delft.
 */

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
