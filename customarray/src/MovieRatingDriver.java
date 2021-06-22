import edu.berkeley.cs.jqf.fuzz.Fuzz;
import edu.berkeley.cs.jqf.fuzz.JQF;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static edu.tud.cs.jqf.bigfuzzplus.BigFuzzPlusDriver.PRINT_METHOD_NAMES;

@RunWith(JQF.class)

public class MovieRatingDriver {

    @Fuzz
    public void testMovieRating(String fileName) throws IOException {
        List<String> fileList = Files.readAllLines(Paths.get(fileName));
        if (PRINT_METHOD_NAMES) { System.out.println("[METHOD] MovieRatingDriver::testMovieRating"); }
        MovieRating analysis = new MovieRating();
        analysis.MovieRating(fileList.get(0));
    }

    public static void main(String[] args) throws IOException {
        MovieRating analysis = new MovieRating();
        analysis.MovieRating("./dataset/salary1.csv");
    }
}
