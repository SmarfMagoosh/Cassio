package smarfmagoosh_mrcoffee;

import othello.*;

import java.util.ArrayList;

public class MyPlayer extends AIPlayer {
    @Override
    public String getName() {
        return "Cassio";
    }

    @Override
    public void getNextMove(Board board, int[] bestMove) throws IllegalCellException, IllegalMoveException {
        long[] numNodesExplored = { 0L };
        try {
            minimax(board, 10, true, bestMove, numNodesExplored);
            System.out.println(numNodesExplored[0]);
        } catch (Exception ignore) {
        }
        System.out.println("" + bestMove[0] + " " + bestMove[1]);
    }

    // HEURISTIC
    @Override
    public double evaluate(Board board) {
        return board.countCells(Board.BLACK) - board.countCells(Board.WHITE);
    }

    @Override
    public double minimax(Board board, int depthLimit, boolean useAlphaBetaPruning, int[] bestMove,
            long[] numNodesExplored) throws InterruptedException {
        CassiosDomain bb = new CassiosDomain(board);
        // Board bb = board.getClone();
        double minimax_value;
        if (board.getPlayer() == Board.BLACK) {
            minimax_value = max_node(
                    bb,
                    depthLimit,
                    useAlphaBetaPruning,
                    0,
                    bestMove,
                    numNodesExplored,
                    Double.NEGATIVE_INFINITY,
                    Double.POSITIVE_INFINITY);
        } else {
            minimax_value = min_node(
                    bb,
                    depthLimit,
                    useAlphaBetaPruning,
                    0,
                    bestMove,
                    numNodesExplored,
                    Double.NEGATIVE_INFINITY,
                    Double.POSITIVE_INFINITY);
        }
        return minimax_value;
    }

    private double min_node(
            Board board,
            int depthLimit,
            boolean useAlphaBetaPruning,
            int depth,
            int[] bestMove,
            long[] numNodesExplores,
            double alpha,
            double beta) throws InterruptedException {
        numNodesExplores[0]++; // nuff said

        // stop if thread is over or terminal node hit
        if (depth == depthLimit) {
            return myEvaluate(board);
        } else if (Thread.interrupted()) {
            throw new InterruptedException();
        }

        // initialize v
        double value = Double.POSITIVE_INFINITY;

        // get successors and sort them by heuristic value
        ArrayList<Long> moves = board.getMoves();
        if (moves.size() == 0) {
            return myEvaluate(board);
        }
        // sortMoves(board, moves);
        // run that minimax baby
        for (long move : moves) {
            CassiosDomain successor = board.getClone();
            successor.makeMove(move);
            double value_cpy = value;
            double recurse = max_node(
                    successor,
                    depthLimit,
                    useAlphaBetaPruning,
                    depth + 1,
                    bestMove,
                    numNodesExplores,
                    alpha,
                    beta);
            value = Math.min(value, recurse);
            if (depth == 0 && value_cpy > value) {
                int[] location = board.location(move);
                bestMove[0] = location[0];
                bestMove[1] = location[1];
            }

            // we do a little beta pruning
            if (value <= alpha && useAlphaBetaPruning) {
                return value;
            }
            beta = Math.min(beta, value);
        }
        return value;
    }

    private double max_node(
            Board board,
            int depthLimit,
            boolean useAlphaBetaPruning,
            int depth,
            int[] bestMove,
            long[] numNodesExplores,
            double alpha,
            double beta) throws InterruptedException {
        numNodesExplores[0]++; // nuff said

        // stop if thread is over or terminal node hit
        if (depth == depthLimit) {
            return myEvaluate(board);
        } else if (Thread.interrupted()) {
            throw new InterruptedException();
        }

        // initialize v
        double value = Double.NEGATIVE_INFINITY;

        // get successors and sort them by heuristic value
        ArrayList<Long> moves = board.getMoves();
        if (moves.size() == 0) {
            return myEvaluate(board);
        }
        // run that minimax baby
        for (long move : moves) {
            CassiosDomain successor = board.getClone();
            successor.makeMove(move);
            double value_cpy = value;
            double recurse = min_node(
                    successor,
                    depthLimit,
                    useAlphaBetaPruning,
                    depth + 1,
                    bestMove,
                    numNodesExplores,
                    alpha,
                    beta);

            value = Math.max(value, recurse);
            if (depth == 0 && value_cpy < value) {
                int[] location = board.location(move);
                bestMove[0] = location[0];
                bestMove[1] = location[1];
            }

            // we do a little beta pruning
            if (value >= beta && useAlphaBetaPruning) {
                return value;
            }
            alpha = Math.max(alpha, value);
        }
        return value;
    }

    private ArrayList<int[]> getMoves(Board board) {
        ArrayList<int[]> moves = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                int[] move = { i, j };
                if (board.isLegalMove(move)) {
                    moves.add(move);
                }
            }
        }
        return moves;
    }

    private void sortMoves(Board board, ArrayList<int[]> moves) {
        moves.sort((m1, m2) -> {
            Board b1 = board.getClone();
            Board b2 = board.getClone();
            try {
                b1.makeMove(m1);
                b2.makeMove(m2);
            } catch (IllegalMoveException ignore) {
            }
            double diff = evaluate(b1) - evaluate(b2);
            int ret = diff > 0 ? 1 : -1;
            if (evaluate(b1) == 0) {
                return 0;
            }
            return board.getPlayer() == Board.BLACK ? ret : -ret;
        });
    }

    private static double myEvaluate(CassiosDomain bb) {
        return bb.countCells(1) - bb.countCells(2);
    }
}
