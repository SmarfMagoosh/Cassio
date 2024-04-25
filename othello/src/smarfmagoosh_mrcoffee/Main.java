package smarfmagoosh_mrcoffee;

public class Main {
    public static void main(String[] args) {
        long test = 0x8000000000000000L;
        System.out.println(test);
        Version2 v2 = new Version2();
        printBoard(CassiosDomain.cell(2, 3));
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