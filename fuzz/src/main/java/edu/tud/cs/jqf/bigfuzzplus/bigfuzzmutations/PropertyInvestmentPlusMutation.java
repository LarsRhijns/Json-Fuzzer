package edu.tud.cs.jqf.bigfuzzplus;

//import org.apache.commons.lang.ArrayUtils;

/*
 mutation for I6: it contains a for loop.
 */

import org.apache.commons.lang.RandomStringUtils;

import java.io.*;
import java.util.*;

import static edu.tud.cs.jqf.bigfuzzplus.BigFuzzPlusDriver.PRINT_MUTATION_DETAILS;

public class PropertyInvestmentMutation implements BigFuzzMutation{

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
        // 0: random change value
        // 1: random change into float
        // 2: random insert
        // 3: random delete one column
        // 4: random add one coumn
        String[] columns = list.get(lineNum).split(",");
        int method = r.nextInt(2);
        int columnID = (int)(Math.random() * columns.length);
//        System.out.println("PropertyInvestmentMutation *** "+method+" "+lineNum+" "+columnID);
//        if(method == 0){
//            columns[columnID] = Integer.toString(r.nextInt());
//        }
        if(method==0) {
            String r = RandomStringUtils.randomAscii((int)(Math.random() * 5));
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
//        else if(method==4) {
//            String one = Integer.toString(r.nextInt(10000));
//            columns = AddOneElement(columns, one, columnID);
//        }
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
            String numberAsString = new String();
            String first= RandomStringUtils.randomNumeric((int)(Math.random() * 4)) + ".0";
            Integer second = r.nextInt(30);
            String third = "0.0" + RandomStringUtils.randomNumeric((int)(Math.random() * 2));
            String fourth = RandomStringUtils.randomAlphanumeric((int)(Math.random() * 4));
            numberAsString = "a0,a1," + first + "," + second + "," + third + ",a5," + fourth;
            rows.add(numberAsString);
        }
    }

}
