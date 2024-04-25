package smarfmagoosh_mrcoffee;

import othello.Board;

public class Version1 extends MyPlayer {

    @Override
    public String getName() {
        return super.getName() + " Version 1";
    }

    @Override
    public int myEvaluate(CassiosDomain bb) {
        return CassiosDomain.countOnes(bb.black) - CassiosDomain.countOnes(bb.white);
    }

    @Override
    public void getNextMove(Board board, int[] bestMove) {
        int currentDepthLimit = 11;
        while (currentDepthLimit <= Math.max(STARTING_DEPTH, board.countCells(Board.EMPTY))) {
            System.out.println("searching with depth limit " + currentDepthLimit);
            long[] numNodesExplored = { 0L };
            try {
                long start = System.nanoTime();
                minimax(board, currentDepthLimit, true, bestMove, numNodesExplored);
                long finish = System.nanoTime();
                long timeElapsed = (finish - start) / 1_000_000;
                System.out.println("searched " + numNodesExplored[0] + " nodes in " + timeElapsed + "ms");
            } catch (InterruptedException ignore) {
                System.out.println("Brutally murdered");
                return;
            }
            currentDepthLimit++;
        }
    }
}
