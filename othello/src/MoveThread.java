/**
 * A thread that gets a move from a player,
 *   but with a time limit.
 */
public class MoveThread extends Thread {
    private Board board;
    private Player player;
    private int move[];

    MoveThread(Board b, Player p, int move[])
    {
        board = b;
        player = p;
        this.move = move;
    }

    public void run()
    {
        try {
            player.getNextMove(board, move);
        } catch (Exception e) {

        }
    }

    public static void timedMove(Board b, Player p, int[] move, long limit)
    {
        MoveThread thread = new MoveThread(b, p, move);
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
        try {
            thread.join(limit);  // wait until the thread is done or limit milliseconds have passed, whichever is first

            if (thread.isAlive()) {
                thread.interrupt();  // if the thread is still running, signal that it should stop.
                // That thread should regularly check Thread.interrupted()
                // and halt itself by returning immediately from getNextMove
            }
        } catch (InterruptedException e) {
        }
    }

}