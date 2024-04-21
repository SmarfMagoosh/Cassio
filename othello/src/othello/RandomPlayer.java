package othello;

/**
 * A simple Othello player which makes a random (legal) move.
 *
 * Written by Dominic Mazzoni
 *
 * Use this as a guide to help you write your player.
 */
public class RandomPlayer implements Player {

    public String getName () {
        return "Mr. Random";
    }

    public void getNextMove(Board b, int [] bestMove)
            throws IllegalCellException, IllegalMoveException
    {

        // Use the array c[] to refer to bestMove[] for
        // shorthand.  Note that this doesn't copy the array,
        // it just makes a reference to it.

        int c[] = bestMove;

        // Loop through the whole board and count the number
        // of possible moves.

        int numLegalMoves = 0;
        for (c[0]=0; c[0]<8; c[0]++)
            for (c[1]=0; c[1]<8; c[1]++)
                if (b.isLegalMove(c))
                    numLegalMoves++;

        // Choose which move we're going to make out of the number
        // of possible moves.

        int randomMove = (int)(Math.random() * numLegalMoves);

        // Loop through the board again, counting down randomMove
        // every time we see a legal move.  When it hits zero,
        // bestMove[] will be set to the coordinates of this move,
        // so we can just return.

        for (c[0]=0; c[0]<8; c[0]++)
            for (c[1]=0; c[1]<8; c[1]++)
                if (b.isLegalMove(c)) {
                    if (randomMove == 0)
                        return;
                    else
                        randomMove--;
                }
    }

}
