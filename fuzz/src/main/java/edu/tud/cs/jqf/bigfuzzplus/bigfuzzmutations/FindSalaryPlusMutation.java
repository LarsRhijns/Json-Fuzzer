package edu.tud.cs.jqf.bigfuzzplus.bigfuzzmutations;

import edu.tud.cs.jqf.bigfuzzplus.BigFuzzPlusMutation;
import org.apache.commons.lang.RandomStringUtils;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static edu.tud.cs.jqf.bigfuzzplus.BigFuzzPlusDriver.PRINT_MUTATION_DETAILS;


public class FindSalaryPlusMutation implements BigFuzzPlusMutation {

    Random r = new Random();
    String delete;
    int maxGenerateTimes = 10;


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
        System.out.println("mutate linenum: " + list.get(lineNum));
//        // 0: random change value
        // 1: random change into string
//        // 2: random insert
        // 3: random delete one column
        // 4: random add one coumn
        String[] columns = list.get(lineNum).split(",");
        int method = r.nextInt(2);
        int columnID = r.nextInt(Integer.parseInt("1"));

        if(method==0) {
            if( columns[columnID] == "") return;

            int next = r.nextInt(2);
            if(next == 0){
                columns[columnID] = "$" + RandomStringUtils.randomAscii((int)(Math.random() * 10));
            } else{
                columns[columnID] = RandomStringUtils.randomAscii((int)(Math.random() * 10));
            }
//            if(columns[columnID].charAt(0)=='$')
//            {
//                columns[columnID] = "$" + RandomStringUtils.randomAscii((int)(Math.random() * 10));
//            }
//            else
//            {
//                columns[columnID] = RandomStringUtils.randomAscii((int)(Math.random() * 6));
//            }
        }
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
                line = line+","+columns[j];
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

    @Override
    public void randomGenerateRows(ArrayList<String> rows) {
        int generatedTimes = r.nextInt(maxGenerateTimes)+1;
        for(int i=0;i<generatedTimes;i++)
        {
            int bits = (int)(Math.random()*6);
            String tempRow = RandomStringUtils.randomNumeric(bits);
            int method =(int)(Math.random() * 2);
            if(method == 0){
                int next = (int)(Math.random()*2);
                if(next == 0) {
                    rows.add("$" + tempRow);
                }else {
                    rows.add(tempRow);
                }
            }
            else{
                rows.add(RandomStringUtils.randomNumeric(3));
            }
        }
    }

}
