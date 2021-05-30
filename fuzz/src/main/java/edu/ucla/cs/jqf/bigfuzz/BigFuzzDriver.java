package edu.ucla.cs.jqf.bigfuzz;

import edu.berkeley.cs.jqf.fuzz.junit.GuidedFuzzing;
import org.apache.commons.io.FileUtils;
import org.scalatest.Entry;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BigFuzzDriver {

    // ---------- LOGGING / STATS OUTPUT ------------
    /** Cleans outputDirectory if true, else adds a new subdirectory in which the results are stored */
    public static boolean CLEAR_ALL_PREVIOUS_RESULTS_ON_START = false;

    // These booleans are for debugging purposes only, toggle them if you want to see the information
    public static boolean PRINT_METHOD_NAMES = false;
    public static boolean PRINT_MUTATION_DETAILS = false;
    public static boolean PRINT_COVERAGE_DETAILS = false;
    public static boolean PRINT_INPUT_SELECTION_DETAILS = false;
    public static boolean LOG_AND_PRINT_STATS = false;

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: java " + BigFuzzDriver.class + " TEST_CLASS TEST_METHOD [MAX_TRIALS]");
            System.exit(1);
        }

        // Read provided arguments, maxTrials is inf by default.
        long startTime = System.currentTimeMillis();
        String testClassName = args[0];
        String testMethodName = args[1];
        long maxTrials = args.length > 2 ? Long.parseLong(args[2]) : Long.MAX_VALUE;

        String outputDirectoryName = "fuzz-results";
        File outputDir = new File(outputDirectoryName);
        // Clear the entire output directory if this field is set to true
        if (CLEAR_ALL_PREVIOUS_RESULTS_ON_START && outputDir.isDirectory()) {
            try {
                FileUtils.cleanDirectory(outputDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        outputDirectoryName += "/" + startTime;

        File outputDirectory = new File(outputDirectoryName);

        String file = "dataset/conf";
        String title = testClassName+"#"+testMethodName;
        Duration duration = Duration.of(10, ChronoUnit.SECONDS);
        double favorRate = 0.8;

        try {
            // Create the Guidance
            BigFuzzGuidance guidance = new BigFuzzGuidance(title, file, maxTrials, duration, outputDirectory, favorRate);

            // Run the Junit test
            GuidedFuzzing.run(testClassName, testMethodName, guidance, System.out);
            long endTime = System.currentTimeMillis();

            // Evaluate the results
            evaluation(testClassName, testMethodName, file, maxTrials, duration, startTime, endTime, guidance);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Prints the configuration and the results from the run to the Terminal.
     *
     * @param testClassName Class name which is being tested
     * @param testMethodName Test method name which is used to perform the test
     * @param file  Input file for the testing
     * @param maxTrials maximal amount of trials configuration
     * @param duration maximal duration of the trials configuration
     * @param startTime start time of the program
     * @param endTime end time of the program
     * @param guidance guidance class which is used to perform the BigFuzz testing
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
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

        // Run time
        long totalDuration = endTime - startTime;
        if (guidance.numTrials != maxTrials) {
            System.out.println("!! Could not complete all trials in the given duration.");
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
        System.out.println("New Coverage found at: " + guidance.newCoverageRuns);
        List<Map.Entry<String, Integer>> fileHits = new ArrayList<>();
        for (Map.Entry<Set<Integer>, Integer> entry : guidance.branchesHitCount.entrySet()) {
            fileHits.add(new Entry<>(guidance.coverageFilePointer.get(entry.getKey()).getName(), entry.getValue()));
        }
        System.out.println("Branches hit count: " + fileHits);
        System.out.println("Number of Seed inputs: " + guidance.newCoverageRuns.size());
    }
}
