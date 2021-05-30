package edu.ucla.cs.jqf.bigfuzz;

import edu.berkeley.cs.jqf.fuzz.ei.ZestGuidance;
import edu.berkeley.cs.jqf.fuzz.guidance.Guidance;
import edu.berkeley.cs.jqf.fuzz.guidance.GuidanceException;
import edu.berkeley.cs.jqf.fuzz.guidance.Result;
import edu.berkeley.cs.jqf.fuzz.util.Coverage;
import edu.berkeley.cs.jqf.instrument.tracing.events.TraceEvent;
import org.apache.commons.io.FileUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.time.Duration;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static edu.ucla.cs.jqf.bigfuzz.BigFuzzDriver.LOG_AND_PRINT_STATS;
import static edu.ucla.cs.jqf.bigfuzz.BigFuzzDriver.PRINT_COVERAGE_DETAILS;
import static edu.ucla.cs.jqf.bigfuzz.BigFuzzDriver.PRINT_METHOD_NAMES;
import static edu.ucla.cs.jqf.bigfuzz.BigFuzzDriver.PRINT_MUTATION_DETAILS;

/**
 * A guidance that performs coverage-guided fuzzing using JDU (Joint Dataflow and UDF)
 * Mutations: 1. randomly mutation
 * code coverage guidance: control flow coverage, dataflow operator's coverage
 */
@SuppressWarnings({"rawtypes", "Duplicates"})
public class BigFuzzGuidance implements Guidance {

    /** The name of the test for display purposes. */
    protected final String testName;

    private boolean keepGoing = true;
    private Coverage coverage;

    /** Time since this guidance instance was created. */
    protected final Date startTime = new Date();

    /** Time at last stats refresh. */
    protected Date lastRefreshTime = startTime;

    /** Total execs at last stats refresh. */
    protected long lastNumTrials = 0;

    /** Minimum amount of time (in millis) between two stats refreshes. */
    protected static final long STATS_REFRESH_TIME_PERIOD = 300;

    /** The max amount of time to run for, in milli-seconds */
    protected final long maxDurationMillis;

    /** The number of trials completed. */
    protected long numTrials = 0;

    /** The number of valid inputs. */
    protected long numValid = 0;

    /** The directory where fuzzing results are written. */
    protected final File outputDirectory;

    /** A percentage indicating how often the less frequent branches preferring method is applied over
     * the baseline input selection method */
    private final double favorRate;

    /** The directory where saved inputs are written. */
    protected File coverageInputsDirectory;

    /** The directory where saved inputs are written. */
    protected File uniqueFailuresDirectory;

    /** The directory in which only the initial inputs are saved. */
    protected File initialInputsDirectory;

    /** The directory where all inputs are written. */
    protected File allInputsDirectory;

    /** The directory where all mutations are written. */
    protected File allInterestingInputsDirectory;

    /** Set of saved inputs to fuzz. */
    protected ArrayList<ZestGuidance.Input> savedInputs = new ArrayList<>();

    /** Queue of seeds to fuzz. */
    protected Deque<ZestGuidance.Input> seedInputs = new ArrayDeque<>();

    /** Index of currentInput in the savedInputs -- valid after seeds are processed (OK if this is inaccurate). */
    protected int currentParentInputIdx = 0;

    /** Number of mutated inputs generated from currentInput. */
    protected int numChildrenGeneratedForCurrentParentInput = 0;

    /** Number of cycles completed (i.e. how many times we've reset currentParentInputIdx to 0. */
    protected int cyclesCompleted = 0;

    /** Files that still have to be used as inputs before this cycle ends. */
    protected List<File> pendingInputs = new ArrayList<>();

    /** Number of favored inputs in the last cycle. */
    protected int numFavoredLastCycle = 0;

    /** Blind fuzzing -- if true then the queue is always empty. */
    protected boolean blind;

    /** Number of saved inputs.
     *
     * This is usually the same as savedInputs.size(),
     * but we do not really save inputs in TOTALLY_RANDOM mode.
     */
    protected int numSavedInputs = 0;

    private final long maxTrials;
    private long numDiscards = 0;

    /** Validity fuzzing -- if true then save valid inputs that increase valid coverage */
    protected boolean validityFuzzing;

    /** Coverage statistics for a single run. */
    protected Coverage runCoverage = new Coverage();

    /** Cumulative coverage statistics. */
    protected Coverage totalCoverage = new Coverage();

    /** Cumulative coverage for valid inputs. */
    protected Coverage validCoverage = new Coverage();

    /** Map which tracks the amount of times each known branch is covered */
    protected Map<Set<Integer>, Integer> branchesHitCount = new HashMap<>();

    /** Map which is used to point to the initial File that discovered the combination of branches. */
    protected Map<Set<Integer>, File> coverageFilePointer = new HashMap<>();

    /** The maximum number of keys covered by any single input found so far. */
    protected int maxCoverage = 0;

    /** The list of total failures found so far. */
    protected int totalFailures = 0;

    /** The set of unique failures found so far. */
    protected Set<List<StackTraceElement>> uniqueFailures = new HashSet<>();

    /** List of runs which have at which new unique failures have been detected. */
    protected List<Long> uniqueFailureRuns = new ArrayList<>();

    /** List of runs at which new coverage has been found. */
    protected List<Integer> newCoverageRuns = new ArrayList<>();

    // ---------- LOGGING / STATS OUTPUT ------------

    /** Whether to print log statements to stderr (debug option; manually edit). */
    protected final boolean verbose = true;

    /** The file where log data is written. */
    protected File logFile;

    /** The file where saved plot data is written. */
    protected File statsFile;

    // ------------- TIMEOUT HANDLING ------------

    /** Date when last run was started. */
    protected Date runStart;

    // ------------- FUZZING HEURISTICS ------------

    /** Baseline number of mutated children to produce from a given parent input. */
    static final int NUM_CHILDREN_BASELINE = 50;

    /** Multiplication factor for number of children to produce for favored inputs. */
    static final int NUM_CHILDREN_MULTIPLIER_FAVORED = 20;

    protected final File initialInputFile;
    BigFuzzMutation mutation = new IncomeAggregationMutation();
    private File currentInputFile;
    private File lastWorkingInputFile;


    public BigFuzzGuidance(String testName, String initialInputFileName, long maxTrials, Duration duration, File outputDirectory, double favorRate) throws IOException {

        this.testName = testName;
        this.maxDurationMillis = duration != null ? duration.toMillis() : Long.MAX_VALUE;
        this.outputDirectory = outputDirectory;
        this.favorRate = favorRate;
        if (maxTrials <= 0) {
            throw new IllegalArgumentException("maxTrials must be greater than 0");
        }
        File initialFile = new File(initialInputFileName);
        this.initialInputFile = initialFile;
        this.currentInputFile = initialFile;
        this.lastWorkingInputFile = initialFile;
        this.maxTrials = maxTrials;

        prepareOutputDirectory();
    }

    private void prepareOutputDirectory() throws IOException {
        // Create the output directory if it does not exist
        if (!outputDirectory.exists()) {
            if (!outputDirectory.mkdirs()) {
                throw new IOException("Could not create output directory" +
                        outputDirectory.getAbsolutePath());
            }
        }

        // Make sure we can write to output directory
        if (!outputDirectory.isDirectory() || !outputDirectory.canWrite()) {
            throw new IOException("Output directory is not a writable directory: " +
                    outputDirectory.getAbsolutePath());
        }

        // Create and name files and directories after AFL
        this.coverageInputsDirectory = new File(outputDirectory, "new_coverage_inputs");
        if (!this.coverageInputsDirectory.mkdirs()) {
            System.out.println("!! Could not create directory: " + coverageInputsDirectory);
        }
        this.uniqueFailuresDirectory = new File(outputDirectory, "unique_failures");
        if (!this.uniqueFailuresDirectory.mkdirs()) {
            System.out.println("!! Could not create directory: " + uniqueFailuresDirectory);
        }
        this.initialInputsDirectory = new File(outputDirectory, "init_inputs");
        if (!this.initialInputsDirectory.mkdirs()) {
            System.out.println("!! Could not create directory: " + initialInputsDirectory);
        }
        this.allInputsDirectory = new File(outputDirectory, "all_inputs");
        if (!this.allInputsDirectory.mkdirs()) {
            System.out.println("!! Could not create directory: " + allInputsDirectory);
        }
        this.allInterestingInputsDirectory = new File(outputDirectory, "interesting_inputs");
        if (!this.allInterestingInputsDirectory.mkdirs()) {
            System.out.println("!! Could not create directory: " + allInterestingInputsDirectory);
        }

        if (LOG_AND_PRINT_STATS) {
            this.statsFile = new File(outputDirectory, "plot_data");
            if (!this.statsFile.createNewFile()) {
                System.out.println("!! Could not create file: " + statsFile);
            }
        }
        this.logFile = new File(outputDirectory, "fuzz.log");
        if (!this.logFile.createNewFile()) {
            System.out.println("!! Could not create file: " + logFile);
        }

        if (LOG_AND_PRINT_STATS) {
            appendLineToFile(statsFile, "# unix_time, cycles_done, cur_path, paths_total, pending_total, " +
                    "pending_favs, map_size, unique_crashes, unique_hangs, max_depth, execs_per_sec, valid_inputs, " +
                    "invalid_inputs, valid_cov");
        }
    }
    
    @SuppressWarnings("SuspiciousMethodCalls")
    @Override
    public InputStream getInput() throws IOException {
        System.out.println("--Current trial: " + numTrials);
        // Clear coverage stats for this run
        runCoverage.clear();

        if (numTrials == 0) { // Copy initial input files if no input exists yet.
            // Handle initially declared inputs
            int countInitFiles = 0;
            Scanner sc = new Scanner(initialInputFile);
            while (sc.hasNextLine()) {
                File nextInitInput = new File(sc.nextLine());
                String initFileName = "init_" + countInitFiles;
                File initInput = new File(initialInputsDirectory, initFileName);
                File nextAllInput = new File(allInputsDirectory, initFileName);
                FileUtils.copyFile(nextInitInput, initInput);
                FileUtils.copyFile(nextInitInput, nextAllInput);
                countInitFiles++;
            }
            sc.close();

            pendingInputs.addAll(Arrays.asList(Objects.requireNonNull(allInputsDirectory.listFiles())));
        }

        // Select initial inputs first and don't mutate them.
        int initLength = Objects.requireNonNull(initialInputsDirectory.listFiles()).length;
        if (numTrials < initLength) {
            currentInputFile = pendingInputs.remove(0);
            return new ByteArrayInputStream(currentInputFile.getPath().getBytes());
        }

        // Start next cycle and refill pendingInputs if cycle is completed.
        if (pendingInputs.isEmpty()) {
            cyclesCompleted++;
            pendingInputs.addAll(Arrays.asList(Objects.requireNonNull(coverageInputsDirectory.listFiles())));
        }

        // Determine which input selection method to use.
        double r = new Random().nextDouble();
        if (r > favorRate) { // Use baseline input selection method
            currentInputFile = pendingInputs.remove(0);
        }
        else { // Use favored input selection method
            int totalBranchCombinationCount = 0;
            for (int i : branchesHitCount.values()) {
                totalBranchCombinationCount += i;
            }

            // Calculate chances in which the least explored branches are preferred
            Map<Collection<Integer>, Double> chancesAfterPref = new HashMap<>();
            for (Set<Integer> branchCombi : branchesHitCount.keySet()) {
                int occurrences = branchesHitCount.get(branchCombi);
                double chance = (double) 1 / occurrences * totalBranchCombinationCount;
                chancesAfterPref.put(branchCombi, chance);
            }

            double totalChance = 0;
            for (double d : chancesAfterPref.values()) {
                totalChance += d;
            }

            // Randomly select an input file using the preferred chances
            double selectedChance = new Random().nextDouble() * totalChance;
            double totalCheckedDoubles = 0;
            for (Map.Entry<Collection<Integer>, Double> entry : chancesAfterPref.entrySet()) {
                totalCheckedDoubles += entry.getValue();
                if (totalCheckedDoubles >= selectedChance) {
                    currentInputFile = coverageFilePointer.get(entry.getKey());
                    break;
                }
            }
        }

        // Mutate the next file from pendingInputs
        String mutationFileName = "mutation_" + numTrials;
        File nextInputFile = new File(allInputsDirectory, mutationFileName);
        File mutationFile = new File(allInterestingInputsDirectory, mutationFileName);
        mutation.mutate(currentInputFile, mutationFile);
        FileUtils.copyFile(mutationFile, nextInputFile);

        // Move reference file to correct directory
        currentInputFile = nextInputFile;

        if (PRINT_METHOD_NAMES) { System.out.println("BigFuzzGuidance::getInput: "+numTrials+": "+currentInputFile ); }

        return new ByteArrayInputStream(currentInputFile.getPath().getBytes());
    }

    /** Writes a line of text to a given log file. */
    protected void appendLineToFile(File file, String line) throws GuidanceException {
        try (PrintWriter out = new PrintWriter(new FileWriter(file, true))) {
            out.println(line);
        } catch (IOException e) {
            throw new GuidanceException(e);
        }
    }

    /** Writes a line of text to the log file. */
    protected void infoLog(String str, Object... args) {
        if (verbose) {
            String line = String.format(str, args);
            if (logFile != null) {
                appendLineToFile(logFile, line);

            } else {
                if (PRINT_MUTATION_DETAILS) {
                    System.err.println(line);
                }
            }
        }
    }


    @Override
    public boolean hasInput() {
        return keepGoing;
    }

    @Override
    public void handleResult(Result result, Throwable error) {
        // Stop timeout handling
        this.runStart = null;

        if (PRINT_METHOD_NAMES) { System.out.println("BigFuzz::handleResult"); }
//        System.out.println("result: " + result);

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
        Date now = new Date();
        long elapsedMillis = now.getTime() - startTime.getTime();
        if (numTrials >= maxTrials - 1
                || elapsedMillis >= this.maxDurationMillis) {
            this.keepGoing = false;
        }

        float maxDiscardRatio = 0.9f;
        if (numTrials > 10 && ((float) numDiscards)/((float) (numTrials)) > maxDiscardRatio) {
            throw new GuidanceException("Assumption is too strong; too many inputs discarded");
        }

        if (result == Result.SUCCESS || result == Result.INVALID) {

            // Coverage before
            int nonZeroBefore = totalCoverage.getNonZeroCount();
            int validNonZeroBefore = validCoverage.getNonZeroCount();

            // Compute a list of keys for which this input can assume responsibility.
            // Newly covered branches are always included.
            // Existing branches *may* be included, depending on the heuristics used.
            // A valid input will steal responsibility from invalid inputs
            Set<Integer> responsibilities = computeResponsibilities();
            if (PRINT_COVERAGE_DETAILS && responsibilities.size() > 0) {
                System.out.println("New responsibilities found: " + responsibilities);
            }

            // Update total coverage
            totalCoverage.updateBits(runCoverage);
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

            // Save if new combination of branches is found
            if (nonZeroAfter > nonZeroBefore) {
                // Must be responsible for some branch
                assert(responsibilities.size() > 0);
                toSave = true;
                why += "+cov";
            }

            // Save if new valid coverage is found
            if (this.validityFuzzing && validNonZeroAfter > validNonZeroBefore) {
                // Must be responsible for some branch
                assert(responsibilities.size() > 0);
                toSave = true;
                why += "+valid";
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

                // Change current input file name
                File src = currentInputFile;
                String branchesFileName = "branches_" + numTrials;
                File des = new File(coverageInputsDirectory, branchesFileName);
                File des2 = new File(allInterestingInputsDirectory, branchesFileName);
                // save the file if it increased coverage
                if (why.contains("+cov")) {
                    if (!des.exists()) {
                        try {
                            if (PRINT_COVERAGE_DETAILS) { System.out.println(des + " created for " + responsibilities); }
                            FileUtils.copyFile(src, des);
                            FileUtils.copyFile(src, des2);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    newCoverageRuns.add((int) numTrials);
                    lastWorkingInputFile = src;
                    coverageFilePointer.put(responsibilities, des);
                }
            }
            else {
                try {
                    mutation.deleteFile(currentInputFile.getPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                currentInputFile = lastWorkingInputFile;
            }
        }else if (result == Result.FAILURE || result == Result.TIMEOUT) {
            String msg = error.getMessage();

            //get the root cause
            Throwable rootCause = error;
            while (rootCause.getCause() != null) {
                rootCause = rootCause.getCause();
            }
            this.totalFailures++;

            //   Attempt to add this to the set of unique failures
            if (uniqueFailures.add(Arrays.asList(rootCause.getStackTrace()))) {
                uniqueFailureRuns.add(numTrials);

                infoLog("%s", "Found crash: " + error.getClass() + " - " + (msg != null ? msg : ""));

//                String how = currentInput.desc;
                String why = result == Result.FAILURE ? "+crash" : "+hang";
//                infoLog("Saved - %s %s %s", saveFile.getPath(), how, why);
                if (PRINT_MUTATION_DETAILS) {
                    System.out.println("Unique failure found: " + why + "\n\t" + rootCause);
                }

                File src = currentInputFile;
                File srcInteresting = new File(allInterestingInputsDirectory, src.getName());
                String failureFileName = "failure_" + numTrials;
                File des = new File(uniqueFailuresDirectory, failureFileName);
                File srcRename = new File(allInterestingInputsDirectory, failureFileName);
                // save the file if it increased coverage
                if (why.contains("+crash")) {
                    try {
                        FileUtils.copyFile(src, des);
                        if (!srcInteresting.renameTo(srcRename)) {
                            System.out.println("!! Could not rename file " + srcInteresting + " to " + srcRename);
                        }
                        lastWorkingInputFile = src;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            else {
                try {
                    mutation.deleteFile(currentInputFile.getPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                currentInputFile = lastWorkingInputFile;
            }
        }

        if (LOG_AND_PRINT_STATS) {
            this.displayStats();
        }

        this.numTrials++;
    }

    private void displayStats() {
        PrintStream console = System.out;

        Date now = new Date();
        long intervalMilliseconds = now.getTime() - lastRefreshTime.getTime();
        if (intervalMilliseconds < STATS_REFRESH_TIME_PERIOD) {
            return;
        }
        long intervalTrials = numTrials - lastNumTrials;
        long intervalExecsPerSec = intervalTrials * 1000L / intervalMilliseconds;
        double intervalExecsPerSecDouble = intervalTrials * 1000.0 / intervalMilliseconds;
        lastRefreshTime = now;
        lastNumTrials = numTrials;
        long elapsedMilliseconds = now.getTime() - startTime.getTime();
        long execsPerSec = numTrials * 1000L / elapsedMilliseconds;

        String currentParentInputDesc;
        if (seedInputs.size() > 0 || savedInputs.isEmpty()) {
            currentParentInputDesc = "<seed>";
        } else {
            ZestGuidance.Input currentParentInput = savedInputs.get(currentParentInputIdx);
            currentParentInputDesc = currentParentInputIdx + " ";
            currentParentInputDesc += currentParentInput.isFavored() ? "(favored)" : "(not favored)";
            currentParentInputDesc += " {" + numChildrenGeneratedForCurrentParentInput +
                    "/" + getTargetChildrenForParent(currentParentInput) + " mutations}";
        }

        int nonZeroCount = totalCoverage.getNonZeroCount();
        double nonZeroFraction = nonZeroCount * 100.0 / totalCoverage.size();
        int nonZeroValidCount = validCoverage.getNonZeroCount();
        double nonZeroValidFraction = nonZeroValidCount * 100.0 / validCoverage.size();

        console.print("\033[2J");
        console.print("\033[H");
        console.println(this.getTitle() + ":");
        if (this.testName != null) {
            console.printf("\tTest name:            %s\n", this.testName);
        }
        console.printf("\tResults directory:    %s\n", this.outputDirectory.getAbsolutePath());
        console.printf("\tElapsed time:         %s (%s)\n", millisToDuration(elapsedMilliseconds),
                maxDurationMillis == Long.MAX_VALUE ? "no time limit" : ("max " + millisToDuration(maxDurationMillis)));
        console.printf("\tNumber of executions: %,d\n", numTrials);
        console.printf("\tValid inputs:         %,d (%.2f%%)\n", numValid, numValid*100.0/numTrials);
        console.printf("\tCycles completed:     %d\n", cyclesCompleted);
        console.printf("\tUnique failures:      %,d\n", uniqueFailures.size());
        console.printf("\tQueue size:           %,d (%,d favored last cycle)\n", savedInputs.size(), numFavoredLastCycle);
        console.printf("\tCurrent parent input: %s\n", currentParentInputDesc);
        console.printf("\tExecution speed:      %,d/sec now | %,d/sec overall\n", intervalExecsPerSec, execsPerSec);
        console.printf("\tTotal coverage:       %,d branches (%.2f%% of map)\n", nonZeroCount, nonZeroFraction);
        console.printf("\tValid coverage:       %,d branches (%.2f%% of map)\n", nonZeroValidCount, nonZeroValidFraction);
        console.println();

        String plotData = String.format("%d, %d, %d, %d, %d, %d, %.2f%%, %d, %d, %d, %.2f, %d, %d, %.2f%%",
                TimeUnit.MILLISECONDS.toSeconds(now.getTime()), cyclesCompleted, currentParentInputIdx,
                numSavedInputs, 0, 0, nonZeroFraction, uniqueFailures.size(), 0, 0, intervalExecsPerSecDouble,
                numValid, numTrials-numValid, nonZeroValidFraction);
        appendLineToFile(statsFile, plotData);

    }

    private int getTargetChildrenForParent(ZestGuidance.Input parentInput) {
        // Baseline is a constant
        int target = NUM_CHILDREN_BASELINE;

        // We like inputs that cover many things, so scale with fraction of max
        if (maxCoverage > 0) {
            target = (NUM_CHILDREN_BASELINE * parentInput.nonZeroCoverage) / maxCoverage;
        }

        // We absolutely love favored inputs, so fuzz them more
        if (parentInput.isFavored()) {
            target = target * NUM_CHILDREN_MULTIPLIER_FAVORED;
        }

        return target;
    }

    /** Returns the banner to be displayed on the status screen */
    private String getTitle() {
        if (blind) {
            return  "Generator-based random fuzzing (no guidance)";
        } else {
            return  "BigFuzz: Efficient fuzz testing for data analytics using framework abstraction";
        }
    }

    private String millisToDuration(long millis) {
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis % TimeUnit.MINUTES.toMillis(1));
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis % TimeUnit.HOURS.toMillis(1));
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        String result = "";
        if (hours > 0) {
            result = hours + "h ";
        }
        if (hours > 0 || minutes > 0) {
            result += minutes + "m ";
        }
        result += seconds + "s";
        return result;
    }

    // Compute a set of branches for which the current input may assume responsibility
    private Set<Integer> computeResponsibilities() {

        // add hit branches to counter
        Collection<Integer> nonZeroIndices = runCoverage.getCounter().getNonZeroIndices();
        Set<Integer> hitBranches = new HashSet<>(nonZeroIndices);
        int hits = branchesHitCount.getOrDefault(hitBranches, 0);
        branchesHitCount.put(hitBranches, hits + 1);
        if (PRINT_COVERAGE_DETAILS) { System.out.println("branches hit: " + hitBranches); }

        return hitBranches;
    }

    @Override
    public Consumer<TraceEvent> generateCallBack(Thread thread) {
        return this::handleEvent;
    }

    /** Handles a trace event generated during test execution */
    protected void handleEvent(TraceEvent e) {
        runCoverage.handleEvent(e);
    }

    /**
     * Returns a reference to the coverage statistics.
     * @return a reference to the coverage statistics
     */
    public Coverage getCoverage() {
        if (coverage == null) {
            coverage = new Coverage();
        }
        return coverage;
    }
}
