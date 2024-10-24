package Chess;

import java.util.ArrayList;
import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Play{

    static Board board = new Board();
    static ArrayList<int[][]> previousMoves = new ArrayList<int[][]>();
    static boolean gameOver = false;
    static Piece.Team turn = board.turn;

    public static void main(String[] args) {
        board.printBoard();
        Display.displayBoard(board);
        String move;
        int[] startLoc = new int[2], newLoc = new int[2];
        while (!gameOver) {
            if(board.turn == Piece.Team.Human){  //TODO check if any valid moves
                while(!Human.moved){
                    try {
                        Thread.sleep(1000);
                    }catch(InterruptedException e){
                        e.printStackTrace();
                    }
                }
                Human.moved = false;
            }else{   //TODO check if any valid moves
                previousMoves.add(Computer.move(board));
                board.turn = Piece.Team.Human;
            }

        }
    }
}