import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class Util
{

    /**
     * A method that parses a line from a CSV file with a comma
     * delimiter. Returns an ArrayList of Integers.
     * @param line a line from a CSV file
     * @return ArrayList<Integer>
     */
    private static ArrayList<Integer> parseLine(String line)
    {
        ArrayList<Integer> output = new ArrayList<>();
        String[] lineArray = line.split(",");

        //A loop that iterates through each string in the array
        //and parses it to an int to be returned.
        for (String i:lineArray)
            output.add(Integer.parseInt(i));

        return output;
    }

    /**
     * A method that will read a CSV file and return an ArrayList of
     * ArrayLists for each line.
     * @param fileName Name of the CSV file to be read
     * @return ArrayList<ArrayList<Integer>>
     */
    public static ArrayList<ArrayList<Integer>> readCSV(String fileName)
    {
        ArrayList<ArrayList<Integer>> output = new ArrayList<>();

        try(Scanner input = new Scanner(new File(fileName)))
        {
            while(input.hasNextLine())
                output.add(parseLine(input.nextLine()));
        }
        catch (Exception e)
        {
            System.err.println("Error: " + e.getMessage());
        }
        return output;
    }

}
