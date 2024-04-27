package smarfmagoosh_mrcoffee;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MyPlayerSortedMoves extends MyPlayer {
  @Override
  public String getName() {
    return "Lord Cassio (Sorted Moves)";
  }

  @Override
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

  @Override
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

  public record BoardState(CassiosDomain board, Long move, Integer eval) {}
}
