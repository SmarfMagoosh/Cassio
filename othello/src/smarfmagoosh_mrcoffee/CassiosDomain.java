package smarfmagoosh_mrcoffee;

import othello.Board;
import othello.IllegalCellException;
import othello.IllegalMoveException;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static smarfmagoosh_mrcoffee.Main.printBoard;

public class CassiosDomain implements Board {
    private static final long eastMask = 0xFE_FE_FE_FE_FE_FE_FE_FEL;
    private static final long westMask = 0x7F_7F_7F_7F_7F_7F_7F_7FL;

    private long black;
    private long white;
    private boolean blacksMove;

    private CassiosDomain(CassiosDomain b) {
        black = b.black;
        white = b.white;
        blacksMove = b.blacksMove;
    }

    public CassiosDomain() {
        initBoard();
    }

    // ABSTRACT METHODS MUST BE IMPLEMENTED
    @Override
    public void initBoard() {
        black = 0x00_00_00_08_10_00_00_00L;
        white = 0x00_00_00_10_08_00_00_00L;
        blacksMove = true;
    }

    @Override
    public Board getClone() {
        return new CassiosDomain(this);
    }

    @Override
    public int getCell(int[] location) throws IllegalCellException {
        final int col = location[0], row = location[1];
        final long mask = cell(location);
        if (!inBounds(location)) {
            throw new IllegalCellException();
        }

        if ((white & mask) != 0) {
            return Board.WHITE;
        } else if ((black & mask) != 0) {
            return Board.BLACK;
        } else {
            return Board.EMPTY;
        }
    }

    @Override
    public int countCells(int cellType) {
        if (cellType == Board.EMPTY) {
            return 64 - countCells(Board.BLACK) - countCells(Board.WHITE);
        } else {
            long tracker = cellType == Board.BLACK ? black : white;
            int count = 0;
            while (tracker > 0) {
                if (tracker % 2 == 1) {
                    count++;
                }
                tracker >>>= 1;
            }
            return count;
        }
    }

    @Override
    public boolean isLegalMove(int[] location) {
        final int col = location[0], row = location[1];
        // false if coordinates are out of bounds
        if (!inBounds(location)) { return false; }

        long mask = cell(location);
        long board = blacksMove ? black : white;

        // false if the square is filled already
        if ((board & mask) != 0) { return false; }

        List<Function<Long, Long>> directions = Arrays.asList(
                CassiosDomain::shiftN,
                CassiosDomain::shiftNE,
                CassiosDomain::shiftE,
                CassiosDomain::shiftSE,
                CassiosDomain::shiftS,
                CassiosDomain::shiftSW,
                CassiosDomain::shiftW,
                CassiosDomain::shiftNW
        );

        for (Function<Long, Long> direction : directions) {
            long shift = direction.apply(mask);
        }

        long move = board | mask;
        return false; // TODO: implement
    }

    @Override
    public void makeMove(int[] location) throws IllegalMoveException {

        blacksMove = !blacksMove;
    }

    @Override
    public int getPlayer() {
        return blacksMove ? Board.BLACK : Board.WHITE;
    }

    @Override
    public int getWinner() {
        int blackCnt = countCells(Board.BLACK);
        int whiteCnt = countCells(Board.WHITE);
        if (blackCnt > whiteCnt) {
            return Board.BLACK;
        } else if (whiteCnt > blackCnt) {
            return Board.WHITE;
        } else {
            return Board.EMPTY;
        }
    }

    // BITBOARD SHIFTING
    private static long shiftN(long bb) {
        return bb << 8;
    }

    private static long shiftS(long bb) {
        return bb >>> 8;
    }
    
    private static long shiftW(long bb) {
        return (bb & westMask) << 1;
    }

    private static long shiftE(long bb) {
        return (bb & eastMask) >>> 1;
    }

    private static long shiftNW(long bb) {
        return shiftN(shiftW(bb));
    }

    private static long shiftNE(long bb) {
        return shiftN(shiftE(bb));
    }

    private static long shiftSW(long bb) {
        return shiftS(shiftW(bb));
    }

    private static long shiftSE(long bb) {
        return shiftS(shiftE(bb));
    }

    // MORE MASKS
    private static long row(int[] location) {
        return 0xFFL << (location[1] * 8);
    }

    private static long col(int[] location) {
        return 0x01_01_01_01_01_01_01_01L << location[0];
    }

    public static long diagNE(int[] location) {
        long cell = cell(location);
        long mask = cell;
        while (inBounds(shiftNE(location))) {
            location = shiftNE(location);
            cell = shiftNE(cell);
            mask |= cell;
        }
        while (inBounds(shiftSW(location))) {
            location = shiftSW(location);
            cell = shiftSW(cell);
            mask |= cell;
        }
        return mask;
    }

    public static long diagNW(int[] location) {
        long cell = cell(location);
        long mask = cell;
        while (inBounds(shiftNW(location))) {
            location = shiftNW(location);
            cell = shiftNW(cell);
            mask |= cell;
        }
        while (inBounds(shiftSE(location))) {
            location = shiftSE(location);
            cell = shiftSE(cell);
            mask |= cell;
        }
        return mask;
    }

    private static long cell(int[] location) {
        return 1L << ((location[1] * 8) + location[0]);
    }

    // COORDINATE SHIFTING
    private static int[] shiftN(int[] location) {
        int[] ret = {location[0], location[1] + 1};
        return ret;
    }

    private static int[] shiftS(int[] location) {
        int[] ret = {location[0], location[1] - 1};
        return ret;
    }

    private static int[] shiftW(int[] location) {
        int[] ret = {location[0] + 1, location[1]};
        return ret;
    }

    private static int[] shiftE(int[] location) {
        int[] ret = {location[0] - 1, location[1]};
        return ret;
    }

    private static int[] shiftNW(int[] location) {
        int[] ret = {location[0] + 1, location[1] + 1};
        return ret;
    }

    private static int[] shiftNE(int[] location) {
        int[] ret = {location[0] - 1, location[1] + 1};
        return ret;
    }

    private static int[] shiftSW(int[] location) {
        int[] ret = {location[0] + 1, location[1] - 1};
        return ret;
    }

    private static int[] shiftSE(int[] location) {
        int[] ret = {location[0] - 1, location[1] - 1};
        return ret;
    }

    private static boolean inBounds(int[] location) {
        final int row = location[1], col = location[0];
        return row >= 0 && row <= 7 && col >= 0 && col <= 7;
    }
}
