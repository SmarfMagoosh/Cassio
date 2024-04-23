package smarfmagoosh_mrcoffee;

import othello.AIPlayer;
import othello.Board;
import othello.IllegalCellException;
import othello.IllegalMoveException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class MyPlayer extends AIPlayer{
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

    private double min_node(
            CassiosDomain board,
            int depthLimit,
            boolean useAlphaBetaPruning,
            int depth,
            int[] bestMove,
            long[] numNodesExplores,
            double alpha,
            double beta
    ) throws InterruptedException {
        if (depth == depthLimit) {
            return evaluate(board);
        } else if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        ArrayList<CassiosDomain> successors = getSuccessors(board);
        for (CassiosDomain successor : successors) {

        }
        return 0.0;
    }

    private double max_node(
            CassiosDomain board,
            int depthLimit,
            boolean useAlphaBetaPruning,
            int depth,
            int[] bestMove,
            long[] numNodesExplores,
            double alpha,
            double beta
    ) throws InterruptedException {
        if (depth == depthLimit) {
            return evaluate(board);
        } else if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        ArrayList<CassiosDomain> successors = getSuccessors(board);
        for (CassiosDomain successor : successors) {

        }
        return 0.0;
    }

    private ArrayList<CassiosDomain> getSuccessors(CassiosDomain board) {
        ArrayList<CassiosDomain> successors = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                int[] move = {i, j};
                try {
                    CassiosDomain successor = board.getClone();
                    successor.makeMove(move);
                    successors.add(successor);
                } catch(IllegalMoveException ignore) {}
            }
        }
        if (board.blacksMove) {
            successors.sort((b1, b2) -> {
                double b1e = this.evaluate(b1);
                double b2e = this.evaluate(b2);
                if (b1e < b2e) { return -1; }
                else if (b1e > b2e) { return 1; }
                else { return 0; }
            });
        } else {
            successors.sort((b1, b2) -> {
                double b1e = this.evaluate(b1);
                double b2e = this.evaluate(b2);
                if (b1e < b2e) { return 1; }
                else if (b1e > b2e) { return -1; }
                else { return 0; }
            });
        }
        return successors;
    }
}
