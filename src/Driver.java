import java.util.ArrayList;

/**
 * Main driver class for solving Nonogram puzzles.
 * Handles file loading, initialization, and the main solving loop.
 * Tracks performance using the Timing class and prints the final board.
 */

public class Driver
{
    Group[] groups;
    ArrayList<int[]> fileContents = new ArrayList<>();
    int width, length;
    GameBoard gBoard;
    long rowSizeBits, columnSizeBits;

    /**
     * Starting point of the program, just calls the run method which
     * handles calling all the steps needed for the solver.
     *
     * @param args Command line arguments passed when program is run.
     */
    public static void main(String[] args)
    {
        new Driver().run();
    }

    /**
     * Method that runs the solver. Calls the necessary methods in sequence to
     * get the filename of the board size and clue, initializes of group objects,
     * perform solving of the board and print board when solved. Numerous timing
     * points are used in this method to track performance of the program.
     */
    public void run()
    {
        String fileName = Util.getFileName();
        Timing.timingStart(Timing.Timings.Total);

        Timing.timingStart(Timing.Timings.FileReading);
        fileContents = Util.readFile(fileName);
        Timing.timingEnd(Timing.Timings.FileReading);

        Timing.timingStart(Timing.Timings.Initialization);
        initializeGroupsFromFile(fileContents);
        Timing.timingEnd(Timing.Timings.Initialization);

        Timing.timingStart(Timing.Timings.Overlap);
        applyOverlap();
        Timing.timingEnd(Timing.Timings.Overlap);

        Timing.timingStart(Timing.Timings.SolvingLoop);
        while (!gBoard.isSolved())
        {
            updateGameBoard();
            Timing.addIteration();
        }
        Timing.timingEnd(Timing.Timings.SolvingLoop);

        printGameBoard();
        Timing.timingEnd(Timing.Timings.Total);
        Timing.saveTimings();
        GenerationStatWriter.writeToFile();
    }

    /**
     * Assigns file content to initialize the game board and groups (rows and columns).
     *
     * @param fileContents The list of clue lines, with the first entry representing
     *                     the grid size.
     */
    private void initializeGroupsFromFile(ArrayList<int[]> fileContents)
    {
        int[] gridSize = fileContents.getFirst();

        width = gridSize[0];
        length = gridSize[1];

        rowSizeBits = (long)Math.pow(2,width);
        columnSizeBits = (long)Math.pow(2, length);

        gBoard = new GameBoard(width, length);
        groups = new Group[width + length];

        int[] rowClues;
        int[] columnClues;

        // Creating group objects for all the rows.
        for (int i = 0; i < length; i++)
        {
            rowClues = fileContents.get(i + 1);
            groups[i] = new Group(rowClues, width, i, rowSizeBits);
        }

        // Creating group objects for all the columns.
        for (int i = length; i < groups.length; i++)
        {
            columnClues = fileContents.get(i + 1);
            groups[i] = new Group(columnClues, length, i, columnSizeBits);
        }
    }

    /**
     * Applies overlap logic to all row and column groups. Uses the group's
     * overlap method to make early cell deductions without generating permutations.
     */
    private void applyOverlap()
    {
        int[] rowOverlap;
        int[] columnOverlap;

        // Checking for any basic overlap on each row and setting those
        // solved cells on the game board.
        for (int i = 0; i < length; i++)
        {
            rowOverlap = groups[i].overlap();
            gBoard.setGBoardRow(rowOverlap, i);
        }

        // Checking for any basic overlap on each column and setting those
        // solved cells on the game board.
        for (int i = 0; i < width; i++)
        {
            columnOverlap = groups[i + length].overlap();
            gBoard.setGBoardColumn(columnOverlap, i);
        }
    }

    /**
     * Performs one full iteration of the solving loop. Updates rows and columns
     * that have new solved cells since their last update. If no rows or columns
     * are marked for update, a low-cost group is forced to generate permutations.
     */
    private void updateGameBoard()
    {
        boolean rowsUnchanged = true;
        boolean columnsUnchanged = true;

        int[] rowPartialSolution;
        int[] rowUpdatedSolution;
        int[] columnPartialSolution;
        int[] columnUpdatedSolution;

        // Checking if there is at least one row marked to be updated.
        for (int element : gBoard.getRowsToUpdate())
            if(element == 1)
            {
                rowsUnchanged = false;
                break;
            }

        // If no rows marked to update, find the lowest-cost row that doesn't
        // have permutations generated and force permutation generation.
        if (rowsUnchanged)
        {
            Group row = lowCostGroup(0, length);
            if (row != null)
            {
                rowPartialSolution = gBoard.getGBoardRow(row.getGroupId());
                rowUpdatedSolution = row.forceGeneration(rowPartialSolution);
                gBoard.setGBoardRow(rowUpdatedSolution, row.getGroupId());
            }
        }
        else
            // Performs an update on any row that is marked to be updated. The updateGroup()
            // from the Group class determined what the update will entail.
            for (int i = 0; i < length; i++)
            {
                if (gBoard.getRowsToUpdate()[i] == 1)
                {
                    rowPartialSolution = gBoard.getGBoardRow(i);
                    rowUpdatedSolution = groups[i].updateGroup(rowPartialSolution);
                    gBoard.setGBoardRow(rowUpdatedSolution, i);
                }
            }
        // Clearing the marked rows for update, new any column updates will prompt an update
        // to the corresponding intersecting row.
        gBoard.resetRowsToUpdate();

        // Checking if there is at least one column marked to be updated.
        for (int element : gBoard.getColumnsToUpdate())
            if (element == 1)
            {
                columnsUnchanged = false;
                break;
            }

        // If no columns marked to update, find the lowest-cost column that doesn't
        // have permutations generated and force permutation generation.
        if (columnsUnchanged)
        {
            Group column = lowCostGroup(length, width + length);
            if (column != null)
            {
                columnPartialSolution = gBoard.getGBoardColumn(column.getGroupId() - length, length);
                columnUpdatedSolution = column.forceGeneration(columnPartialSolution);
                gBoard.setGBoardColumn(columnUpdatedSolution, column.getGroupId() - length);
            }
        }
        else
            // Performs an update on any column that is marked to be updated. The updateGroup()
            // from the Group class determined what the update will entail.
            for (int i = 0; i < width; i++)
            {
                if (gBoard.getColumnsToUpdate()[i] == 1)
                {
                    columnPartialSolution = gBoard.getGBoardColumn(i, length);
                    columnUpdatedSolution = groups[i + length].updateGroup(columnPartialSolution);
                    gBoard.setGBoardColumn(columnUpdatedSolution, i);
                }
            }
        // Clearing the marked columns for update, new any row updates will prompt an update
        // to the corresponding intersecting column.
        gBoard.resetColumnsToUpdate();
    }

    /**
     * Selects the group (row or column) with the lowest maximum permutation count
     * that hasn't yet generated its permutations. The start and end indexes are
     * used to search through only rows or columns.
     *
     * @param startIndex The starting index in the group array (inclusive)
     * @param endIndex The ending index in the group array (exclusive)
     * @return The lowest-cost group to generate next, or null if all are generated.
     */
    private Group lowCostGroup(int startIndex, int endIndex)
    {
        Group bestGroup = null;
        Group currentGroup;
        long currentMax;
        long lowestMax = 0L;

        // Checking the maximum permutations of each row or each column and only those
        // with no permutations generated.
        for (int i = startIndex; i < endIndex; i++)
            if (!groups[i].getPermutationsGenerated())
            {
                currentGroup = groups[i];
                currentMax = currentGroup.getMaxPermutationCount();

                if (currentMax < lowestMax || lowestMax == 0)
                {
                    lowestMax = currentMax;
                    bestGroup = currentGroup;
                }
            }

        return bestGroup;
    }

    /**
     * A method for printing out the game board to the console. Will print a ■ for
     * filled square and an x represents a blank square.
     */
    private void printGameBoard()
    {
        char printChar;

        // Looping through each row of the game board.
        for (int i = 0; i < length; i++)
        {
            // Printing horizontal dividing line, breaking game board into 5x5 squares.
            if (i % 5 == 0)
                System.out.println(" " + "-".repeat(width * 2 + (width/5) * 2 + 1));

            // Looping through each cell of a row on the game board.
            for (int j = 0; j < gBoard.getGBoardRow(i).length; j++)
            {
                // Printing vertical dividing line, breaking game board into 5x5 squares.
                if (j % 5 == 0)
                    System.out.print(" |");

                if (gBoard.getGBoardRow(i)[j] == 1)
                    printChar = '■';
                else if (gBoard.getGBoardRow(i)[j] == 0)
                    printChar = ' ';
                else
                    printChar = 'X';

                System.out.print(" " + printChar);
            }
            System.out.println(" |");
        }
        System.out.println(" " + "-".repeat(width * 2 + (width/5) * 2 + 1));
    }
}