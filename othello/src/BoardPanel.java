import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import javax.swing.JPanel;

import java.util.Properties;
import java.util.StringTokenizer;

/**
 * This is part of OthelloGUI.
 * You should not edit anything here.
 */
public class BoardPanel extends JPanel implements Board {
    private Board board;
    private Board lastBoard;
    private int[] lastMove;        

	public static final String BACKGROUND_KEY="othello.backgroundColor";
	public static final String GRID_KEY="othello.gridColor";
	public static final String LEGAL_MOVE_KEY="othello.legalMovesColor";
	public static final String PIECE_BORDER_KEY="othello.pieceBorderColor";
	public static final String LAST_BORDER_KEY="othello.lastBorderColor";
	public static final String FLIPPED_BORDER_KEY="othello.flipperBorderColor";
	public static final String PLAYER1_KEY="othello.player1Color";
	public static final String PLAYER2_KEY="othello.player2Color";

    private Color backgroundColor = Color.green.darker();
    private Color gridColor = Color.black;
    private Color legalMoveColor = Color.green;
    private Color pieceBorderColor = Color.black;
    private Color lastPieceBorderColor = Color.red.darker();
    private Color flippedPieceBorderColor = Color.red;
    private Color player1Color = Color.darkGray;
    private Color player2Color = Color.white;

    private Stroke borderStroke = new BasicStroke(2.0f);
    private Stroke gridStroke = new BasicStroke();
    private Stroke pieceBorderStroke = new BasicStroke(2.0f);

    public BoardPanel (Board board) {
        this (board, System.getProperties());
    }

	public BoardPanel (Board board, Properties props) {
		this.board = board;	   
		setBackground(parseColor(props.getProperty(BACKGROUND_KEY), backgroundColor));
		gridColor = parseColor(props.getProperty(GRID_KEY), gridColor);
		legalMoveColor = parseColor(props.getProperty(LEGAL_MOVE_KEY), legalMoveColor);
		pieceBorderColor = parseColor(props.getProperty(PIECE_BORDER_KEY), pieceBorderColor);
		lastPieceBorderColor = parseColor(props.getProperty(LAST_BORDER_KEY), lastPieceBorderColor);
		flippedPieceBorderColor = parseColor(props.getProperty(FLIPPED_BORDER_KEY), flippedPieceBorderColor);
		player1Color = parseColor(props.getProperty(PLAYER1_KEY), player1Color);
		player2Color = parseColor(props.getProperty(PLAYER2_KEY), player2Color);
    }

    public Board getBoard () {
        return board;
    }

    public void setBoard (Board board) {
        this.board = board;
        repaint();
    }

	public Color getPlayer1Color () {
		return player1Color;
	}

	public Color getPlayer2Color () {
		return player2Color;
	}

    public void paintBoard (Graphics2D g) {
        int width = getWidth();
        int height = getHeight();
        g.setColor(gridColor);

        g.setStroke(borderStroke);
        g.drawRect(0,0,width,height);

        g.setStroke(gridStroke);
        for (int line=1; line<Board.BOARD_DIM; line++) {
            g.drawLine(0,(line*height)/Board.BOARD_DIM,width,(line*height)/Board.BOARD_DIM);
            g.drawLine((line*width)/Board.BOARD_DIM,0,(line*width)/Board.BOARD_DIM,height);
        }
    }

    public void paintLegalMove (Graphics2D g, int[] location) {
        int width = getWidth();
        int height = getHeight();
	
		int buffer = 1;
        int x = (location[0]*width)/Board.BOARD_DIM + buffer;
        int y = (location[1]*height)/Board.BOARD_DIM + buffer;
        int squareWidth = width/Board.BOARD_DIM - 2*buffer + 1;
        int squareHeight = height/Board.BOARD_DIM - 2*buffer + 1;
		g.setColor (legalMoveColor);
		g.fillRect(x,y,squareWidth, squareHeight);
	}

    public void paintPiece (Graphics2D g, int[] location) {
        int width = getWidth();
        int height = getHeight();
        try {
			if (getCell(location) == BLACK) {
				g.setColor (player1Color);
			} else {
				g.setColor (player2Color);
			}
		} catch (IllegalCellException ice) {
            System.out.println ("Got IllegalCellExeption in paintPiece");
		} 
        
        int buffer = 3;
        int x = (location[0]*width)/Board.BOARD_DIM + buffer;
        int y = (location[1]*height)/Board.BOARD_DIM + buffer;
        int pieceWidth = width/Board.BOARD_DIM - 2*buffer;
        int pieceHeight = height/Board.BOARD_DIM - 2*buffer;
        g.fillOval(x,y,pieceWidth,pieceHeight);
        
        g.setStroke(pieceBorderStroke);
        if (isLastMove(location)) {
			g.setColor(lastPieceBorderColor);
		} else if (isFlippedPiece(location)) {
			g.setColor(flippedPieceBorderColor);
		} else {
    		g.setColor(pieceBorderColor);
		 }
        g.drawOval(x,y,pieceWidth,pieceHeight);
    }

    public void paintPieces (Graphics2D g) {
        try {
            int[] location = new int[2];
            for (location[0]=0;location[0]<Board.BOARD_DIM;location[0]++) {
                for (location[1]=0;location[1]<Board.BOARD_DIM;location[1]++) {
                    if (getCell(location) != EMPTY) {
                        paintPiece (g, location);
                    } else if (isLegalMove (location)) {
						paintLegalMove(g, location);
					}
                }
            }
        } catch (IllegalCellException ice) {
            System.out.println ("Got IllegalCellExeption in paint");
        }
    }

    public void paint (Graphics g) {
        super.paint(g);
        Graphics2D gr = (Graphics2D)g;
        paintBoard(gr);
        paintPieces(gr);
    }

    public void initBoard() {
        board.initBoard();
        repaint();
    }

    public Board getClone() {
        return board.getClone();
    }

    public int getCell(int[] location)
	throws IllegalCellException {
        return board.getCell(location);
    }
    
    public int countCells(int cellType) {
        return board.countCells(cellType);
    }

    public boolean isLegalMove(int[] location) {
        return board.isLegalMove (location);
    }

    public void makeMove(int[] location)
	throws IllegalMoveException {
        Board last = board.getClone();
        board.makeMove(location);
        lastMove = location;
        lastBoard = last;
        repaint();
    }

    public boolean isLastMove (int[] location) {
        return ((lastMove != null) &&
                (location[0] == lastMove[0]) && 
                (location[1] == lastMove[1]));
    }

    public boolean isFlippedPiece (int[] location) {
        try {
			return ((lastBoard != null) &&
					(lastBoard.getCell(location) != board.getCell(location)));
		} catch (IllegalCellException ice) {
            System.out.println ("Got IllegalCellExeption in isFlippedPiece");
			return false;
		}
	}

    public int getPlayer() {
        return board.getPlayer();
    }

    public int getWinner() {
        return board.getWinner();
    }

	public static Color parseColor (String colorString, Color defaultColor) {
		try {
			StringTokenizer tokens = new StringTokenizer (colorString, ",");
			int r = Integer.parseInt(tokens.nextToken());
			int g = Integer.parseInt(tokens.nextToken());
			int b = Integer.parseInt(tokens.nextToken());
			return new Color(r,g,b);
		} catch (Exception e) {
			return defaultColor;
		}
	}
}
