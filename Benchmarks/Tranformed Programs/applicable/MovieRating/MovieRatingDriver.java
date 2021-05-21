package edu.ucla.cs.bigfuzz.customarray.applicable.MovieRating;

import edu.berkeley.cs.jqf.fuzz.Fuzz;
import edu.berkeley.cs.jqf.fuzz.JQF;
import edu.ucla.cs.bigfuzz.customarray.applicable.MovieRating.MovieRating;
import org.junit.runner.RunWith;

import java.io.IOException;
@RunWith(JQF.class)

public class MovieRatingDriver {

@Fuzz
    public void testMovieRating(String fileName) throws IOException {
        System.out.println("edu.ucla.cs.bigfuzz.customarray.applicable.MovieRating.MovieRatingDriver::testMovieRating: "+fileName);
        MovieRating analysis = new MovieRating();
        analysis.MovieRating(fileName);
    }
}
