package Chess;

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Computer {

    /*
       Order of Computer Move Decision
       1. Check if any pieces are in danger
       2. See if can attack higher piece
       3. If cant attack higher and in danger, see if can defend itself vs can move out of danger (which gives human less open moves)
       4. If not in danger, see which move gives human least amount of moves
       5. If all same then return random move

       AI Section (3 levels)
       1. look ahead (3, 5 or 10) moves to see best move
     */

    static boolean testingMoves = false;

    public static int[][] move(Board board) {
        int[][] move = bestMove(board);
        if (move == null) {
            return null;
        }
        if (!testingMoves) {    //Only change the board and turn if making an actual move
            Display.makeChange(board, move[0], move[1]);
            Play.turn = Piece.Team.Human;
        }
        Move.move(board, Piece.Team.Computer, move[0], move[1]);
        return move;
    }

    private static int[][] bestMove(Board board) {
        Map<String,ArrayList<Piece>> allMoves = Move.allMoves(board, Piece.Team.Computer);
        if (allMoves.isEmpty()) {
            Play.gameOver = true;
            return null;
        }
        int[][] bestMove = null;
        ArrayList<Piece> inDanger = inDanger(board);
        if (inDanger.isEmpty()) {
            bestMove = canAttackFree(board);
        } else {
            bestMove = canAttackHigher(board, inDanger);
            if(bestMove == null){
                //bestMove = canDefend()
            }
            if(bestMove == null){
                //bestMove = canAttackSacrifice()
            }
            if(bestMove == null){
                //bestMove = canEndanger()
            }
        }
        if (bestMove != null) {
            return bestMove;
        }
        return randomMove(allMoves);
    }

    private static int[][] randomMove(Map<String,ArrayList<Piece>> allMoves){
        Random random = new Random();
        int moveNum = random.nextInt(allMoves.keySet().size());
        String moveString = (String) allMoves.keySet().toArray()[moveNum];
        Piece piece = allMoves.get(moveString).get(random.nextInt(allMoves.get(moveString).size()));
        return new int[][]{piece.loc, new int[]{moveString.charAt(0), moveString.charAt(1)}};
    }

    private static int[][] canAttackFree(Board board){
        for(Piece human: board.humanPieces){
            for(Piece computer: board.computerPieces){
                if(Move.canMove(board, Piece.Team.Computer, computer.loc, human.loc)){
                    
                }
            }
        }
    }

    private static int[][] canAttackHigher(Board board, ArrayList<Piece> inDanger) {        //Higher or Equal
        int maxCompValue = -1;
        String bestMove;
        int bestMoveVal = -1;
        ArrayList<Piece> attackingPieces;

        Board copiedBoard = board.copyBoard();
        for (Piece compPiece : inDanger) {
            if (compPiece.val > maxCompValue) {
                maxCompValue = compPiece.val;
            }
        }

        Map<String, ArrayList<Piece>> allMoves = Move.allMoves(copiedBoard, Piece.Team.Computer);
        int i,j;
        Piece currPiece;
        for(String key: allMoves.keySet()){
            i = Character.getNumericValue(key.charAt(0));
            j = Character.getNumericValue(key.charAt(1));
            currPiece = copiedBoard.getSpace(i,j).piece;
            if(currPiece != null && currPiece.team == Piece.Team.Human && currPiece.val >= bestMoveVal){
                bestMove = key;
                bestMoveVal = currPiece.val;
                attackingPieces = allMoves.get(key);
            }
        }


        return bestMove;
    }

    private static ArrayList<Piece> inDanger(Board board) {
        Board copiedBoard = board.copyBoard();
        ArrayList<int[][]> allHumanMoves = Move.allMoves(copiedBoard, Piece.Team.Human);
        ArrayList<Piece> piecesInDanger = new ArrayList<Piece>();
        for (Piece compPiece : copiedBoard.computerPieces) {
            if (!isDefended(copiedBoard, compPiece)) {
                piecesInDanger.add(compPiece);
            }
        }
        return piecesInDanger;
    }

    private static boolean isDefended(Board board, Piece piece) {
        Board copiedBoard = board.copyBoard();
        Piece lowestPieceAttacking = null;
        int dangerCount = 0;
        for (Piece human : copiedBoard.humanPieces) {
            if (Move.canMove(copiedBoard, Piece.Team.Human, human.loc, piece.loc)) {
                dangerCount++;
                if (lowestPieceAttacking == null || lowestPieceAttacking.val < piece.val) {
                    lowestPieceAttacking = human;
                    if (lowestPieceAttacking.val < piece.val) {
                        return false;
                    }
                }
            }
        }

        if (dangerCount == 0) {
            return true;
        }

        int[] pieceLoc = new int[]{piece.loc[0], piece.loc[1]};
        copiedBoard.removePiece(piece.loc);
        for (Piece compPiece : copiedBoard.computerPieces) {
            if (Move.canMove(copiedBoard, Piece.Team.Computer, compPiece.loc, pieceLoc)) {
                return true;
            }
        }
        return false;
    }


}
