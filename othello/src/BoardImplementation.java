import java.lang.*;

/**
 * A basic implementation of the Board interface.
 * @author Lacey Neagle
 * @author Britton Wolfe (Yes, this is code I wrote in college :-)  It's not pretty)
 */

public class BoardImplementation implements Board {
	int[][] boardCells = new int[BOARD_DIM][BOARD_DIM];
	int playr;

	/**
	 * Returns a string representation of this board
	 * That string can be used to reconstruct the board
	 */
	public static String getBoardString(Board b){
		StringBuilder sb = new StringBuilder(200);
		int[] loc = new int[2];

		sb.append(b.getPlayer() + "|");
		for(loc[0]=0; loc[0]<BOARD_DIM; loc[0]++){
			for(loc[1]=0; loc[1]<BOARD_DIM; loc[1]++){
				try{
					sb.append(b.getCell(loc) + ",");
				}catch(IllegalCellException ice){
					System.err.println("In getBoardString, this error should never happen:");
					System.err.println(ice);
					System.exit(1);
				}
			}
			sb.append("|");
		}

		return sb.toString();
	}

	/**
	 * Set this Board to be like the one given by str
	 */
	public void assignFromString(String str){
		String[] tokens = str.split("[,|]+");
		int index = 0;

		playr = Integer.parseInt(tokens[index]);
		index++;

		for(int x=0; x<BOARD_DIM; x++){
			for(int y=0; y<BOARD_DIM; y++){
				boardCells[x][y] = Integer.parseInt(tokens[index]);
				index++;
			}
		}
	}

	public BoardImplementation(){
		initBoard();
	}

	public BoardImplementation(BoardImplementation orig){
		for(int i=0; i<BOARD_DIM; i++)
			for(int j=0; j<BOARD_DIM; j++){
				int[] loc=new int[2];
				loc[0]=i;
				loc[1]=j;
				boardCells[i][j] = orig.boardCells[i][j];
			}
		playr = orig.playr;
	}

	public void initBoard(){
		playr = BLACK;
		for(int i=0; i<BOARD_DIM; i++)
			for(int j=0; j<BOARD_DIM; j++)
				boardCells[i][j] = EMPTY;
		boardCells[4][3] = BLACK;
		boardCells[4][4] = WHITE;
		boardCells[3][3] = WHITE;
		boardCells[3][4] = BLACK;
	}

	public Board getClone(){
		Board CloneBoard = new BoardImplementation(this);
		return CloneBoard;
	}

	public int getCell(int[] location) throws IllegalCellException {
		if((location[0]>=0)&&(location[0]<BOARD_DIM)&&(location[1]>=0)&&(location[1]<BOARD_DIM))
			return boardCells[location[0]][location[1]];
		else
			throw new IllegalCellException();
	}

	public int countCells(int cellType){
		int count = 0;
		for(int i=0; i<BOARD_DIM; i++)
			for(int j=0; j<BOARD_DIM; j++)
				if(boardCells[i][j] == cellType)
					count++;
		return count;
	}

	public boolean isLegalMove(int[] location){
		if((location[0]>=0)&&(location[0]<BOARD_DIM)&&(location[1]>=0)&&(location[1]<BOARD_DIM)){
			int player = getPlayer();
			boolean legal = false;
			int i = 1;
			int opp;
			if (player == WHITE)
				opp = BLACK;
			else
				opp = WHITE;
			int xVal = location[0];
			int yVal = location[1];
			if(boardCells[xVal][yVal] == EMPTY){
				if((xVal>1) && (yVal>1) && (boardCells[xVal-1][yVal-1] == opp)){
					legal = check(1, xVal-2, yVal-2, player, opp);
					if(legal == true)
						return legal;
				}
				if((yVal>1) && (boardCells[xVal][yVal-1] == opp)){
					legal = check(2, xVal, yVal-2, player, opp);
					if(legal == true)
						return legal;
				}
				if((xVal<6) && (yVal>1) && (boardCells[xVal+1][yVal-1] == opp)){
					legal = check(3, xVal+2, yVal-2, player, opp);
					if(legal == true)
						return legal;
				}
				if((xVal<6) && (boardCells[xVal+1][yVal] == opp)){
					legal = check(4, xVal+2, yVal, player, opp);
					if(legal == true)
						return legal;
				}
				if((xVal<6) && (yVal<6) && (boardCells[xVal+1][yVal+1] == opp)){
					legal = check(5, xVal+2, yVal+2, player, opp);
					if(legal == true)
						return legal;
				}
				if((yVal<6) && (boardCells[xVal][yVal+1] == opp)){
					legal = check(6, xVal, yVal+2, player, opp);
					if(legal == true)
						return legal;
				}
				if((xVal>1) && (yVal<6) && (boardCells[xVal-1][yVal+1] == opp)){
					legal = check(7, xVal-2, yVal+2, player, opp);
					if(legal == true)
						return legal;
				}
				if((xVal>1) && boardCells[xVal-1][yVal] == opp){
					legal = check(BOARD_DIM, xVal-2, yVal, player, opp);
					if(legal == true)
						return legal;
				}
			}
			return legal;
		}
		else
			return false;
	}

	public void makeMove(int[] location) throws IllegalMoveException{
		int player=getPlayer();
		int opp;
		if (player == WHITE)
			opp = BLACK;
		else
			opp = WHITE;
		if (isLegalMove(location)){
			int xVal = location[0];
			int yVal = location[1];
			boardCells[xVal][yVal]=player;
			if(xVal>1 && yVal>1 && boardCells[xVal-1][yVal-1] == opp){
				if(check(1, xVal-2, yVal-2, player, opp)){
					int x = xVal-1;
					int y = yVal-1;
					while(boardCells[x][y]==opp){
						boardCells[x][y] = player;
						x--;
						y--;
					}
				}
			}
			if(yVal>1 && boardCells[xVal][yVal-1] == opp){
				if(check(2, xVal, yVal-2, player, opp)){
					int c = yVal-1;
					while(boardCells[xVal][c]==opp){
						boardCells[xVal][c] = player;
						c--;
					}
				}
			}
			if(xVal<7 && yVal>1 && boardCells[xVal+1][yVal-1] == opp){
				if(check(3, xVal+2, yVal-2, player, opp)){
					int x = xVal+1;
					int y = yVal-1;
					while(boardCells[x][y]==opp){
						boardCells[x][y] = player;
						x++;
						y--;
					}
				}
			}
			if(xVal<7 && boardCells[xVal+1][yVal] == opp){
				if(check(4, xVal+2, yVal, player, opp)){
					int x = xVal+1;
					while(boardCells[x][yVal]==opp){
						boardCells[x][yVal] = player;
						x++;
					}
				}
			}
			if(xVal<7 && yVal<7 && boardCells[xVal+1][yVal+1] == opp){
				if(check(5, xVal+2, yVal+2, player, opp)){
					int x = xVal+1;
					int y = yVal+1;
					while(boardCells[x][y]==opp){
						boardCells[x][y] = player;
						x++;
						y++;
					}
				}
			}
			if(yVal<7 && boardCells[xVal][yVal+1] == opp){
				if(check(6, xVal, yVal+2, player, opp)){
					int y = yVal+1;
					while(boardCells[xVal][y]==opp){
						boardCells[xVal][y] = player;
						y++;
					}
				}
			}
			if(xVal>1 && yVal<7 && boardCells[xVal-1][yVal+1] == opp){
				if(check(7, xVal-2, yVal+2, player, opp)){
					int x = xVal-1;
					int y = yVal+1;
					while(boardCells[x][y]==opp){
						boardCells[x][y] = player;
						x--;
						y++;
					}
				}
			}
			if((xVal>1) && boardCells[xVal-1][yVal] == opp){
				if(check(BOARD_DIM, xVal-2, yVal, player, opp)){
					int x = xVal-1;
					while(boardCells[x][yVal]==opp){
						boardCells[x][yVal] = player;
						x--;
					}
				}
			}
			playr = opp;
			if(!hasLegalMove()){
				playr=player;
				if(!hasLegalMove())
					playr=EMPTY;
			}
		}
		else{
			throw new IllegalMoveException();
		}
	}




	public int getPlayer(){
		return playr;
	}

	public int getWinner(){
		if(playr != EMPTY){
			// someone has a legal move remaining
			return EMPTY;
		}

		int wcount = 0;
		int bcount = 0;
		for(int i=0; i<BOARD_DIM; i++)
			for(int j=0; j<BOARD_DIM; j++){
				if(boardCells[i][j] == BLACK)
					bcount++;
				else if(boardCells[i][j] == WHITE)
					wcount++;
			}
		if (bcount>wcount)
			return BLACK;
		else if (wcount>bcount)
			return WHITE;
		else
			return EMPTY;
	}

	boolean check(int code, int xVal, int yVal, int player, int opp){
		boolean checker = false;
		if((xVal>=0)&&(xVal<BOARD_DIM)&&(yVal>=0)&&(yVal<BOARD_DIM)&&(boardCells[xVal][yVal] == player))
			checker = true;
		else switch (code){
			case 1:
				if((xVal>0) && (yVal>0) && (boardCells[xVal][yVal] == opp))
					checker = check(1,xVal-1,yVal-1,player,opp);
				break;
			case 2:
				if((yVal>0)&&(boardCells[xVal][yVal] == opp))
					checker = check(2,xVal,yVal-1,player,opp);
				break;
			case 3:
				if((xVal<7) && (yVal>0) && (boardCells[xVal][yVal] == opp))
					checker = check(3,xVal+1,yVal-1,player,opp);
				break;
			case 4:
				if((xVal<7) && (boardCells[xVal][yVal] == opp))
					checker = check(4,xVal+1,yVal,player,opp);
				break;
			case 5:
				if((xVal<7) && (yVal<7) && (boardCells[xVal][yVal] == opp))
					checker = check(5,xVal+1,yVal+1,player,opp);
				break;
			case 6:
				if((yVal<7) && (boardCells[xVal][yVal] == opp))
					checker = check(6,xVal,yVal+1,player,opp);
				break;
			case 7:
				if((xVal>0) && (yVal<7) && (boardCells[xVal][yVal] == opp))
					checker = check(7,xVal-1,yVal+1,player,opp);
				break;
			case 8:
				if((xVal>0) && (boardCells[xVal][yVal] == opp))
					checker = check(BOARD_DIM,xVal-1,yVal,player,opp);
				break;
		}
		return checker;
	}

	boolean hasLegalMove(){
		int[] loc = new int[2];
		for(int i=0; i<BOARD_DIM; i++)
			for(int j=0; j<BOARD_DIM; j++){
				if(boardCells[i][j] == EMPTY){
					loc[0] = i;
					loc[1] = j;
					if (isLegalMove(loc))
						return true;
				}
			}
		return false;
	}
}
