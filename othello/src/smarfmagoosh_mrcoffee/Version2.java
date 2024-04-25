package smarfmagoosh_mrcoffee;

import othello.Board;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Version2 extends MyPlayer {
    public static final long CORNER_MASK = 0x8100000000000081L;

    public static final long X_MASK = 0x0042000000004200L;

    public static final long C_MASK = 0x4281000000008142L;

    public final Map<Long, Integer> combos = new HashMap<>();

    public final int depthLimit = 10;

    public int[][] blackPositionWeights = {
            {500, -10, 11, 6, 6, 11, -10, 500},
            {-10, -20,  1, 2, 2,  1, -20, -10},
            { 10,   1,  5, 4, 4,  5,   1,  10},
            {  6,   2,  4, 2, 2,  4,   2,   6},
            {  6,   2,  4, 2, 2,  4,   2,   6},
            { 10,   1,  5, 4, 4,  5,   1,  10},
            {-10, -20,  1, 2, 2,  1, -20, -10},
            {500, -10, 11, 6, 6, 11, -10, 500}
    };

    public int[][] whitePositionWeights = {
            {500, -10, 11, 6, 6, 11, -10, 500},
            {-10, -20,  1, 2, 2,  1, -20, -10},
            { 10,   1,  5, 4, 4,  5,   1,  10},
            {  6,   2,  4, 2, 2,  4,   2,   6},
            {  6,   2,  4, 2, 2,  4,   2,   6},
            { 10,   1,  5, 4, 4,  5,   1,  10},
            {-10, -20,  1, 2, 2,  1, -20, -10},
            {500, -10, 11, 6, 6, 11, -10, 500}
    };

    public Version2() {
        super();
        long[] corners = {
                0x0000000000000001L,
                0x0000000000000080L,
                0x0100000000000000L,
                0x8000000000000000L,
        };
        long[] xs = {
            0x0040000000000000L,
            0x0002000000000000L,
            0x0000000000000200L,
            0x0000000000004000L
        };
        for (int i = 0; i < 16; i++) {
            long cornerMask = 0L;
            long xMask = 0L;
            char[] hasCorners = String.format("%4s", Integer.toBinaryString(i)).toCharArray();
            for (int j = 0; j < hasCorners.length; j++) {
                if (hasCorners[j] == '1') {
                    cornerMask += corners[j];
                    xMask += xs[j];
                }
            }
            combos.put(xMask, CassiosDomain.countOnes(xMask));
            combos.put(cornerMask, CassiosDomain.countOnes(cornerMask));
        }

        long[] cs = {
                0x4000000000000000L,
                0x0200000000000000L,
                0x0080000000000000L,
                0x0001000000000000L,
                0x0000000000008000L,
                0x0000000000000100L,
                0x0000000000000040L,
                0x0000000000000002L
        };
        for (int i = 0; i < 256; i++) {
            long cMask = 0L;
            char[] hasCorners = String.format("%8s", Integer.toBinaryString(i)).toCharArray();
            for (int j = 0; j < hasCorners.length; j++) {
                if (hasCorners[j] == '1') {
                    cMask += cs[j];
                }
            }
            combos.put(cMask, CassiosDomain.countOnes(cMask));
        }
    }

    @Override
    public String getName() {
        return super.getName() + " Overthrower Overthrower";
    }

    @Override
    public int myEvaluate(CassiosDomain bb) {
        int remainingMoves = CassiosDomain.countOnes(~(bb.black | bb.white));
        boolean cornerClaimed = bb.blacksMove ? (bb.black & CORNER_MASK) != 0 : (bb.white & CORNER_MASK) != 0;

        if (remainingMoves > depthLimit) {
            return positionScore(bb) + tokenScore(bb) + mobilityScore(bb);
        } else {
            return tokenScore(bb);
        }
    }

    @Override
    public void getNextMove(Board board, int[] bestMove) {
        //while (currentDepthLimit <= Math.max(STARTING_DEPTH, board.countCells(Board.EMPTY))) {
            long[] numNodesExplored = { 0L };
            try {
                minimax(board, depthLimit, true, bestMove, numNodesExplored);
            } catch (InterruptedException ignore) {
                return;
            }
        //}
    }

    // want this to be high but shouldn't care as much until end game
    private int tokenScore(CassiosDomain bb) {
        return bb.countCells(Board.BLACK) - bb.countCells(Board.WHITE);
    }

    private int positionScore(CassiosDomain bb) { // TODO: optimize perhaps
        int blackScore = 0;
        int whiteScore = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                long cell = CassiosDomain.cell(i, j);
                if ((bb.black & cell) != 0) {
                    blackScore += blackPositionWeights[j][i];
                } else if ((bb.white & cell) != 0) {
                    whiteScore += whitePositionWeights[j][i];
                }
            }
        }
        return blackScore - whiteScore;
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
        return CassiosDomain.countOnes(whiteShift) - CassiosDomain.countOnes(blackShift);
    }

    // average distance score

    // stability score


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
