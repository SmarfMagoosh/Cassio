package smarfmagoosh_mrcoffee;

import othello.BoardImplementation;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        BoardImplementation b = new BoardImplementation();
        CassiosDomain cd = new CassiosDomain(b);
        cd.getMoves();
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

/**
 *  public boolean isLegalMove(int[] location) {
 *         if(inBounds(location)){
 *             int player = getPlayer();
 *             boolean legal = false;
 *             int opp = player == 1 ? 2 : 1;
 *             int xVal = location[0];
 *             int yVal = location[1];
 *             if(cellValue(location) == 0){
 *                 if((xVal>1) && (yVal>1) && (cellValue(xVal-1, yVal-1) == opp)){
 *                     legal = check(1, xVal-2, yVal-2, player, opp);
 *                     if(legal)
 *                         return legal;
 *                 }
 *                 if((yVal>1) && (cellValue(xVal, yVal-1) == opp)){
 *                     legal = check(2, xVal, yVal-2, player, opp);
 *                     if(legal)
 *                         return legal;
 *                 }
 *                 if((xVal<6) && (yVal>1) && (cellValue(xVal+1, yVal-1) == opp)){
 *                     legal = check(3, xVal+2, yVal-2, player, opp);
 *                     if(legal)
 *                         return legal;
 *                 }
 *                 if((xVal<6) && (cellValue(xVal+1, yVal) == opp)){
 *                     legal = check(4, xVal+2, yVal, player, opp);
 *                     if(legal)
 *                         return legal;
 *                 }
 *                 if((xVal<6) && (yVal<6) && (cellValue(xVal+1, yVal+1) == opp)){
 *                     legal = check(5, xVal+2, yVal+2, player, opp);
 *                     if(legal)
 *                         return legal;
 *                 }
 *                 if((yVal<6) && (cellValue(xVal, yVal+1) == opp)){
 *                     legal = check(6, xVal, yVal+2, player, opp);
 *                     if(legal)
 *                         return legal;
 *                 }
 *                 if((xVal>1) && (yVal<6) && (cellValue(xVal-1, yVal+1) == opp)){
 *                     legal = check(7, xVal-2, yVal+2, player, opp);
 *                     if(legal)
 *                         return legal;
 *                 }
 *                 if((xVal>1) && cellValue(xVal-1, yVal) == opp){
 *                     legal = check(8, xVal-2, yVal, player, opp);
 *                     if(legal)
 *                         return legal;
 *                 }
 *             }
 *             return legal;
 *         }
 *         else
 *             return false;
 *     }
 *
 *     public void makeMove(int[] location) throws IllegalMoveException {
 *         int player = getPlayer();
 *         int opp = player == 1 ? 2 : 1;
 *         int xVal = location[0];
 *         int yVal = location[1];
 *         long mask = cell(xVal, yVal);
 *         if (isLegalMove(location)){
 *             if(xVal > 1 && yVal > 1 && cellValue(xVal-1, yVal-1) == opp){
 *                 if(check(1, xVal-2, yVal-2, player, opp)){
 *                     int x = xVal-1;
 *                     int y = yVal-1;
 *                     while(cellValue(x, y) == opp){
 *                         mask |= cell(x, y);
 *                         x--;
 *                         y--;
 *                     }
 *                 }
 *             }
 *             if(yVal>1 && cellValue(xVal, yVal-1) == opp){
 *                 if(check(2, xVal, yVal-2, player, opp)){
 *                     int c = yVal-1;
 *                     while(cell(xVal, c) == opp){
 *                         mask |= cell(xVal, c);
 *                         c--;
 *                     }
 *                 }
 *             }
 *             if(xVal<7 && yVal>1 && cellValue(xVal+1, yVal-1) == opp){
 *                 if(check(3, xVal+2, yVal-2, player, opp)){
 *                     int x = xVal+1;
 *                     int y = yVal-1;
 *                     while(cellValue(x, y) == opp){
 *                         mask |= cell(x, y);
 *                         x++;
 *                         y--;
 *                     }
 *                 }
 *             }
 *             if(xVal<7 && cellValue(xVal+1, yVal) == opp){
 *                 if(check(4, xVal+2, yVal, player, opp)){
 *                     int x = xVal+1;
 *                     while(cellValue(x, yVal) == opp){
 *                         mask |= cell(x, yVal);
 *                         x++;
 *                     }
 *                 }
 *             }
 *             if(xVal<7 && yVal<7 && cellValue(xVal+1, yVal+1) == opp){
 *                 if(check(5, xVal+2, yVal+2, player, opp)){
 *                     int x = xVal+1;
 *                     int y = yVal+1;
 *                     while(cellValue(x, y)==opp){
 *                         mask |= cell(x, y);
 *                         x++;
 *                         y++;
 *                     }
 *                 }
 *             }
 *             if(yVal<7 && cellValue(xVal, yVal+1) == opp){
 *                 if(check(6, xVal, yVal+2, player, opp)){
 *                     int y = yVal+1;
 *                     while(cellValue(xVal, y)==opp){
 *                         mask |= cell(xVal, y);
 *                         y++;
 *                     }
 *                 }
 *             }
 *             if(xVal>1 && yVal<7 && cellValue(xVal-1, yVal+1) == opp){
 *                 if(check(7, xVal-2, yVal+2, player, opp)){
 *                     int x = xVal-1;
 *                     int y = yVal+1;
 *                     while(cellValue(x, y)==opp){
 *                         mask |= cell(x, y);
 *                         x--;
 *                         y++;
 *                     }
 *                 }
 *             }
 *             if((xVal>1) && cellValue(xVal-1, yVal) == opp){
 *                 if(check(8, xVal-2, yVal, player, opp)){
 *                     int x = xVal-1;
 *                     while(cellValue(x, yVal)==opp){
 *                         mask |= cell(x, yVal);
 *                         x--;
 *                     }
 *                 }
 *             }
 *         }
 *         else{
 *             throw new IllegalMoveException();
 *         }
 *         if (!isLegalMove(location)) {
 *             throw new IllegalMoveException();
 *         }
 *
 *         long myBoard = blacksMove ? black : white;
 *         long theirBoard = blacksMove ? white : black;
 *
 *         myBoard |= mask;
 *         theirBoard &= ~mask;
 *         if (blacksMove) {
 *             black = myBoard;
 *             white = theirBoard;
 *         } else {
 *             white = myBoard;
 *             black = theirBoard;
 *         }
 *         blacksMove = !blacksMove;
 *     }
 *
 *     private boolean check(int code, int xVal, int yVal, int player, int opp) {
 *         boolean checker = false;
 *         int[] location = {xVal, yVal};
 *         if (inBounds(location) && cellValue(location) == player) {
 *             return true;
 *         } else switch (code) {
 *             case 1 -> {
 *                 if ((xVal > 0) && (yVal > 0) && (cellValue(location) == opp))
 *                     checker = check(1, xVal - 1, yVal - 1, player, opp);
 *             }
 *             case 2 -> {
 *                 if ((yVal > 0) && (cellValue(location) == opp))
 *                     checker = check(2, xVal, yVal - 1, player, opp);
 *             }
 *             case 3 -> {
 *                 if ((xVal < 7) && (yVal > 0) && (cellValue(location) == opp))
 *                     checker = check(3, xVal + 1, yVal - 1, player, opp);
 *             }
 *             case 4 -> {
 *                 if ((xVal < 7) && (cellValue(location) == opp))
 *                     checker = check(4, xVal + 1, yVal, player, opp);
 *             }
 *             case 5 -> {
 *                 if ((xVal < 7) && (yVal < 7) && (cellValue(location) == opp))
 *                     checker = check(5, xVal + 1, yVal + 1, player, opp);
 *             }
 *             case 6 -> {
 *                 if ((yVal < 7) && (cellValue(location) == opp))
 *                     checker = check(6, xVal, yVal + 1, player, opp);
 *             }
 *             case 7 -> {
 *                 if ((xVal > 0) && (yVal < 7) && (cellValue(location) == opp))
 *                     checker = check(7, xVal - 1, yVal + 1, player, opp);
 *             }
 *             case 8 -> {
 *                 if ((xVal > 0) && (cellValue(location) == opp))
 *                     checker = check(8, xVal - 1, yVal, player, opp);
 *             }
 *         }
 *         return checker;
 *     }
 */