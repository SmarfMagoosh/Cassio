/**
 * The Board interface is used to play the game of Othello.
 * An implementation of Board represents the state of the game
 * board and players.
 * 
 * Written by Chris Tremonte, Noble Shore, Dominic Mazzoni,
 * and Jay Detweiler.
 *
 * <li>Each cell in the board can be empty, or it can have either a black
 *   or a white piece on it.  In the <tt>Board</tt> class, empty is
 *   represented by the number 0, black by 1, and white by 2.
 *   Preferably, you can use the constants <tt>EMPTY</tt>,
 *   <tt>BLACK</tt>, and <tt>WHITE</tt> that we have defined for you.
 *	<p>
 * <li>Cell locations on the Othello board are represented by their coordinates
 *   using an array of two integers.
 *   The methods
 *   <tt>getCell</tt>,
 *   <tt>isLegalMove</tt>, and
 *   <tt>makeMove</tt> all take cell coordinates as parameters.
 *   The first coordinate (<tt>location[0]</tt>) is the column,
 *   and the second coordinate (<tt>location[1]</tt>) 
 *   is the row, so (0,0) is the top-left and (7,7) is the bottom-right,
 *   like this:
 *  <p>
 *  <center>
 *  <table border=0>
 *  <tr>
 *  <td valign=top>(0,0)</td>
 *  <td rowspan=2><img src="http://www.armory.com/~iioa/othguide/faq/othrules1.gif">
 *  </td>
 *  <td valign=top>(7,0)</td>
 *  </tr>
 *  <tr>
 *  <td valign=bottom>(0,7)</td>
 *  <td valign=bottom>(7,7)</td>
 *  </tr>
 *  </table>
 *  </center>
 */

package othello;
public interface Board {

    /**
     * A constant value representing an empty cell
     */
    public static final int EMPTY = 0;
    /**
     * A constant value representing a black (player 1) cell
     */
    public static final int BLACK = 1;
    /**
     * A constant value representing a white (player 2) cell
     */
    public static final int WHITE = 2;

    /**
     * The number of rows and columns in the board
     */
    public static final int BOARD_DIM = 8;


    /**
     * initBoard initializes the board to the standard initial state
     * with two White pieces and two Black pieces and sets the
     * current player to Black.
     */
    public abstract void initBoard();

    /**
     * Constructs and return an identical Board object
     * including whose turn it is.
     * @return a new Board object
     */
    public abstract Board getClone();

    /**
     * Returns the contents of the given cell location.  This method
     * may throw an exception on an invalid location.
     * @param location an array containing column and row
     * @return EMPTY, BLACK or WHITE
     * @throws IllegalCellException if the location is not valid
     */
    public abstract int getCell(int[] location)
		throws IllegalCellException;

    /**
     * Count and return the number of cells in the Board
     * containing the given value.
     * @param cellType EMPTY, BLACK or WHITE
     * @return number of cells 
     */    
    public abstract int countCells(int cellType);

    /**
     * Return whether or not the location is a valid move 
     * for the current player.
     * @return true, if location is a valid move 
     */
    public abstract boolean isLegalMove(int[] location);

    /**
     * Update the state of the board by placing a piece of 
     * the current player in the given location.  This includes
     * flipping pieces according to the rules and updating the 
     * current player.  This method may throw an exception if
     * the given location is not a legal move.
     * @param location an array containing column and row
     */
    public void makeMove(int[] location)
		throws IllegalMoveException;

    /**
     * Return the player to move next or EMPTY if the game
     * is over.
     * @return EMPTY, BLACK or WHITE
     */
    public abstract int getPlayer();

    /**
     * Return the winner of the game or EMPTY if the game is 
     * not over yet.
     * @return EMPTY, BLACK or WHITE
     */
    public abstract int getWinner();

}
