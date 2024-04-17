public class Misc{

    /**
     * Returns an instance of a Player from its class name
     * Example:  wolfeb.MyPlayer
     */

    public static Player getPlayerInstance(String className) {

        Player p = null;

        try {
            Class<?> playerClass = Class.forName(className);
            p = (Player)playerClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            System.err.println("EXCEPTION (details follow): " + e.toString());
            System.err.println("Could not instantiate Player class "+className);
            System.err.println("(If using the GUI, you may need to edit your "+
                    "othello.ini file.)");
            return null;
        }

        return p;

    }


    public static void printBoard(Board board)
    {
        int[] a = new int[2];
        try {
            System.out.println(" /-0-1-2-3-4-5-6-7-\\");
            for (int i=0; i<8; i++) {
                System.out.print(" "+i+" ");
                for (int j=0; j<8; j++) {
                    a[0] = j;
                    a[1] = i;
                    if (board.getCell(a) == Board.EMPTY)
                        System.out.print("  ");
                    if (board.getCell(a) == Board.BLACK)
                        System.out.print("x ");
                    if (board.getCell(a) == Board.WHITE)
                        System.out.print("o ");
                }
                System.out.println("|");
            }
            System.out.println(" \\-----------------/");
        } catch (IllegalCellException e) {
            System.out.println("");
            System.out.println("Got IllegalCellException while trying "+
                    "to print the board.");
            System.exit(0);
        }

    }

}