package edu.tabfuzz;

import edu.berkeley.cs.jqf.fuzz.guidance.Guidance;
import edu.berkeley.cs.jqf.fuzz.guidance.GuidanceException;
import edu.berkeley.cs.jqf.fuzz.guidance.Result;
import edu.berkeley.cs.jqf.instrument.tracing.events.TraceEvent;

import java.io.*;
import java.time.Duration;
import java.util.*;
import java.util.function.Consumer;

public class TabFuzzGuidance implements Guidance {
    private boolean keepGoing = true;
    protected final String testName;
    protected final long maxDurationMillis;
    private final PrintStream out;

    /** Input file info */
    protected final String initialInputFile;
    private String currentInputFile;

    /** Trials information */
    private long numTrials = 0;
    private final long maxTrials;


    Mutation mutation = new Mutation();
    ArrayList<String> testInputFiles = new ArrayList<String>();

    public TabFuzzGuidance(String testName, String initialInputFile, long maxTrials, Duration duration, PrintStream out) throws IOException {

        this.testName = testName;
        this.maxDurationMillis = duration != null ? duration.toMillis() : Long.MAX_VALUE;
        //this.outputDirectory = outputDirectory;

        if (maxTrials <= 0) {
            throw new IllegalArgumentException("maxTrials must be greater than 0");
        }
        this.initialInputFile = initialInputFile;
        this.currentInputFile = initialInputFile;
        this.maxTrials = maxTrials;
        this.out = out;
    }

    @Override
    public InputStream getInput() throws IllegalStateException, GuidanceException, IOException {
        if (!testInputFiles.isEmpty()) {
            // Mutate here
        }
        testInputFiles.add(currentInputFile);

        return new ByteArrayInputStream(currentInputFile.getBytes());
    }

    @Override
    public boolean hasInput() {
        return keepGoing;
    }

    @Override
    public void handleResult(Result result, Throwable error) throws GuidanceException {
        this.numTrials++;
        System.out.println("Current trial: " + numTrials);
        // Stopping criteria
        if (numTrials >= maxTrials) {
            this.keepGoing = false;
        }
    }

    @Override
    public Consumer<TraceEvent> generateCallBack(Thread thread) {
        return null;
    }

}
