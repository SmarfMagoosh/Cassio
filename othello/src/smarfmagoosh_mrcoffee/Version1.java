package smarfmagoosh_mrcoffee;

public class Version1 extends MyPlayer {
    @Override
    public String getName() {
        return super.getName() + " Version 1";
    }

    @Override
    public int myEvaluate(CassiosDomain bb) {
        return CassiosDomain.countOnes(bb.black) - CassiosDomain.countOnes(bb.white);
    }
}
