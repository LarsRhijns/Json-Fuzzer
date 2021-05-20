package edu.bigfuzztabulardata;

import java.io.File;

public class MainDriver {
    public static void main(String[] args) {
        String dataSpecificationInput = "fuzz/src/main/java/edu/bigfuzztabulardata/dataset/onlyDatatypes";
        File file = new File(dataSpecificationInput);

        InputManager im = new InputManager(file);

        System.out.println(im.toString());

        InputGenerator ig = new InputGenerator(im.getInputs());

        String fileName = ig.generateInputFile();
        System.out.println("Input file generated: " + fileName);

        Mutation m = new Mutation(im.getInputs());

        for (int i = 0; i < 0; i++) {
            m.mutateFile(fileName);
        }

    }
}
