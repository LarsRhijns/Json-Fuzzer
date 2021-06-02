import P9.NumberSeries.Spec_BigFuzz.InfiniteloopCustomArray;
import P9.NumberSeries.Spec_BigFuzz.map2;
import main.java.sharedClasses.CustomArray;


import java.io.IOException;
import java.util.ArrayList;

public class Infiniteloop {
public void Infiniteloop(String inputFile) throws IOException {
ArrayList<String> results0 = CustomArray.read(inputFile);
ArrayList<Integer> results1 = InfiniteloopCustomArray.Map1(results0);
ArrayList<map2> results2 = InfiniteloopCustomArray.Map2(results1);
ArrayList<map2> results3 = InfiniteloopCustomArray.Filter1(results2);
}}
