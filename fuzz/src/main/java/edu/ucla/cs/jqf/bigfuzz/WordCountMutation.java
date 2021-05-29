package edu.ucla.cs.jqf.bigfuzz;

//import org.apache.commons.lang.ArrayUtils;

/*
 mutation: randomByteMutation.
 */

import org.apache.commons.lang.RandomStringUtils;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static edu.ucla.cs.jqf.bigfuzz.BigFuzzDriver.PRINT_MUTATION_DETAILS;

public class WordCountMutation implements BigFuzzMutation{

    Random r = new Random();
    String delete;
    int maxDuplicatedTimes = 20;


    @Override
    public void writeFile(File outputFile, List<String> fileRows) throws IOException {
        FileOutputStream fos = new FileOutputStream(outputFile);
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

    public void deleteFile(String currentInputFile) throws IOException {
        File del = new File(delete);
        del.delete();
    }

    public void mutate(File inputFile, File nextInputFile) throws IOException
    {
        ArrayList<String> mutatedInput = mutateFile(inputFile);
        if (mutatedInput != null) {
            writeFile(nextInputFile, mutatedInput);
        }
        delete = nextInputFile.getPath();
    }

    public ArrayList<String> mutateFile(File inputFile) throws IOException
    {
        if (PRINT_MUTATION_DETAILS) {
            System.out.println("mutate file: " + inputFile.getPath());
        }

        ArrayList<String> rows = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(inputFile));

        if(inputFile.exists())
        {
            String readLine;
            while((readLine = br.readLine()) != null){
                rows.add(readLine);
            }
        }
        else
        {
            System.out.println("File does not exist!");
            return null;
        }

        int method =(int)(Math.random() * 2);
        if(method == 0){
            ArrayList<String> tempRows = new ArrayList<>();
            randomGenerateRows(tempRows);
            if (PRINT_MUTATION_DETAILS) {
                System.out.println("rows: " + tempRows);
            }
            rows = tempRows;

            int next =(int)(Math.random() * 2);
            if(next == 0){
                mutate(rows);
            }
        }else{
            mutate(rows);
        }

        return rows;
    }

    public static String[] removeOneElement(String[] input, int index) {
        List result = new LinkedList();

        for(int i=0;i<input.length;i++)
        {
            if(i==index)
            {
                continue;
            }
            result.add(input[i]);
        }

        return (String [])result.toArray(input);
    }
    public static String[] AddOneElement(String[] input, String value, int index) {
        List result = new LinkedList();

        for(int i=0;i<input.length;i++)
        {
            result.add(input[i]);
            if(i==index)
            {
                result.add(value);
            }
        }

        return (String [])result.toArray(input);
    }

    public void mutate(ArrayList<String> list)
    {
        r.setSeed(System.currentTimeMillis());
        int lineNum = r.nextInt(list.size());

        String line = randomChangeByte(list.get(lineNum));
        list.set(lineNum, line);
    }

    private String randomChangeByte(String instr)
    {
        String ret = "";
        System.out.println("randomChangeByte instr length: " + instr.length());
        //int pos = r.nextInt(instr.length());
        int pos = (int)(Math.random() * instr.length());
        System.out.println("randomChangeByte pos: " + pos);

        //random change several bytes
        //char temp = (char)(Math.random() * 256);
        String r = RandomStringUtils.randomAscii((int)(Math.random() * 5));
        System.out.println("randomChangeByte randomAscii: " + r);
//        char[] rchars = r.toCharArray();


//        char[] characters = instr.toCharArray();
//        characters[pos] = rchars[0];

        StringBuilder sb = new StringBuilder(instr);
        sb.insert(pos, r);
        instr = sb.toString();

//        return new String(characters);
        return instr;
    }

    @Override
    public void randomDuplicateRows(ArrayList<String> rows) {
        int ind = r.nextInt(rows.size());
        int duplicatedTimes = r.nextInt(maxDuplicatedTimes)+1;
        String duplicatedValue = rows.get(ind);
        for(int i=0;i<duplicatedTimes;i++)
        {
            int insertPos = r.nextInt(rows.size());
            rows.add(insertPos, duplicatedValue);
        }

    }

    @Override
    public void randomGenerateRows(ArrayList<String> rows) {

    }

    @Override
    public void randomGenerateOneColumn(int columnID, int minV, int maxV, ArrayList<String> rows) {

    }

    @Override
    public void randomDuplacteOneColumn(int columnID, int intV, int maxV, ArrayList<String> rows) {

    }

    @Override
    public void improveOneColumn(int columnID, int intV, int maxV, ArrayList<String> rows) {

    }

}
