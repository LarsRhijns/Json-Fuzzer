package edu.ucla.cs.jqf.bigfuzz;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Mutation class for applying mutations systematically. Based on MutationTemplate.
 *
 * @author Lars van Koetsveld van Ankeren
 */
public class SystematicMutation implements BigFuzzMutation {
	private String delimiter = ",";
	private String delete;
	private Random r = new Random();
	private ArrayList<String> fileRows = new ArrayList<String>();
	private static int mutationCounter;

	/**
	 * Mutate on an csv file
	 *
	 * @param inputFile     csv input file.
	 * @param nextInputFile path of output file written to by the class. Will contain mutated data.
	 * @throws IOException if file cannot be found.
	 */
	@Override
	public void mutate(String inputFile, String nextInputFile) throws IOException {
		List<String> fileList = Files.readAllLines(Paths.get(inputFile));
		Random random = new Random();
		int n = random.nextInt(fileList.size());
		String fileToMutate = fileList.get(n);
		mutateFile(fileToMutate);

		String fileName = nextInputFile + "+" + fileToMutate.substring(fileToMutate.lastIndexOf('/') + 1);
		writeFile(fileName);

		String path = System.getProperty("user.dir") + "/" + fileName;

		delete = path;
		// write next input config
		BufferedWriter bw = new BufferedWriter(new FileWriter(nextInputFile));

		for (int i = 0; i < fileList.size(); i++) {
			if (i == n)
				bw.write(path);
			else
				bw.write(fileList.get(i));
			bw.newLine();
			bw.flush();
		}
		bw.close();
	}

	//    /**
//     * mutate file based on index (support multiple data-specific mutations)
//     *
//     * @param inputFile
//     * @param index
//     * @throws IOException
//     */
//    @Override
//    public void mutateFile(String inputFile, int index) throws IOException {
//
//    }
	private void mutateFile(String inputFile) throws IOException {
		File file = new File(inputFile);

		ArrayList<String> rows = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new FileReader(inputFile));

		if (file.exists()) {
			String readLine = null;
			while ((readLine = br.readLine()) != null) {
				rows.add(readLine);
			}
		} else {
			System.out.println("File does not exist!");
			return;
		}

		br.close();

		mutate(rows);

		fileRows = rows;
	}

	/**
	 * Mutate on rows of an input file. Applies 6 higher order mutations systematically.
	 *
	 * @param rows Input to be mutated.
	 */
	@Override
	public void mutate(ArrayList<String> rows) {
		r.setSeed(System.currentTimeMillis());
		int lineNum = r.nextInt(rows.size());
		String[] columns = rows.get(lineNum).split(delimiter);

		//can only mutate if data is present
		assert columns.length > 0;
		int columnIndex = r.nextInt(columns.length);

		switch (mutationCounter) {
			case 0:     //change value
				columns[columnIndex] = Integer.toString(r.nextInt());
				break;
			case 1:     //change data type
				changeType(columns);
				break;
			case 2:     //change delimiter
				changeDelimiter();
				break;
			case 3:     //insert characters
				insertChar(columns, columnIndex);
				break;
			case 4:     //remove column
				removeOneElement(columns, columnIndex);
				break;
			case 5:     //change to empty string
				columns[columnIndex] = "";
		}
		//output mutated rows
		String line = columns[0];
		for (String column : columns) {
			line += delimiter + column;
		}
		rows.set(lineNum, line);
		mutationCounter = (mutationCounter + 1) % 6;

	}

	/**
	 * Change the data type of a number column from int to float or from float to int.
	 *
	 * @param columns Input to mutate
	 * @return Input
	 */
	private void changeType(String[] columns) {
		for (int i = 0; i < columns.length; i++) {
			String element = columns[i];
			try {
				Integer.parseInt(element);                      //if int
				columns[i] += ".0";                             //change to float
				break;
			} catch (NumberFormatException eInt) {              //if float
				try {                                           //change to int
					Float.parseFloat(element);
					columns[i] = element.substring(0, element.lastIndexOf("."));
					break;
				} catch (NumberFormatException ignored) {       //not a number, continue looping
				}
			}
		}
	}

	/**
	 * Change delimiter to new character. Changes "," to "~", other delimiter characters to ",".
	 */
	private void changeDelimiter() {
		if (delimiter.equals(",")) {
			delimiter = "~";
		} else {
			delimiter = ",";
		}
	}

	/**
	 * Set delimiter to new value.
	 *
	 * @param d New string for delimiter
	 */
	public void setDelimiter(String d) {
		delimiter = d;
	}

	private void insertChar(String[] columns, int columnIndex) {
		char insertChar = (char) r.nextInt(255);
		String element = columns[columnIndex];
		int pos = r.nextInt(element.length());
		columns[columnIndex] = element.substring(0, pos) + insertChar + element.substring(pos);
	}

	private void removeOneElement(String[] columns, int columnIndex) {
	}

	/**
	 * Randomly generate some rows and then randomly insert into the input lines
	 *
	 * @param rows input to mutate
	 */
	@Override
	public void randomGenerateRows(ArrayList<String> rows) {

	}

	/**
	 * Write rows into csv txt file.
	 *
	 * @param outputFile path of output file.
	 */
	@Override
	public void writeFile(String outputFile) throws IOException {
		File fout = new File(outputFile);
		FileOutputStream fos = new FileOutputStream(fout);

		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

		for (String fileRow : fileRows) {
			if (fileRow == null) {
				continue;
			}
			bw.write(fileRow);
			bw.newLine();
		}

		bw.close();
		fos.close();
	}

	@Override
	public void deleteFile(String currentFile) throws IOException {
		File del = new File(delete);
		del.delete();
	}

	//unused methods

	@Override
	public void mutateFile(String inputFile, int index) throws IOException {

	}

	/**
	 * Randomly duplicate some rows and then randomly insert into the input lines
	 *
	 * @param rows
	 */
	@Override
	public void randomDuplicateRows(ArrayList<String> rows) {

	}

	@Override
	public void randomGenerateOneColumn(int columnID, int minV, int maxV, ArrayList<String> rows) {

	}

	@Override
	public void randomDuplicateOneColumn(int columnID, int intV, int maxV, ArrayList<String> rows) {

	}

	@Override
	public void improveOneColumn(int columnID, int intV, int maxV, ArrayList<String> rows) {

	}
}
