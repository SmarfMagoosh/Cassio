package smarfmagoosh_mrcoffee;

import othello.AIPlayer;
import othello.Board;
import othello.IllegalCellException;
import othello.IllegalMoveException;
import java.util.Arrays;

public class MyPlayer extends AIPlayer {
    @Override
    public String getName() {
        return "Cassio";
    }

    @Override
    public void getNextMove(Board board, int[] bestMove) throws IllegalCellException, IllegalMoveException {
        System.out.println(Arrays.toString(bestMove));
    }

    // HEURISTIC
    @Override
    public double evaluate(Board board) {
        return board.countCells(Board.BLACK) - board.countCells(Board.WHITE);
    }

    @Override
    public double minimax(Board board, int depthLimit, boolean useAlphaBetaPruning, int[] bestMove, long[] numNodesExplored) throws InterruptedException {
        CassiosDomain bb = new CassiosDomain(board);
        return 0;
    }

    private double default_minimax(CassiosDomain bb, int depthLimit, int[] bestMove, long[] numNodesExplored) throws InterruptedException {
        return 0;
    }

    private double alpha_beta(CassiosDomain bb, int depthLimit, int[] bestMove, long[] numNodesExplored) throws InterruptedException {
        return 0;
    }
}
