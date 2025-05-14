import java.util.Arrays;

public class GameBoard {

    private final int[][] gBoard;
    private final int[] rowsToUpdate;
    private final int[] columnsToUpdate;


    /**
     * A constructor for a game board of a specified length and width.
     * @param width of the game board
     * @param length of the game board
     */
    public GameBoard(int width, int length)
    {
        gBoard = new int[length][width];
        rowsToUpdate = new int[length];
        columnsToUpdate = new int[width];
    }

    /**
     * A method for returning the int[] of a row of gBoard
     * @param row the row of gBoard to be returned
     * @return int[] specified row of gBoard
     */
    public int[] getGBoardRow(int row)
    {
        return gBoard[row];
    }

    /**
     * A method for setting the values of the int[] of
     * a specified row of gBoard.
     * @param arr the int[] of values to set for the specified row
     * @param row the number of row to be set
     */
    public void setGBoardRow(int[] arr, int row)
    {
        for (int i = 0; i < arr.length; i++)
            if (arr[i] != 0 && arr[i] != gBoard[row][i])
            {
                gBoard[row][i] = arr[i];
                columnsToUpdate[i] = 1;
            }
    }

    /**
     * A method for returning an int[] that represents the
     * specified column of gBoard.
     * @param column the column of gBoard to be returned
     * @return int[] specified column gBoard
     */
    public int[] getGBoardColumn(int column, int length)
    {
        int[] output = new int[length];

        for (int i = 0; i < output.length; i++)
            output[i] = gBoard[i][column];

        return output;
    }

    /**
     * A method for setting the values of the int[] that
     * represents a specified column of gBoard.
     * @param arr the int[] of values to set for the specified column
     * @param column the number of column to be set
     */
    public void setGBoardColumn(int[] arr, int column)
    {
        for (int i = 0; i < arr.length; i++)
            if (arr[i] != 0 && arr[i] != gBoard[i][column])
            {
                gBoard[i][column] = arr[i];
                rowsToUpdate[i] = 1;
            }
    }

    /**
     * A method for determining if the game board is in a solved state.
     * The game board is considered in a solved state if there are no 0s
     * contained in the 2D array gBoard.
     * @return boolean that states if the game board is in a solved state.
     */
    public boolean isSolved()
    {
        //A loop for iterating through the array of arrays in gBoard and
        //checking each element if they are 0. Returns false on the first
        //0 found, if no 0s found gBoard is solved and return true.
        for (int[] row : gBoard)
            for (int cell : row)
                if(cell == 0)
                    return false;

        return true;
    }

    public int[] getRowsToUpdate()
    {
        return rowsToUpdate;
    }

    public void resetRowsToUpdate()
    {
        Arrays.fill(rowsToUpdate, 0);
    }

    public int[] getColumnsToUpdate()
    {
        return columnsToUpdate;
    }

    public void resetColumnsToUpdate()
    {
        Arrays.fill(columnsToUpdate, 0);
    }
}