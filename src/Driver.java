import java.util.ArrayList;
import java.util.Arrays;

public class Driver
{
    static ArrayList<Group> groups = new ArrayList<>();
    static int width, length;
    static GameBoard gBoard;

    public static void main(String[] args)
    {
        String fileName = Util.getFileName();
        ArrayList<ArrayList<Integer>> CSVContents = Util.readCSV(fileName);
        assignCSVContents(CSVContents);
        gBoard = new GameBoard(width, length);

        applyOverlap();



        //Testing inputs to verify correct permutation calculations
        ArrayList<Integer> test = new ArrayList<>();
        test.add(2);
        test.add(3);
        test.add(7);
        test.add(1);
        test.add(1);
        test.add(5);
        Group test1 = new Group(test, 30);
        System.out.println(Arrays.toString(test1.Overlap()));
        //test1.printPermutations();
    }

    /**
     * A method that takes the parsed contents of the CSV file and assigns the
     * values that will be the width and length of the gameboard as well as
     * create all the objects that will represent the possible solutions of
     * the rows and columns.
     * @param CSVContents
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
            gBoard.setGBoardRow(groups.get(i).Overlap(), i);
        //A loop for iterating through each Group object that represents
        //a column and assigning the output of the Overlap method to the
        //game board.
        for(int i=0; i<width; ++i)
            gBoard.setGBoardColumn(groups.get(i+length).Overlap(), i);
    }
}
