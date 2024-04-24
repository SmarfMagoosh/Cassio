package othello;

import java.io.*;
import java.util.StringTokenizer;

/**
 * This is a player which prompts the user to type in the coordinates
 * of a move.  It is intended to be used by the Tournament class, but
 * it could also be used by OthelloGUI.
 */
public class HumanPlayer implements Player {

    public String getName () {
        return "Human";
    }

    // precondition: the board has at least one legal move available
    public void getNextMove(Board b, int [] bestMove)
    {
        BufferedReader stdin = new BufferedReader (new InputStreamReader(System.in));

        int x, y;

        System.out.print("Player "+b.getPlayer()+"'s move ");
        if (b.getPlayer()==b.BLACK)
            System.out.println("(x): ");
        else
            System.out.println("(o): ");

        try {
            do {
                x=-1;
                y=-1;

                while (x<0 || x>=Board.BOARD_DIM || y<0 || y>=Board.BOARD_DIM) {
                    String  inline = stdin.readLine();
                    StringTokenizer stok = new StringTokenizer( inline );

                    try {
                        if (stok.hasMoreTokens()) {
                            String  tok = stok.nextToken();
                            x = Integer.parseInt(tok);
                        }

                        if (stok.hasMoreTokens()) {
                            String  tok = stok.nextToken();
                            y = Integer.parseInt(tok);
                        }
                    } catch (Exception e) {
                    }

                    if (x<0 || x>=Board.BOARD_DIM || y<0 || y>=Board.BOARD_DIM) {
                        System.out.println("Please enter two numbers 0..7 "+
                                "separated by a space (column then row).");
                    }
                }

                bestMove[0]=x;
                bestMove[1]=y;

                if (!b.isLegalMove(bestMove))
                    System.out.println("Illegal move!");

            } while (!b.isLegalMove(bestMove));
        } catch (Exception e) {
        }
    }

}
