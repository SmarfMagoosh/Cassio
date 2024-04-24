/**
 * The AIPlayer interface represents an object that can supply
 * moves to the game of Othello and implements minimax with alpha-beta pruning
 */

package othello;

public abstract class AIPlayer implements Player {
    /**
     * Return a name for this player
     *
     * @return a String
     */
    public abstract String getName();

    /**
     * Given a Board, return a move in the array bestMove.
     */
    public abstract void getNextMove(Board board, int[] bestMove)
            throws IllegalCellException, IllegalMoveException;

    public abstract double evaluate(Board board);

    public abstract double minimax(Board board, final int depthLimit,
                                   final boolean useAlphaBetaPruning,
                                   int[] bestMove, long[] numNodesExplored) throws InterruptedException;

}
