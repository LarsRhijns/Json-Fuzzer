package edu.ucla.cs.jqf.bigfuzz;

//import edu.berkeley.cs.jqf.fuzz.junit.GuidedFuzzing;

import edu.berkeley.cs.jqf.fuzz.junit.GuidedFuzzing;

import java.io.File;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class BigFuzzDriver {
    // These booleans are for debugging purposes only, toggle them if you want to see the information
    public static boolean PRINT_METHODNAMES = false;
    public static boolean PRINT_MUTATIONDETAILS = false;

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

        // This variable is used for the multiMutationMethod: Smart_mutate
        // If the selected multiMutationMethod is smart_mutate and this argument is not given, default is set to 2. If smart_mutate is not selected, set to 0
        int intMutationStackCount = args.length > 4 ? Integer.parseInt(args[4]) : multiMutationMethod == MultiMutation.MultiMutationMethod.Smart_mutate ? 2 : 0 ;
        System.out.println("maximal amount of stacked mutation: " + intMutationStackCount);


        ArrayList<ArrayList<Integer>> uniqueFailureResults = new ArrayList();
        ArrayList<ArrayList<String>> inputs = new ArrayList();
        ArrayList<ArrayList<String>> methods = new ArrayList();
        ArrayList<ArrayList<String>> columns = new ArrayList();
        for (int i = 0; i < 1; i++) {
            System.out.println("******** START OF PROGRAM ITERATION: " + i + "**********************");


            String file = "dataset/conf";
            try {
                long startTime = System.currentTimeMillis();

                String title = testClassName + "#" + testMethodName;
                Duration duration = Duration.of(100, ChronoUnit.SECONDS);
                //NoGuidance guidance = new NoGuidance(file, maxTrials, System.err);
                BigFuzzGuidance guidance = new BigFuzzGuidance("Test" + i, file, maxTrials, startTime, duration, System.err, "output");

                // Set the provided input argument multiMutationMethod in the guidance mutation
                guidance.setMultiMutationMethod(multiMutationMethod);
                guidance.setMutationStackCount(intMutationStackCount);

                // Set the test class name in the guidance for the failure tracking
                guidance.setTestClassName(testClassName);

                // Run the Junit test
                GuidedFuzzing.run(testClassName, testMethodName, guidance, System.out);
                long endTime = System.currentTimeMillis();

                // Evaluate the results
                evaluation(testClassName, testMethodName, file, maxTrials, duration, startTime, endTime, guidance);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < uniqueFailureResults.size(); i++) {
            System.out.println(uniqueFailureResults.get(i));
        }
        for (int i = 0; i < inputs.size(); i++) {
            System.out.print("Run " + i +" [");
            for (int j = 0; j < inputs.get(i).size(); j++) {
                System.out.print("\"" + inputs.get(i).get(j) + "\", ");
            }
            System.out.println();
        }

        for (int i = 0; i < methods.size(); i++) {
            System.out.print("Run " + i +"[");
            for (int j = 0; j < methods.get(i).size(); j++) {
                System.out.print("(" + methods.get(i).get(j) + "), ");
            }
            System.out.println("]");
        }
        for (int i = 0; i < columns.size(); i++) {
            System.out.print("Run " + i +"[");
            for (int j = 0; j < columns.get(i).size(); j++) {
                System.out.print("(" + columns.get(i).get(j) + "), ");
            }
            System.out.println("]");
        }


    }

    private static void writeToLists(BigFuzzGuidance guidance, Long maxTrials, ArrayList<ArrayList<String>> inputs, ArrayList<ArrayList<Integer>> uniqueFailureResults, ArrayList<ArrayList<String>> methods, ArrayList<ArrayList<String>> columns) {
        int cumm = 0;
        ArrayList<Integer> runFoundUniqueFailureCumm = new ArrayList<>();
        for (long j = 0; j < maxTrials; j++) {
            if (guidance.uniqueFailureRuns.contains(j))
                cumm++;
            runFoundUniqueFailureCumm.add(cumm);
        }
        LinkedList<Integer> methodtracker = ((MutationTemplate) guidance.mutation).mutationMethodTracker;
        LinkedList<Integer> columntracker = ((MutationTemplate) guidance.mutation).mutationColumnTracker;
        HashMap<Integer,Integer> methodMap = new HashMap();
        HashMap<Integer,Integer> columnMap = new HashMap();
        for (int i = 0; i < methodtracker.size(); i++) {
            int method = methodtracker.get(i);
            int column = columntracker.get(i);
            if(methodMap.containsKey(method)) {
                methodMap.put(method, methodMap.get(method) +1);
            } else {
                methodMap.put(method, 1);
            }
            if(columnMap.containsKey(column)) {
                columnMap.put(column, columnMap.get(column) +1);
            } else {
                columnMap.put(column, 1);
            }
        }
        Iterator<Map.Entry<Integer,Integer>> it = methodMap.entrySet().iterator();
        ArrayList<String>  methodStringList = new ArrayList();
        while(it.hasNext()) {
            Map.Entry e = it.next();
            methodStringList.add( e.getKey() + ": " + e.getValue());
        }

        Iterator<Map.Entry<Integer,Integer>> it2 = columnMap.entrySet().iterator();
        ArrayList<String> columnStringList =  new ArrayList();
        while(it2.hasNext()) {
            Map.Entry e = it2.next();
            columnStringList.add(  e.getKey() + ": " + e.getValue());
        }


        methods.add(methodStringList);
        columns.add(columnStringList);
        inputs.add(guidance.inputs);
        uniqueFailureResults.add(runFoundUniqueFailureCumm);
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
            if (guidance.uniqueFailureRuns.contains(i))
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
