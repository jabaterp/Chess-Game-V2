package Chess;

import Chess.Board.Space;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Display{
    static JFrame frame = new JFrame("Chess Game");
    static boolean selected = false;
    static int[] firstLocClicked, secondLocClicked;
    static ImageIcon firstImage;
    static JLabel[][] boardLabels = new JLabel[8][8];

    public static void displayBoard(Board board) {
        String name, pieceColor, color;
        JPanel mainPanel = new JPanel();
        JPanel gridPanel = new JPanel(new GridLayout(8, 8));
        gridPanel.setBorder(BorderFactory.createTitledBorder("Chess Board"));

        JPanel runPanel = new JPanel(new GridLayout(1, 4));
        JPanel computerPanel = new JPanel(new GridLayout(8, 2));
        JPanel humanPanel = new JPanel(new GridLayout(8, 2));

        Image image;
        for (int i = 7; i >= 0; i--) {
            for (int j = 0; j < 8; j++) {
                int finalI = i;
                int finalJ = j;
                Space sp = board.getSpace(i, j);
                JLabel square;
                color = sp.color.toString().toLowerCase();
                if (!sp.isEmpty) {
                    pieceColor = sp.piece.team == Piece.Team.Computer ? "tan" : "black";
                    name = sp.piece.name.toString().toLowerCase();
                    image = new ImageIcon("resources/" + pieceColor + " " + name + " " + color +
                            " square.jpg").getImage().getScaledInstance(80,80, Image.SCALE_SMOOTH);
                    square = new JLabel(new ImageIcon(image));
                } else {
                    image = new ImageIcon("resources/" + color + " square.jpg").getImage().getScaledInstance(80,80,Image.SCALE_SMOOTH);
                    square = new JLabel(new ImageIcon(image));
                }
                square.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        Display display = new Display();
                        if (!selected && (board.getSpace(finalI, finalJ).isEmpty || board.getSpace(finalI, finalJ).piece.team == Piece.Team.Computer)) {
                            return;
                        }
                        String pieceColor, name, color;
                        if (selected) {
                            secondLocClicked = new int[]{finalI, finalJ};
                            selected = false;
                            if(!Human.move(board, firstLocClicked, secondLocClicked)){
                                boardLabels[firstLocClicked[0]][firstLocClicked[1]].setIcon(firstImage);
                                firstImage = null;
                            }
                        } else {
                            selected = true;
                            firstLocClicked = new int[]{finalI, finalJ};
                            firstImage = (ImageIcon) boardLabels[finalI][finalJ].getIcon();
                            Piece piece = board.getSpace(firstLocClicked).piece;
                            pieceColor = piece.team == Piece.Team.Computer ? "tan" : "black";
                            name = piece.name.toString().toLowerCase();
                            color = board.spaces[finalI][finalJ].color.toString().toLowerCase();
                            Image image = new ImageIcon("resources/" + pieceColor + " " + name + " " + color +
                                    " square selected.jpg").getImage().getScaledInstance(80,80,Image.SCALE_SMOOTH);
                            boardLabels[finalI][finalJ].setIcon(new ImageIcon(image));
                        }
                    }
                });
                boardLabels[i][j] = square;
                gridPanel.add(square);
            }
        }

        mainPanel.add(gridPanel, BorderLayout.CENTER);
        frame.add(mainPanel);
        frame.setSize(710, 810);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public static void makeChange(Board board, int[] startLoc, int[] newLoc) {

        Piece piece = board.getSpace(startLoc).piece;
        String pieceColor = piece.team == Piece.Team.Computer ? "tan" : "black";
        String name = piece.name.toString().toLowerCase();
        String newLocColor = board.spaces[newLoc[0]][newLoc[1]].color.toString().toLowerCase();
        String oldLocColor = board.spaces[startLoc[0]][startLoc[1]].color.toString().toLowerCase();
        Image newLocImage, oldLocImage;
        oldLocImage = new ImageIcon("resources/" + oldLocColor + " square.jpg").getImage().
                getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        newLocImage = new ImageIcon("resources/" + pieceColor + " " + name + " " + newLocColor + " square.jpg")
                .getImage().getScaledInstance(80,80, Image.SCALE_SMOOTH);
        boardLabels[startLoc[0]][startLoc[1]].setIcon(new ImageIcon(oldLocImage));
        boardLabels[newLoc[0]][newLoc[1]].setIcon(new ImageIcon(newLocImage));
        board.turn = board.turn == Piece.Team.Computer ? Piece.Team.Human : Piece.Team.Computer;
        frame.repaint();
        firstLocClicked = null;
        secondLocClicked = null;
    }
}
