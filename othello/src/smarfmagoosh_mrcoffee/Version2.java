package smarfmagoosh_mrcoffee;

import othello.Board;

public class Version2 extends MyPlayer {
    private static final long CORNER_MASK = 0x8100000000000081L;

    public static final long X_MASK = 0x0042000000004200L;

    public static final long C_MASK = 0x4281000000008142L;

    @Override
    public String getName() {
        return super.getName() + " Overthrower";
    }

    @Override
    public int myEvaluate(CassiosDomain bb) {
        int remainingMoves = CassiosDomain.countOnes(~(bb.black | bb.white));
        int[] scores = {
                tokenScore(bb),
                cornerScore(bb),
                xScore(bb),
                cScore(bb),
                mobilityScore(bb),
                stabilityScore(bb)
        };
        if (remainingMoves > 17) {
            return tokenScore(bb);
        } else {
            return tokenScore(bb);
        }
    }

    @Override
    public void getNextMove(Board board, int[] bestMove) {
        int currentDepthLimit = 8;
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

    // want this to be high but shouldn't care as much until end game
    private int tokenScore(CassiosDomain bb) {
        return bb.countCells(Board.BLACK) - bb.countCells(Board.WHITE);
    }

    // want this to be high
    private int cornerScore(CassiosDomain bb) {
        return (
                CassiosDomain.countOnes(bb.black & CORNER_MASK) -
                CassiosDomain.countOnes(bb.white & CORNER_MASK)
        );
    }

    // want this to be low
    private int xScore(CassiosDomain bb) {
        return CassiosDomain.countOnes(bb.black & X_MASK) - CassiosDomain.countOnes(bb.white & X_MASK);
    }

    // want this to be low
    private int cScore(CassiosDomain bb) {
        return CassiosDomain.countOnes(bb.black & C_MASK) - CassiosDomain.countOnes(bb.white & C_MASK);
    }

    // want this to be high
    private int mobilityScore(CassiosDomain bb) {
        long whiteShift = bb.white;
        long blackShift = bb.black;
        for (int i = 0; i < 8; i++) {
            whiteShift |= CassiosDomain.shift(bb.white, i);
            blackShift |= CassiosDomain.shift(bb.black, i);
        }
        whiteShift &= ~(bb.black | bb.white);
        blackShift &= ~(bb.black | bb.white);
        return CassiosDomain.countOnes(blackShift) - CassiosDomain.countOnes(whiteShift);
    }

    // arguably most important one

    /**
     * A stable disk is one that cannot be flipped ever.
     * Corner tiles are stable by default but how to find out if a tile is stable
     * I think a tile is stable if two orthogonally adjacent cells are stable and the diagonal
     * between them are stable (for this definition, cells not on the board are also stable).
     * EX 1: corner squares
     * +---
     * |x
     * |
     * x is stable because the cell above it and to the left of it are stable as well as the cell to
     * the top left
     * @param bb
     * @return
     */
    private int stabilityScore(CassiosDomain bb) {
        return 0; // TODO: kill myself
    }
}
