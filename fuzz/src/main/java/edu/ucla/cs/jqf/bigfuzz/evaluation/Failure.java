package edu.ucla.cs.jqf.bigfuzz.evaluation;

public class Failure {
    private final String exceptionName;
    private final String className;
    private final int lineNumber;
    private int trialNumber;

    public Failure(String exceptionName, String className, int lineNumber, int trialNumber) {
        this.exceptionName = exceptionName;
        this.className = className;
        this.lineNumber = lineNumber;
        this.trialNumber = trialNumber;
    }

    public String getExceptionName() {
        return exceptionName;
    }

    public String getClassName() {
        return className;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public int getTrialNumber() {
        return trialNumber;
    }

    public String toString() {
        String str = "Exception: " + this.exceptionName + " (";
        str += this.className + " " + this.lineNumber + "), trial: " + this.trialNumber;
        return str;
    }

    public boolean equals(Object o) {
        if (o instanceof Failure) {
            Failure that = (Failure) o;
            if (this.exceptionName.equals(that.exceptionName)) {
                if (this.className.equals(that.className)) {
                    return this.lineNumber == that.lineNumber;
                }
            }
        }
        return false;
    }

    public int hashCode() {
        return (this.exceptionName + this.className + this.lineNumber).hashCode();
    }
}
