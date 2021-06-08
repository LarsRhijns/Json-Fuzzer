package edu;

import edu.tud.cs.jqf.bigfuzzplus.BigFuzzPlusDriver;
import edu.tud.cs.jqf.bigfuzzplus.BigFuzzPlusLog;

public class EvaluationDriver {

    public static void main (String[] args) {
        String testClass = "ExternalUDFDriver";
        String testMethod = "testExternalUDF";
        String mutationMethod = "StackedMutation";
        String numTrials = "5000";
        String[] stackedmethod = { "1","3","4"};
        String maxStack = "5";
        String[] args2 = {testClass,testMethod,mutationMethod,numTrials,"0",maxStack};
        for (int i = 0; i < 3; i++) {
            args2[4] = stackedmethod[i];
            BigFuzzPlusDriver.main(args2);
            BigFuzzPlusLog.resetInstance();
        }
    }
}
