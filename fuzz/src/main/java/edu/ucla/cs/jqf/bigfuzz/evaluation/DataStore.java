package edu.ucla.cs.jqf.bigfuzz.evaluation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class DataStore {
    private ArrayList<Failure> uniqueFailures = new ArrayList<>();
    private String outputFolder = null;
    private ArrayList<DataPoint> points = new ArrayList<>();

    public DataStore(String outputFolder) {
        this.outputFolder = outputFolder;
    }

    public ArrayList<Failure> getUniqueFailures() {
        return uniqueFailures;
    }

    public void setUniqueFailures(ArrayList<Failure> uniqueFailures) {
        this.uniqueFailures = uniqueFailures;
    }

    public void printUniqueFailures() {
        ArrayList<Failure> uniques = getUniques();
        System.out.println("******************Unique Failures******************");
        System.out.println("Amount of unique failures found: " + uniques.size());


        for (Failure fail : uniques) {
            System.out.println("\t" + fail.toString());
        }
    }

    public void storeUniqueFailures(long folder) throws IOException {
        File outputDir = new File("output/" + folder + "/failures");
        if (outputDir.mkdir()) {
            BufferedWriter out = new BufferedWriter(new FileWriter("output/" + folder + "/failures/uniqueFailures.txt"));
            for (Failure uniqueFailure : this.uniqueFailures) {
                out.write(uniqueFailure.toString());
                out.newLine();
            }
            out.close();
        }
    }

    private ArrayList<Failure> getUniques() {
        Set<Failure> set = new HashSet<>(uniqueFailures);
        return new ArrayList<>(set);
    }

    public void addFailure(Throwable error, int trialNumber) {
        String err = error.toString();
        if (error.getStackTrace().length <= 0) {
            // Weird bug happens sometimes with an empty exception, skip for now.
            return;
        }
        String trace = error.getStackTrace()[0].getClassName();
        int line = error.getStackTrace()[0].getLineNumber();
        Failure fail = new Failure(err, trace, line, trialNumber);
        if (!uniqueFailures.contains(fail)) {
            uniqueFailures.add(fail);
            addDataPoint(trialNumber);
        }
    }

    private void addDataPoint(int trialNumber) {
        int cumError = getUniques().size();
        points.add(new DataPoint(trialNumber, cumError));
    }

    public void storeDataPoints (long folder) throws IOException {
        File outputDir = new File("output/" + folder + "/datapoints");
        if (outputDir.mkdir()) {
            BufferedWriter out = new BufferedWriter(new FileWriter("output/" + folder + "/datapoints/points.txt"));
            for (DataPoint point : this.points) {
                out.write(point.toString());
                out.newLine();
            }
            out.close();
        }
    }
}
