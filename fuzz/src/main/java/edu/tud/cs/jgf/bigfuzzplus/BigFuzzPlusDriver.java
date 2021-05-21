package edu.tud.cs.jgf.bigfuzzplus;

//import edu.berkeley.cs.jqf.fuzz.junit.GuidedFuzzing;

import edu.berkeley.cs.jqf.fuzz.junit.GuidedFuzzing;
import edu.tud.cs.jgf.bigfuzzplus.multiMutation.HighOrderMutation;
import edu.tud.cs.jgf.bigfuzzplus.multiMutation.MultiMutation;
import edu.tud.cs.jgf.bigfuzzplus.multiMutation.MultiMutationReference;

import java.io.*;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;

@SuppressWarnings("StringConcatenationInsideStringBufferAppend")
public class BigFuzzPlusDriver {
    // These booleans are for debugging purposes only, toggle them if you want to see the information
    public static boolean PRINT_METHOD_NAMES = false;
    public static boolean PRINT_MUTATION_DETAILS = false;
    public static boolean PRINT_ERRORS = false;
    public static boolean PRINT_MUTATIONS = false;
    public static boolean PRINT_TEST_RESULTS = false;
    public static StringBuilder program_configuration = new StringBuilder();
    public static StringBuilder iteration_results = new StringBuilder();
    public static StringBuilder summarized_results = new StringBuilder();

    /**
     * Run the BigFuzzPlus program with the following parameters:
     * [0] - test class
     * [1] - test method
     * [2] - mutation method
     * [3] - max Trials                (default = Long.MAXVALUE)
     * [4] - multi mutation method     (default = disabled)
     * [5] - max mutation stack        (default = 2)
     *
     * @param args program arguments
     */
    public static void main(String[] args) {

        // LOAD PROGRAM ARGUMENTS
        if (args.length < 3) {
            System.err.println("Usage: java " + BigFuzzPlusDriver.class + " TEST_CLASS TEST_METHOD MUTATION_CLASS [MAX_TRIALS]");
            System.exit(1);
        }

        String testClassName = args[0];
        String testMethodName = args[1];
        String mutationMethodClassName = args[2];

        Long maxTrials = args.length > 3 ? Long.parseLong(args[3]) : Long.MAX_VALUE;
        System.out.println("maxTrials: " + maxTrials);

        int intMultiMutationMethod = args.length > 4 ? Integer.parseInt(args[4]) : 0;
        MultiMutationReference.MultiMutationMethod multiMutationMethod = MultiMutationReference.intToMultiMutationMethod(intMultiMutationMethod);
        System.out.println("mutationMethod: " + multiMutationMethod);

        // This variable is used for the multiMutationMethod: Smart_mutate
        // If the selected multiMutationMethod is smart_mutate and this argument is not given, default is set to 2. If smart_mutate is not selected, set to 0
        int intMutationStackCount = args.length > 5 ? Integer.parseInt(args[5]) : multiMutationMethod == MultiMutationReference.MultiMutationMethod.Smart_mutate ? 2 : 0;
        System.out.println("maximal amount of stacked mutation: " + intMutationStackCount);

        // **************

        long programStartTime = System.currentTimeMillis();
        File outputDir = new File("output/" + programStartTime);

        program_configuration.append("Program started with the following parameters: ");
        program_configuration.append("\n\tTest class: " + testClassName);
        program_configuration.append("\n\tTest method: " + testMethodName);
        program_configuration.append("\n\tTest method: " + mutationMethodClassName);
        program_configuration.append("\n\tTest multiMutation method: " + multiMutationMethod);
        program_configuration.append("\n\tTest maximal stacked mutations: " + intMutationStackCount);

        program_configuration.append("\nOutput directory is set to: " + outputDir);
        program_configuration.append("\nProgram is started at: " + programStartTime);

        boolean newOutputDirCreated = outputDir.mkdir();
        if (!newOutputDirCreated) {
            System.out.println("Something went wrong with making the output directory for this run");
            System.exit(0);
        }

        ArrayList<ArrayList<Integer>> uniqueFailureResults = new ArrayList();
        ArrayList<ArrayList<String>> inputs = new ArrayList();
        ArrayList<ArrayList<String>> methods = new ArrayList();
        ArrayList<ArrayList<String>> columns = new ArrayList();
        ArrayList<Long> durations = new ArrayList();
        for (int i = 0; i < 5; i++) {
            int atIteration = i + 1;
            System.out.println("******** START OF PROGRAM ITERATION: " + atIteration + "**********************");

            String file = "dataset/conf";
            try {
                long iterationStartTime = System.currentTimeMillis();

                Duration maxDuration = Duration.of(10, ChronoUnit.MINUTES);
                //NoGuidance guidance = new NoGuidance(file, maxTrials, System.err);
                String iterationOutputDir = outputDir + "/Test" + atIteration;
                BigFuzzPlusGuidance guidance = new BigFuzzPlusGuidance("Test" + atIteration, file, maxTrials, iterationStartTime, maxDuration, System.err, iterationOutputDir, mutationMethodClassName);

                // Set the provided input argument multiMutationMethod in the guidance mutation
                guidance.setMultiMutationMethod(multiMutationMethod);
                guidance.setMutationStackCount(intMutationStackCount);

                // Set the randomization seed to the program start time. Seed is passed to allow for custom seeds, independent of the program start time
                guidance.setRandomizationSeed(programStartTime);

                // Set the test class name in the guidance for the failure tracking
                guidance.setTestClassName(testClassName);

                // Run the Junit test
                GuidedFuzzing.run(testClassName, testMethodName, guidance, System.out);
                long endTime = System.currentTimeMillis();

                // Evaluate the results
                evaluation(testClassName, testMethodName, file, maxTrials, maxDuration, iterationStartTime, endTime, guidance, atIteration);
                writeToLists(guidance, maxTrials, inputs, uniqueFailureResults, methods, columns);
                durations.add(endTime - iterationStartTime);
                System.out.println("************************* END OF PROGRAM ITERATION ************************");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        summarizeProgramIterations(uniqueFailureResults, inputs, methods, columns, durations);
        writeLogToFile(outputDir);
    }

    /**
     * Write collected log in the variables log, summarized results and iteration results to a file in the output folder named log.txt.
     * @param outputDir Directory where the log file should be written to
     */
    private static void writeLogToFile(File outputDir) {
        File f_out = new File(outputDir + "/log.txt");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(f_out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(0);
        }

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
        String output = program_configuration.append("\n\n").append(summarized_results).append("\n\n").append(iteration_results).toString();

        try {
            bw.write(output);
            bw.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Convert collected data lists to a readable string in summarized_results
     * @param uniqueFailureResults List of unique failures per program iteration
     * @param inputs    List of sequential mutated inputs per program iteration
     * @param methods   List of summed HighOrderMutation methods applied per iteration
     * @param columns   List of count per column how many times mutation was applied to said column
     * @param durations List of iteration durations
     */
    private static void summarizeProgramIterations(ArrayList<ArrayList<Integer>> uniqueFailureResults, ArrayList<ArrayList<String>> inputs, ArrayList<ArrayList<String>> methods, ArrayList<ArrayList<String>> columns, ArrayList<Long> durations) {
        summarized_results.append("********* PROGRAM SUMMARY **********");
        // --------------- UNIQUE FAILURES --------------
        summarized_results.append("\nCUMULATIVE UNIQUE FAILURE PER TEST PER ITERATION");
        for (int i = 0; i < uniqueFailureResults.size(); i++) {
            summarized_results.append("\nRun " + (i + 1) + ": " + uniqueFailureResults.get(i));
        }

        // --------------- INPUTS --------------
        summarized_results.append("\n\nAPPLIED MUTATIONS PER ITERATION");
        for (int i = 0; i < inputs.size(); i++) {
            summarized_results.append("\nRun " + (i + 1) + " [");
            for (int j = 0; j < inputs.get(i).size(); j++) {
                if (j != 0) {
                    summarized_results.append(", ");
                }
                summarized_results.append("\"" + inputs.get(i).get(j) + "\"");
            }
            summarized_results.append("]");
        }

        // --------------- MUTATION COUNTER --------------
        summarized_results.append("\n\n MUTATED INPUTS PER ITERATION");
        for (int i = 0; i < methods.size(); i++) {
            summarized_results.append("\nRun " + (i + 1) + ": [");
            for (int j = 0; j < methods.get(i).size(); j++) {
                if (j != 0) {
                    summarized_results.append(", ");
                }
                summarized_results.append("(" + methods.get(i).get(j) + ")");
            }
            summarized_results.append("]");
        }

        // --------------- COLUMN COUNTER --------------
        summarized_results.append("\n\n MUTATIONS APPLIED ON COLUMN PER ITERATION");
        for (int i = 0; i < columns.size(); i++) {
            summarized_results.append("\nRun " + (i + 1) + ": [");
            for (int j = 0; j < columns.get(i).size(); j++) {
                if (j != 0) {
                    summarized_results.append(", ");
                }
                summarized_results.append("(" + columns.get(i).get(j) + ")");
            }
            summarized_results.append("]");
        }

        // --------------- DURATION --------------
        summarized_results.append("\n\n DURATION PER ITERATION");
        summarized_results.append("\ndurations: " + durations);
        for (int i = 0; i < durations.size(); i++) {
            summarized_results.append("\nRun " + (i + 1) + ": " + durations.get(i) + " ms ");
        }
        System.out.println(summarized_results);
    }

    /**
     * Transforms data in guidance to required lists.
     * @param guidance  guidance class which contains all data
     * @param maxTrials maximal amount of trials (configuration)
     * @param inputs list of inputs passed to the program that is being tested
     * @param uniqueFailureResults list of unique failures
     * @param methods list of methods applied
     * @param columns list of count how many times mutation was applied per column
     */
    private static void writeToLists(BigFuzzPlusGuidance guidance, Long maxTrials, ArrayList<ArrayList<String>> inputs, ArrayList<ArrayList<Integer>> uniqueFailureResults, ArrayList<ArrayList<String>> methods, ArrayList<ArrayList<String>> columns) {
        // Unique failure results
        int cumulative = 0;
        ArrayList<Integer> runFoundUniqueFailureCumulative = new ArrayList<>();
        for (long j = 0; j < maxTrials; j++) {
            if (guidance.uniqueFailureRuns.contains(j))
                cumulative++;
            runFoundUniqueFailureCumulative.add(cumulative);
        }
        // Methods and columns
        ArrayList<HighOrderMutation.HighOrderMutationMethod> methodTracker = ((MultiMutation) guidance.mutation).getMutationMethodTracker();
        ArrayList<Integer> columnTracker = ((MultiMutation) guidance.mutation).getMutationColumnTracker();
        HashMap<HighOrderMutation.HighOrderMutationMethod, Integer> methodMap = new HashMap();
        HashMap<Integer, Integer> columnMap = new HashMap();
        for (int i = 0; i < methodTracker.size(); i++) {
            HighOrderMutation.HighOrderMutationMethod method = methodTracker.get(i);
            int column = columnTracker.get(i);
            if (methodMap.containsKey(method)) {
                methodMap.put(method, methodMap.get(method) + 1);
            } else {
                methodMap.put(method, 1);
            }
            if (columnMap.containsKey(column)) {
                columnMap.put(column, columnMap.get(column) + 1);
            } else {
                columnMap.put(column, 1);
            }
        }
        Iterator<Map.Entry<HighOrderMutation.HighOrderMutationMethod, Integer>> it = methodMap.entrySet().iterator();
        ArrayList<String> methodStringList = new ArrayList();
        while (it.hasNext()) {
            Map.Entry e = it.next();
            methodStringList.add(e.getKey() + ": " + e.getValue());
        }

        Iterator<Map.Entry<Integer, Integer>> it2 = columnMap.entrySet().iterator();
        ArrayList<String> columnStringList = new ArrayList();
        while (it2.hasNext()) {
            Map.Entry e = it2.next();
            columnStringList.add(e.getKey() + ": " + e.getValue());
        }


        methods.add(methodStringList);
        columns.add(columnStringList);
        inputs.add(guidance.inputs);
        uniqueFailureResults.add(runFoundUniqueFailureCumulative);
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
     * @param atIteration    Counter indicating for which iteration this evaluation is.
     */
    private static void evaluation(String testClassName, String testMethodName, String file, Long maxTrials, Duration duration, long startTime, long endTime, BigFuzzPlusGuidance guidance, int atIteration) {
        StringBuilder e_log = new StringBuilder();
        // Print configuration
        e_log.append("*** TEST " + atIteration + " LOG ***");
        e_log.append("\n---CONFIGURATION---");
        e_log.append("\nFiles used..." + "\n\tconfig:\t\t" + file + "\n\ttestClass:\t" + testClassName + "\n\ttestMethod:\t" + testMethodName);
        e_log.append("Max trials: " + maxTrials);
        e_log.append("Max duration: " + duration.toMillis() + "ms");

        e_log.append("\n---REPRODUCIBILITY---");
        if (guidance.mutation instanceof MultiMutation) {
            e_log.append("\n\tRandomization seed: " + ((MultiMutation) guidance.mutation).getRandomizationSeed());
        }
        e_log.append("\n\tMutated inputs: [");
        for (int i = 0; i < guidance.inputs.size(); i++) {
            if (i != 0) {
                e_log.append(", ");
            }
            e_log.append("\"" + guidance.inputs.get(i) + "\"");
        }
        e_log.append("]");

        // Print results
        e_log.append("\n---RESULTS---");

        // Failures
        e_log.append("\n\tTotal Failures: " + guidance.totalFailures);
        e_log.append("\n\tUnique Failures: " + guidance.uniqueFailures.size());
        e_log.append("\n\tUnique Failures found at: " + guidance.uniqueFailureRuns);
        List<Boolean> runFoundUniqueFailure = new ArrayList<>();
        int cumulative = 0;
        List<Integer> runFoundUniqueFailureCumulative = new ArrayList<>();
        for (long i = 0; i < maxTrials; i++) {
            runFoundUniqueFailure.add(guidance.uniqueFailureRuns.contains(i));
            if (guidance.uniqueFailureRuns.contains(i))
                cumulative++;
            runFoundUniqueFailureCumulative.add(cumulative);
        }
        e_log.append("\n\tUnique Failure found per run: " + runFoundUniqueFailure);
        e_log.append("\n\tUnique Failure found per run: " + runFoundUniqueFailureCumulative);

        // Run time
        long totalDuration = endTime - startTime;
        if (guidance.numTrials != maxTrials) {
            e_log.append("Could not complete all trials in the given duration.");
        }
        e_log.append("\n\tTotal run time：" + totalDuration + "ms");
        e_log.append("\n\tTests run: " + guidance.numTrials);
        e_log.append("\n\tAverage test run time: " + (float) totalDuration / guidance.numTrials + "ms");

        // Coverage
        int totalCov = guidance.totalCoverage.getNonZeroCount();
        int validCov = guidance.validCoverage.getNonZeroCount();
        e_log.append("\n\tTotal coverage: " + totalCov);
        e_log.append("\n\tValid coverage: " + validCov);
        e_log.append("\n\tPercent valid coverage: " + (float) validCov / totalCov * 100 + "%");
        System.out.println(e_log);
        iteration_results.append(e_log);
    }
}
