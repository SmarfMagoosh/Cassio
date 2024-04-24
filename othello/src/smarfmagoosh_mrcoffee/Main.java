package smarfmagoosh_mrcoffee;

public class Main {
    public static void main(String[] args) {
        CassiosDomain cd = new CassiosDomain();
        int[] test = {2, 3};
        int[] test2 = {2, 4};
        try {
            System.out.println(cd.getPlayer());
            cd.makeMove(test);
            System.out.println(cd.getPlayer());
            cd.makeMove(test2);
            System.out.println(cd.getPlayer());
            printBoard(cd.black);
            printBoard(cd.white);
        } catch (Exception e) {

        }

    }

    public static void printBoard(long bb) {
        String bbStr = Long.toBinaryString(bb);
        StringBuilder result = new StringBuilder("0".repeat(Math.max(0, 64 - bbStr.length())));
        result.append(bbStr);
        for (int i = 0; i < result.length(); i += 8) {
            System.out.println(result.substring(i, i + 8));
        }
        System.out.println("\n");
    }
}
