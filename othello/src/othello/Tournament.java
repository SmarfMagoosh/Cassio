
package othello;

import java.io.FileWriter;
import java.io.*;
import java.util.Vector;

/**
 * This class simulates a tournament game of Othello, given an Othello
 * Board class and two Players.  Each player is limited to 4 seconds
 * per move and if one player goes over without having found a valid move,
 * the other player wins.
 */
class Tournament {

	public final long moveLimit; // in milliseconds

	public Tournament(long moveTimeLimit){
		moveLimit = moveTimeLimit;
	}

	public static void main(String[] args) throws Exception
	{

		final int returnCode_ERROR_PLAYER_CLASSES = -2;
		final int returnCode_ERROR_ARGS = -1;
		final int returnCode_DRAW = 0;
		final int returnCode_BLACK = 1;
		final int returnCode_WHITE = 2;

		if (args.length != 3) {
			System.err.println("Usage: java othello.Tournament "+
					"<mode: 0 for untimed, 1 for timed> \n" +
					"<player1class> <player2class> \n");
			System.exit(returnCode_ERROR_ARGS);
		}

		int argIndex = 0;

		int modeCode = Integer.parseInt(args[argIndex++]);
		String blackPlayerClass = args[argIndex++];
		String whitePlayerClass = args[argIndex++];

		int verbosity = 15;

		if(argIndex != args.length){
			System.err.println("Error in command line argument parsing");
			System.exit(returnCode_ERROR_ARGS);
		}


		Player player1 = Misc.getPlayerInstance(blackPlayerClass);
		Player player2 = Misc.getPlayerInstance(whitePlayerClass);

		if(player1 == null || player2 == null){
			System.exit(returnCode_ERROR_PLAYER_CLASSES);
		}

		Tournament tournament;

		if(modeCode != 0){
			tournament = new Tournament(4000); // 4 second limit
		}
		else{
			tournament = new Tournament(Long.MAX_VALUE);
		}

		Board board = new othello.BoardImplementation();
		board.initBoard();

		Vector<String> boardStrings = new Vector<>(100);
		boardStrings.add(BoardImplementation.getBoardString(board));

		if(verbosity > 1){
			System.out.println(player1.getName() + " vs. " + player2.getName());
			System.out.println("(" + blackPlayerClass + ") vs. ("
					+ whitePlayerClass + ")");
			System.out.println("x is Black and o is White");
		}

		if(verbosity > 10){
			Misc.printBoard(board);
		}

		while (board.getPlayer() != Board.EMPTY) {

			int[] move = new int[2];
			move[0] = -1;    // initialize to an invalid move
			move[1] = -1;

			if (board.getPlayer() == Board.BLACK)
				MoveThread.timedMove(board.getClone(), player1, move, tournament.moveLimit);
			else if (board.getPlayer() == Board.WHITE)
				MoveThread.timedMove(board.getClone(), player2, move, tournament.moveLimit);
			else
				break;

			if ((move[0]==-1 && move[1]==-1) // timed out
					|| !board.isLegalMove(move)) { // tried an illegal move

				if (board.getPlayer() == Board.BLACK){
					System.err.println("Black player (" + blackPlayerClass + ") timed out or tried an invalid move.");
					System.exit(returnCode_WHITE);
				}
				else{
					System.err.println("White player (" + whitePlayerClass + ") timed out or tried an invalid move.");
					System.exit(returnCode_BLACK);
				}
				break;
			}

			final int thePlayer = board.getPlayer();

			board.makeMove(move);
			boardStrings.add(BoardImplementation.getBoardString(board));

			if(verbosity > 10){
				System.out.println("Player "+ thePlayer +
						" makes move: "+move[0]+", "+move[1]);

				Misc.printBoard(board);

				if(verbosity > 80){
					System.out.println("Press any key to continue... ");
					BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
					String input = in.readLine();
				}
			}


		} // while there is a next player

		int winner = board.getWinner();
		int gameValue = board.countCells(Board.BLACK) -
				board.countCells(Board.WHITE);

		System.out.print("Game finished in " + (boardStrings.size()-1) + " moves\n");

		if(verbosity > 1){
			System.out.println("Final Score: black " + board.countCells(Board.BLACK)+
					", white "+ board.countCells(Board.WHITE));
		}

		if(winner == Board.WHITE){
			System.out.println("Winner: " + whitePlayerClass);
			System.exit(returnCode_WHITE);
		}
		else if (winner == Board.BLACK){
			System.out.println("Winner: " + blackPlayerClass);
			System.exit(returnCode_BLACK);
		}
		else{
			System.out.println("Tie game");
			System.exit(returnCode_DRAW);
		}

	}

}
