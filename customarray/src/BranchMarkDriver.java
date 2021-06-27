import edu.berkeley.cs.jqf.fuzz.Fuzz;
import edu.berkeley.cs.jqf.fuzz.JQF;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static edu.tud.cs.jqf.bigfuzzplus.BigFuzzPlusDriver.PRINT_METHOD_NAMES;

@RunWith(JQF.class)
public class BranchMarkDriver {

	@Fuzz
	public void testBranchMark(String fileName) throws IOException {
		if (PRINT_METHOD_NAMES) { System.out.println("[METHOD] BranchMarkDriver::testBranchMark"); }

		List<String> fileList = Files.readAllLines(Paths.get(fileName));
		BranchMark analysis = new BranchMark();
		analysis.BranchMark(fileList.get(0));
	}

	public static void main(String[] args) throws IOException {
		String fileName = "./dataset/conf_branchmark";
		if (PRINT_METHOD_NAMES) { System.out.println("[METHOD] BranchMarkDriver::testBranchMark: "+fileName); }

		List<String> fileList = Files.readAllLines(Paths.get(fileName));
		BranchMark analysis = new BranchMark();
		analysis.BranchMark(fileList.get(0));
	}

}
