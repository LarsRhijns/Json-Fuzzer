package edu.tud.cs.jqf.bigfuzzplus;

//import edu.berkeley.cs.jqf.fuzz.junit.GuidedFuzzing;

import edu.berkeley.cs.jqf.fuzz.junit.GuidedFuzzing;
import edu.tud.cs.jqf.bigfuzzplus.stackedMutation.HighOrderMutation;
import edu.tud.cs.jqf.bigfuzzplus.stackedMutation.MutationPair;
import edu.tud.cs.jqf.bigfuzzplus.stackedMutation.StackedMutation;
import edu.tud.cs.jqf.bigfuzzplus.stackedMutation.StackedMutationEnum;

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

    private static BigFuzzPlusLog log = BigFuzzPlusLog.getInstance();

    /**
     * Run the BigFuzzPlus program with the following parameters:
     * [0] - test class
     * [1] - test method
     * [2] - mutation method
     * [3] - max Trials                (default = Long.MAXVALUE)
     * [4] - stacked mutation method   (default = 0)
     *          0 = Disabled
     *          1 = Permute_random (permute between 1 and the max amount of mutations)
     *          2 = Permute_max (Always permute until the max amount of mutations)
     *          3 = Smart_stack (Apply highorder mutation exclusion rules)
     *          4 = Single mutate (Only apply 1 mutation per column)
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

        int intStackedMutationMethod = args.length > 4 ? Integer.parseInt(args[4]) : 0;
        StackedMutationEnum.StackedMutationMethod stackedMutationMethod = StackedMutationEnum.intToStackedMutationMethod(intStackedMutationMethod);

        // This variable is used for the stackedMutationMethod: Smart_mutate
        // If the selected stackedMutationMethod is smart_mutate and this argument is not given, default is set to 2. If smart_mutate is not selected, set to 0
        int intMutationStackCount = args.length > 5 ? Integer.parseInt(args[5]) : stackedMutationMethod == StackedMutationEnum.StackedMutationMethod.Smart_stack ? 2 : 0;

        // **************

        long programStartTime = System.currentTimeMillis();
        File outputDir = new File("output/" + programStartTime);
        boolean newOutputDirCreated = outputDir.mkdir();
        if (!newOutputDirCreated) {
            System.err.println("Something went wrong with making the output directory for this run");
            System.exit(0);
        }

        log.logProgramArguments(testClassName,testMethodName,mutationMethodClassName,stackedMutationMethod,intMutationStackCount,outputDir,programStartTime);
        log.printProgramArguments();

        for (int i = 0; i < 5; i++) {
            int atIteration = i + 1;
            System.out.println("******** START OF PROGRAM ITERATION: " + atIteration + "**********************");

            String file = "dataset/conf";
            try {
                long iterationStartTime = System.currentTimeMillis();

                Duration maxDuration = Duration.of(5, ChronoUnit.MINUTES);
                //NoGuidance guidance = new NoGuidance(file, maxTrials, System.err);
                String iterationOutputDir = outputDir + "/Test" + atIteration;
                BigFuzzPlusGuidance guidance = new BigFuzzPlusGuidance("Test" + atIteration, file, maxTrials, iterationStartTime, maxDuration, System.err, iterationOutputDir, mutationMethodClassName);

                // Set the provided input argument stackedMutationMethod in the guidance mutation
                if(guidance.mutation instanceof StackedMutation) {
                    ((StackedMutation)guidance.mutation).setStackedMutationMethod(stackedMutationMethod);
                    ((StackedMutation)guidance.mutation).setMutationStackCount(intMutationStackCount);
                }
                
                // Set the randomization seed to the program start time. Seed is passed to allow for custom seeds, independent of the program start time
                guidance.setRandomizationSeed(iterationStartTime);

                // Set the test class name in the guidance for the failure tracking
                guidance.setTestClassName(testClassName);

                // Run the Junit test
                GuidedFuzzing.run(testClassName, testMethodName, guidance, System.out);
                long endTime = System.currentTimeMillis();

                // Evaluate the results
                log.evaluation(testClassName, testMethodName, file, maxTrials, maxDuration, iterationStartTime, endTime, guidance, atIteration);
                log.writeToLists(guidance, maxTrials);
                log.addDuration(endTime - iterationStartTime);
                System.out.println("************************* END OF PROGRAM ITERATION ************************");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        log.summarizeProgramIterations();
        log.writeLogToFile(outputDir);
    }
}
