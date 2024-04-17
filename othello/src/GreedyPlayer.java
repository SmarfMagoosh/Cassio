/**
 * An Othello player which always makes the move which gets
 * it the most pieces.
 *
 * Written by Dominic Mazzoni
 */
public class GreedyPlayer implements Player {

    public String getName () {
        return "Mr. Greedy";
    }

    public void getNextMove(Board b, int [] bestMove)
            throws IllegalCellException, IllegalMoveException
    {
        // Let c be an array of size two to store the
        // coordinates of the move we're considering

        int c[] = new int[2];

        // Loop through the whole board and find the move which
        // gets the most pieces
        // of possible moves.

        int myColor = b.getPlayer();
        int bestNumPieces = -1;

        for (c[0]=0; c[0]<Board.BOARD_DIM; c[0]++) {
            for (c[1]=0; c[1]<Board.BOARD_DIM; c[1]++) {
                if (b.isLegalMove(c)) {

                    // To figure out what would happen if we made
                    // this move, we clone the board and then call
                    // makeMove, and that way we don't affect the
                    // original board.

                    Board newBoard = b.getClone();
                    newBoard.makeMove(c);
                    int numPieces = newBoard.countCells(myColor);

                    // If this is better than any move we've seen
                    // so far, store this as our best move.

                    if (numPieces > bestNumPieces) {
                        bestNumPieces = numPieces;
                        bestMove[0] = c[0];
                        bestMove[1] = c[1];
                    }

                }
            }
        }

        // At the end of this loop, if we had any legal moves,
        // the greediest one will be in bestMove[], and if we
        // didn't have any legal moves, it doesn't matter what
        // we return.

    }
}


