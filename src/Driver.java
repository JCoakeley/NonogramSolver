import java.util.ArrayList;
import java.util.Arrays;

public class Driver
{
    static ArrayList<Group> groups = new ArrayList<>();
    static ArrayList<ArrayList<Integer>> CSVContents;
    static int width, length, maxRowClues, maxColumnClues;
    static GameBoard gBoard;

    public static void main(String[] args)
    {
        String fileName = Util.getFileName();
        CSVContents = Util.readCSV(fileName);
        assignCSVContents(CSVContents);
        gBoard = new GameBoard(width, length);

        applyOverlap();

        int i=0;
        while (i<10)
        {
            updateGameBoard();
            ++i;
        }
        printGameBoard();


//        //Testing inputs to verify correct permutation calculations
//        ArrayList<Integer> test = new ArrayList<>();
//        test.add(2);
//        test.add(3);
//        test.add(7);
//        test.add(1);
//        test.add(1);
//        test.add(5);
//        Group test1 = new Group(test, 30);
//        System.out.println(Arrays.toString(test1.overlap()));
//        //test1.printPermutations();
    }

    /**
     * A method that takes the parsed contents of the CSV file and assigns the
     * values that will be the width and length of the game board as well as
     * create all the objects that will represent the possible solutions of
     * the rows and columns.
     * @param CSVContents an ArrayList<ArrayList<Integer>> that is the content from a CSV file
     */
    private static void assignCSVContents(ArrayList<ArrayList<Integer>> CSVContents)
    {
        width = CSVContents.getFirst().getFirst();
        length = CSVContents.getFirst().getLast();

        //A loop for creating all the Group objects that will represent the
        //possible solutions for the rows.
        for(int i=0; i<length; ++i)
            groups.add(new Group(CSVContents.get(i+1), width));

        //A loop for creating all the Group objects that will represent the
        //possible solutions for the columns.
        for(int i=0; i<width; ++i)
            groups.add(new Group(CSVContents.get(i+length+1), length));

    }

    /**
     * A method that will retrieve that will call each Group object's
     * Overlap method and assign this to the corresponding row/column
     * of the gBoard object.
     */
    private static void applyOverlap()
    {
        //A loop for iterating through each Group object that represents
        //a row and assigning the output of the Overlap method to the
        //game board.
        for(int i=0; i<length; ++i)
            gBoard.setGBoardRow(groups.get(i).overlap(), i);
        //A loop for iterating through each Group object that represents
        //a column and assigning the output of the Overlap method to the
        //game board.
        for(int i=0; i<width; ++i)
            gBoard.setGBoardColumn(groups.get(i+length).overlap(), i);
    }

    /**
     * A method for updating the state of the game board row by row then
     * column by column. It updates a row or column by first updating the
     * possible permutations of said row or column then assigning any
     * common values from all permutations to the game board.
     */
    private static void updateGameBoard()
    {
        //A loop for iterating through each row of the game board and updating it.
        for(int i=0; i<length; ++i)
        {
            //Passing the current state of a row to that rows Group object
            //and updating the possible permutations.
            groups.get(i).managePermutations(gBoard.getGBoardRow(i));
            //Assigning any common values between all the possible permutations
            //to the current state of the row.
            gBoard.setGBoardRow(groups.get(i).commonPermutation(), i);
        }

        //A loop for iterating through each column of the game board and updating it.
        for(int i=0; i<width; ++i)
        {
            //Passing the current state of a column to that columns Group object
            //and updating the possible permutations.
            groups.get(i+length).managePermutations(gBoard.getGBoardColumn(i));
            //Assigning any common values between all the possible permutations
            //to the current state of the column.
            gBoard.setGBoardColumn(groups.get(i+length).commonPermutation(), i);
        }
    }

    /**
     * A method for calculating the maximum numbers of clues for rows
     * and column for the purposes of printing the finished game board
     * to the console.
     */
    private static void calculateMaxRowColumnClues()
    {
        for(int i=0; i<length; ++i)
            if(groups.get(i).printingClueLength()>maxRowClues)
                maxRowClues = groups.get(i).printingClueLength();

        for(int i=0; i<width; ++i)
            if(groups.get(i+length).printingClueLength()>maxColumnClues)
                maxColumnClues = groups.get(i+length).printingClueLength();
    }

    /**
     * A method for printing out the game board to the console. Will print a 1 for
     * filled square and an x represents a blank square.
     */
    private static void printGameBoard()
    {
        char printChar;

        for(int i=0; i<length; ++i)
        {
            if(i%5 == 0)
            {
                System.out.println(" " + "-".repeat(width * 2 + 5));
            }
            for(int j=0; j<gBoard.getGBoardRow(i).length; ++j)
            {
                if(j%5 == 0)
                    System.out.print(" |");
                if(gBoard.getGBoardRow(i)[j] == 1)
                    printChar = '1';
                else if (gBoard.getGBoardRow(i)[j] == 0)
                    printChar = '0';
                else
                    printChar = 'x';
                System.out.print(" " + printChar);
            }
            System.out.print(" |");
            System.out.println();
        }
        System.out.println(" " + "-".repeat(width*2+5));
    }
}
