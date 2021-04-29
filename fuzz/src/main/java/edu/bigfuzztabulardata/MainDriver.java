package edu.bigfuzztabulardata;

import java.io.File;
import java.io.FileNotFoundException;

public class MainDriver {
    public static void main(String[] args) {
        String dataSpecificationInput = "fuzz/src/main/java/edu/bigfuzztabulardata/dataset/inputDataFormat";
        File file = new File(dataSpecificationInput);

        InputManager im = new InputManager(file);
        System.out.println(im.getInputs()[0].generateInputInRange());
        System.out.println(im.getInputs()[1].generateInputInRange());
        System.out.println(im.getInputs()[2].generateInputInRange());
        System.out.println(im.getInputs()[3].generateInputInRange());
        System.out.println(im.toString());

    }
}
