package edu.tud.cs.jqf.bigfuzzplus.bigfuzzmutations;

//import org.apache.commons.lang.ArrayUtils;

import edu.tud.cs.jqf.bigfuzzplus.BigFuzzPlusMutation;
import org.apache.commons.lang.RandomStringUtils;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

import static edu.tud.cs.jqf.bigfuzzplus.BigFuzzPlusDriver.PRINT_MUTATION_DETAILS;

public class StudentGradePlusMutation implements BigFuzzPlusMutation {

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
        List<String> fileList = Files.readAllLines(inputFile.toPath());
        int n = new Random().nextInt(fileList.size());
        File fileToMutate = new File(fileList.get(n));
        ArrayList<String> mutatedInput = mutateFile(fileToMutate);
        if (mutatedInput != null) {
            writeFile(nextInputFile, mutatedInput);
        }

        delete = nextInputFile.getPath();

        // write next ref file
        File refFile = new File(nextInputFile + "_ref");
        BufferedWriter bw = new BufferedWriter(new FileWriter(refFile));
        for(int i = 0; i < fileList.size(); i++)
        {
            if(i == n)
                bw.write(nextInputFile.getPath());
            else
                bw.write(fileList.get(i));
            bw.newLine();
            bw.flush();
        }
        bw.close();
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

        if(list.get(lineNum).isEmpty()) return;
        int method =(int)(Math.random() * 3);
//        System.out.println("select method:" + method);
        if(method == 0){
            String[] first = list.get(lineNum).split(",");

            int firstID = (int)(Math.random() * first.length);
            String[] columns = first[firstID].split(":");
            int columnID = (int)(Math.random() * 2);
            columns = removeOneElement(columns,columnID);
            int delimeter =(int)(Math.random() * 2);
            if(delimeter == 0){
                if(columnID == 0) first[firstID] = ":" + columns[0];
                else if(columnID == 1) first[firstID] = columns[0] + ":";
            }else if(delimeter == 1){
                if(columnID == 0) first[firstID] = "#" + columns[0];
                else if(columnID == 1) first[firstID] = columns[0] + "#";
            }

            String line = "";
            for(int j=0;j<first.length;j++) {
                if(j==0)
                {
                    line = line + first[j];
                }
                else
                {
                    line = line+","+first[j];
                }
            }

//            System.out.println("deleting column,,,,,,,,," + line);
            list.set(lineNum, line);
        }
        else if(method == 1){
            String[] first = list.get(lineNum).split(",");

            int firstID = (int)(Math.random() * first.length);
            String[] columns = first[firstID].split(":");
            columns[1] = RandomStringUtils.randomAscii(2);
            int delimeter =(int)(Math.random() * 2);
            if(delimeter == 0){
                first[firstID] = columns[0] + ":" + columns[1];
            }else if(delimeter == 1){
                first[firstID] = columns[0] + "#" + columns[1];
            }

            String line = "";
            for(int j=0;j<first.length;j++) {
                if(j==0)
                {
                    line = line + first[j];
                }
                else
                {
                    line = line+","+first[j];
                }
            }

//            System.out.println("random change column ,,,,,,,,," + line);
            list.set(lineNum, line);
        }
        else if(method == 2){
            String[] first = list.get(lineNum).split(",");

            int firstID = (int)(Math.random() * first.length);
            String[] columns = first[firstID].split(":");
            int columnID = (int)(Math.random() * 2);
            columns[columnID] = "";
            int delimeter =(int)(Math.random() * 2);
            if(delimeter == 0){
                if(columnID == 0) first[firstID] = ":" + columns[0];
                else if(columnID == 1) first[firstID] = columns[0] + ":";
            }else if(delimeter == 1){
                if(columnID == 0) first[firstID] = "#" + columns[0];
                else if(columnID == 1) first[firstID] = columns[0] + "#";
            }


            String line = "";
            for(int j=0;j<first.length;j++) {
                if(j==0)
                {
                    line = line + first[j];
                }
                else
                {
                    line = line+","+first[j];
                }
            }
//            System.out.println("random empty column ,,,,,,,,,,,,,,,," + line);
            list.set(lineNum, line);
        }
    }

    @Override
    public void randomGenerateRows(ArrayList<String> rows) {
        int generatedTimes = r.nextInt(maxGenerateTimes)+1;

        for(int i=0;i<generatedTimes;i++)
        {
            int numberInRow = (int)(Math.random() * 5);
//            int numberInRow = 1;
            String numberAsString = new String();
            for (int j = 0; j < numberInRow; j++){
                String course= Integer.toString(r.nextInt(10) + 1);
                String dep = new String();
                if(r.nextBoolean()){
                    dep = "EE";
                }else dep = "CS";
                String grade = Integer.toString(r.nextInt(90)+10);

                numberAsString = dep + course + ":" + grade + numberAsString;
                if(j < (numberInRow-1)) numberAsString = "," + numberAsString;
            }
            rows.add(numberAsString);
        }
    }

}
