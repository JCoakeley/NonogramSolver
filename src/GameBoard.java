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
        gBoard[row] = arr;
    }

    /**
     * A method for returning an int[] that represents the
     * specified column of gBoard.
     * @param column the column of gBoard to be returned
     * @return int[] specified column gBoard
     */
    public int[] getGBoardColumn(int column)
    {
        int[] output = new int[column];
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
            gBoard[i][column] = arr[i];
    }
}