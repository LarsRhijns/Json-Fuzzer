package edu.tud.cs.jqf.bigfuzzplus.bigfuzzmutations;

//import org.apache.commons.lang.ArrayUtils;

/*
 mutation for I4: two JDU tree
 */

import edu.tud.cs.jqf.bigfuzzplus.BigFuzzPlusMutation;
import org.apache.commons.lang.RandomStringUtils;

import java.io.*;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static edu.tud.cs.jqf.bigfuzzplus.BigFuzzPlusDriver.PRINT_MUTATION_DETAILS;

public class AgeAnalysisPlusMutation implements BigFuzzPlusMutation {

    Random r = new Random();
    int maxDuplicatedTimes = 10;
    int maxGenerateTimes = 20;
    int maxGenerateValue = 10000000;
    DecimalFormat decimalFormat = new DecimalFormat("#,##0");
    String delete;

    /**
     * Randomly duplicate some lines and then randomly insert into the input lines
     * @param rows
     */
    public void randomDuplicateRows(ArrayList<String> rows)
    {
        int ind = r.nextInt(rows.size());
        int duplicatedTimes = r.nextInt(maxDuplicatedTimes)+1;
        String duplicatedValue = rows.get(ind);
        for(int i=0;i<duplicatedTimes;i++)
        {
            int insertPos = r.nextInt(rows.size());
            rows.add(insertPos, duplicatedValue);
        }
    }

    /**
     * Randomly generate some lines and then randomly insert into the input lines
     * @param rows
     */
    public void randomGenerateRows(ArrayList<String> rows)
    {
        int generatedTimes = r.nextInt(maxGenerateTimes)+1;
        for(int i=0;i<generatedTimes;i++)
        {
            double tempnumber = r.nextGaussian()*10000+10000;
            if(tempnumber<0)
            {
                tempnumber = 0;
            }
            int number = (int)tempnumber;
            String numberAsString = Integer.toString(number);
            String zip = "9" + "0"+ "0" + r.nextInt(10) + r.nextInt(10);
            int age = (int)(Math.random()*99);
            numberAsString = zip +","+Integer.toString(age)+","+numberAsString;
            rows.add(numberAsString);
        }
    }

    public void randomGenerateOneColumn(int columnID, int minV, int maxV, ArrayList<String> rows)
    {
        int generatedTimes = r.nextInt(maxGenerateTimes)+1;
        for(int i=0;i<generatedTimes;i++)
        {
            int rowID = r.nextInt(rows.size());
            String row = rows.get(rowID);
            String[] columns = row.split(",");
            columns[columnID] = Integer.toString(r.nextInt(maxV-minV)+minV);
            int insertPos = r.nextInt(rows.size());

            String insertRow = columns[0];

            for(int j=1;j<columns.length;j++)
            {
                insertRow = insertRow + ","+columns[j];
            }

            rows.add(insertPos, insertRow);
        }
    }

    public void randomDuplicateOneColumn(int columnID, int minV, int maxV, ArrayList<String> rows)
    {
        int generatedTimes = r.nextInt(maxGenerateTimes)+1;
        ArrayList<String> tempRows = new ArrayList<String>(rows);
        for(int i=0;i<rows.size();i++) {
            String row = rows.get(i);
            String[] columns = row.split(",");
            int val = Integer.parseInt(columns[columnID]);
            if (val >= minV && val <= maxV) {
                int insertPos = r.nextInt(tempRows.size());
                tempRows.add(insertPos, row);
            }
        }
        rows = tempRows;
    }

    public void improveOneColumn(int columnID, int minV, int maxV, ArrayList<String> rows)
    {
        for(int i=0;i<rows.size();i++) {
            String row = rows.get(i);
            String[] columns = row.split(",");
            int val = Integer.parseInt(columns[columnID]);
            if (val < minV || val > maxV) {
                columns[columnID] = Integer.toString(r.nextInt(maxV-minV)+minV);
                String insertRow = columns[0];

                for(int j=1;j<columns.length;j++)
                {
                    insertRow = insertRow + ","+columns[j];
                }

                rows.set(i, insertRow);
            }
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
        System.out.println("mutate size: " + list.size());
        int lineNum = r.nextInt(list.size());
        System.out.println("mutate linenum: " +list.get(lineNum));
//        // 0: random change value
        // 1: random change into string
//        // 2: random insert
        // 3: random delete one column
//        // 4: random add one coumn
        String[] columns = list.get(lineNum).split(",");

        int method = (int)(Math.random() * 2);
        int columnID = (int)(Math.random() * 2)+1;
        System.out.println("AgeAnalysisMutation *** "+method+" "+lineNum+" "+columnID);
//        if(method == 0){
//            columns[columnID] = Integer.toString(r.nextInt());
//        }
        if(method==0) {
//            int value = 0;
//            value = Integer.parseInt(columns[columnID]);
//            float v = (float)value + r.nextFloat();
            String r = RandomStringUtils.randomAscii((int)(Math.random() * 5));
//            columns[columnID] = Float.toString(v);
            columns[columnID] = r;
        }
//        else if(method==2) {
//            char temp = (char)r.nextInt(255);
//            int pos = r.nextInt(columns[columnID].length());
//            columns[columnID] = columns[columnID].substring(0, pos)+temp+columns[columnID].substring(pos);
//        }
        else if(method==1) {
            columns = removeOneElement(columns, columnID);
        }
//        else if(method==2) {
//            String one = Integer.toString(r.nextInt(10000));
//            columns = AddOneElement(columns, one, columnID);
//        }
        String line = "";
        for(int j=0;j<columns.length;j++) {
            if(j==0)
            {
                line = columns[j];
            }
            else
            {
                int next = (int)(Math.random() * 2);
                if(next == 0){
                    line = line+","+columns[j];
                }else{
                    line = line + "#" + columns[j];
                }
            }
        }
        list.set(lineNum, line);
        /*for(int i=0;i<list.size();i++)
        {
            String line = list.get(i);
            String[] components = line.split(",");
            line = "";
            for(int j=0;j<components.length;j++)
            {
                if(r.nextDouble()>0.8)
                {
                    components[j] = randomChangeByte(components[j]);
                }
                if(line.equals(""))
                {
                    line = components[j];
                }
                else
                {
                    line = line+","+components[j];
                }
            }

            list.set(i, line);
        }*/
    }

    private String randomChangeByte(String instr)
    {
        String ret = "";
        System.out.println(instr.length());
        //int pos = r.nextInt(instr.length());
        int pos = (int)(Math.random() * instr.length());
        System.out.println(pos);
        //random change byte
        char temp = (char)r.nextInt(256);
        char[] characters = instr.toCharArray();
        characters[pos] = temp;
        return new String(characters);
    }

//    private String randomChangeByte(String instr)
//    {
//        // 0: random replace one char
//        // 1: random delete one char
//        // 2: random add one char
//        String ret = "";
//        int pos = r.nextInt(instr.length());
//        int method = r.nextInt(3);
//        if(method == 0)
//        {
//            char[] temp = instr.toCharArray();
//            temp[pos] = (char)r.nextInt(256);
//            ret = String.valueOf(temp);
//        }
//        else if(method==1)
//        {
//            ret = instr.substring(0, pos)+instr.substring(pos+1);
//        }
//        else
//        {
//            char temp = (char)r.nextInt(256);
//            ret = instr.substring(0, pos)+temp+instr.substring(pos);
//        }
//        return ret;
//    }
    /*public static void main(String[] args) throws IOException{

        SalaryAnalysisMutation mutation = new SalaryAnalysisMutation();
        mutation.mutate("/home/qzhang/Downloads/BigTest-JPF-integrated/benchmarks/src/datasets/salary.csv");
        mutation.writeFile("/home/qzhang/Downloads/BigTest-JPF-integrated/benchmarks/src/datasets/salary2.csv");
    }*/

}
