package edu.tud.cs.jqf.bigfuzzplus.bigfuzzmutations;

//import org.apache.commons.lang.ArrayUtils;

import edu.tud.cs.jqf.bigfuzzplus.BigFuzzPlusMutation;
import org.apache.commons.lang.RandomStringUtils;

import java.io.*;
import java.util.*;

import static edu.tud.cs.jqf.bigfuzzplus.BigFuzzPlusDriver.PRINT_MUTATION_DETAILS;

public class MovieRatingPlusMutation implements BigFuzzPlusMutation {

    Random r = new Random();
    String delete;
    int maxGenerateTimes = 5;


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
            if (PRINT_MUTATION_DETAILS) { System.out.println("[MUTATE] rows: " + tempRows); }
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
        List<String> list1 = Arrays.asList(input);
        List<String> arrList = new ArrayList<String>(list1);
        arrList.remove(input[index]);
        return arrList.toArray(new String[arrList.size()]);
//        List result = new LinkedList();
//
//        for(int i=0;i<input.length;i++)
//        {
//            if(i==index)
//            {
//                continue;
//            }
//            result.add(input[i]);
//        }
//
//        return (String [])result.toArray(input);
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
//        System.out.println("mutate size: " + list.size());
        int lineNum = r.nextInt(list.size());
//        System.out.println("mutate linenum: " + list.get(lineNum));

        int method =(int)(Math.random() * 3);
//        System.out.println("select method:" + method);
        if(method == 0){
            String[] columns = list.get(lineNum).split(":");

            int columnID = (int)(Math.random() * 2);;
            columns = removeOneElement(columns, columnID);

            String line = "";
            for(int j=0;j<columns.length;j++) {
                if(j==0)
                {
                    line = columns[j];
                }
                else
                {
                    line = line+":"+columns[j];
                }
            }
//            System.out.println("::::::::::::: " + line);
            list.set(lineNum, line);
        }
        else if(method ==1){
            String[] first = list.get(lineNum).split(":");

            String[] columns = first[1].split(",");
            int columnID = (int)(Math.random() * columns.length);
            columns[columnID]="";
            String line = first[0]+":";
            for(int j=0;j<columns.length;j++) {
                if(j==0)
                {
                    line = line + columns[j];
                }
                else
                {
                    line = line+","+columns[j];
                }
            }
//            System.out.println(",,,,,,,,,,,, " + line);
            list.set(lineNum, line);
        }
        else{
            String[] first = list.get(lineNum).split(":");

            String[] columns = first[1].split(",");

            int next = (int)(Math.random() * columns.length);
            for (int i = 0; i < next; i++){
                int columnID = 0;
                String rr = RandomStringUtils.randomAscii(2);
                int empty = (int)(Math.random() * 2);
                if(empty == 1){
                    columns[columnID] = "_";
                }else{
                    columns[columnID] = "_" + rr;
                }
//                System.out.println("column: " + columns[columnID]);
            }
            String line = first[0]+":";
            for(int j=0;j<columns.length;j++) {
                if(j==0)
                {
                    line = line + columns[j];
                }
                else
                {
                    line = line+","+columns[j];
                }
            }
            list.set(lineNum, line);
        }

    }

    @Override
    public void randomGenerateRows(ArrayList<String> rows) {
        int generatedTimes = r.nextInt(maxGenerateTimes)+1;
        for(int i=0;i<generatedTimes;i++)
        {
            String numberAsString = new String();
            int bits = (int)(Math.random() * 10);
            String name = RandomStringUtils.randomAlphanumeric(bits);
            numberAsString = name + ":";

            int numberRow = (int)(Math.random() * 2)+1;
            for(int j = 0; j<numberRow; j++){
                int rating = (int)(Math.random()*99);
                numberAsString = numberAsString + "_" + Integer.toString(rating);

                if(j < (numberRow-1)) numberAsString = numberAsString+",";
            }

            rows.add(numberAsString);
        }
    }

}
