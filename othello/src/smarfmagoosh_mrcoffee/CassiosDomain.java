package smarfmagoosh_mrcoffee;

import othello.Board;

import java.util.ArrayList;
import java.util.Arrays;

import static smarfmagoosh_mrcoffee.Main.printBoard;

public class CassiosDomain {
    public long black;
    public long white;
    public boolean blacksMove;

    static final long[] MASKS = {
            0x7F7F7F7F7F7F7F7FL, // E
            0x007F7F7F7F7F7F7FL, // SE
            0xFFFFFFFFFFFFFFFFL, // S
            0x00FEFEFEFEFEFEFEL, // SW
            0xFEFEFEFEFEFEFEFEL, // W
            0xFEFEFEFEFEFEFE00L, // NW
            0xFFFFFFFFFFFFFFFFL, // N
            0x7F7F7F7F7F7F7F00L // NE
    };

    static final long[] LEFT_SHIFTS = {
            0, // E
            0, // SE
            0, // S
            0, // SW
            1, // W
            9, // NW
            8, // N
            7 // NE
    };

    static final long[] RIGHT_SHIFTS = {
            1, // E
            9, // SE
            8, // S
            7, // SW
            0, // W
            0, // NW
            0, // N
            0 // NE
    };

    private CassiosDomain(CassiosDomain b) {
        black = b.black;
        white = b.white;
        blacksMove = b.blacksMove;
    }

    public CassiosDomain(othello.Board b) {
        blacksMove = b.getPlayer() == Board.BLACK;
        black = 0L;
        white = 0L;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                int[] loc = { i, j };
                try {
                    if (b.getCell(loc) == Board.BLACK) {
                        black |= cell(loc);
                    } else if (b.getCell(loc) == Board.WHITE) {
                        white |= cell(loc);
                    }
                } catch (Exception ignore) {
                }
            }
        }
    }

    public CassiosDomain getClone() {
        return new CassiosDomain(this);
    }

//    private int cellValue(int[] location) {
//        final long mask = cell(location);
//
//        if ((white & mask) != 0) {
//            return Board.WHITE;
//        } else if ((black & mask) != 0) {
//            return Board.BLACK;
//        } else {
//            return Board.EMPTY;
//        }
//    }
//
//    private long cellValue(int xVal, int yVal) {
//        final long mask = cell(xVal, yVal);
//
//        if ((white & mask) != 0) {
//            return Board.WHITE;
//        } else if ((black & mask) != 0) {
//            return Board.BLACK;
//        } else {
//            return Board.EMPTY;
//        }
//    }

    public int countCells(int cellType) {
        if (cellType == Board.EMPTY) {
            return 64 - countOnes(white | black);
        } else {
            return countOnes(cellType == Board.BLACK ? black : white);
        }
    }

    public void makeMove(long cell) {
        long x, bounding_disk;
        long captured_disks = 0;

        long myBoard = blacksMove ? black : white;
        long theirBoard = blacksMove ? white : black;
        myBoard |= cell;

        for (int dir = 0; dir < 8; dir++) {
            x = shift(cell, dir) & theirBoard;

            x |= shift(x, dir) & theirBoard;
            x |= shift(x, dir) & theirBoard;
            x |= shift(x, dir) & theirBoard;
            x |= shift(x, dir) & theirBoard;
            x |= shift(x, dir) & theirBoard;

            bounding_disk = shift(x, dir) & myBoard;
            captured_disks |= (bounding_disk != 0 ? x : 0); // will this be correct if the sign bit is 1?
        }

        myBoard ^= captured_disks;
        theirBoard ^= captured_disks;
        if (blacksMove) {
            black = myBoard;
            white = theirBoard;
        } else {
            white = myBoard;
            black = theirBoard;
        }
        blacksMove = !blacksMove;
    }

    public int[] location(long cell) {
        int index = Long.numberOfTrailingZeros(cell);
        return new int[]{index % 8, index / 8};
    }

    public int getPlayer() {
        return blacksMove ? Board.BLACK : Board.WHITE;
    }

    // BITBOARD MASKS
    private static long cell(int[] location) {
        return 1L << ((location[1] * 8) + location[0]);
    }

    private static long cell(int xVal, int yVal) {
        return 1L << ((yVal * 8) + xVal);
    }

    public static int countOnes(long bb) {
        long copy = bb;
        int count = 0;
        while (copy != 0) {
            count += copy & 1L;
            copy >>>= 1;
        }
        return count;
    }

    public ArrayList<Long> getMoves() {
        long empty = ~(white | black);
        long legal = 0;
        long myBoard = blacksMove ? black : white;
        long theirBoard = blacksMove ? white : black;

        for (int i = 0; i < 8; i++) {
            long x = shift(myBoard, i) & theirBoard;

            x |= shift(x, i) & theirBoard;
            x |= shift(x, i) & theirBoard;
            x |= shift(x, i) & theirBoard;
            x |= shift(x, i) & theirBoard;
            x |= shift(x, i) & theirBoard;

            legal |= shift(x, i) & empty;
        }

        ArrayList<Long> moves = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                long cell = cell(i, j);
                if ((cell & legal) != 0) {
                    moves.add(cell);
                }
            }
        }

        return moves;
    }

    /* Shift disks in direction dir. */
    private static long shift(long board, int dir) {
        return dir < (8 / 2) ? (board >>> RIGHT_SHIFTS[dir]) & MASKS[dir] : (board << LEFT_SHIFTS[dir]) & MASKS[dir];
    }
}