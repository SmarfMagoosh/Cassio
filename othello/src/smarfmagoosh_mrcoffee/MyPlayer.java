package smarfmagoosh_mrcoffee;

import othello.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyPlayer extends AIPlayer {
    public int depthLimit = 8;

    public static final long CORNER_MASK = 0x8100000000000081L;

    public static final long X_MASK = 0x0042000000004200L;

    public static final long C_MASK = 0x4281000000008142L;

    public final Map<Long, Integer> combos = new HashMap<>();

    public MyPlayer() {
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

    public int myEvaluate(CassiosDomain bb) {
        int numRemaining = CassiosDomain.countOnes(~(bb.black | bb.white));
        if (numRemaining > depthLimit) {
            int tokenScore = bb.countCells(Board.BLACK) - bb.countCells(Board.WHITE);
            return tokenScore +
                    -16 * xScore(bb) +
                    -8 * cScore(bb) +
                    40 * cornerScore(bb) +
                    7 * mobilityScore(bb) +
                    15 * stabilityScore(bb);
        } else {
            return bb.countCells(Board.BLACK) - bb.countCells(Board.WHITE);
        }
    }

    public int cornerScore(CassiosDomain bb) {
        return combos.getOrDefault(bb.black & CORNER_MASK, 0) - combos.getOrDefault(bb.white & CORNER_MASK, 0);
    }

    public int xScore(CassiosDomain bb) {
        return combos.getOrDefault(bb.black & X_MASK, 0) - combos.getOrDefault(bb.white & X_MASK, 0);
    }

    public int cScore(CassiosDomain bb) {
        return combos.getOrDefault(bb.black & C_MASK, 0) - combos.getOrDefault(bb.white & C_MASK, 0);
    }

    public int mobilityScore(CassiosDomain bb) {
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

    public static int stabilityScore(CassiosDomain bb) {
        if (((bb.white | bb.black) & CORNER_MASK) == 0) {
            return 0;
        } else {
            long blackStables = bottomRightStability(bb.black) | bottomLeftStability(bb.black) | topRightStability(bb.black) | topLeftStability(bb.black);
            long whiteStables = bottomRightStability(bb.white) | bottomLeftStability(bb.white) | topRightStability(bb.white) | topLeftStability(bb.white);
            return CassiosDomain.countOnes(blackStables) - CassiosDomain.countOnes(whiteStables);
        }
    }

    @Override
    public String getName() {
        return "Lord Cassio";
    }

    @Override
    public void getNextMove(Board board, int[] bestMove) {
        int currentDepthLimit = depthLimit;
        while (currentDepthLimit <= Math.max(depthLimit, board.countCells(Board.EMPTY))) {
            long[] numNodesExplored = { 0L };
            try {
                minimax(board, currentDepthLimit, true, bestMove, numNodesExplored);
            } catch (InterruptedException ignore) {
                return;
            }
            currentDepthLimit++;
        }
    }

    @Override
    public double evaluate(Board board) {
        return 0;
    }

    @Override
    public double minimax(Board board, int depthLimit, boolean useAlphaBetaPruning, int[] bestMove, long[] numNodesExplored) throws InterruptedException {
        CassiosDomain bb = new CassiosDomain(board);
        if (board.getPlayer() == Board.BLACK) {
            return max_node(
                bb,
                depthLimit,
                useAlphaBetaPruning,
                0,
                bestMove,
                numNodesExplored,
                Integer.MIN_VALUE,
                Integer.MAX_VALUE
            );
        } else {
            return min_node(
                bb,
                depthLimit,
                useAlphaBetaPruning,
                0,
                bestMove,
                numNodesExplored,
                Integer.MIN_VALUE,
                Integer.MAX_VALUE
            );
        }
    }

    public int min_node(
        CassiosDomain board,
        int depthLimit,
        boolean useAlphaBetaPruning,
        int depth,
        int[] bestMove,
        long[] numNodesExplores,
        int alpha,
        int  beta
    ) throws InterruptedException {
        numNodesExplores[0]++;
        if (depth == depthLimit) {
            return myEvaluate(board);
        } else if (Thread.interrupted()) {
            throw new InterruptedException();
        }

        if (board.getLegal() == 0) {
            board.blacksMove = !board.blacksMove;
            if (board.getLegal() == 0) {
                return myEvaluate(board);
            }
            return max_node(
                board,
                depthLimit,
                useAlphaBetaPruning,
                depth,
                bestMove,
                numNodesExplores,
                alpha,
                beta
            );
        }
        int value = Integer.MAX_VALUE;
        ArrayList<Long> moves = board.getMoves();
        if (moves.isEmpty()) {
            return myEvaluate(board);
        }

        // run that minimax baby
        for (long move : moves) {
            CassiosDomain successor = board.getClone();
            successor.makeMove(move);
            int value_cpy = value;
            int recurse = max_node(
                successor,
                depthLimit,
                useAlphaBetaPruning,
                depth + 1,
                bestMove,
                numNodesExplores,
                alpha,
                beta
            );

            value = Math.min(value, recurse);
            if (depth == 0 && value_cpy > value) {
                int[] location = board.location(move);
                bestMove[0] = location[0];
                bestMove[1] = location[1];
            }

            // we do a little beta pruning
            if (useAlphaBetaPruning && value <= alpha) {
                return value;
            }
            beta = Math.min(beta, value);
        }
        return value;
    }

    public int max_node(
        CassiosDomain board,
        int depthLimit,
        boolean useAlphaBetaPruning,
        int depth,
        int[] bestMove,
        long[] numNodesExplores,
        int alpha,
        int beta
    ) throws InterruptedException {
        numNodesExplores[0]++;
        if (depth == depthLimit) {
            return myEvaluate(board);
        } else if (Thread.interrupted()) {
            throw new InterruptedException();
        }

        if (board.getLegal() == 0) {
            board.blacksMove = !board.blacksMove;
            if (board.getLegal() == 0) {
                return myEvaluate(board);
            }
            return min_node(
                board,
                depthLimit,
                useAlphaBetaPruning,
                depth,
                bestMove,
                numNodesExplores,
                alpha,
                beta
            );
        }

        int value = Integer.MIN_VALUE;

        ArrayList<Long> moves = board.getMoves();
        if (moves.isEmpty()) {
            return myEvaluate(board);
        }

        // run that minimax baby
        for (long move : moves) {
            CassiosDomain successor = board.getClone();
            successor.makeMove(move);
            int value_cpy = value;
            int recurse = min_node(
                successor,
                depthLimit,
                useAlphaBetaPruning,
                depth + 1,
                bestMove,
                numNodesExplores,
                alpha,
                beta
            );

            value = Math.max(value, recurse);
            if (depth == 0 && value_cpy < value) {
                int[] location = board.location(move);
                bestMove[0] = location[0];
                bestMove[1] = location[1];
            }

            // we do a little beta pruning
            if (useAlphaBetaPruning && value >= beta) {
                return value;
            }
            alpha = Math.max(alpha, value);
        }
        return value;
    }

    public static long bottomRightStability(long bb) {
        long stables = 0;
        int brNumStable = Integer.MAX_VALUE;
        for (int i = 0; i < 8; i++) {
            long rowMask = ((0xFFL << (8 * i)) & (~bb)) >>> (8 * i);
            if (rowMask != 0) {
                brNumStable = Math.min(Long.numberOfTrailingZeros(rowMask), brNumStable - 1);
                stables |= ((1L << brNumStable) - 1) << (8 * i);
            } else if (brNumStable >= 8){
                stables |= 0xFFL << (8 * i);
            }
            if (brNumStable == 0) {
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

    public static class CassiosDomain {
        // standard member variables
        public long black;
        public long white;
        public boolean blacksMove;

        public static final long[] MASKS = {
                0x7F7F7F7F7F7F7F7FL, // E
                0x007F7F7F7F7F7F7FL, // SE
                0xFFFFFFFFFFFFFFFFL, // S
                0x00FEFEFEFEFEFEFEL, // SW
                0xFEFEFEFEFEFEFEFEL, // W
                0xFEFEFEFEFEFEFE00L, // NW
                0xFFFFFFFFFFFFFFFFL, // N
                0x7F7F7F7F7F7F7F00L // NE
        };

        public static final long[] LEFT_SHIFTS = {0, 0, 0, 0, 1, 9, 8, 7};

        public static final long[] RIGHT_SHIFTS = {1, 9, 8, 7, 0, 0, 0, 0};

        public CassiosDomain(CassiosDomain b) {
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
                    int[] loc = {i, j};
                    try {
                        if (b.getCell(loc) == Board.BLACK) {
                            black |= cell(loc);
                        } else if (b.getCell(loc) == Board.WHITE) {
                            white |= cell(loc);
                        }
                    } catch (Exception ignore) {}
                }
            }
        }

        public CassiosDomain getClone() {
            return new CassiosDomain(this);
        }

        public int countCells(int cellType) {
            if (cellType == Board.EMPTY) {
                return 64 - countOnes(white | black);
            } else {
                return countOnes(cellType == Board.BLACK ? black : white);
            }
        }

        public void makeMove(long cell) {
            long captureBranch;
            long captured_disks = 0;

            long myBoard = blacksMove ? black : white;
            long theirBoard = blacksMove ? white : black;
            myBoard |= cell;

            for (int dir = 0; dir < 8; dir++) {
                captureBranch = shift(cell, dir) & theirBoard;

                captureBranch |= shift(captureBranch, dir) & theirBoard;
                captureBranch |= shift(captureBranch, dir) & theirBoard;
                captureBranch |= shift(captureBranch, dir) & theirBoard;
                captureBranch |= shift(captureBranch, dir) & theirBoard;
                captureBranch |= shift(captureBranch, dir) & theirBoard;

                long endPoint = shift(captureBranch, dir) & myBoard;
                captured_disks |= (endPoint != 0 ? captureBranch : 0);
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
        public static long cell(int[] location) {
            return 1L << ((location[1] * 8) + location[0]);
        }

        public static long cell(int xVal, int yVal) {
            return 1L << ((yVal * 8) + xVal);
        }

        public static int countOnes(long bb) {
            long copy = bb;
            int count;
            for (count = 0; copy != 0 ; count++) {
                copy &= (copy - 1);
            }
            return count;
        }

        public long getLegal() {
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
            return legal;
        }

        public ArrayList<Long> getMoves() {
            long legal = getLegal();
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

        public static long shift(long board, int dir) {
            return dir < 4 ? (board >>> RIGHT_SHIFTS[dir]) & MASKS[dir] : (board << LEFT_SHIFTS[dir]) & MASKS[dir];
        }
    }
}
