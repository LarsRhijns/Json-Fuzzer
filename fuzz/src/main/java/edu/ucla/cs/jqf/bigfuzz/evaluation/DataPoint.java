package edu.ucla.cs.jqf.bigfuzz.evaluation;

public class DataPoint {
    private int trialNumber;
    private int cumErrors;

    public DataPoint(int trialNumber, int cumErrors) {
        this.trialNumber = trialNumber;
        this.cumErrors = cumErrors;
    }

    public boolean equals(Object o) {
        if (o instanceof DataPoint) {
            DataPoint that = (DataPoint) o;
            return (this.cumErrors == that.cumErrors) && (this.trialNumber == that.trialNumber);
        }
        return false;
    }

    public int getTrialNumber() {
        return trialNumber;
    }

    public int getCumErrors() {
        return cumErrors;
    }

    public String toString() {
        return trialNumber + "," + cumErrors;
    }
}
