package edu.ucla.cs.jqf.bigfuzz;

//import edu.berkeley.cs.jqf.fuzz.junit.GuidedFuzzing;

import edu.berkeley.cs.jqf.fuzz.junit.GuidedFuzzing;

import java.io.File;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class BigFuzzDriver {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: java " + BigFuzzDriver.class + " TEST_CLASS TEST_METHOD [MAX_TRIALS]");
            System.exit(1);
        }

        String testClassName = args[0];
        String testMethodName = args[1];

        Long maxTrials = args.length > 2 ? Long.parseLong(args[2]) : Long.MAX_VALUE;
        System.out.println("maxTrials: " + maxTrials);

        int intMultiMutationMethod = args.length > 3 ? Integer.parseInt(args[3]) : 0;
        MultiMutation.MultiMutationMethod multiMutationMethod = MultiMutation.intToMultiMutationMethod(intMultiMutationMethod);
        System.out.println("mutationMethod: " + multiMutationMethod);

        String file = "dataset/conf";
        try {
            long startTime = System.currentTimeMillis();

            String title = testClassName + "#" + testMethodName;
            Duration duration = Duration.of(100, ChronoUnit.SECONDS);
            //NoGuidance guidance = new NoGuidance(file, maxTrials, System.err);
            BigFuzzGuidance guidance = new BigFuzzGuidance("Test1", file, maxTrials, duration, System.err, "output");

            // Set the provided input argument multiMutationMethod in the guidance mutation
            guidance.setMultiMutationMethod(multiMutationMethod);

            // Run the Junit test
            GuidedFuzzing.run(testClassName, testMethodName, guidance, System.out);
            long endTime = System.currentTimeMillis();

            // Evaluate the results
            evaluation(testClassName, testMethodName, file, maxTrials, duration, startTime, endTime, guidance);


        } catch (Exception e) {
            e.printStackTrace();
//            System.exit(2);
        }
    }

    /**
     * Prints the configuration and the results from the run to the Terminal.
     *
     * @param testClassName  Class name which is being tested
     * @param testMethodName Test method name which is used to perform the test
     * @param file           Input file for the testing
     * @param maxTrials      maximal amount of trials configuration
     * @param duration       maximal duration of the trials configuration
     * @param startTime      start time of the program
     * @param endTime        end time of the program
     * @param guidance       guidance class which is used to perform the BigFuzz testing
     */
    private static void evaluation(String testClassName, String testMethodName, String file, Long maxTrials, Duration duration, long startTime, long endTime, BigFuzzGuidance guidance) {
        // Print configuration
        System.out.println("---CONFIGURATION---");
        System.out.println("Files used..." + "\n\tconfig:\t\t" + file + "\n\ttestClass:\t" + testClassName + "\n\ttestMethod:\t" + testMethodName);
        System.out.println("Max trials: " + maxTrials);
        System.out.println("Max duration: " + duration.toMillis() + "ms");

        // Print results
        System.out.println("\n---RESULTS---");
        if (Boolean.getBoolean("jqf.logCoverage")) {
            System.out.printf("Covered %d edges.%n",
                    guidance.getCoverage().getNonZeroCount());
        }

        // Failures
        System.out.println("Total Failures: " + guidance.totalFailures);
        System.out.println("Unique Failures: " + guidance.uniqueFailures.size());
        System.out.println("Unique Failures found at: " + guidance.uniqueFailureRuns);
        List<Boolean> runFoundUniqueFailure = new ArrayList<>();
        int cumm = 0;
        List<Integer> runFoundUniqueFailureCumm = new ArrayList<>();
        for (long i = 0; i < maxTrials; i++) {
            runFoundUniqueFailure.add(guidance.uniqueFailureRuns.contains(i));
            if(guidance.uniqueFailureRuns.contains(i))
                cumm++;
            runFoundUniqueFailureCumm.add(cumm);
        }
        System.out.println("Unique Failure found per run: " + runFoundUniqueFailure);
        System.out.println("Unique Failure found per run: " + runFoundUniqueFailureCumm);

        // Run time
        long totalDuration = endTime - startTime;
        if (guidance.numTrials != maxTrials) {
            System.out.println("Could not complete all trials in the given duration.");
        }
        System.out.println("Total run time：" + totalDuration + "ms");
        System.out.println("Tests run: " + guidance.numTrials);
        System.out.println("Average test run time: " + (float) totalDuration / guidance.numTrials + "ms");

        // Coverage
        int totalCov = guidance.totalCoverage.getNonZeroCount();
        int validCov = guidance.validCoverage.getNonZeroCount();
        System.out.println("Total coverage: " + totalCov);
        System.out.println("Valid coverage: " + validCov);
        System.out.println("Percent valid coverage: " + (float) validCov / totalCov * 100 + "%");
    }
}
