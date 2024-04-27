/**
 * This is part of OthelloGUI.
 * You should not need to edit anything here.
 */

package othello;

public abstract class OthelloThread extends Thread {
	private Board board;
	private Player player1;
	private Player player2;
	private boolean paused = false;
	private long delay = 0;

	public OthelloThread (Board b, Player p, Player q, long d) {
		board = b;
		player1 = p;
		player2 = q;
		delay = d;
	}

	public void run() {
		int move[] = new int[2];
		move[0] = -1;    // initialize to an invalid move
		move[1] = -1;

		try {
			while (board.getPlayer() != Board.EMPTY) {

				try {
					sleep(delay);
					synchronized(this) {
						while (paused) {
							wait();
						}
					}
				} catch (Exception e) {
				}

				if (board.getPlayer() == Board.BLACK)
					MoveThread.timedMove(board.getClone(), player1, move, 4000);
				else if (board.getPlayer() == Board.WHITE)
					MoveThread.timedMove(board.getClone(), player2, move, 4000);
				else
					break;
				board.makeMove(move);
				madeMove();
			}
			gameOver();
		} catch (IllegalMoveException ime) {
			System.out.println ("Got IllegalMovelException in game");
			ime.printStackTrace();
			System.exit(-1);
		}
	}

	public synchronized void playGame () {
		if (isAlive()) {
			paused = false;
			notify();
		} else {
			start();
		}
	}

	public synchronized void pauseGame () {
		paused = true;
	}

	public abstract void madeMove ();
	public abstract void gameOver ();
}
