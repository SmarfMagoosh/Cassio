package smarfmagoosh_mrcoffee;

public class Main {
    public static void main(String[] args) {
        long x = 0b0000000000000000000000000001000100000000000111111111111111111111L;
        long stables = Version2.bottomLeftStability(x);
        System.out.println(Long.toBinaryString(stables));
    }
}