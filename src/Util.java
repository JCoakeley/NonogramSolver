import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class Util
{
    private static final String DEFAULT_FILE_NAME = "25x25 Nonogram.csv";

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
        for (String integer:lineArray)
            output.add(Integer.parseInt(integer));

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

    /**
     * A method for getting the filename from the user and checking to ensure
     * the file exists before proceeding. If the user doesn't enter anything
     * a default filename will be used.
     * @return String the filename entered by the user
     */
    public static String getFileName()
    {
        String fileName;
        Scanner input = new Scanner(System.in);
        System.out.print("Please enter the CSV filename that contains the grid size");
        System.out.print(" followed by the clues of the nonogram to be solved: ");
        fileName = input.nextLine();

        if(!fileName.isEmpty())
        {
            File file = new File(fileName);

            //A loop that will continue to ask the user for a filename and check
            //to ensure it exists before proceeding.
            while(!file.exists())
            {
                System.out.print("File not found, please enter the correct file name: ");
                fileName = input.nextLine();
                if(fileName.isEmpty())
                    fileName = DEFAULT_FILE_NAME;

                file = new File(fileName);
            }
        }
        else
            fileName = DEFAULT_FILE_NAME;

        return fileName;
    }
}
