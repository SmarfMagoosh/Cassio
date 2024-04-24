/**
 * This is part of OthelloGUI.
 * You should need to edit anything here.
 */

package othello;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

import java.util.Vector;

public class PlayerPanel extends JPanel implements Player {
    private BoardPanel board;
    private Player currentPlayer;
    private Color color;
    private JComboBox playerChooser;
    private JLabel countLabel;

    public PlayerPanel (BoardPanel board, String[] playerNames, Color color) {
        super (new FlowLayout());
        this.board = board;
        this.color = color;
        JLabel colorLabel = new JLabel ("      ");
        colorLabel.setBackground(color);
        colorLabel.setOpaque(true);
        add (colorLabel);

        Vector<Player> players = getPlayers (playerNames);

	for(int i=0; i<players.size(); i++){
	    if (players.get(i) instanceof HumanGUIPlayer) {
		((HumanGUIPlayer)(players.get(i))).setPanel(board);
	    }
	}

        playerChooser = new JComboBox (players);
        playerChooser.setRenderer(new PlayerRenderer ());
        currentPlayer = (Player)playerChooser.getSelectedItem();
        playerChooser.addActionListener(new ActionListener () {
                                            /**
                                             * Invoked when an action occurs.
                                             */
                                            public void actionPerformed(ActionEvent e) {
                                                currentPlayer = (Player)playerChooser.getSelectedItem();
                                            }
                                        });
        add (playerChooser);

        countLabel = new JLabel ("  2");
        add (countLabel);
    }

    public void setCount (int count) {
        countLabel.setText("  "+count);
    }

    public Color getColor () {
        return color;
    }

    public void getNextMove(Board board, int[] bestMove) 
    throws IllegalCellException,IllegalMoveException {        
        if (currentPlayer != null) {
            if (board instanceof BoardPanel) {
                board = ((BoardPanel)board).getBoard();
            }
            currentPlayer.getNextMove(board, bestMove);
        }
    }

    public String getName() {
        if (currentPlayer != null) {
            return currentPlayer.getName();
        }
        return "<NONE>";
    }

    private Vector<Player> getPlayers(String[] playerNames) {
        Vector<Player> players = new Vector<Player>();
        for (int i=0; i<playerNames.length; i++) {
	    Player p = Misc.getPlayerInstance(playerNames[i]);
	    if(p != null){
		players.add(p);
	    }
        }
        return players;
    }



    public class PlayerRenderer extends DefaultListCellRenderer {
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list,value,index,isSelected,cellHasFocus);
            if (value instanceof Player) {
                setText (((Player)value).getName());
            }
            return this;
        }
    }
}
