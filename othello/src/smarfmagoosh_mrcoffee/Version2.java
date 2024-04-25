package smarfmagoosh_mrcoffee;

import othello.Board;

public class Version2 extends MyPlayer {
    private static long CORNER_MASK = 0x8100000000000081L;

    @Override
    public String getName() {
        return super.getName() + " Overthrower";
    }

    @Override
    public int myEvaluate(CassiosDomain bb) {
        int remainingMoves = CassiosDomain.countOnes(~(bb.black | bb.white));
        if (remainingMoves > 15) {
            return cornerEval(bb) + cxScore(bb);
        } else {
            return bb.countCells(Board.BLACK) - bb.countCells(Board.WHITE);
        }
    }

    private int cornerEval(CassiosDomain bb) {
        return 10 * (
                CassiosDomain.countOnes(bb.black & CORNER_MASK) -
                CassiosDomain.countOnes(bb.white & CORNER_MASK)
        );
    }

    private int stableDiscs(CassiosDomain bb) {
        return 0; // TODO: kill myself
    }

    private int possibleMobility(CassiosDomain bb) {
        CassiosDomain mut = bb.getClone();
        long whiteShift = bb.white;
        long blackShift = bb.black;
        for (int i = 0; i < 8; i++) {
            whiteShift |= CassiosDomain.shift(bb.white, i);
            blackShift |= CassiosDomain.shift(bb.black, i);
        }
        whiteShift &= ~(bb.black | bb.white);
        blackShift &= ~(bb.black | bb.white);
        return 3 * (CassiosDomain.countOnes(blackShift) - CassiosDomain.countOnes(whiteShift));
    }

    private int cxScore(CassiosDomain bb) {
        int blackCScore = CassiosDomain.countOnes(bb.black & CassiosDomain.C_MASK);
        int whiteCScore = CassiosDomain.countOnes(bb.white & CassiosDomain.C_MASK);

        int blackXScore = CassiosDomain.countOnes(bb.black & CassiosDomain.X_MASK);
        int whiteXScore = CassiosDomain.countOnes(bb.white & CassiosDomain.X_MASK);

        return -1 * (blackCScore - whiteCScore) - 2 * (blackXScore - whiteXScore);
    }
}
