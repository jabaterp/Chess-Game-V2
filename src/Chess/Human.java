package Chess;
import java.util.ArrayList;

public class Human{
    static boolean moved = false;


    public static boolean move(Board board, int[] startLoc, int[] newLoc){
        if(Move.canMove(board, Piece.Team.Human, startLoc, newLoc)){
            if(Play.turn == Piece.Team.Human) {    //Check to see if computer is testing boards
                Display.makeChange(board, startLoc, newLoc);
            }
            Move.move(board, Piece.Team.Human, startLoc, newLoc);
            board.turn = Piece.Team.Computer;
            Play.turn = Piece.Team.Computer;
            moved = true;
            return true;
        }
        return false;
    }

}