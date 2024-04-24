/**
 * The Player interface represents an object that can supply
 * moves to the game of Othello.
 */

package othello;

public interface Player {    
    /**
     * Return a name for this player
     * @return a String
     */
    public String getName();
    
    /**
     * Given a Board, return a move in the array bestMove.
     */
    public void getNextMove(Board board, int [] bestMove)
	throws IllegalCellException, IllegalMoveException;

}
