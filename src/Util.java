import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Utility class for reading files, getting filenames and calculation combinations.
 */
public class Util
{
    /**
     * Default file name to use if the user doesn't provide one.
     */
    private static final String DEFAULT_FILE_NAME = "25x25 Nonogram.txt";

    /**
     * Reads a file and converts each row into an integer array.
     * Handles potential errors during file reading.
     *
     * @param fileName The name of the file to read.
     * @return An ArrayList of integer arrays, where each array represents a row of the file.
     *         Returns an empty ArrayList if an error occurs during file reading.
     */
    public static ArrayList<int[]> readFile(String fileName) {

        ArrayList<int[]> output = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName)))
        {
            String line;

            // Reading each line of the file one at a time.
            while ((line = br.readLine()) != null)
            {
                String[] lineArray = line.split(",");
                int[] row = new int[lineArray.length];

                // Converting each element of the line into an integer.
                for (int i = 0; i < lineArray.length; i++)
                    row[i] = Integer.parseInt(lineArray[i]);

                output.add(row);
            }
        } catch (Exception e) {
            System.err.println("Error reading File: " + e.getMessage());
        }

        return output;
    }

    /**
     * Prompts the user for a file name and validates its existence. If the user
     * enters an empty string, a default file name is used.
     *
     * @return The file name entered by the user or the default file name if no
     *         input is provided.
     */
    public static String getFileName()
    {
        String fileName;
        Scanner input = new Scanner(System.in);
        System.out.print("Please enter the filename that contains the grid size");
        System.out.print(" followed by the clues of the nonogram to be solved: ");
        fileName = input.nextLine();

        if (!fileName.isEmpty())
        {
            File file = new File(fileName);

            // A loop that will continue to ask the user for a filename and check
            // to ensure it exists before proceeding.
            while (!file.exists())
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

    /**
     * Calculates the binomial coefficient "n choose r" nCr.
     *
     * @param n The total number of items.
     * @param r The number of item to choose.
     * @return The binomial coefficient nCr. Return 0 is f is invalid.
     */
    public static int nCr(int n, int r)
    {
        if (r < 0 || r > n)
            return 0;

        if (r == 0 || r == n)
            return 1;

        r = Math.min(r, n - r); // Use symmetry to reduce calculations
        long result = 1;

        for (int i = 1; i <= r; i++)
        {
            result *= n - (r - i);
            result /= i;
        }

        return (int)result; // Cast back to int (assuming the result won't overflow)
    }
}
