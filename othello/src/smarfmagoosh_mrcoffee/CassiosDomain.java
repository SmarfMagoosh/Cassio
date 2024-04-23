package smarfmagoosh_mrcoffee;

import othello.Board;
import othello.BoardImplementation;
import othello.IllegalCellException;
import othello.IllegalMoveException;

import java.util.function.Function;

public class CassiosDomain implements Board {
    public long black;
    public long white;
    private boolean blacksMove;

    private CassiosDomain(CassiosDomain b) {
        black = b.black;
        white = b.white;
        blacksMove = b.blacksMove;
    }

    private CassiosDomain(BoardImplementation b) {
        blacksMove = b.getPlayer() == Board.BLACK;
        black = 0L;
        white= 0L;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                int[] loc = {i, j};
                try {
                    int token = b.getCell(loc);
                    if (token == Board.BLACK) {
                        black +=1;
                    } else if (token == Board.WHITE) {
                        white +=1;
                    }
                } catch (Exception ignored) {}
                black <<= black;
                white <<= white;
            }
        }
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
        // false if the square is filled already or it is out of bounds
        try {
            if (getCell(location) != Board.EMPTY) {
                return false;
            }
        } catch (IllegalCellException e) {
            return false;
        }

        // get my board, their board, and a single cell mask
        long cell = cell(location);
        long myBoard = blacksMove ? black : white;
        long theirBoard = blacksMove ? white : black;

        // shift the single-cell mask 1 unit in each directions
        long[] masks = {cell, cell, cell, cell, cell, cell, cell, cell};
        Function<Long, Long>[] shifts = new Function[8];
        shifts[0] = CassiosDomain::shiftN;
        shifts[1] = CassiosDomain::shiftNE;
        shifts[2] = CassiosDomain::shiftE;
        shifts[3] = CassiosDomain::shiftSE;
        shifts[4] = CassiosDomain::shiftS;
        shifts[5] = CassiosDomain::shiftSW;
        shifts[6] = CassiosDomain::shiftW;
        shifts[7] = CassiosDomain::shiftNW;

        for (int i = 0; i < 8; i++) {
            masks[i] = shifts[i].apply(masks[i]);
        }

        int distance = 1;

        boolean pathToCheck = true;
        while(pathToCheck) {
            boolean done = true;
            for (int i = 0; i < 8; i++) {
                // skip dead masks
                if (masks[i] == 0) { continue; }

                // if there is any live masks, continue
                if (masks[i] != 0) { done = false; }

                // if the mask goes over an empty square, kill it
                if ((myBoard & masks[i]) == 0 && (theirBoard & masks[i]) == 0) {
                    masks[i] = 0;
                    continue;
                }

                // if we find a piece of the same color, the move is legal
                if ((masks[i] & myBoard) != 0) {
                    // if we came from a black square kill it
                    if (distance == 1) {
                        masks[i] = 0;
                        continue;
                    }
                    // will only be true if we came across at least one white square and now a black one
                    else {
                        return true;
                    }
                }
                // shift all masks in their respective directions
                masks[i] = shifts[i].apply(masks[i]);
            }
            distance++;
            pathToCheck = !done;
        }
        // return false if all masks died before finding a move
        return false;
    }

    @Override
    public void makeMove(int[] location) throws IllegalMoveException {
        if (!isLegalMove(location)) {
            System.out.println("throwing exception");
            throw new IllegalMoveException();
        }

        long myBoard = blacksMove ? black : white;
        long theirBoard = blacksMove ? white : black;

        long cell = cell(location);
        long mask = cell;
        long[] masks = {cell, cell, cell, cell, cell, cell, cell, cell};
        long[] branches = new long[8];
        Function<Long, Long>[] shifts = new Function[8];
        shifts[0] = CassiosDomain::shiftN;
        shifts[1] = CassiosDomain::shiftNE;
        shifts[2] = CassiosDomain::shiftE;
        shifts[3] = CassiosDomain::shiftSE;
        shifts[4] = CassiosDomain::shiftS;
        shifts[5] = CassiosDomain::shiftSW;
        shifts[6] = CassiosDomain::shiftW;
        shifts[7] = CassiosDomain::shiftNW;

        for (int i = 0; i < 8; i++) {
            masks[i] = shifts[i].apply(masks[i]);
            branches[i] = masks[i];
        }

        boolean pathToCheck = true;
        int distance = 1;
        while(pathToCheck) {
            boolean done = true;
            for (int i = 0; i < 8; i++) {
                // skip dead masks
                if (masks[i] == 0) {
                    continue;
                }

                // kill mask if we hit an empty square
                if ((masks[i] & myBoard) == 0 && (masks[i] & theirBoard) == 0) {
                    masks[i] = 0;
                    continue;
                }

                // add white square to corresponding branch
                if ((masks[i] & theirBoard) != 0) {
                    done = false;
                    branches[i] |= masks[i];
                }

                // save result if we hit a square on our board
                if ((masks[i] & myBoard) != 0) {
                    done = false;
                    if (distance == 1) {
                        masks[i] = 0;
                    } else {
                        mask |= masks[i];
                        mask |= branches[i];
                        masks[i] = 0;
                    }
                    continue;
                }

                masks[i] = shifts[i].apply(masks[i]);
            }
            distance++;
            pathToCheck = !done;
        }
        black |= mask;
        white &= ~mask;
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
    private static final long westMask = 0xFE_FE_FE_FE_FE_FE_FE_FEL;

    private static final long eastMask = 0x7F_7F_7F_7F_7F_7F_7F_7FL;

    private static long shiftN(long bb) {
        return bb << 8;
    }

    private static long shiftS(long bb) {
        return bb >>> 8;
    }
    
    private static long shiftW(long bb) {
        return (bb << 1) & westMask;
    }

    private static long shiftE(long bb) {
        return (bb >>> 1) & eastMask;
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

    private static long cell(int[] location) { return 1L << ((location[1] * 8) + location[0]); }

    private static boolean inBounds(int[] location) {
        final int row = location[1], col = location[0];
        return row >= 0 && row <= 7 && col >= 0 && col <= 7;
    }

    // MORE MASKS
}
