package edu.bigfuzztabulardata;

import java.io.File;

public class MainDriver {
    public static void main(String[] args) {
        String dataSpecificationInput = "fuzz/src/main/java/edu/bigfuzztabulardata/dataset/salaryAnalysisFileFormat";
        File file = new File(dataSpecificationInput);

        InputManager im = new InputManager(file);

//        System.out.println(im.getInputs()[0].generateInputInRange());
//        System.out.println(im.getInputs()[1].generateInputInRange());
//        System.out.println(im.getInputs()[2].generateInputInRange());
//        System.out.println(im.getInputs()[3].generateInputInRange());
//        System.out.println(im.getInputs()[4].generateInputInRange());
//        System.out.println(im.getInputs()[5].generateInputInRange());
//        System.out.println(im.getInputs()[6].generateInputInRange());
//        System.out.println(im.getInputs()[7].generateInputInRange());
        System.out.println(im.toString());
        InputGenerator ig = new InputGenerator(im.getInputs());
        String filePath = ig.generateInputFile();
        System.out.println("Input file generated at: " + filePath);

    }
}
