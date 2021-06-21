package edu.tud.cs.jqf.bigfuzzplus.bigfuzzmutations;

import edu.tud.cs.jqf.bigfuzzplus.BigFuzzPlusMutation;
import org.apache.commons.lang.RandomStringUtils;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class CommuteTypePlusMutation implements BigFuzzPlusMutation {

    Random r = new Random();
    String delete;
    int maxGenerateTimes = 20;

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
            System.out.println("rows: " + tempRows);
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

    public ArrayList<String> mutateFile1(File inputFile) throws IOException
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
            randomGenerateRows1(tempRows);
            System.out.println("rows: " + tempRows);
            rows = tempRows;

            int next =(int)(Math.random() * 2);
            if(next == 0){
                mutate1(rows);
            }
        }else{
            mutate1(rows);
        }

        return rows;
    }

    @Override
    public void mutate(ArrayList<String> list) {
        r.setSeed(System.currentTimeMillis());
        System.out.println("mutate size: " + list.size());
        int lineNum = r.nextInt(list.size());
        System.out.println("mutate linenum: " + list.get(lineNum));
        // 0: random change value
        // 1: random change into float
        // 2: random insert
        // 3: random delete one column
        // 4: random add one coumn
        String[] columns = list.get(lineNum).split(",");
        int method = r.nextInt(2);
        int columnID = (int)(Math.random() * columns.length);
        System.out.println("CommuteTypeMutation ***"+method+" "+lineNum+" "+columnID);

        if(method==0) {
            String r = RandomStringUtils.randomAscii((int)(Math.random() * 5));
            columns[columnID] = r;
        }
        else if(method==1) {
            columns = removeOneElement(columns, columnID);
        }

        String line = "";
        int delimeter = r.nextInt(2);
        for(int j=0;j<columns.length;j++) {
            if(j==0)
            {
                line = columns[j];
            }
            else
            {
                if(delimeter == 0){
                    line = line+","+columns[j];
                }else{
                    line = line + "#" + columns[j];
                }
            }
        }
        list.set(lineNum, line);
    }

    public void mutate1(ArrayList<String> list) {
        r.setSeed(System.currentTimeMillis());
        System.out.println("mutate1 size: " + list.size());
        int lineNum = r.nextInt(list.size());
        System.out.println("mutate1 linenum: " + list.get(lineNum));
        // 0: random change value
        // 1: random change into float
        // 2: random insert
        // 3: random delete one column
        // 4: random add one coumn
        String[] columns = list.get(lineNum).split(",");
        int method = r.nextInt(2);
        int columnID = (int)(Math.random() * columns.length);
        System.out.println("CommuteTypeMutation *** "+method+" "+lineNum+" "+columnID);

        if(method==0) {
            String r = RandomStringUtils.randomAscii((int)(Math.random() * 5));
            columns[columnID] = r;
        }else if(method==1) {
            columns = removeOneElement(columns, columnID);
        }

        String line = "";
        int delimeter = r.nextInt(2);
        for(int j=0;j<columns.length;j++) {
            if(j==0)
            {
                line = columns[j];
            }
            else
            {
                if(delimeter == 0){
                    line = line+","+columns[j];
                }else{
                    line = line + "#" + columns[j];
                }
            }
        }
        list.set(lineNum, line);
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

    @Override
    public void randomGenerateRows(ArrayList<String> rows) {
        int generatedTimes = r.nextInt(maxGenerateTimes)+1;
        for(int i=0;i<generatedTimes;i++) {
            String numberAsString;
            Integer index = i + 1;
            String zip1 = "9" + "0" + "0" + r.nextInt(10) + r.nextInt(10);
            String zip2 = "9" + "0" + "0" + r.nextInt(10) + r.nextInt(10);
            String dis = RandomStringUtils.randomNumeric((int) (Math.random() * 2));
            String time = RandomStringUtils.randomNumeric((int) (Math.random() * 4));
            numberAsString = index +","+zip1 + "," + zip2 + "," + dis + "," + time;
            rows.add(numberAsString);
        }
    }

    public void randomGenerateRows1(ArrayList<String> rows) {
        int generatedTimes = r.nextInt(maxGenerateTimes)+1;
        for(int i=0;i<generatedTimes;i++)
        {
            String numberAsString;
            String zip = "9" + "0"+ "0" + r.nextInt(10) + r.nextInt(10);
            String name = RandomStringUtils.randomAlphabetic((int)(Math.random() * 5));
            numberAsString = zip + "," + name;
            rows.add(numberAsString);
        }
    }

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

    @Override
    public void deleteFile(String currentFile) throws IOException {
        File del = new File(delete);
        del.delete();
    }
}
