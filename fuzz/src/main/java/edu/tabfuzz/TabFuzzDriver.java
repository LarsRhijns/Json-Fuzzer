package edu.tabfuzz;

import edu.berkeley.cs.jqf.fuzz.junit.GuidedFuzzing;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;

public class TabFuzzDriver {
    public static void main(String[] args) {

        String dataSpecificationInput = "fuzz/src/main/java/edu/tabfuzz/dataset/salaryAnalysisFileFormat";
        File file = new File(dataSpecificationInput);
        InputManager im = new InputManager(file);
        System.out.println(im.toString());


        boolean seed = false;
        // Get the fileConf location:
        String fileConf = "";
        if (seed) {
            // TODO: Make sure the tested input files go to a separate directory
            fileConf = "fuzz/src/main/java/edu/tabfuzz/dataset/configuration";

        } else {
            // Generate a random seed
            InputGenerator ig = new InputGenerator(im.getInputs());
            String fileName = ig.generateInputFile();
            System.out.println("Random seed generated: " + fileName);
            fileConf = "fuzz/src/main/java/edu/tabfuzz/generatedConfigurations/" + fileName + ".csv";
            try {
                FileWriter fw = new FileWriter(fileConf);
                fw.write("fuzz/src/main/java/edu/tabfuzz/generatedInputFiles/" + fileName + ".csv");
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        // BigFuzz replica below:

        try {
            long startTime = System.currentTimeMillis();

            Duration duration = Duration.of(100, ChronoUnit.SECONDS);
            TabFuzzGuidance guidance = new TabFuzzGuidance("Test1", fileConf, im.getInputs(), 5, duration, System.err);

            GuidedFuzzing.run("SalaryAnalysisDriver", "testSalaryAnalysis", guidance, System.out);

//            if (Boolean.getBoolean("jqf.logCoverage")) {
//                System.out.println(String.format("Covered %d edges.",
//                        guidance.getCoverage().getNonZeroCount()));
//            }


            long endTime = System.currentTimeMillis();
            System.out.println("*********Running Time：" + (endTime - startTime) + "ms");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
