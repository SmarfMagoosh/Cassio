package smarfmagoosh_mrcoffee;

import othello.*;

import java.util.*;

/**
 * An AI for playing othello using minimax and alpha-beta pruning
 *
 * @author Evan Dreher, Micah Nicodemus
 */
public class MyPlayer extends AIPlayer {
    // starting depth limit before iterative deepening
    public int depthLimit = 8;

    /**
     * Mask for corner squares
     * 10000001
     * 00000000
     * 00000000
     * 00000000
     * 00000000
     * 00000000
     * 00000000
     * 10000001
     */
    public static final long CORNER_MASK = 0x8100000000000081L;

    /**
     * Mask for x squares
     * 00000000
     * 01000010
     * 00000000
     * 00000000
     * 00000000
     * 00000000
     * 01000010
     * 00000000
     */
    public static final long X_MASK = 0x0042000000004200L;

    /**
     * Mask for c squares
     * 01000010
     * 10000001
     * 00000000
     * 00000000
     * 00000000
     * 00000000
     * 10000001
     * 01000010
     */
    public static final long C_MASK = 0x4281000000008142L;

    /**
     * A static map for mapping all possible bitboards containing all corner-squares, x-squares, and c-squares
     * to the number of squares owned in the board
     */
    public final Map<Long, Integer> combos = new HashMap<>();

    /**
     * Constructor initializes and fills the combos map
     */
    public MyPlayer() {
        super();
        // mask for each individual corner
        long[] corners = {
                0x0000000000000001L,
                0x0000000000000080L,
                0x0100000000000000L,
                0x8000000000000000L,
        };

        // mask for each individual x square
        long[] xs = {
                0x0040000000000000L,
                0x0002000000000000L,
                0x0000000000000200L,
                0x0000000000004000L
        };

        // mask for each individual c-square
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

        // update combos map
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

        // update combos map
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

    /**
     * static board eval function
     * @param bb the board
     * @return the goatedness of the board
     */
    public int myEvaluate(CassiosDomain bb) {
        int numRemaining = CassiosDomain.countOnes(~(bb.black | bb.white));
        if (numRemaining > depthLimit) { // if we can't see to the end of the game, use heuristic
            int tokenScore = bb.countCells(Board.BLACK) - bb.countCells(Board.WHITE);
            return tokenScore +
                    -16 * xScore(bb) +
                    -8 * cScore(bb) +
                    40 * cornerScore(bb) +
                    7 * mobilityScore(bb) +
                    15 * stabilityScore(bb);
        } else { // if the whole game can be computed maximize tokens
            return bb.countCells(Board.BLACK) - bb.countCells(Board.WHITE);
        }
    }

    /**
     * partial evaluation based of corners, this number should be high
     * @param bb the board
     * @return number of black corners - number of white corners
     */
    public int cornerScore(CassiosDomain bb) {
        return combos.getOrDefault(bb.black & CORNER_MASK, 0) - combos.getOrDefault(bb.white & CORNER_MASK, 0);
    }

    /**
     * partial evaluation based of x squares, this number should be low
     * @param bb the board
     * @return number of black xs - number of white xs
     */
    public int xScore(CassiosDomain bb) {
        return combos.getOrDefault(bb.black & X_MASK, 0) - combos.getOrDefault(bb.white & X_MASK, 0);
    }

    /**
     * partial evaluation based of c squares, this number should be low
     * @param bb the board
     * @return number of black cs - number of white cs
     */
    public int cScore(CassiosDomain bb) {
        return combos.getOrDefault(bb.black & C_MASK, 0) - combos.getOrDefault(bb.white & C_MASK, 0);
    }

    /**
     * partial evaluation based of moves available, this number should be high
     * does not perfectly count the number of moves. instead it calculates the number of empty
     * squares adjacent to all tokens of either color
     * @param bb the board
     * @return number of white moves - number of black moves
     */
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

    /**
     * partial evaluation function based on the number of stable tokens held by either side.
     * This function does not perfectly compute the number of stable tokens,
     * @param bb
     * @return
     */
    public static int stabilityScore(CassiosDomain bb) {
        if (((bb.white | bb.black) & CORNER_MASK) == 0) {
            return 0;
        } else {
            long blackStables = bottomRightStability(bb.black) | bottomLeftStability(bb.black) | topRightStability(bb.black) | topLeftStability(bb.black);
            long whiteStables = bottomRightStability(bb.white) | bottomLeftStability(bb.white) | topRightStability(bb.white) | topLeftStability(bb.white);
            return CassiosDomain.countOnes(blackStables) - CassiosDomain.countOnes(whiteStables);
        }
    }

    /**
     * All hail lord cassio the savior of mankind
     * @return
     */
    @Override
    public String getName() {
        return "Lord Cassio";
    }

    /**
     * Perform iterative deepening minimax search on the given board and update the best move array as it improves
     *
     * @param board the board to perform minimax search on
     * @param bestMove an array for the best move to be stored in
     */
    @Override
    public void getNextMove(Board board, int[] bestMove) {
        int currentDepthLimit = depthLimit;
        while (currentDepthLimit <= Math.max(depthLimit, board.countCells(Board.EMPTY))) {
            long[] numNodesExplored = { 0L };
            try {
                int[] layerBest = {-1, -1};
                minimax(board, currentDepthLimit, true, layerBest, numNodesExplored);
                bestMove[0] = layerBest[0];
                bestMove[1] = layerBest[1];
            } catch (InterruptedException ignore) {
                return;
            }
            currentDepthLimit++;
        }
    }

    /**
     * This method is cringe and useless but we have to use it because of inheritance
     * @param board a useless meaningless board
     * @return 0
     */
    @Override
    public double evaluate(Board board) {
        return 0;
    }

    /**
     * runs the standard minimax algorithm with the option to use pruning.
     * @param board the board to start minimax search on
     * @param depthLimit the depth to search out to
     * @param useAlphaBetaPruning whether or not to use ab pruning
     * @param bestMove the array storing the current best move
     * @param numNodesExplored the number of nodes explored in the search
     * @return the minimax value for this node
     * @throws InterruptedException when the time runs out
     */
    @Override
    public double minimax(
            Board board,
            int depthLimit,
            boolean useAlphaBetaPruning,
            int[] bestMove,
            long[] numNodesExplored
    ) throws InterruptedException {
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

    /**
     * The max part of the minimax algorithm
     * @param board the board being searched
     * @param depthLimit the depth to search out to
     * @param useAlphaBetaPruning whether or not to use alpha beta pruning
     * @param depth the current depth
     * @param bestMove the current best move found
     * @param numNodesExplores the number of nodes explored in the search
     * @param alpha the current alpha value
     * @param beta the current beta value
     * @return the minimax value for this node
     * @throws InterruptedException if time has run out
     */
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
        // if we hit a terminal node, return utility
        if (depth == depthLimit) {
            return myEvaluate(board);
        } else if (Thread.interrupted()) {
            throw new InterruptedException();
        }

        // if there are no legal moves
        if (board.getLegal() == 0) {
            // toggle current player
            board.blacksMove = !board.blacksMove;

            // if other player has no legal moves game is over, return utility
            if (board.getLegal() == 0) {
                return myEvaluate(board);
            }
            // otherwise switch this node to a max node
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
        // initialize value to worst case scenario
        int value = Integer.MAX_VALUE;

        // get moves sorted by heuristic for a best first search
        ArrayList<BoardState> successors = new ArrayList<>();
        for (long move : board.getMoves()) {
            CassiosDomain successor = board.getClone();
            successor.makeMove(move);
            successors.add(new BoardState(successor, move, myEvaluate(successor)));
        }
        successors.sort(Comparator.comparingInt(x -> x.eval));

        // run that minimax baby
        for (BoardState successor : successors) {
            int value_cpy = value;
            int recurse = max_node(
                    successor.board,
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
                int[] location = board.location(successor.move);
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

    /**
     * The max part of the minimax algorithm
     * @param board the board being searched
     * @param depthLimit the depth to search out to
     * @param useAlphaBetaPruning whether or not to use alpha beta pruning
     * @param depth the current depth
     * @param bestMove the current best move found
     * @param numNodesExplores the number of nodes explored in the search
     * @param alpha the current alpha value
     * @param beta the current beta value
     * @return the minimax value for this node
     * @throws InterruptedException if time has run out
     */
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
        // if we hit the maximum depth, return utility
        if (depth == depthLimit) {
            return myEvaluate(board);
        } else if (Thread.interrupted()) {
            throw new InterruptedException();
        }

        // if current player has no legal moves, toggle player
        if (board.getLegal() == 0) {
            board.blacksMove = !board.blacksMove;
            // if other player has no legal moves, return utility since the game is over
            if (board.getLegal() == 0) {
                return myEvaluate(board);
            }
            // otherwise switch this node to a min node
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
        // initialize value to worst case scenario
        int value = Integer.MIN_VALUE;

        // get successors sorted by utility for best first search
        ArrayList<BoardState> successors = new ArrayList<>();
        for (long move : board.getMoves()) {
            CassiosDomain successor = board.getClone();
            successor.makeMove(move);
            successors.add(new BoardState(successor, move, myEvaluate(successor)));
        }
        successors.sort(Collections.reverseOrder(Comparator.comparingInt(x -> x.eval)));

        // run that minimax baby
        for (BoardState successor : successors) {
            int value_cpy = value;
            int recurse = min_node(
                    successor.board,
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
                int[] location = board.location(successor.move);
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

    // data class for sorting successors
    private record BoardState(CassiosDomain board, Long move, Integer eval) {}

    /**
     * used in partial eval to calculate number of stable tokens in bottom right corner
     * not perfect since it can't detect stability via a filled row of tokens from both players
     * @param bb a bit board represented by a long
     * @return the same bit board but with only the stable tokens included
     */
    public static long bottomRightStability(long bb) {
        // tracks stables tokens
        long stables = 0;

        // tracks number of stable tokens in the last row
        int brNumStable = Integer.MAX_VALUE;
        for (int i = 0; i < 8; i++) {
            // for each row, isolate it and not it so the tokens are 0s
            long rowMask = ((0xFFL << (8 * i)) & (~bb)) >>> (8 * i);
            /* if there was an empty square on the row, the number of stable tokens is equal to
            the number of trailing zeros, or the number of stable tokens on the row above, whichever is lower
            */
            if (rowMask != 0) {
                brNumStable = Math.min(Long.numberOfTrailingZeros(rowMask), brNumStable - 1);
                stables |= ((1L << brNumStable) - 1) << (8 * i);
            } else if (brNumStable >= 8) { // if the whole row was 0s and all previous rows were all 0s, the whole row is stable
                stables |= 0xFFL << (8 * i);
            }
            if (brNumStable == 0) { // if there are no stable tokens lift, finish
                break;
            }
        }
        return stables;
    }

    /**
     * used in partial eval to calculate number of stable tokens in top right corner
     * not perfect since it can't detect stability via a filled row of tokens from both players
     * @param bb a bit board represented by a long
     * @return the same bit board but with only the stable tokens included
     */
    public static long topRightStability(long bb) {
        // see bottomRightStability for explanation of the algorithm
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

    /**
     * used in partial eval to calculate number of stable tokens in bottom left corner
     * not perfect since it can't detect stability via a filled row of tokens from both players
     * @param bb a bit board represented by a long
     * @return the same bit board but with only the stable tokens included
     */
    public static long bottomLeftStability(long bb) {
        // see bottomRightStability for explanation of the algorithm
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

    /**
     * used in partial eval to calculate number of stable tokens in top left corner
     * not perfect since it can't detect stability via a filled row of tokens from both players
     * @param bb a bit board represented by a long
     * @return the same bit board but with only the stable tokens included
     */
    public static long topLeftStability(long bb) {
        // see bottomRightStability for explanation of the algorithm
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

    /**
     * Based bitboard nested class since implementing the board interface impeded efficiency
     */
    public static class CassiosDomain {
        // bitboards for black and white
        public long black;
        public long white;

        // is it blacks move?
        public boolean blacksMove;

        // masks for each cardinal direction
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

        // used for shifting left
        public static final long[] LEFT_SHIFTS = {0, 0, 0, 0, 1, 9, 8, 7};

        // used when shifting right
        public static final long[] RIGHT_SHIFTS = {1, 9, 8, 7, 0, 0, 0, 0};

        // average deep copy enjoyer
        public CassiosDomain(CassiosDomain b) {
            black = b.black;
            white = b.white;
            blacksMove = b.blacksMove;
        }

        /**
         * converts the cringe 2D array board to a based bitboard implementation
         * @param b the cringe 2D array implementation
         */
        public CassiosDomain(othello.Board b) {
            blacksMove = b.getPlayer() == Board.BLACK;
            black = 0L;
            white = 0L;
            // for every possibly cell, add it to the correct board
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

        // average deep copy enjoyer
        public CassiosDomain getClone() {
            return new CassiosDomain(this);
        }

        /**
         * counts the number of cells of a specific type on the board
         * @param cellType 0, 1, or 2 for empty, black, or white respectively
         * @return the number of tokens of the given type on the board
         */
        public int countCells(int cellType) {
            if (cellType == Board.EMPTY) {
                return 64 - countOnes(white | black);
            } else {
                return countOnes(cellType == Board.BLACK ? black : white);
            }
        }

        /**
         * places a token on the board and flips tokens that are captured
         * @param cell a long with only 1 non-zero digit indicating the location of the placed token
         */
        public void makeMove(long cell) {
            // variables for tracking captured tokens
            long captureBranch;
            long captured_disks = 0;

            long myBoard = blacksMove ? black : white;
            long theirBoard = blacksMove ? white : black;
            myBoard |= cell;

            // for each direction...
            for (int dir = 0; dir < 8; dir++) {
                // see if there is an enemy disk in that direction
                captureBranch = shift(cell, dir) & theirBoard;

                // continue down that direction adding cells as you find them
                captureBranch |= shift(captureBranch, dir) & theirBoard;
                captureBranch |= shift(captureBranch, dir) & theirBoard;
                captureBranch |= shift(captureBranch, dir) & theirBoard;
                captureBranch |= shift(captureBranch, dir) & theirBoard;
                captureBranch |= shift(captureBranch, dir) & theirBoard;

                // capture the disks if we find a token of my color
                long endPoint = shift(captureBranch, dir) & myBoard;
                captured_disks |= (endPoint != 0 ? captureBranch : 0);
            }
            // flip captured tokens on my board
            myBoard ^= captured_disks;

            // flip captured tokens on their board
            theirBoard ^= captured_disks;
            if (blacksMove) {
                black = myBoard;
                white = theirBoard;
            } else {
                white = myBoard;
                black = theirBoard;
            }
            // toggle current player
            blacksMove = !blacksMove;
        }

        /**
         * gets the coordinates of a given cell mask
         * @param cell the cell mask
         * @return {col, row}
         */
        public int[] location(long cell) {
            int index = Long.numberOfTrailingZeros(cell);
            return new int[]{index % 8, index / 8};
        }

        /**
         * returns the current player
         * @return
         */
        public int getPlayer() {
            return blacksMove ? Board.BLACK : Board.WHITE;
        }

        /**
         * gets a cell mask for a given location
         * @param location {col, row}
         * @return the cell mask for the given location
         */
        public static long cell(int[] location) {
            return 1L << ((location[1] * 8) + location[0]);
        }

        /**
         * gets a cell mask for a given location
         * @param xVal the x coordinate of the cell
         * @param yVal the y coordinate of the cell
         * @return the cell mask for the given location
         */
        public static long cell(int xVal, int yVal) {
            return 1L << ((yVal * 8) + xVal);
        }

        /**
         * counts the number of 1s digits in a given long
         * @param bb
         * @return
         */
        public static int countOnes(long bb) {
            int count;
            for (count = 0; bb != 0 ; count++) {
                bb &= (bb - 1);
            }
            return count;
        }

        /**
         *
         * @return a long with a 1 in the location of all legal moves
         */
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

        /**
         * an arraylist of cellMasks for each possible legal move location
         * @return
         */
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

        /**
         * generalized bitboard shifting
         * @param board a bitboard represented by a long
         * @param dir the direction to shift in
         * @return a shifted bitboard with masking to handle overflow
         */
        public static long shift(long board, int dir) {
            return dir < 4 ? (board >>> RIGHT_SHIFTS[dir]) & MASKS[dir] : (board << LEFT_SHIFTS[dir]) & MASKS[dir];
        }
    }
}
