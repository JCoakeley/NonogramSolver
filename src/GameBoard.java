import java.util.Arrays;

public class GameBoard {

    public int[][] gBoard;

    /**
     * A constructor for a game board of a specified length and width.
     * @param width of the game board
     * @param length of the game board
     */
    public GameBoard(int width, int length)
    {
        gBoard = new int[length][width];
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
        for(int i=0; i< arr.length; ++i)
        {
            if(arr[i] != 0)
                gBoard[row][i] = arr [i];
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
        for(int i=0; i<output.length; ++i)
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
        for(int i=0; i<arr.length; ++i)
            if (arr[i] != 0)
                gBoard[i][column] = arr[i];
    }

    /**
     * A method for determining if the game board is in a solved state.
     * The game board is considered in a solved state if there are no 0s
     * contained in the 2D array gBoard.
     * @return boolean that states if the game board is in a solved state.
     */
    public boolean isSolved()
    {
        boolean solvedState = true;

        //A loop for iterating through the array of arrays in gBoard and
        //checking if the int[] have a 0 in them.
        for(int[] rows:gBoard)
        {
            if(Arrays.binarySearch(rows, 0)>=0)
            {
                solvedState = false;
                break;
            }
        }
        return solvedState;
    }
}