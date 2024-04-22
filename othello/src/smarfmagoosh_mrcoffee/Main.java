package smarfmagoosh_mrcoffee;

import java.util.Arrays;

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
        String bbStr = Long.toBinaryString(bb);
        StringBuilder result = new StringBuilder("0".repeat(Math.max(0, 64 - bbStr.length())));
        result.append(bbStr);
        for (int i = 0; i < result.length(); i += 8) {
            System.out.println(result.substring(i, i + 8));
        }
        System.out.println("\n");
    }
}
