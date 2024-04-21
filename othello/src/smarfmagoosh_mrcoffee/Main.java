package smarfmagoosh_mrcoffee;

public class Main {
    public static void main(String[] args) {
        int[][]coords = new int[2][2];
        coords[0][0] = 0;
        coords[0][1] = 0;
        coords[1][0] = 3;
        coords[1][1] = 4;
        CassiosDomain cd = new CassiosDomain();
        printBoard(CassiosDomain.diagNE(coords[0]));
        printBoard(CassiosDomain.diagNE(coords[1]));
        printBoard(CassiosDomain.diagNW(coords[0]));
        printBoard(CassiosDomain.diagNW(coords[1]));
    }

    public static void printBoard(long bb) {
        String s = Long.toBinaryString(bb);
        for (int i = s.length(); i < 64; i++) {
            s += "0";
        }
        for (int i = 0; i < s.length(); i += 8) {
            System.out.println(s.substring(i, i + 8));
        }
        System.out.println("\n");
    }
}
