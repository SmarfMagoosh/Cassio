package smarfmagoosh_mrcoffee;

import othello.Board;

import java.util.HashMap;
import java.util.Map;

public class Version2 extends MyPlayer {
    public static final long CORNER_MASK = 0x8100000000000081L;

    public final Map<Long, Integer> combos = new HashMap<>();

    public final int depthLimit = 11;

    public static int[][] positionWeights = {
            {500, -10, 11, 6, 6, 11, -10, 500},
            {-10, -20,  1, 2, 2,  1, -20, -10},
            { 10,   1,  5, 4, 4,  5,   1,  10},
            {  6,   2,  4, 2, 2,  4,   2,   6},
            {  6,   2,  4, 2, 2,  4,   2,   6},
            { 10,   1,  5, 4, 4,  5,   1,  10},
            {-10, -20,  1, 2, 2,  1, -20, -10},
            {500, -10, 11, 6, 6, 11, -10, 500}
    };

    public static final long[] CORNERS = {
            0x01L,
            0x80L,
            0x0100000000000000L,
            0x8000000000000000L,
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
        return super.getName() + " Version 2";
    }

    @Override
    public int myEvaluate(CassiosDomain bb) {
        int remainingMoves = CassiosDomain.countOnes(~(bb.black | bb.white));

        if (remainingMoves > depthLimit) {
            return positionScore(bb) + tokenScore(bb) + mobilityScore(bb);
        } else {
            return tokenScore(bb);
        }
    }

    @Override
    public void getNextMove(Board board, int[] bestMove) {
        long[] numNodesExplored = { 0L };
        try {
            minimax(board, depthLimit, true, bestMove, numNodesExplored);
        } catch (InterruptedException ignore) {
            return;
        }
    }

    private int tokenScore(CassiosDomain bb) {
        return bb.countCells(Board.BLACK) - bb.countCells(Board.WHITE);
    }

    private int positionScore(CassiosDomain bb) {
        int blackScore = 0;
        int whiteScore = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                long cell = CassiosDomain.cell(i, j);
                if ((bb.black & cell) != 0) {
                    blackScore += positionWeights[j][i];
                } else if ((bb.white & cell) != 0) {
                    whiteScore += positionWeights[j][i];
                }
            }
        }
        return blackScore - whiteScore;
    }

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

    private int stabilityScore(CassiosDomain bb) {
        if (((bb.white | bb.black) & CORNER_MASK) == 0) {
            return 0;
        } else {
            long stables = boardStability(bb.black, bb.white);
            return CassiosDomain.countOnes(bb.black & stables) - CassiosDomain.countOnes(bb.white & stables);
        }
    }

    private static long boardStability(long black, long white) {
        long stables = (black | white) & CORNER_MASK;
        if (stables == 0) {
            return stables;
        }
        long corner = CORNERS[0];
        if ((corner & black) != 0) {
            int spread = 0;
            while((corner & black) != 0) {
                stables |= corner;
                corner <<= 1;
                spread++;
            }
            corner = CORNERS[0];
            while((corner & black) != 0) {
                corner <<= 8;
                long shifter = corner;
                for (int i = 0; i < spread; i++) {
                    if ((shifter & black) != 0) {
                        stables |= shifter;
                        shifter <<= 1;
                    } else {
                        spread = i;
                    }
                }
            }
        } else if ((corner & white) != 0) {
            int spread = 0;
            while((corner & white) != 0) {
                stables |= corner;
                corner <<= 1;
                spread++;
            }
            corner = CORNERS[0];
            while((corner & white) != 0) {
                corner <<= 8;
                long shifter = corner;
                for (int i = 0; i < spread; i++) {
                    if ((shifter & white) != 0) {
                        stables |= shifter;
                        shifter <<= 1;
                    } else {
                        spread = i;
                    }
                }
            }
        }
        return stables;
    }

    public static long bottomRightStability(long bb) {
        long stables = 0;
        int numStable = Integer.MAX_VALUE;
        for (int i = 0; i < 8; i++) {
            long rowMask = ((0xFFL << (8 * i)) & (~bb)) >>> (8 * i);
            if (rowMask != 0) {
                numStable = Math.min(Long.numberOfTrailingZeros(rowMask), numStable - 1);
                stables |= ((1L << numStable) - 1) << (8 * i);
            } else if (numStable >= 8){
                stables |= 0xFFL << (8 * i);
            }
            if (numStable == 0) {
                break;
            }
        }
        return stables;
    }

    public static long topRightStability(long bb) {
        long stables = 0;
        int numStable = Integer.MAX_VALUE;
        for (int i = 7; i >= 0; i--) {
            long rowMask = ((0xFFL << (8 * i)) & (~bb)) >>> (8 * i);
            if (rowMask != 0) {
                numStable = Math.min(Long.numberOfTrailingZeros(rowMask), numStable - 1);
                stables |= ((1L << numStable) - 1) << (8 * i);
            } else if (numStable >= 8){
                stables |= 0xFFL << (8 * i);
            }
            if (numStable == 0) {
                break;
            }
        }
        return stables;
    }

    public static long bottomLeftStability(long bb) {
        long stables = 0;
        int numStable = Integer.MAX_VALUE;
        for (int i = 0; i < 8; i++) {
            long rowMask = ((0xFFL << (8 * i)) & (~bb)) >>> (8 * i);
            if (rowMask != 0) {
                numStable = Math.min(Long.numberOfLeadingZeros(rowMask)-56, numStable - 1);
                stables |= ((0xFFL << (8-numStable)) & 0xFFL) << (8 * i);
            } else if (numStable >= 8){
                stables |= 0xFFL << (8 * i);
            }
            if (numStable == 0) {
                break;
            }
        }
        return stables;
    }

    public static long topLeftStability(long bb) {
        long stables = 0;
        int numStable = Integer.MAX_VALUE;
        for (int i = 7; i >= 0; i--) {
            long rowMask = ((0xFFL << (8 * i)) & (~bb)) >>> (8 * i);
            if (rowMask != 0) {
                numStable = Math.min(Long.numberOfLeadingZeros(rowMask)-56, numStable - 1);
                stables |= ((0xFFL << (8-numStable)) & 0xFFL) << (8 * i);
            } else if (numStable >= 8){
                stables |= 0xFFL << (8 * i);
            }
            if (numStable == 0) {
                break;
            }
        }
        return stables;
    }

    // average distance score
}
