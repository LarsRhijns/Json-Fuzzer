package edu.tud.cs.jqf.bigfuzzplus.systematicMutation;

import edu.berkeley.cs.jqf.fuzz.guidance.GuidanceException;
import edu.tud.cs.jqf.bigfuzzplus.systematicMutation.MutationTree.Mutation;
import edu.tud.cs.jqf.bigfuzzplus.BigFuzzPlusMutation;
import edu.tud.cs.jqf.bigfuzzplus.systematicMutation.MutationTree.MutationType;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Mutation class for applying mutations systematically. Based on MutationTemplate.
 *
 * @author Lars van Koetsveld van Ankeren
 */
public class SystematicMutation implements BigFuzzPlusMutation {
	protected static final Random r = new Random();
	private static String delimiter;
	private String deletePath;
	private String seedFile;

	//mutation tree
	private MutationTree mutationTree;
	//mutation data per mutation depth level
	private final ArrayList<String[]> levelData;
	//current level in tree
	private int currentLevel;

	//maximum depth of tree
	public static int MUTATION_DEPTH;
	//explore all columns
	public static boolean MUTATE_COLUMNS;
	//apply random first order mutations for simulating BigFuzz
	public static boolean MUTATE_RANDOM;

	//print level and mutation type for every mutation
	public static final boolean EVALUATE = false;
	//number of times the tree has been restarted
	public static int restartAmount;

	/**
	 * Constructor for SystematicMutation class. Reads input conf file for path of seed.
	 *
	 * @param inputFile path of input conf file containing path of seed
	 */
	public SystematicMutation(String inputFile) {
		revertDelimiter();
		currentLevel = 0;
		levelData = new ArrayList<>(MUTATION_DEPTH);

		//reads files and sets seedFile and fileRows.
		try {
			BufferedReader br = new BufferedReader(new FileReader(inputFile));
			seedFile = br.readLine();
			br = new BufferedReader(new FileReader(seedFile));
			levelData.add(br.readLine().split(delimiter));
			levelData.add(null);
			br.close();
		} catch (IOException e) {
			System.out.println("Error loading mutation files.");
		}
		mutationTree = new MutationTree(levelData.get(0).length);
		if (MUTATE_RANDOM) {
			currentLevel = 1;
		}
	}

	public String evaluation() {
		return "Next mutation: " + mutationTree.getCurrentMutation().getMutationType() +
				"\nLevel: " + currentLevel +
				"\nColumn: " + mutationTree.getCurrentMutation().getColumn();
	}

	/**
	 * Start mutating on a csv input file or continue mutating with previous mutant.
	 *
	 * @param outputFile path of output file written to by the class. Will contain mutated data
	 * @throws IOException if file cannot be found
	 */
	@Override
	public void mutate(File inputFile, File outputFile) throws IOException {
		if (EVALUATE) {
			System.out.println(evaluation());
		}
		//for simulating BigFuzz
		if (MUTATE_RANDOM) {
			mutateRandom(outputFile);
			return;
		}
		Mutation currentMutation = mutationTree.traverseTree();
		currentLevel = currentMutation.getLevel();

		//Start from seed after all mutations have been applied
		if (currentLevel == 0) {
			if (EVALUATE) {
				System.out.println("Reached end of tree, restarting.");
			}
			restartAmount++;
			mutationTree = new MutationTree(levelData.get(0).length);
			levelData.subList(1, levelData.size()).clear();
			revertDelimiter();
			currentMutation = mutationTree.traverseTree();
			currentLevel = currentMutation.getLevel();
		}

		int columnsBefore = levelData.get(currentLevel - 1).length;
		String[] mutationRows = new String[columnsBefore];
		System.arraycopy(levelData.get(currentLevel - 1), 0, mutationRows, 0, columnsBefore);
		mutationRows = applyMutation(mutationRows, currentMutation);

		if (levelData.size() <= currentLevel) {
			levelData.add(currentLevel, mutationRows);
		} else {
			levelData.set(currentLevel, mutationRows);
		}

		List<String> fileList = Files.readAllLines(inputFile.toPath());
		int n = new Random().nextInt(fileList.size());
		File fileToMutate = new File(fileList.get(n));
		ArrayList<String> mutatedInput = mutateFile(fileToMutate);
		if (mutatedInput != null) {
			writeFile(outputFile, mutatedInput);
		}

		deletePath = outputFile.getPath();

		// write next ref file
		File refFile = new File(outputFile + "_ref");
		BufferedWriter bw = new BufferedWriter(new FileWriter(refFile));
		for(int i = 0; i < fileList.size(); i++)
		{
			if(i == n)
				bw.write(outputFile.getPath());
			else
				bw.write(fileList.get(i));
			bw.newLine();
			bw.flush();
		}
		bw.close();
	}

	/**
	 * Applies random mutation type on seed input. Used for simulating BigFuzz.
	 *
	 * @param outputFile path of output file written to by the class, will contain mutated data
	 * @throws IOException if file cannot be found
	 */
	private void mutateRandom(String outputFile) throws IOException {
		int columnsBefore = levelData.get(0).length;
		String[] mutationRows = new String[columnsBefore];
		System.arraycopy(levelData.get(0), 0, mutationRows, 0, columnsBefore);
		mutationRows = randomMutation(mutationRows, columnsBefore);

		levelData.set(1, mutationRows);

		List<String> fileList = Files.readAllLines(inputFile.toPath());
		int n = new Random().nextInt(fileList.size());
		File fileToMutate = new File(fileList.get(n));
		ArrayList<String> mutatedInput = mutateFile(fileToMutate);
		if (mutatedInput != null) {
			writeFile(outputFile, mutatedInput);
		}

		deletePath = outputFile.getPath();

		// write next ref file
		File refFile = new File(outputFile + "_ref");
		BufferedWriter bw = new BufferedWriter(new FileWriter(refFile));
		for(int i = 0; i < fileList.size(); i++)
		{
			if(i == n)
				bw.write(outputFile.getPath());
			else
				bw.write(fileList.get(i));
			bw.newLine();
			bw.flush();
		}
		bw.close();

		if (delimiter.equals("~")) {
			changeDelimiter();
		}
	}

	/**
	 * Mutate on rows of an input file. Applies 7 mutations systematically.
	 *
	 * @param mutationRows input rows that will be mutated.
	 * @param mutation     mutation to be applied
	 * @return array of strings containing mutated data
	 */
	private String[] applyMutation(String[] mutationRows, Mutation mutation) {
		r.setSeed(System.currentTimeMillis());
		//can only mutate if data is present
		assert mutationRows.length > 0;
		int columnIndex = mutation.getColumn();

		switch (mutation.getMutationType()) {
			case ChangeValue:       //change value
				mutationRows[columnIndex] = Integer.toString(r.nextInt());
				break;
			case ChangeType:        //change data type
				changeType(columnIndex, mutationRows);
				break;
			case ChangeDelimiter:               //change delimiter
				changeDelimiter();
				break;
			case InsertChar:                    //insert characters
				insertChar(columnIndex, mutationRows);
				break;
			case RemoveElement:                 //remove column
				mutationRows = removeOneElement(columnIndex, mutationRows);
				break;
			case AddElement:                    //add column
				mutationRows = addOneElement(mutationRows);
				break;
			case EmptyColumn:                   //change to empty string
				mutationRows[columnIndex] = "";
				break;
			case NoMutation:
				throw new GuidanceException("Can not mutate without mutation");
		}
		return mutationRows;
	}

	/**
	 * Applies random mutation type on data from input file.
	 *
	 * @param mutationRows input data to mutate
	 * @param columnAmount number of columns in input file
	 * @return array of strings containing mutated data
	 */
	private String[] randomMutation(String[] mutationRows, int columnAmount) {
		r.setSeed(System.currentTimeMillis());
		//can only mutate if data is present
		assert mutationRows.length > 0;
		int columnIndex = r.nextInt(columnAmount);

		MutationType nextType = MutationType.values()[r.nextInt(MutationType.values().length - 1) + 1];
		switch (nextType) {
			case ChangeValue:       //change value
				mutationRows[columnIndex] = Integer.toString(r.nextInt());
				break;
			case ChangeType:        //change data type
				changeType(columnIndex, mutationRows);
				break;
			case ChangeDelimiter:               //change delimiter
				if (delimiter.equals(",")) {
					changeDelimiter();
				}
				break;
			case InsertChar:                    //insert characters
				insertChar(columnIndex, mutationRows);
				break;
			case RemoveElement:                 //remove column
				mutationRows = removeOneElement(columnIndex, mutationRows);
				break;
			case AddElement:                    //add column
				mutationRows = addOneElement(mutationRows);
				break;
			case EmptyColumn:                   //change to empty string
				mutationRows[columnIndex] = "";
				break;
			case NoMutation:
				throw new GuidanceException("Can not mutate without mutation");
		}
		return mutationRows;
	}

	/**
	 * Changes type of column from float to String, int to float and String to int.
	 *
	 * @param columnIndex  column to be mutated
	 * @param mutationRows input to be mutated
	 */
	private void changeType(int columnIndex, String[] mutationRows) {
		String element = mutationRows[columnIndex];
		try {
			//if float change to String
			Float.parseFloat(element);
			if (element.lastIndexOf(".") != -1) {
				mutationRows[columnIndex] = "ChangeType";
			}
			//if int change to float
			else {
				mutationRows[columnIndex] += ".0";
			}
		} catch (NumberFormatException ignored) {
			//if string change to number
			mutationRows[columnIndex] = "0";
		}
	}

	/**
	 * Change delimiter to new character. Changes "," to "~" and changes other delimiter characters to ",".
	 * The change is applied when writeFile is called.
	 */
	private void changeDelimiter() {
		if (delimiter.equals(",")) {
			delimiter = "~";
		} else {
			delimiter = ",";
		}
	}

	/**
	 * Insert random char into row.
	 *
	 * @param columnIndex index of column
	 */
	private void insertChar(int columnIndex, String[] mutationRows) {
		char insertChar = (char) r.nextInt(255);
		String element = mutationRows[columnIndex];
		int pos = 0;
		if (element.length() != 0) {
			pos = r.nextInt(element.length());
		}
		mutationRows[columnIndex] = element.substring(0, pos) + insertChar + element.substring(pos);
	}

	/**
	 * Removes one column from input.
	 *
	 * @param removeIndex  index of column to remove
	 * @param mutationRows input to be mutated
	 * @return array of strings with column removed at provided index
	 */
	private String[] removeOneElement(int removeIndex, String[] mutationRows) {
		if (mutationTree.getCurrentMutation().getColumnAmount() < 2) {
			return mutationRows;
		}
		String[] result = new String[mutationRows.length - 1];
		int updateIndex = 0;

		for (int i = 0; i < mutationRows.length - 1; i++) {
			if (i == removeIndex) {
				updateIndex++;
			}
			result[i] = mutationRows[updateIndex];
			updateIndex++;
		}
		return result;
	}

	/**
	 * Add one element at last column.
	 *
	 * @param mutationRows input to be mutated
	 * @return array of strings with random element added at provided index
	 */
	public String[] addOneElement(String[] mutationRows) {
		String[] result = new String[mutationRows.length + 1];
		System.arraycopy(mutationRows, 0, result, 0, mutationRows.length);
		result[mutationRows.length] = Integer.toString(r.nextInt(10000));
		return result;
	}

	//TODO merge random generation

	/**
	 * Randomly generate some rows and then randomly insert into the input lines.
	 */
	@Override
	public void randomGenerateRows(ArrayList<String> rows) {

	}

	/**
	 * Writes mutated data into csv txt file, and writes next input file
	 *
	 * @param outputFile path of output file
	 */
	private void writeFile(String outputFile) throws IOException {
		File fOut = new File(outputFile);
		FileOutputStream fos = new FileOutputStream(fOut);
		String[] mutationRows = levelData.get(currentLevel);

		StringBuilder sb = new StringBuilder(mutationRows[0]);
		for (int i = 1; i < mutationRows.length; i++) {
			String element = mutationRows[i];
			sb.append(delimiter).append(element);
		}
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
		bw.write(sb.toString());
		bw.close();
		fos.close();
	}

	/**
	 * Reverts delimiter to standard value: ",".
	 */
	public static void revertDelimiter() {
		delimiter = ",";
	}

	public void deleteFile(String currentFile) throws IOException {
		// Check if delete is not null (which it is when the file is deleted in the first run)
		if (deletePath != null) {
			File del = new File(deletePath);
			//noinspection ResultOfMethodCallIgnored
			del.delete();
		}
	}

	/**
	 * Unused method to implement BigFuzzMutation interface.
	 */
	@Override
	public void mutate(ArrayList<String> rows) {
	}

	/**
	 * Unused method to implement BigFuzzMutation interface.
	 */
	@Override
	public void writeFile(File outputFile, List<String> fileRows) throws IOException {
	}

	/**
	 * Unused method to implement BigFuzzMutation interface.
	 */
	@Override
	public ArrayList<String> mutateFile(File inputFile) throws IOException {
		return null;
	}
}
