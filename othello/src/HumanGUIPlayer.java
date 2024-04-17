import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;

/**
 * This is part of OthelloGUI.
 * You should not need to edit anything here.
 */
public class HumanGUIPlayer extends MouseAdapter
        implements Player, MouseMotionListener {
    BoardPanel panel;
    int[] move = new int[2];

    public void setPanel (BoardPanel p) {
        panel = p;
    }

    public String getName () {
        return "Human";
    }

    public synchronized void getNextMove(Board b, int [] bestMove) {
        do {
            panel.addMouseListener(this);
            panel.addMouseMotionListener(this);
            try {
                wait();
            } catch (InterruptedException ie) {
            }
        } while (!b.isLegalMove(move));
        bestMove[0] = move[0];
        bestMove[1] = move[1];
        panel.removeMouseListener (this);
        panel.removeMouseMotionListener (this);
    }

    public void mouseDragged(MouseEvent e) {
    }

    public void mouseMoved(MouseEvent e) {
        int[] cell = new int[2];
        int width = panel.getWidth();
        int height = panel.getHeight();
        Point p = e.getPoint();
        cell[0] = p.x*Board.BOARD_DIM/width;
        cell[1] = p.y*Board.BOARD_DIM/height;
        if (panel.isLegalMove(cell)) {
            panel.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        } else {
            panel.setCursor(Cursor.getDefaultCursor());
        }
    }

    /**
     * Invoked when the mouse has been clicked on a component.
     */
    public synchronized void mouseClicked(MouseEvent e) {
        int width = panel.getWidth();
        int height = panel.getHeight();
        Point p = e.getPoint();
        move[0] = p.x*Board.BOARD_DIM/width;
        move[1] = p.y*Board.BOARD_DIM/height;
        notify();
    }
}
