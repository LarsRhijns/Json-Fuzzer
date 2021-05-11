package edu.bigfuzztabulardata;

import java.io.File;

public class MainDriver {
    public static void main(String[] args) {
        String dataSpecificationInput = "fuzz/src/main/java/edu/bigfuzztabulardata/dataset/salaryAnalysisFileFormat";
        File file = new File(dataSpecificationInput);

        InputManager im = new InputManager(file);
        Mutation m = new Mutation();

        System.out.println(im.toString());

        InputGenerator ig = new InputGenerator(im.getInputs());

        String fileName = ig.generateInputFile();
        System.out.println("Input file generated: " + fileName);

        m.mutateFile(fileName);
        m.mutateFile(fileName);

    }
}
