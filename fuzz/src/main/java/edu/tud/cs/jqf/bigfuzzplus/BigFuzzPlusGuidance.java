package edu.tud.cs.jqf.bigfuzzplus;

import edu.berkeley.cs.jqf.fuzz.ei.ZestGuidance;
import edu.berkeley.cs.jqf.fuzz.guidance.Guidance;
import edu.berkeley.cs.jqf.fuzz.guidance.GuidanceException;
import edu.berkeley.cs.jqf.fuzz.guidance.Result;
import edu.berkeley.cs.jqf.fuzz.util.Coverage;
import edu.berkeley.cs.jqf.instrument.tracing.events.TraceEvent;
import edu.tud.cs.jqf.bigfuzzplus.bigfuzzmutations.*;
import edu.tud.cs.jqf.bigfuzzplus.jsonMutation.JsonPlusMutation;
import edu.tud.cs.jqf.bigfuzzplus.stackedMutation.MutationPair;
import edu.tud.cs.jqf.bigfuzzplus.stackedMutation.StackedMutation;
import edu.tud.cs.jqf.bigfuzzplus.systematicMutation.SystematicMutation;
import org.apache.commons.io.FileUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static edu.tud.cs.jqf.bigfuzzplus.BigFuzzPlusDriver.LOG_AND_PRINT_STATS;
import static edu.tud.cs.jqf.bigfuzzplus.BigFuzzPlusDriver.PRINT_COVERAGE_DETAILS;
import static edu.tud.cs.jqf.bigfuzzplus.BigFuzzPlusDriver.PRINT_INPUT_SELECTION_DETAILS;
import static edu.tud.cs.jqf.bigfuzzplus.BigFuzzPlusDriver.PRINT_LINE_FOR_EACH_TRIAL;
import static edu.tud.cs.jqf.bigfuzzplus.BigFuzzPlusDriver.PRINT_METHOD_NAMES;
import static edu.tud.cs.jqf.bigfuzzplus.BigFuzzPlusDriver.PRINT_MUTATION_DETAILS;
import static edu.tud.cs.jqf.bigfuzzplus.BigFuzzPlusDriver.PRINT_TEST_RESULTS;
import static edu.tud.cs.jqf.bigfuzzplus.BigFuzzPlusDriver.SAVE_UNIQUE_FAILURES;

/**
 * A guidance that performs coverage-guided fuzzing using JDU (Joint Dataflow and UDF)
 * Mutations: 1. randomly mutation
 * code coverage guidance: control flow coverage, dataflow operator's coverage
 */
@SuppressWarnings({"rawtypes", "Duplicates"})
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

	/**Time since this guidance instance was created. */
    protected final Date startTime = new Date();
	 /** Time at last stats refresh. */
	 protected Date lastRefreshTime = startTime;

	/**Total execs at last stats refresh. */
    protected long lastNumTrials = 0;

    /** Minimum amount of time (in millis) between two stats refreshes. */
    protected static final long STATS_REFRESH_TIME_PERIOD = 100000;
	 /** The max amount of time to run for, in milli-seconds
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
	protected long numDiscards = 0;
    /** The directory where fuzzing results are written. */
    protected final File outputDirectory;

    /** The input selection method used. */
    private final SelectionMethod selection;

    /** The directory where saved inputs are written. */
    protected File coverageInputsDirectory;

    /** The directory where saved inputs are written. */
    protected File uniqueFailuresDirectory;

    /** The directory in which only the initial inputs are saved. */
    protected File initialInputsDirectory;

    /** The directory where all inputs are written. */
    protected File allInputsDirectory;

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

	/**Number of saved inputs.
     *
     * This is usually the same as savedInputs.size(),
     * but we do not really save inputs in TOTALLY_RANDOM mode.
     */
    protected int numSavedInputs = 0;

	 /** Validity fuzzing -- if true then save valid inputs that increase valid coverage
	 */
	protected boolean validityFuzzing;

    /** Coverage statistics for a single run. */
    protected Coverage runCoverage = new Coverage();

    /** Cumulative coverage statistics. */
    protected Coverage totalCoverage = new Coverage();

    /** Cumulative coverage for valid inputs. */
    protected Coverage validCoverage = new Coverage();

    /** Map which tracks the amount of times each known branch is covered */
    protected Map<Set<Integer>, Integer> branchesHitCount = new HashMap<>();

    /** List in which trial numbers are saved which discovered new Coverage. */
    public ArrayList<Long> newDiscoveryTrials = new ArrayList<>();

    /** Map which is used to point to the initial File that discovered the combination of branches. */
    protected Map<Set<Integer>, File> coverageFilePointer = new HashMap<>();

    /** The maximum number of keys covered by any single input found so far. */
    protected int maxCoverage = 0;

	/**
	 * The list of total failures found so far.
	 */
	protected int totalFailures = 0;

    /** The set of unique failures found so far. */
    protected Set<List<StackTraceElement>> uniqueFailures = new HashSet<>();
    protected HashMap<ArrayList<StackTraceElement>, Long> uniqueFailuresWithTrial = new HashMap<>();

    /** List of runs which have at which new unique failures have been detected. */
    protected List<Long> uniqueFailureRuns = new ArrayList<>();
    protected ArrayList<ArrayList<MutationPair>> mutationsPerRun = new ArrayList<>();
    protected ArrayList<String> inputs = new ArrayList<>();

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
    BigFuzzPlusMutation mutation;
    private File currentInputFile;
    private File lastWorkingInputFile;
    private final Random r = new Random();

    public BigFuzzPlusGuidance(String testName, String initialInputFileName, long maxTrials, Duration duration, File outputDirectory, String mutationMethodClassName, SelectionMethod selection) throws IOException {
        this.testName = testName;
        this.maxDurationMillis = duration != null ? duration.toMillis() : Long.MAX_VALUE;

        this.selection = selection;
        if (maxTrials <= 0) {
            throw new IllegalArgumentException("maxTrials must be greater than 0");
        }
        File initialFile = new File(initialInputFileName);
        this.initialInputFile = initialFile;
        this.currentInputFile = initialFile;
        this.lastWorkingInputFile = initialFile;
        this.maxTrials = maxTrials;

        this.outputDirectory = outputDirectory;
        prepareOutputDirectory();

        setMutation(mutationMethodClassName);
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
        if (SAVE_UNIQUE_FAILURES) {
            this.uniqueFailuresDirectory = new File(outputDirectory, "unique_failures");
            if (!this.uniqueFailuresDirectory.mkdirs()) {
                System.out.println("!! Could not create directory: " + uniqueFailuresDirectory);
            }
        }
        this.initialInputsDirectory = new File(outputDirectory, "init_inputs");
        if (!this.initialInputsDirectory.mkdirs()) {
            System.out.println("!! Could not create directory: " + initialInputsDirectory);
        }
        this.allInputsDirectory = new File(outputDirectory, "all_inputs");
        if (!this.allInputsDirectory.mkdirs()) {
            System.out.println("!! Could not create directory: " + allInputsDirectory);
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

    /**
     * Set the mutation class to the passed mutationMethodClassName. If the class name is not implemented in this function the program will terminate
     * @param mutationMethodClassName String of mutation method class name.
     */
    private void setMutation(String mutationMethodClassName) {
        switch (mutationMethodClassName) {
            case "StackedMutation":
                mutation = new StackedMutation();
                break;
            case "SystematicMutation":
	        case "random":
                mutation = new SystematicMutation(initialInputFile);
                break;
            case "IncomeAggregationMutation":
                mutation = new IncomeAggregationPlusMutation();
                break;
            case "AgeAnalysisMutation":
                mutation = new AgeAnalysisPlusMutation();
                break;
            case "CommuteTypeMutation":
                mutation = new CommuteTypePlusMutation();
                break;
            case "ExternalUDFMutation":
                mutation = new ExternalUDFPlusMutation();
                break;
            case "FindSalaryMutation":
                mutation = new FindSalaryPlusMutation();
                break;
            case "MovieRatingMutation":
                mutation = new MovieRatingPlusMutation();
                break;
            case "NumberSeriesMutation":
                mutation = new NumberSeriesPlusMutation();
                break;
            case "OneDFMutation":
                mutation = new OneDFPlusMutation();
                break;
            case "PropertyInvestmentMutation":
                mutation = new PropertyInvestmentPlusMutation();
                break;
            case "StudentGradeMutation":
                mutation = new StudentGradePlusMutation();
                break;
            case "WordCountMutation":
                mutation = new WordCountPlusMutation();
                break;
            case "JsonMutation":
                mutation = new JsonPlusMutation();
                break;
            default:
                System.err.println("could not match the provided mutation class to an existing class. Provided mutation class: " + mutationMethodClassName);
                System.exit(0);
                break;
        }
    }

    @SuppressWarnings({"SuspiciousMethodCalls"})
    @Override
    public InputStream getInput() throws IOException {
        //progress bar
        String completedTrialsString = "\rCompleted trials: " + numTrials * 100 / Math.max(1, maxTrials) + "% (" + numTrials + "/" + maxTrials + ")";
        if (PRINT_LINE_FOR_EACH_TRIAL) {
            System.out.println("\n" + completedTrialsString);
        }
        else {
            if (!SystematicMutation.EVALUATE && numTrials % Math.max(1, maxTrials / 20) == 0) {
                System.out.print(completedTrialsString);
            }
        }

        // Clear coverage stats for this run
        runCoverage.clear();

        // Select initial inputs first and don't mutate them.
        if (numTrials == 0) {
            File initCopy = new File(initialInputsDirectory, initialInputFile.getName());
            FileUtils.copyFile(initialInputFile, initCopy);

            if (PRINT_INPUT_SELECTION_DETAILS) { System.out.println("[SELECT] selected config input: " + initialInputFile.getName());}
            return new ByteArrayInputStream(initialInputFile.getPath().getBytes());
        }

        // Start next cycle and refill pendingInputs if cycle is completed.
        if (pendingInputs.isEmpty()) {
            cyclesCompleted++;
            if (selection != SelectionMethod.BLACK_BOX) {
                File[] covFiles = coverageInputsDirectory.listFiles();
                if (Objects.requireNonNull(covFiles).length != 0) {
                    pendingInputs.addAll(Arrays.asList(covFiles));
                } else {
                    pendingInputs.add(initialInputFile);
                }
            }
            else {
                pendingInputs.add(initialInputFile);
            }
        }
        if (pendingInputs.isEmpty()) {
            System.out.println("Can't find any input files. Are you sure that you provided one in " + initialInputFile.getPath() + "?");
            System.exit(0);
        }

        // Determine which input selection method to use.
        boolean useFavoredSelection;
        if (selection == SelectionMethod.FULLY_BOOSTED_GREY_BOX) {
            useFavoredSelection = true;
        } else if (selection == SelectionMethod.HALF_BOOSTED_GREY_BOX) {
            useFavoredSelection = r.nextDouble() <= 0.5;
        } else {
            useFavoredSelection = false;
        }

        if (PRINT_INPUT_SELECTION_DETAILS) {
            String method = useFavoredSelection ? "favored" : "baseline";
            System.out.println("[SELECT] Selection method used: " + method);
        }

        if (!useFavoredSelection) {
            // Use baseline input selection method
            currentInputFile = pendingInputs.remove(0);
        }
        else { // Use favored input selection method
            // Calculate chances in which the least explored branches are preferred
            Map<Collection<Integer>, Double> chancesAfterPref = new HashMap<>();
            for (Set<Integer> branchCombi : branchesHitCount.keySet()) {
                int occurrences = branchesHitCount.get(branchCombi);
                double chance = (double) 1 / occurrences;
                chancesAfterPref.put(branchCombi, chance);
            }

            // Select random number under total chance.
            double totalChance = 0;
            for (double d : chancesAfterPref.values()) {
                totalChance += d;
            }
            double selectedChance = r.nextDouble() * totalChance;

            // Select file based on random number from above.
            double totalCheckedDoubles = 0;
            for (Map.Entry<Collection<Integer>, Double> entry : chancesAfterPref.entrySet()) {
                totalCheckedDoubles += entry.getValue();
                if (totalCheckedDoubles >= selectedChance) {
                    currentInputFile = coverageFilePointer.get(entry.getKey());
                    break;
                }
            }

            if (PRINT_INPUT_SELECTION_DETAILS) {
                List<Entry<String, Integer>> fileHits = new ArrayList<>();
                List<Entry<String, Double>> fileChance = new ArrayList<>();
                List<Entry<String, Double>> fileChanceBoundaries = new ArrayList<>();
                double before = 0;
                for (Map.Entry<Collection<Integer>, Double> entry : chancesAfterPref.entrySet()) {
                    before += entry.getValue();
                    String fileName = coverageFilePointer.get(entry.getKey()).getName();
                    fileHits.add(new Entry<>(fileName, branchesHitCount.get(entry.getKey())));
                    fileChance.add(new Entry<>(fileName, entry.getValue()));
                    fileChanceBoundaries.add(new Entry<>(fileName, before));
                }

                System.out.println("[SELECT] known branch hits: " + fileHits);
                System.out.println("[SELECT] favored chances: " + fileChance);
                System.out.println("[SELECT] favored chance boundaries: " + fileChanceBoundaries);
                System.out.println("[SELECT] selected random number: " + selectedChance);
            }
        }

        // Mutate the next file from pendingInputs
        String mutationFileName = "mutation_" + numTrials;
        File nextInputFile = new File(allInputsDirectory, mutationFileName);
        File mutationFile = new File(allInputsDirectory, mutationFileName);
        if (PRINT_INPUT_SELECTION_DETAILS) { System.out.println("[SELECT] selected mutate input: " + currentInputFile.getName()); }
        mutation.mutate(currentInputFile, mutationFile);

        // Move reference file to correct directory
        currentInputFile = nextInputFile;

        if (mutation instanceof StackedMutation) {
            mutationsPerRun.add(((StackedMutation)mutation).getAppliedMutations());
        }

        if (PRINT_METHOD_NAMES) { System.out.println("[METHOD] BigFuzzGuidance::getInput"); }

        saveInput();

        File refFile = new File(currentInputFile + "_ref");
        return new ByteArrayInputStream(refFile.getPath().getBytes());
    }

    /**
     * Save input that is in currentInputFile.
     */
    private void saveInput() {
        StringBuilder contentBuilder = new StringBuilder();
        try {
            for (String line : Files.readAllLines(currentInputFile.toPath())) {
                if (line != null) {
                    contentBuilder.append(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        inputs.add(contentBuilder.toString());
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
                if (PRINT_MUTATION_DETAILS) { System.err.println("[MUTATE] " + line); }
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

        if (PRINT_METHOD_NAMES) { System.out.println("[METHOD] BigFuzz::handleResult"); }
        if (PRINT_TEST_RESULTS) { System.out.println("[RESULT] " + result); }

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
		long currentMillis = System.currentTimeMillis() - startTime.getTime();
		if (numTrials >= maxTrials
				|| currentMillis >= this.maxDurationMillis) {
			this.keepGoing = false;
		}

        float maxDiscardRatio = 0.9f;
        if (numTrials > 10 && ((float) numDiscards)/((float) (numTrials)) > maxDiscardRatio) {
            throw new GuidanceException("Assumption is too strong; too many inputs discarded");
        }

        if (result == Result.SUCCESS || result == Result.INVALID) {

            // Coverage before
            int validNonZeroBefore = validCoverage.getNonZeroCount();

            // Compute a list of keys for which this input can assume responsibility.
            // Newly covered branches are always included.
            // Existing branches *may* be included, depending on the heuristics used.
            // A valid input will steal responsibility from invalid inputs
            Set<Integer> responsibilities = computeResponsibilities();

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
            if (branchesHitCount.get(responsibilities) == 1) { // responsibilities is only found once (this run)
                assert(responsibilities.size() > 0); // Must be responsible for some branch
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
                infoLog("Saving new input (at run %d): total coverage = %d",
                        numTrials, nonZeroAfter);

                // Change current input file name
                boolean isInitFile = initialInputFile.equals(currentInputFile);
                File src = new File(currentInputFile + (isInitFile ? "" : "_ref"));
                File des = new File(coverageInputsDirectory, src.getName());
                // save the file if it increased coverage
                if (why.contains("+cov")) {
                    if (!des.exists()) {
                        try {
                            if (PRINT_COVERAGE_DETAILS) { System.out.println("[COV] " + des.getName() + " created for " + responsibilities); }
                            FileUtils.copyFile(src, des);
                            newDiscoveryTrials.add(numTrials);
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

            // Only check the Stack trace elements until the program driver that is being tested
            ArrayList<StackTraceElement> testProgramTraceElements = new ArrayList<>();
            boolean testClassFound = false;
            for (int i = 0; i < rootCause.getStackTrace().length; i++) {
                // If the test class has been found in the stacktrace, but this element is no longer said test class then the stacktrace will only contain the test framework, not the test program.
                if (testClassFound && !rootCause.getStackTrace()[i].getClassName().equals(testClassName)) {
                    break;
                }

                testProgramTraceElements.add(rootCause.getStackTrace()[i]);

                // Check the correct element of the stacktrace if it originated from the test class.
                if (rootCause.getStackTrace()[i].getClassName().equals(testClassName)) {
                    testClassFound = true;
                }
            }

            //   Attempt to add this to the set of unique failures
            if(!uniqueFailuresWithTrial.containsKey(testProgramTraceElements)) {
                uniqueFailures.add(testProgramTraceElements);
                uniqueFailuresWithTrial.put(testProgramTraceElements, numTrials);
                uniqueFailureRuns.add(numTrials);

                infoLog("%s", "Found crash: " + error.getClass() + " - " + (msg != null ? msg : ""));

                String why = result == Result.FAILURE ? "+crash" : "+hang";
                if (PRINT_MUTATION_DETAILS) { System.out.println("[MUTATE] Unique failure found: " + why + "\n\t" + rootCause); }

                boolean isInitFile = initialInputFile.equals(currentInputFile);
                File src = new File(currentInputFile + (isInitFile ? "" : "_ref"));
                File des = new File(uniqueFailuresDirectory, src.getName());
                // save the file if it increased coverage
                if (why.contains("+crash")) {
                    if (SAVE_UNIQUE_FAILURES) {
                        try {
                            FileUtils.copyFile(src, des);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            currentInputFile = lastWorkingInputFile;
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
        console.println("[STATS] " + this.getTitle() + ":");
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
        if (PRINT_COVERAGE_DETAILS) {
            System.out.println("[COV] branches hit: " + hitBranches);
            System.out.println("[COV] branches known: " + branchesHitCount.keySet());
            File equalCovFile = coverageFilePointer.get(hitBranches);
            if (equalCovFile != null) {
                System.out.println("[COV] equal branches discovered as " + equalCovFile.getName());
            }
        }
        branchesHitCount.put(hitBranches, hits + 1);

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
        r.setSeed(seed);
    }
}
