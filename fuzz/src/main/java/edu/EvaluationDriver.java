package edu;

import edu.tud.cs.jqf.bigfuzzplus.BigFuzzPlusDriver;
import edu.tud.cs.jqf.bigfuzzplus.BigFuzzPlusLog;

public class EvaluationDriver {

    public static void main (String[] args) {
        String testClass = "PropertyDriver";
        String testMethod = "testProperty";
        String mutationMethod = "StackedMutation";
        String numTrials = "5000";
        String stackedmethod = "3";
        String[] maxStack = {"5","7","10"};
        String[] args2 = {testClass,testMethod,mutationMethod,numTrials,stackedmethod,"1"};
        for (int i = 0; i < 3; i++) {
            args2[5] = maxStack[i];
            BigFuzzPlusDriver.main(args2);
            BigFuzzPlusLog.resetInstance();
        }
    }
}
