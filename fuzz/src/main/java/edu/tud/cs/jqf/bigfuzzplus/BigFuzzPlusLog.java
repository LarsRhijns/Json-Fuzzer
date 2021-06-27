package edu.tud.cs.jqf.bigfuzzplus;

import edu.tud.cs.jqf.bigfuzzplus.stackedMutation.HighOrderMutation;
import edu.tud.cs.jqf.bigfuzzplus.stackedMutation.MutationPair;
import edu.tud.cs.jqf.bigfuzzplus.stackedMutation.StackedMutation;
import edu.tud.cs.jqf.bigfuzzplus.stackedMutation.StackedMutationEnum;
import edu.tud.cs.jqf.bigfuzzplus.systematicMutation.SystematicMutation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static edu.tud.cs.jqf.bigfuzzplus.BigFuzzPlusDriver.maxDuration;

@SuppressWarnings("StringConcatenationInsideStringBufferAppend")
public class BigFuzzPlusLog {
    private static final boolean LOG_UNIQUE_FAILURES_PER_TRIAL = true;
    private static final boolean LOG_INPUTS = true;
    private static final boolean LOG_APPLIED_MUTATION_AND_MUTATED_COLUMN = true;
    private static final boolean LOG_MUTATION_STACKS = true;
    private static final boolean LOG_ERROR_INPUT_COUNT = true;
    private static final boolean LOG_VALID_INPUT_COUNT = true;
    private static final boolean LOG_BRANCH_COVERAGE = true;
    private static final boolean LOG_UNIQUE_FAILURE_AND_MUTATION = true;

    private static final boolean PRINT_TO_CONSOLE = true;

    private static BigFuzzPlusLog INSTANCE;

    private static StringBuilder program_configuration = new StringBuilder();
    private static StringBuilder iteration_results = new StringBuilder();
    private static StringBuilder summarized_results = new StringBuilder();

    private static ArrayList<ArrayList<Integer>> uniqueFailureResults = new ArrayList<>();
    private static ArrayList<ArrayList<String>> inputs = new ArrayList<>();
    private static ArrayList<ArrayList<String>> appliedMutationMethods = new ArrayList<>();
    private static ArrayList<ArrayList<String>> mutatedColumns = new ArrayList<>();
    private static ArrayList<ArrayList<String>> mutationStacks = new ArrayList<>();

    private static ArrayList<Long> errorInputCount = new ArrayList<>();
    private static ArrayList<Long> validInputCount = new ArrayList<>();
    private static ArrayList<Long> durations = new ArrayList<>();
    private static ArrayList<Long> randomizationSeeds = new ArrayList<>();
    private static ArrayList<Map<Set<Integer>, Integer>> branchesHit = new ArrayList<>();
    private static ArrayList<Collection<Integer>> totalBranches = new ArrayList<>();
    private static ArrayList<ArrayList<Long>> newDiscoveryTrials = new ArrayList<>();


    private BigFuzzPlusLog() {}

    public static BigFuzzPlusLog getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new BigFuzzPlusLog();
        }
        return INSTANCE;
    }

    public static void resetInstance() {
        INSTANCE = new BigFuzzPlusLog();

        // TODO: singleton has not been implemented correctly/might not be suited for the job.
        program_configuration = new StringBuilder();
        iteration_results = new StringBuilder();
        summarized_results = new StringBuilder();

        uniqueFailureResults = new ArrayList<>();
        inputs = new ArrayList<>();
        appliedMutationMethods = new ArrayList<>();
        mutatedColumns = new ArrayList<>();
        mutationStacks = new ArrayList<>();

        errorInputCount = new ArrayList<>();
        validInputCount = new ArrayList<>();
        durations = new ArrayList<>();
        randomizationSeeds = new ArrayList<>();
        branchesHit = new ArrayList<>();
        totalBranches = new ArrayList<>();
        newDiscoveryTrials = new ArrayList<>();
    }

    public void logProgramArguments(String testClassName, String testMethodName, String mutationMethodClassName, File outputDir, long programStartTime, String configfile, long maxTrials) {
        program_configuration.append("\n************ PROGRAM CONFIGURATION ************");
        program_configuration.append("\nOutput directory is set to: " + outputDir);
        program_configuration.append("\nProgram is started at: " + programStartTime);

        program_configuration.append("\nProgram arguments: ");
        program_configuration.append("\n\tTest class: " + testClassName);
        program_configuration.append("\n\tTest method: " + testMethodName);
        program_configuration.append("\n\tMutation class: " + mutationMethodClassName);
        program_configuration.append("\n\tFiles used..." +
                "\n\t\tconfig:\t\t" + configfile +
                "\n\t\ttestClass:\t" + testClassName +
                "\n\t\ttestMethod:\t" + testMethodName);
        program_configuration.append("\n\tMax trials: " + maxTrials);
        String durationString = maxDuration == null ? "inf " : Long.toString(maxDuration.toMillis());
        program_configuration.append("\n\tMax duration: " + durationString + "ms");
    }

    public void logProgramArgumentsStackedMutation(StackedMutationEnum.StackedMutationMethod stackedMutationMethod, int intMutationStackCount) {
        program_configuration.append("\n\tStackedMutation method: " + stackedMutationMethod);
        program_configuration.append("\n\tMaximal stacked mutations: " + intMutationStackCount);
    }

    public void logProgramArgumentsSystematicMutation(boolean mutateColumns, int mutationDepth) {
        program_configuration.append("\n\tMutate columns: " + mutateColumns);
        program_configuration.append("\n\tMutate depth: " + mutationDepth);
    }

    public void printProgramArguments() {
        if(PRINT_TO_CONSOLE)
            System.out.println(program_configuration);
    }

    public void writeToLists(BigFuzzPlusGuidance guidance, Long maxTrials) {
        // Unique failure results
        if(LOG_UNIQUE_FAILURES_PER_TRIAL)
            writeUniqueFailureResults(guidance,maxTrials);
        if(LOG_INPUTS)
            inputs.add(guidance.inputs);

        // StackedMutation log
        if (guidance.mutation instanceof StackedMutation) {
            writeStackedMutationLists(guidance);
        }
    }

    private void writeUniqueFailureResults(BigFuzzPlusGuidance guidance, Long maxTrials) {
        int cumulative = 0;
        ArrayList<Integer> runFoundUniqueFailureCumulative = new ArrayList<>();
        for (long j = 0; j < maxTrials; j++) {
            if (guidance.uniqueFailureRuns.contains(j))
                cumulative++;
            runFoundUniqueFailureCumulative.add(cumulative);
        }

        uniqueFailureResults.add(runFoundUniqueFailureCumulative);
    }

    private void writeStackedMutationLists(BigFuzzPlusGuidance guidance) {
        if(LOG_APPLIED_MUTATION_AND_MUTATED_COLUMN)
            writeMutationAndColumnSummaryToList(guidance);

        if(LOG_ERROR_INPUT_COUNT)
            errorInputCount.add((long)guidance.totalFailures);
        if(LOG_UNIQUE_FAILURE_AND_MUTATION)
            validInputCount.add(guidance.numValid);

        if(LOG_MUTATION_STACKS)
            writeMutationStack(guidance);

        if (guidance.mutation instanceof StackedMutation) {
            randomizationSeeds.add(((StackedMutation) guidance.mutation).getRandomizationSeed());
        }
        branchesHit.add(guidance.branchesHitCount);
        totalBranches.add(guidance.totalCoverage.getCounter().getNonZeroIndices());
        newDiscoveryTrials.add(guidance.newDiscoveryTrials);
    }

    private void writeMutationStack(BigFuzzPlusGuidance guidance) {
        ArrayList<Integer> stackCountList = ((StackedMutation) guidance.mutation).getMutationStackTracker();
        // Create a hashmap of the count and how many times it occurred
        HashMap<Integer,Integer> stackCount = new HashMap<>();
        for (Integer integer : stackCountList) {
            if (stackCount.containsKey(integer)) {
                stackCount.put(integer, stackCount.get(integer) + 1);
            } else {
                stackCount.put(integer, 1);
            }
        }
        Iterator<Map.Entry<Integer, Integer>> it3 = stackCount.entrySet().iterator();
        ArrayList<String> mutationStackStringList = new ArrayList<>();
        while (it3.hasNext()) {
            Map.Entry<Integer, Integer> e = it3.next();
            mutationStackStringList.add(e.getKey() + ": " + e.getValue());
        }

        mutationStacks.add(mutationStackStringList);
    }

    private void writeMutationAndColumnSummaryToList(BigFuzzPlusGuidance guidance) {
        ArrayList<HighOrderMutation.HighOrderMutationMethod> methodTracker;
        ArrayList<Integer> columnTracker;
        methodTracker = ((StackedMutation) guidance.mutation).getMutationMethodTracker();
        columnTracker = ((StackedMutation) guidance.mutation).getMutationColumnTracker();

        HashMap<HighOrderMutation.HighOrderMutationMethod, Integer> methodMap = new HashMap<>();
        HashMap<Integer, Integer> columnMap = new HashMap<>();
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
        ArrayList<String> methodStringList = new ArrayList<>();
        while (it.hasNext()) {
            Map.Entry<HighOrderMutation.HighOrderMutationMethod, Integer> e = it.next();
            methodStringList.add(e.getKey() + ": " + e.getValue());
        }


        @SuppressWarnings("DuplicatedCode") Iterator<Map.Entry<Integer, Integer>> it2 = columnMap.entrySet().iterator();
        ArrayList<String> columnStringList = new ArrayList<>();
        while (it2.hasNext()) {
            Map.Entry<Integer, Integer> e = it2.next();
            columnStringList.add(e.getKey() + ": " + e.getValue());
        }
        appliedMutationMethods.add(methodStringList);
        mutatedColumns.add(columnStringList);
    }

    public void summarizeProgramIterations() {
        summarized_results.append("************ PROGRAM SUMMARY ************");
        int maxTrials = uniqueFailureResults.get(0).size();

        // --------------- INPUTS --------------
        summarized_results.append("\nMUTATION RESULTS PER ITERATION");
        if(!LOG_INPUTS) {
            summarized_results.append("\n\tData log disabled");
        } else {
            summarized_results.append(dataPerIterationListToLog(inputs));
        }

        // --------------- MUTATION COUNTER --------------
        summarized_results.append("\n\nMUTATED INPUTS PER ITERATION");
        if(!LOG_APPLIED_MUTATION_AND_MUTATED_COLUMN) {
            summarized_results.append("\n\tData log disabled");
        } else {
            summarized_results.append(dataPerIterationListToLog(appliedMutationMethods));
        }

        // --------------- COLUMN COUNTER --------------
        summarized_results.append("\n\nMUTATIONS APPLIED ON COLUMN PER ITERATION");
        if(!LOG_APPLIED_MUTATION_AND_MUTATED_COLUMN) {
            summarized_results.append("\n\tData log disabled");
        } else {
            summarized_results.append(dataPerIterationListToLog(mutatedColumns));
        }

        // --------------- DURATION --------------
        summarized_results.append("\n\nDURATION PER ITERATION (in ms)");
        summarized_results.append("\n\tDurations: " + durations);
        float[] averageTestDuration = new float[durations.size()];
        for(int a = 0; a < durations.size(); a++) {
            averageTestDuration[a] = (float) durations.get(a) / maxTrials;
        }
        summarized_results.append("\n\tAverage duration per test: " + Arrays.toString(averageTestDuration));
        for (int i = 0; i < durations.size(); i++) {
            summarized_results.append("\n\tRun " + (i + 1) + ": " + durations.get(i));
        }
        // Run time


        // --------------- STACKED MUTATION ---------------------
        summarized_results.append("\n\nSTACKED COUNT PER MUTATION PER ITERATION");
        if(!LOG_MUTATION_STACKS || mutationStacks.isEmpty()) {
            summarized_results.append("\n\tData log disabled");
        } else if (mutationStacks.get(0).isEmpty()) {
            summarized_results.append("\n\tMutation depth = 0");
        }
        else {
            summarized_results.append(dataPerIterationListToLog(mutationStacks));
        }

        if (!randomizationSeeds.isEmpty()) {
            summarized_results.append("\n\tRandomization seeds: " + randomizationSeeds);
        }

        // --------------- RESTARTS ---------------------
        summarized_results.append("\n\nRESTARTS");
        summarized_results.append("\n\tTotal amount of restarts: " + SystematicMutation.restartAmount);

        // --------------- ERRORS ---------------------
        summarized_results.append("\n\nERROR/VALID COUNT PER ITERATION");
        if(!LOG_ERROR_INPUT_COUNT) {
            summarized_results.append("\n\tData log disabled (error input count)");
        } else {
            summarized_results.append("\n\ttotal_errors: " + errorInputCount);
            for (int i = 0; i < errorInputCount.size(); i++) {
                summarized_results.append("\n\tRun " + (i + 1) + ": " + errorInputCount.get(i));
            }
        }
        if(!LOG_VALID_INPUT_COUNT) {
            summarized_results.append("\n\tData log disabled (valid input count)");
        } else {
            summarized_results.append("\n\ttotal_valid_inputs: " + validInputCount);
            for (int i = 0; i < validInputCount.size(); i++) {
                summarized_results.append("\n\tRun " + (i + 1) + ": " + validInputCount.get(i));
            }
        }

        // --------------- UNIQUE FAILURES --------------
        summarized_results.append("\n\nCUMULATIVE UNIQUE FAILURES PER RUN");
        if(!LOG_UNIQUE_FAILURES_PER_TRIAL) {
            summarized_results.append("\nData log disabled");
        } else {
            for (int i = 0; i < uniqueFailureResults.size(); i++) {
                summarized_results.append("\n\tRun " + (i + 1) + ": " + uniqueFailureResults.get(i));
            }
        }

        // --------------- COVERAGE INFORMATION -----------------
        ArrayList<ArrayList<Integer>> discoveriesCountAtTrial = new ArrayList<>();
        int totalBranchesSize = 0;

        summarized_results.append("\n\nCOVERAGE INFORMATION");
        if (!LOG_BRANCH_COVERAGE) {
            summarized_results.append("\n\tData log disabled");
        }
        else {
            for (int i = 0; i < branchesHit.size(); i++) {
                ArrayList<Long> runDiscoveryTrials = newDiscoveryTrials.get(i);
                totalBranchesSize += runDiscoveryTrials.size();

                discoveriesCountAtTrial.add(new ArrayList<>());
                int knownDiscoveries = 0;
                for (int t = 0; t < maxTrials; t++) {
                    if (newDiscoveryTrials.get(i).contains((long) t)) {
                        knownDiscoveries++;
                    }
                    discoveriesCountAtTrial.get(i).add(knownDiscoveries);
                }

                summarized_results.append("\n\tRun " + (i + 1) + ": " +
                        "\n\t\t# discovered branches = " + runDiscoveryTrials.size() +
                        "\n\t\tnew discovery trials = " + newDiscoveryTrials.get(i) +
                        "\n\t\ttotal branches = " + totalBranches.get(i) +
                        "\n\t\tdistribution = " + branchesHit.get(i)
                );
            }
        }
        float avgBranchesSize = (float) totalBranchesSize / totalBranches.size();
        summarized_results.append("\n\tAverage:" +
                "\n\t\t# discovered branches: " + avgBranchesSize);
        for (int i = 0; i < discoveriesCountAtTrial.size(); i++) {
            summarized_results.append("\n\t\tRun " + (i + 1) + ": " + discoveriesCountAtTrial.get(i));
        }

        System.out.println();
        if(PRINT_TO_CONSOLE)
            System.out.println(summarized_results);
    }


    private static StringBuilder dataPerIterationListToLog(ArrayList<ArrayList<String>> lists) {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < lists.size(); i++) {
            res.append("\n\tRun " + (i + 1) + ": [");
            for (int j = 0; j < lists.get(i).size(); j++) {
                if (j != 0) {
                    res.append(", ");
                }
                res.append("\"" + lists.get(i).get(j) + "\"");
            }
            res.append("]");
        }
        return res;
    }


    private static StringBuilder printUniqueFailuresWithMutations(BigFuzzPlusGuidance guidance) {
        StringBuilder sb = new StringBuilder();
        Iterator<List<StackTraceElement>> uFailuresIterator = guidance.uniqueFailures.iterator();
        int counter = 1;
        while(uFailuresIterator.hasNext()) {
            List<StackTraceElement> e = uFailuresIterator.next();
            sb.append("\n\nUNIQUE FAILURE #" + counter);
            sb.append("\n-- failure triggered at trial " + Math.toIntExact(guidance.uniqueFailuresWithTrial.get(e)) + " --");
            StringBuilder headerRow = new StringBuilder("\n#\t\t");
            StringBuilder classRow = new StringBuilder("\nFile\t");
            StringBuilder methodRow = new StringBuilder("\nMethod\t");
            StringBuilder lineRow = new StringBuilder("\nLine\t");
            for (int i = 0; i < e.size(); i++) {
                // Usually the filename and method name are much longer than the line number. Use this amount to create tabs
                int maxLengthColumn = Math.max(Objects.requireNonNull(e.get(i).getFileName()).length(), e.get(i).getMethodName().length());
                headerRow.append(i).append(getAmountOfSpaces(maxLengthColumn, i + ""));
                classRow.append(e.get(i).getFileName()).append(getAmountOfSpaces(maxLengthColumn, Objects.requireNonNull(e.get(i).getFileName())));
                methodRow.append(e.get(i).getMethodName()).append(getAmountOfSpaces(maxLengthColumn, e.get(i).getMethodName()));
                lineRow.append(e.get(i).getLineNumber()).append(getAmountOfSpaces(maxLengthColumn, e.get(i).getLineNumber() + ""));
            }
            sb.append(headerRow).append(classRow).append(methodRow).append(lineRow);
            counter ++;

            sb.append("\nMutation(s) triggering the error: ");
            if (guidance.mutation instanceof StackedMutation) {
                int atIteration = Math.toIntExact(guidance.uniqueFailuresWithTrial.get(e));
                // If the unique failure is recorded at the first trial, there is no mutation applied
                if(atIteration == 0) {
                    sb.append("\nUnique failure occurred on input seed");
                }
                else if(atIteration >= guidance.mutationsPerRun.size() ) {
                    sb.append("\nMutation has not been recorded, something went wrong.");
                } else {
                    ArrayList<MutationPair> mutationPerformedAtTrial = guidance.mutationsPerRun.get(atIteration);
                    sb.append(generateMutationLog(mutationPerformedAtTrial));
                }
            }
        }
        return sb;
    }

    private static StringBuilder generateMutationLog(ArrayList<MutationPair> mutationPerformedAtTrial) {
        StringBuilder sb = new StringBuilder();
        sb.append( " \n\t # \t column \t mutation");
        for (int j = 0; j < mutationPerformedAtTrial.size(); j++) {
            // Add one to the column nr to make it not 0 indexed
            int columnNr = mutationPerformedAtTrial.get(j).getElementId() + 1;
            String mutation = String.valueOf(mutationPerformedAtTrial.get(j).getMutation());
            sb.append("\n\tM_" +j + ": " + columnNr + "\t\t - \t" + mutation);
        }
        return sb;
    }

    private static String getAmountOfSpaces(int maxLengthColumn, String s) {
        StringBuilder res = new StringBuilder("\t");
        int diff = maxLengthColumn - s.length();
        for (int i = 0; i < Math.ceil(diff/4.0); i++) {
            res.append("\t");
        }
        return res.toString();
    }

    public void addDuration(long l) {
        durations.add(l);
    }


    /**
     * Write collected log in the variables log, summarized results and iteration results to a file in the output folder named log.txt.
     *
     * @param outputDir Directory where the log file should be written to
     */
    public void writeLogToFile(File outputDir) {
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
     * Prints the configuration and the results from the run to the Terminal.
     *
     * @param maxTrials      maximal amount of trials configuration
     * @param guidance       guidance class which is used to perform the BigFuzz testing
     * @param atIteration    Counter indicating for which iteration this evaluation is.
     */
    public void evaluation(Long maxTrials, BigFuzzPlusGuidance guidance, int atIteration) {
        StringBuilder e_log = new StringBuilder();
        // Print configuration
        e_log.append("*** ITERATION " + atIteration + " LOG");

        // Print results
        e_log.append("\nTotal run count: " + guidance.numTrials);
        e_log.append("\n\tTotal Failures: " + guidance.totalFailures);
        e_log.append("\n\tTotal Valid: " + guidance.numValid);
        e_log.append("\n\tTotal Invalid: " + guidance.numDiscards);
        if(!LOG_UNIQUE_FAILURES_PER_TRIAL) {
            e_log.append("\nUnique failure log disabled");
        } else {
            e_log.append("\n\tUnique Failures: " + guidance.uniqueFailures.size());
            e_log.append("\n\tUnique Failures found at: " + guidance.uniqueFailureRuns);
            int cumulative = 0;
            List<Integer> runFoundUniqueFailureCumulative = new ArrayList<>();
            for (long i = 0; i < maxTrials; i++) {
                if (guidance.uniqueFailureRuns.contains(i))
                    cumulative++;
                runFoundUniqueFailureCumulative.add(cumulative);
            }
            e_log.append("\n\tUnique Failures found per run: " + runFoundUniqueFailureCumulative);
        }

        if(LOG_UNIQUE_FAILURE_AND_MUTATION)
            e_log.append(printUniqueFailuresWithMutations(guidance));

        if(PRINT_TO_CONSOLE)
            System.out.println(e_log);

        iteration_results.append(e_log);
    }
}
