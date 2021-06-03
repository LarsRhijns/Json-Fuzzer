package edu;

import edu.tud.cs.jqf.bigfuzzplus.BigFuzzPlusDriver;

public class EvaluationDriver {

    public static void main (String[] args) {
        String testClass = "SalaryAnalysisDriver";
        String testMethod = "testSalaryAnalysis";
        String mutationMethod = "StackedMutation";
        String numTrials = "5000";
        String stackedmethod = "3";
        String maxStack = "3";
        String[] args2 = {testClass,testMethod,mutationMethod,numTrials,stackedmethod,maxStack};
        BigFuzzPlusDriver.main(args2);




    }
}
