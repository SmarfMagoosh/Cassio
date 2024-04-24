/**
 * This is part of OthelloGUI.
 * You should not need to edit anything here.
 */

package othello;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.util.Properties;
import java.util.StringTokenizer;
import java.io.FileInputStream;

public class OthelloFrame extends JFrame {
    public static final String PROPERTY_FILE="othello.ini";
    public static final String BOARD_KEY="othello.boardClass";
    public static final String PLAYERS_KEY="othello.playerClasses";

    Properties props = new Properties (System.getProperties());
    private BoardPanel board;
    private OthelloThread game;
    private PlayerPanel player1;
    private PlayerPanel player2;
    private JButton init;
    private JButton play;
    private JButton exit;
    private JCheckBox shouldDelay;

    public OthelloFrame () {
        super ("Othello");
        try {
            props.load(new FileInputStream (PROPERTY_FILE));
        } catch (Exception e) {
            System.out.println ("Couldn't get properties from "+PROPERTY_FILE);
            System.out.println ("(You should have a file called "+
                    PROPERTY_FILE+" in this directory.");
            System.out.println ("Get a new copy from the class site if you "+
                    "messed up yours.)");
            return;
        }

        board = new BoardPanel(new BoardImplementation(), props);
        board.initBoard();
        String[] players = parseArray(props.get(PLAYERS_KEY).toString());
        player1 = new PlayerPanel (board, players, board.getPlayer1Color());
        player2 = new PlayerPanel (board, players, board.getPlayer2Color());
        JPanel playerPanel = new JPanel (new GridLayout (2,1));
        playerPanel.add(player1);
        playerPanel.add(player2);

        JPanel buttonPanel = new JPanel (new FlowLayout(FlowLayout.CENTER));
        JPanel buttons = new JPanel (new GridLayout (1,0,5,5));
        init = new JButton ("Init");
        play = new JButton ("Play");
        exit = new JButton ("Exit");
        shouldDelay = new JCheckBox("Pause After Move", true);

        init.addActionListener (new ActionListener() {
            /**
             * Invoked when an action occurs.
             */
            public void actionPerformed(ActionEvent e) {
                pause();
                init();
                play.setText("Play");
            }
        });
        play.addActionListener (new ActionListener() {
            /**
             * Invoked when an action occurs.
             */
            public void actionPerformed(ActionEvent e) {
                if (play.getText().equals("Play")) {
                    play();
                    play.setText("Pause");
                } else {
                    pause();
                    play.setText("Play");
                }
            }
        });
        exit.addActionListener (new ActionListener() {
            /**
             * Invoked when an action occurs.
             */

            public void actionPerformed(ActionEvent e) {
                exit();
            }
        });

        buttons.add (init);
        buttons.add (play);
        buttons.add (exit);

        buttonPanel.add (shouldDelay);
        buttonPanel.add (buttons);

        getContentPane().add(board, BorderLayout.CENTER);
        getContentPane().add(playerPanel, BorderLayout.NORTH);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        addWindowListener(new AppWindowListener());
        setSize(500,600);
    }

    class AppWindowListener extends WindowAdapter {
        public void windowClosing(WindowEvent e) {
            exit();
        }
    }

    public static String[] parseArray (String str) {
        StringTokenizer tokens = new StringTokenizer (str, ",");
        String[] array = new String[tokens.countTokens()];
        for (int i=0;i<array.length; i++) {
            array[i] = tokens.nextToken().trim();
        }
        return array;
    }

    public void init () {
        board.initBoard();
    }

    public void play () {
        if (game == null) {
            long delay = 0;
            if(	shouldDelay.isSelected() ){
                delay = 2000; // in milliseconds
            }
            game = new OthelloThread (board, player1, player2, delay) {
                public void madeMove () {
                    player1.setCount (board.countCells(Board.BLACK));
                    player2.setCount (board.countCells(Board.WHITE));
                }
                public void gameOver () {
                    game = null;
                    showWinner();
                    play.setText("Play");
                }
            };
        }
        game.playGame();
    }

    public void pause () {
        if (game != null) {
            game.pauseGame();
        }
    }

    public void exit () {
        System.exit(1);
    }

    public void showWinner () {
        if (board.getWinner() == Board.EMPTY) {
            JOptionPane.showMessageDialog(this, "The game is a tie");
        } else {
            Player winner = ((board.getWinner() == Board.BLACK) ? player1 : player2);
            JOptionPane.showMessageDialog(this, winner.getName()+" wins the game");
        }
    }
}
