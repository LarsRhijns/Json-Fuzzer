package edu.ucla.cs.jqf.bigfuzz;

//import edu.berkeley.cs.jqf.fuzz.junit.GuidedFuzzing;
import edu.berkeley.cs.jqf.fuzz.junit.GuidedFuzzing;
import edu.ucla.cs.jqf.bigfuzz.evaluation.DataStore;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class BigFuzzDriver {
    public static DataStore ds;
    private static long folder;

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: java " + BigFuzzDriver.class + " TEST_CLASS TEST_METHOD [MAX_TRIALS]");
            System.exit(1);
        }

        String testClassName = args[0];
        String testMethodName = args[1];
        Long maxTrials = args.length > 2 ? Long.parseLong(args[2]) : Long.MAX_VALUE;
        System.out.println("maxTrials: "+maxTrials);
//        File outputDirectory = new File("../fuzz-results");


        String file = "dataset/conf";

        // Create evaluation storage. 
        ds = new DataStore("output/");

       try {
           long startTime = System.currentTimeMillis();
           folder = startTime;

            String title = testClassName+"#"+testMethodName;
              Duration duration = Duration.of(100, ChronoUnit.SECONDS);
             //NoGuidance guidance = new NoGuidance(file, maxTrials, System.err);
             File outputDir = new File("output/" + startTime);
             outputDir.mkdir();
             BigFuzzGuidance guidance = new BigFuzzGuidance("Test1", file, maxTrials, duration, System.err, "output/" + startTime + "/bigfuzzdata");

             // Run the Junit test
            GuidedFuzzing.run(testClassName, testMethodName, guidance, System.out);

            if (Boolean.getBoolean("jqf.logCoverage")) {
                System.out.println(String.format("Covered %d edges.",
                        guidance.getCoverage().getNonZeroCount()));
            }


           long endTime = System.currentTimeMillis();
           System.out.println("*********Running Time：" + (endTime - startTime) + "ms");

       } catch (Exception e) {
            e.printStackTrace();
//            System.exit(2);
        }

       ds.printUniqueFailures();
       try {
           ds.storeUniqueFailures(folder);
           ds.storeDataPoints(folder);
       } catch (IOException e) {
           System.out.println("Storing failed");
       }
    }
}
