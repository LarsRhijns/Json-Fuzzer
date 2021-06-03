import P5.StudentGrade.Spec_BigFuzz.StudentGradesCustomArray;
import P5.StudentGrade.Spec_BigFuzz.map3;
import P5.StudentGrade.Spec_BigFuzz.map4;
import main.java.sharedClasses.CustomArray;


import java.io.IOException;
import java.util.ArrayList;

public class StudentGrades {
public void StudentGrades(String inputFile) throws IOException {
String results0 = CustomArray.readStr(inputFile);
String[] results1 = StudentGradesCustomArray.FlatMap1(results0);
ArrayList<map4> results2 = StudentGradesCustomArray.Map1(results1);
ArrayList<map3> results3 = StudentGradesCustomArray.Map2(results2);
ArrayList<map3> results4 = StudentGradesCustomArray.ReduceByKey1(results3);
ArrayList<map3> results5 = StudentGradesCustomArray.Filter1(results4);
}}
