import edu.ucla.cs.bigfuzz.customarray.CustomArray;
import edu.ucla.cs.bigfuzz.customarray.applicable.BranchMark.BranchMarkCustomArray;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class BranchMark {

	public void BranchMark(String input) throws IOException {
		File file=new File(input);
		ArrayList<String> results0;
		if(file.exists())
		{
			results0 = CustomArray.read(input);
		}
		else
		{
			System.out.println("File does not exist!");
			return;
		}

		ArrayList<String> results1 = BranchMarkCustomArray.mapDoNothing(results0);
		String results2 = BranchMarkCustomArray.filterOnlyFirstInput(results1);
		// todo: test if first value changes between 0 and 1000 in intervals of 10? is a better measure metric.
		String results3 = BranchMarkCustomArray.branchCountCommas(results2);
	}

}
