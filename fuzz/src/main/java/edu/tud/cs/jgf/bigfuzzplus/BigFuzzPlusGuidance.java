package edu.tud.cs.jgf.bigfuzzplus;

import edu.berkeley.cs.jqf.fuzz.guidance.Guidance;
import edu.berkeley.cs.jqf.fuzz.guidance.GuidanceException;
import edu.berkeley.cs.jqf.fuzz.guidance.Result;
import edu.berkeley.cs.jqf.fuzz.util.Coverage;
import edu.berkeley.cs.jqf.instrument.tracing.events.TraceEvent;
import edu.tud.cs.jgf.bigfuzzplus.stackedMutation.MutationPair;
import edu.tud.cs.jgf.bigfuzzplus.stackedMutation.StackedMutation;
import edu.tud.cs.jgf.bigfuzzplus.stackedMutation.StackedMutationEnum;
import edu.ucla.cs.jqf.bigfuzz.BigFuzzMutation;
import edu.ucla.cs.jqf.bigfuzz.IncomeAggregationMutation;
import edu.ucla.cs.jqf.bigfuzz.mutationclasses.*;
import org.apache.commons.io.FileUtils;

import java.io.*;

import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;
import java.util.function.Consumer;

import static edu.tud.cs.jgf.bigfuzzplus.BigFuzzPlusDriver.*;

/**
 * A guidance that performs coverage-guided fuzzing using JDU (Joint Dataflow and UDF)
 * code coverage guidance: control flow coverage, dataflow operators's coverage
 */
public class BigFuzzPlusGuidance implements Guidance {

    /**
     * The name of the test for display purposes.
     */
    public final String testName;

    /**
     * testClassName for error tracking purposes
     */
    private String testClassName;

    private boolean keepGoing = true;
    private Coverage coverage;

    /**
     * Time at which the driver started running.
     */
    private final long startTime;

    /**
     * The max amount of time to run for, in milli-seconds
     */
    protected final long maxDurationMillis;

    /**
     * The number of trials completed.
     */
    protected long numTrials = 0;

    /**
     * The number of valid inputs.
     */
    protected long numValid = 0;

    protected final long maxTrials;
    private final PrintStream out;
    protected long numDiscards = 0;
    // Ratio is used to terminate the program if the ratio of invalid inputs reaches the discard ratio
    private final float maxDiscardRatio = 0.9f;

    /**
     * Validity fuzzing -- if true then save valid inputs that increase valid coverage
     */
    protected boolean validityFuzzing;

    /**
     * Coverage statistics for a single run.
     */
    protected Coverage runCoverage = new Coverage();

    /**
     * Cumulative coverage statistics.
     */
    protected Coverage totalCoverage = new Coverage();

    /**
     * Cumulative coverage for valid inputs.
     */
    protected Coverage validCoverage = new Coverage();

    /**
     * The maximum number of keys covered by any single input found so far.
     */
    protected int maxCoverage = 0;

    /**
     * The list of total failures found so far.
     */
    protected int totalFailures = 0;

    /**
     * The set of unique failures found so far.
     */
    protected Set<List<StackTraceElement>> uniqueFailures = new HashSet<>();

    /**
     * List of runs which have at which new unique failures have been detected.
     */
    protected List<Long> uniqueFailureRuns = new ArrayList<>();
    protected ArrayList<ArrayList<MutationPair>> mutationsPerRun = new ArrayList<>();
    protected ArrayList<String> inputs = new ArrayList();

    // ---------- LOGGING / STATS OUTPUT ------------

    /**
     * Whether to print log statements to stderr (debug option; manually edit).
     */
    protected final boolean verbose = true;


    /**
     * The file where log data is written.
     */
    protected File logFile;

    /**
     * OutputDirectory where files should be written to.
     */
    private final String outputDirName;


    // ------------- TIMEOUT HANDLING ------------

    /**
     * Date when last run was started.
     */
    protected Date runStart;

    protected final String initialInputFile;
    BigFuzzMutation mutation;
    private String currentInputFile;

    ArrayList<String> testInputFiles = new ArrayList();


    public BigFuzzPlusGuidance(String testName, String initialInputFile, long maxTrials, long startTime, Duration duration, PrintStream out, String outputDirName, String mutationMethodClassName) throws IOException {
        this.testName = testName;
        this.startTime = startTime;
        this.maxDurationMillis = duration != null ? duration.toMillis() : Long.MAX_VALUE;

        // create or empty the output directory
        this.outputDirName = outputDirName;
        File outputDir = new File(outputDirName);
        boolean newOutputDirCreated = outputDir.mkdir();

        // Clear the output directory if it already existed to prevent write errors or scrambled results.
        if (!newOutputDirCreated) {
            FileUtils.cleanDirectory(FileUtils.getFile(outputDirName));
        }

        // Max trials needs to be larger than 0, otherwise testing will be stopped instantly
        if (maxTrials <= 0) {
            throw new IllegalArgumentException("maxTrials must be greater than 0");
        }
        this.initialInputFile = initialInputFile;
        this.currentInputFile = initialInputFile;
        this.maxTrials = maxTrials;
        this.out = out;

        setMutation(mutationMethodClassName);
    }

    /**
     * Set the mutation class to the passed mutationMethodClassName. If the class name is not implemented in this function the program will terminate
     * @param mutationMethodClassName String of mutation method class name.
     */
    private void setMutation(String mutationMethodClassName) {
        switch (mutationMethodClassName) {
            case "StackedMutation":
                mutation = new StackedMutation();
                break;
            case "IncomeAggregationMutation":
                mutation = new IncomeAggregationMutation();
                break;
            case "AgeAnalysisMutation":
                mutation = new AgeAnalysisMutation();
                break;
            case "CommuteTypeMutation":
                mutation = new CommuteTypeMutation();
                break;
            case "ExternalUDFMutation":
                mutation = new ExternalUDFMutation();
                break;
            case "FindSalaryMutation":
                mutation = new FindSalaryMutation();
                break;
            case "MovieRatingMutation":
                mutation = new MovieRatingMutation();
                break;
            case "NumberSeriesMutation":
                mutation = new NumberSeriesMutation();
                break;
            case "OneDFMutation":
                mutation = new OneDFMutation();
                break;
            case "PropertyInvestmentMutation":
                mutation = new PropertyInvestmentMutation();
                break;
            case "StudentGradeMutation":
                mutation = new StudentGradeMutation();
                break;
            case "WordCountMutation":
                mutation = new WordCountMutation();
                break;
            default:
                System.err.println("could not match the provided mutation class to an existing class. Provided mutation class: " + mutationMethodClassName);
                System.exit(0);
                break;
        }
    }

    /**
     * Copy file.
     * @param source File source that should be copied
     * @param dest File destination where the copy should be placed in
     * @throws IOException Throws IO exception of copy fails.
     */
    private static void copyFileUsingFileChannels(File source, File dest) throws IOException {
        try (FileChannel inputChannel = new FileInputStream(source).getChannel(); FileChannel outputChannel = new FileOutputStream(dest).getChannel()) {
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        }
    }

    @Override
    public InputStream getInput() {
        // Clear coverage stats for this run
        runCoverage = new Coverage();

        ///copy the configuration/input file
        if (testInputFiles.isEmpty()) {
            // if test input files has not been filled yet, copy the current input file
            String fileName = currentInputFile.substring(currentInputFile.lastIndexOf('/') + 1);
            File src = new File(currentInputFile);
            File dst = new File(fileName);
            try {
                copyFileUsingFileChannels(src, dst);
            } catch (IOException e) {
                e.printStackTrace();
            }
            currentInputFile = fileName;
        } else {
            try {
                // Use the current date and number of trials to create a new file name. This file is used mutate
                String nextInputFile = new SimpleDateFormat("yyyyMMddHHmmss'_" + this.numTrials + "'").format(new Date());
                nextInputFile = this.outputDirName + "/" + nextInputFile;
                mutation.mutate(initialInputFile, nextInputFile);//currentInputFile
                currentInputFile = nextInputFile;

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        testInputFiles.add(currentInputFile);

        if(mutation instanceof StackedMutation) {
            mutationsPerRun.add(((StackedMutation)mutation).getAppliedMutations());
        }

        if (PRINT_METHOD_NAMES) {
            System.out.println("BigFuzzGuidance::getInput: " + numTrials + ": " + currentInputFile);
        }
        InputStream targetStream = new ByteArrayInputStream(currentInputFile.getBytes());//currentInputFile.getBytes()
        saveInput();
        return targetStream;
    }

    /**
     * Save input that is in currentInputFile.
     */
    private void saveInput() {
        String inputFileName = loadInputLocation(currentInputFile);
        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(inputFileName))) {

            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                contentBuilder.append(sCurrentLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        inputs.add(contentBuilder.toString());
    }

    /**
     * Load the contents of the file which points to the location of the input for the program
     * @param inputLocationPointerFileName File which contains the location of the input
     * @return File location of the input
     */
    private String loadInputLocation(String inputLocationPointerFileName) {
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(inputLocationPointerFileName)))) {
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    /**
     * Writes a line of text to a given log file.
     */
    protected void appendLineToFile(File file, String line) throws GuidanceException {

        try (PrintWriter out = new PrintWriter(new FileWriter(file, true))) {
            out.println(line);
        } catch (IOException e) {
            //System.out.println("appendLineToFile:throw: "+e.getMessage());
            throw new GuidanceException(e);
        } finally {
            out.close();
        }

    }

    /**
     * Writes a line of text to the log file.
     */
    protected void infoLog(String str, Object... args) {
        if (verbose && PRINT_MUTATION_DETAILS) {
            String line = String.format(str, args);
            if (logFile != null) {
                appendLineToFile(logFile, line);

            } else {
                System.err.println(line);
            }
        }
    }


    @Override
    public boolean hasInput() {
        return keepGoing;
    }

    @Override
    public void handleResult(Result result, Throwable error) {
        System.out.print("\r Trial " + numTrials + " / " + maxTrials);
        // Stop timeout handling
        this.runStart = null;

        if (PRINT_METHOD_NAMES) {
            System.out.println("BigFuzz::handleResult");
        }
        if (PRINT_TEST_RESULTS) {
            System.out.println(result);
        }

        this.numTrials++;

        boolean valid = result == Result.SUCCESS;

        if (valid) {
            // Increment valid counter
            this.numValid++;
        }

        // Keep track of discards
        if (result == Result.INVALID) {
            numDiscards++;
        }

        // Stopping criteria
        long currentMillis = System.currentTimeMillis() - startTime;
        if (numTrials >= maxTrials
                || currentMillis >= this.maxDurationMillis) {
            this.keepGoing = false;
        }

        if (numTrials > 10 && ((float) numDiscards) / ((float) (numTrials)) > maxDiscardRatio) {
            throw new GuidanceException("Assumption is too strong; too many inputs discarded");
        }

        if (result == Result.SUCCESS || result == Result.INVALID) {

            // Coverage before
            int nonZeroBefore = totalCoverage.getNonZeroCount();
            int validNonZeroBefore = validCoverage.getNonZeroCount();

            // Compute a list of keys for which this input can assume responsiblity.
            // Newly covered branches are always included.
            // Existing branches *may* be included, depending on the heuristics used.
            // A valid input will steal responsibility from invalid inputs
            Set<Object> responsibilities = computeResponsibilities(valid);
            //System.out.println("Responsibilities of this input: "+responsibilities);

            // Update total coverage
            boolean coverageBitsUpdated = totalCoverage.updateBits(runCoverage);
            if (valid) {
                validCoverage.updateBits(runCoverage);
            }

            // Coverage after
            int nonZeroAfter = totalCoverage.getNonZeroCount();
            if (nonZeroAfter > maxCoverage) {
                maxCoverage = nonZeroAfter;
            }
            int validNonZeroAfter = validCoverage.getNonZeroCount();

            // Possibly save input
            boolean toSave = false;
            String why = "";

            // Save if new total coverage found
            if (nonZeroAfter > nonZeroBefore) {
                // Must be responsible for some branch
                assert (responsibilities.size() > 0);
                toSave = true;
                why = why + "+cov";
            }

            // Save if new valid coverage is found
            if (this.validityFuzzing && validNonZeroAfter > validNonZeroBefore) {
                // Must be responsible for some branch
                assert (responsibilities.size() > 0);
                toSave = true;
                why = why + "+valid";
            }

            if (toSave) {
                infoLog("Saving new input (at run %d): " +
//                                "input #%d " +
//                                "of size %d; " +
                                "total coverage = %d",
                        numTrials,
//                        savedInputs.size(),
//                        currentInput.size(),
                        nonZeroAfter);

                // Change current inputfile name
                File src = new File(currentInputFile);
                currentInputFile += why;
                File des = new File(currentInputFile);
                src.renameTo(des);
            } else {
                try {
                    mutation.deleteFile(currentInputFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                File src2 = new File(currentInputFile);
                src2.delete();
            }
        } else if (result == Result.FAILURE || result == Result.TIMEOUT) {

            String msg = error.getMessage();

            //get the root cause
            Throwable rootCause = error;
            while (rootCause.getCause() != null) {
                rootCause = rootCause.getCause();
            }
            this.totalFailures++;


            // Only check the Stack trace elements until the program driver that is being tested
            ArrayList<StackTraceElement> testProgramTraceElements = new ArrayList<>();
            boolean testClassFound = false;
            for (int i = 0; i < rootCause.getStackTrace().length; i++) {
                // If the test class has been found in the stacktrace, but this element is no longer said test class then the stacktrace will only contain the test framework, not the test program.
                if (testClassFound && !rootCause.getStackTrace()[i].getClassName().equals(testClassName)) {
                    break;
                }

                testProgramTraceElements.add(rootCause.getStackTrace()[i]);

                // Check the currect element of the stacktrace if it originated from the test class.
                if (rootCause.getStackTrace()[i].getClassName().equals(testClassName)) {
                    testClassFound = true;
                }
            }

            if (uniqueFailures.add(testProgramTraceElements)) {
                int crashIdx = uniqueFailures.size() - 1;
                uniqueFailureRuns.add(numTrials);

                infoLog("%s", "Found crash: " + error.getClass() + " - " + (msg != null ? msg : ""));

                String why = result == Result.FAILURE ? "+crash" : "+hang";

                File src = new File(currentInputFile);
                currentInputFile = currentInputFile + why + "+" + crashIdx + "+" + rootCause;
                File des = new File(currentInputFile);
                src.renameTo(des);
            } else {
                try {
                    mutation.deleteFile(currentInputFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                File src2 = new File(currentInputFile);
                src2.delete();
            }
        }
        runCoverage = new Coverage();
    }

    // Compute a set of branches for which the current input may assume responsibility
    private Set<Object> computeResponsibilities(boolean valid) {
        Set<Object> result = new HashSet<>();

        // This input is responsible for all new coverage
        Collection<?> newCoverage = runCoverage.computeNewCoverage(totalCoverage);
        if (newCoverage.size() > 0) {
            result.addAll(newCoverage);
        }

        // If valid, this input is responsible for all new valid coverage
        if (valid) {
            Collection<?> newValidCoverage = runCoverage.computeNewCoverage(validCoverage);
            if (newValidCoverage.size() > 0) {
                result.addAll(newValidCoverage);
            }
        }

        return result;
    }

    @Override
    public Consumer<TraceEvent> generateCallBack(Thread thread) {
        return this::handleEvent;
    }

    /**
     * Handles a trace event generated during test execution
     */
    protected void handleEvent(TraceEvent e) {
        runCoverage.handleEvent(e);
    }


    /**
     * Returns a reference to the coverage statistics.
     *
     * @return a reference to the coverage statistics
     */
    public Coverage getCoverage() {
        if (coverage == null) {
            coverage = new Coverage();
        }
        return coverage;
    }

    /**
     * Field setter for the mutation class.
     *
     * @param stackedMutationMethod stacked mutation method the guidance should follow.
     */
    public void setStackedMutationMethod(StackedMutationEnum.StackedMutationMethod stackedMutationMethod) {
        mutation.setStackedMutationMethod(stackedMutationMethod);
    }

    /**
     * Field setter for the mutation class. Is only applied if the mutation class is MutationTemplate
     *
     * @param intMutationStackCount The max amount of mutations that should be applied to the input seed.
     */

    public void setMutationStackCount(int intMutationStackCount) {
        if (mutation instanceof StackedMutation) {
            ((StackedMutation) mutation).setMutationStackCount(intMutationStackCount);
        }
    }

    /**
     * Field setter
     *
     * @param testClassName Driver class name of the class that is tested.
     */
    public void setTestClassName(String testClassName) {
        this.testClassName = testClassName;
    }

    /**
     * Set the randomization seed of the mutation class. Only implemented for MutationTemplate
     *
     * @param seed seed that needs to be assigned to the Random object
     */
    public void setRandomizationSeed(long seed) {
        if (mutation instanceof StackedMutation) {
            ((StackedMutation) mutation).setSeed(seed);
        }
    }
}
