import P6.MovieRating.Spec_BigFuzz.MovieRating;
import edu.berkeley.cs.jqf.fuzz.Fuzz;
import edu.berkeley.cs.jqf.fuzz.JQF;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@RunWith(JQF.class)

public class MovieRatingDriver {

    @Fuzz
    public void testMovieRating(String fileName) throws IOException {
        List<String> fileList = Files.readAllLines(Paths.get(fileName));
        MovieRating analysis = new MovieRating();
        analysis.MovieRating(fileList.get(0));
    }
}
