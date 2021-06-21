package edu;

import edu.berkeley.cs.jqf.fuzz.guidance.GuidanceException;
import edu.tud.cs.jqf.bigfuzzplus.BigFuzzPlusDriver;
import edu.tud.cs.jqf.bigfuzzplus.BigFuzzPlusLog;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class EvaluationDriver {

    public static void main(String[] args) {
        String[] testClasses = {"ExternalUDFDriver",
                "FindSalaryDriver",
                "StudentGradesDriver",
                "MovieRatingDriver",
                "SalaryAnalysisDriver",
                "PropertyDriver"};
        String[] testMethods = {"testExternalUDF",
                "testFindSalary",
                "testStudentGrades",
                "testMovieRating",
                "testSalaryAnalysis",
                "testProperty"};

        String[] seedLocation = {"externalUDF.csv",
                "findsalary.csv",
                "studentgrades.csv",
                "movierating.csv",
                "salaryanalysis.csv",
                "property.csv" };
        
        String[] maxStacks = {
                "5",
                "3",
                "3",
                "3",
                "5",
                "15"
        };

        for (int i = 0; i < 6; i++) {
            // Program arguments
            String testClass = testClasses[i];
            String testMethod = testMethods[i];
            String mutationMethod = "StackedMutation";
            String numTrials = "5000";
            String[] stackedmethod = {"0", "1", "3", "4"};
            String maxStack = maxStacks[i];
            String[] args2 = {testClass, testMethod, mutationMethod, numTrials, "0", maxStack};

            // Change seed
            changeSeedLocation(seedLocation[i]);

            for (int j = 0; j < 4; j++) {
                args2[4] = stackedmethod[j];
                BigFuzzPlusDriver.main(args2);
                BigFuzzPlusLog.resetInstance();
            }
        }
    }

    private static void changeSeedLocation(String s) {
        File src = new File("dataset/" + s);
        File dest = new File("dataset/salary1.csv");
        try {
            FileUtils.copyFile(src, dest);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }

    }
}
