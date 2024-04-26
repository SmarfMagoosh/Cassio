package smarfmagoosh_mrcoffee;

public class Main {
    public static void main(String[] args) {
//        long x = 0b11111111_00000011_00001111_00010001_00000000_00001111_00000011_11111111L;
//        long stables = Version2.bottomRightStability(x);
//        System.out.println(Long.toBinaryString(stables));
//
//        stables = Version2.topRightStability(x);
//        System.out.println(Long.toBinaryString(stables));

        long x = 0b11111111_11000000_11110000_00010001_00000000_11110000_11000000_11111111L;
        long stables = Version2.topLeftStability(x);
        System.out.println(Long.toBinaryString(stables));
    }
}