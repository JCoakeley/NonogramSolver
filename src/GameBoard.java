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

    public int[] getGameBoardRow(int row)
    {
        return gBoard[row];
    }
}