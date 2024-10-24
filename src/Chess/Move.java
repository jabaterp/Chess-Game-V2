package Chess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Move {

    public static void move(Board board, Piece.Team team, int[] startLoc, int[] newLoc) {
        Piece piece = board.getSpace(startLoc).piece;
        piece.loc = newLoc;
        board.getSpace(startLoc).isEmpty = true;
        board.getSpace(startLoc).piece = null;
        if(board.castledRook!=null){
            int[] rookLoc = board.castledRook[0];
            int[] rookNewLoc = board.castledRook[1];
            board.castledRook = null;
            move(board, team, rookLoc, rookNewLoc);

        }
        if (!board.getSpace(newLoc).isEmpty) {
            Piece captured = board.getSpace(newLoc).piece;
            if (team == Piece.Team.Human) {
                board.humanScore += captured.val;
                board.humanCaptured.add(captured.name);
                board.computerPieces.remove(captured);
            } else {
                board.computerScore += captured.val;
                board.computerCaptured.add(captured.name);
                board.humanPieces.remove(captured);
            }
            captured.loc = null;
        }
        board.getSpace(newLoc).isEmpty = false;
        board.getSpace(newLoc).piece = piece;
    }


    public static boolean canMove(Board board, Piece.Team team, int[] startLoc, int[] newLoc) {
        Piece.Team opponent = team == Piece.Team.Computer ? Piece.Team.Human : Piece.Team.Computer;
        board.castledRook = null;

        if (newLoc[0] > 7 || newLoc[0] < 0 || newLoc[1] > 7 || newLoc[1] < 0) {
            return false;
        }
        if (newLoc[0] == startLoc[0] && newLoc[1] == startLoc[1]) {
            return false;
        }
        if (board.getSpace(startLoc).isEmpty || board.getSpace(startLoc).piece.team == opponent) {
            return false;
        }
        if (!board.getSpace(newLoc).isEmpty && board.getSpace(newLoc).piece.team == team) {
            return false;
        }

        Piece.PieceType piece = board.getSpace(startLoc).piece.name;
        switch (piece) {
            case King:
                if (Math.abs(startLoc[0] - newLoc[0]) <= 1 && Math.abs(startLoc[1] - newLoc[1]) <= 1
                        && (board.getSpace(newLoc).isEmpty || board.getSpace(newLoc).piece.team == opponent)) {
                    return true;
                    //TODO set when pieces are moved
                } else if (Math.abs(startLoc[1] - newLoc[1]) == 2 && startLoc[0] - newLoc[0] == 0 && !board.getSpace(startLoc).piece.moved) {
                    int[] rookLoc;
                    if (newLoc[1] == 6) {
                        rookLoc = new int[]{startLoc[0], 7};
                        Piece rook = board.getSpace(rookLoc).piece;
                        if (!board.getSpace(rookLoc).isEmpty && rook.name == Piece.PieceType.Rook && rook.team == team && !rook.moved) {
                            if (board.getSpace(new int[]{startLoc[0], 6}).isEmpty && board.getSpace(new int[]{startLoc[0], 5}).isEmpty) {
                                board.castledRook = new int[][]{rook.loc, new int[]{startLoc[0],5}};
                                return true;
                                //TODO check to make sure king wouldnt be in check at either space
                            }
                        }
                    } else {
                        rookLoc = new int[]{startLoc[0], 0};
                        Piece rook = board.getSpace(rookLoc).piece;
                        if (!board.getSpace(rookLoc).isEmpty && rook.name == Piece.PieceType.Rook && rook.team == team && !rook.moved) {
                            if (board.getSpace(new int[]{startLoc[0], 1}).isEmpty && board.getSpace(new int[]{startLoc[0], 2}).isEmpty
                                    && board.getSpace(new int[]{startLoc[0], 3}).isEmpty) {
                                board.castledRook = new int[][]{rook.loc, new int[]{startLoc[0],3}};
                                return true;
                                //TODO check to make sure king wouldnt be in check at either space
                            }
                        }
                    }
                }
                break;
            case Queen:
                return bishopMove(board, startLoc, newLoc, opponent) || rookMove(board, startLoc, newLoc, opponent);
            case Bishop:
                return bishopMove(board, startLoc, newLoc, opponent);
            case Rook:
                return rookMove(board, startLoc, newLoc, opponent);
            case Knight:
                if ((Math.abs(newLoc[0] - startLoc[0]) == 2 && Math.abs(newLoc[1] - startLoc[1]) == 1)
                        || (Math.abs(newLoc[1] - startLoc[1]) == 2 && Math.abs(newLoc[0] - startLoc[0]) == 1)) {
                    if (board.getSpace(newLoc).isEmpty || board.getSpace(newLoc).piece.team == opponent) {
                        return true;
                    }
                }
                break;
            case Pawn:
                if (team == Piece.Team.Human) {
                    if (newLoc[0] - startLoc[0] == 1) {
                        if (startLoc[1] == newLoc[1] && board.getSpace(newLoc).isEmpty) {
                            return true;
                        }
                        if (Math.abs(startLoc[1] - newLoc[1]) == 1 && !board.getSpace(newLoc).isEmpty &&
                                board.getSpace(newLoc).piece.team == Piece.Team.Computer) {
                            return true;
                        }
                    }
                    if (newLoc[0] - startLoc[0] == 2 && startLoc[1] == newLoc[1] && startLoc[0] == 1
                            && board.getSpace(new int[]{newLoc[0] + 1, newLoc[1]}).isEmpty && board.getSpace(newLoc).isEmpty) {
                        return true;
                    }
                } else {
                    if (startLoc[0] - newLoc[0] == 1) {
                        if (startLoc[1] == newLoc[1] && board.getSpace(newLoc).isEmpty) {
                            return true;
                        }
                        if (Math.abs(newLoc[1] - startLoc[1]) == 1 && !board.getSpace(newLoc).isEmpty
                                && board.getSpace(newLoc).piece.team == Piece.Team.Human) {
                            return true;
                        }
                    }
                    if (startLoc[0] - newLoc[0] == 2 && startLoc[1] == newLoc[1] && startLoc[0] == 6
                            && board.getSpace(new int[]{newLoc[0] - 1, newLoc[1]}).isEmpty && board.getSpace(newLoc).isEmpty) {
                        return true;
                    }
                }
                break;
            default:
                return false;
        }
        return false;
    }

    private static boolean rookMove(Board board, int[] startLoc, int[] newMove, Piece.Team opponent) {
        int max, min;
        Board.Space sp;
        if (startLoc[0] == newMove[0]) {
            max = Math.max(startLoc[1], newMove[1]) - 1; //Dont need to check the max since we checked if newMove was valid above
            min = Math.min(startLoc[1], newMove[1]);
            while (max > min) {
                sp = board.getSpace(new int[]{startLoc[0], max--});
                if (!sp.isEmpty) {
                    return false;
                }
            }
        } else if (startLoc[1] == newMove[1]) {
            max = Math.max(startLoc[0], newMove[0]) - 1;
            min = Math.min(startLoc[0], newMove[0]);
            while (max > min) {
                sp = board.getSpace(new int[]{max--, startLoc[1]});
                if (!sp.isEmpty) {
                    return false;
                }
            }
        } else {
            return false;
        }
        return true;
    }

    private static boolean bishopMove(Board board, int[] startLoc, int[] newMove, Piece.Team opponent) {
        int rowMax, rowMin, colMax, colMin;
        int[] testLoc;
        if (Math.abs(startLoc[0] - newMove[0]) == Math.abs(startLoc[1] - newMove[1])) {
            testLoc = new int[]{newMove[0] - 1, newMove[1] - 1};    //Already checked if newMove is a valid place in canMove
            if (newMove[0] > startLoc[0] && newMove[1] > startLoc[1]) {
                while (testLoc[0] != startLoc[0]) {
                    if (!board.getSpace(testLoc).isEmpty) {
                        return false;
                    }
                    testLoc[0]--;
                    testLoc[1]--;
                }
            } else if (newMove[0] > startLoc[0] && newMove[1] < startLoc[1]) {
                testLoc = new int[]{newMove[0] - 1, newMove[1] + 1};
                while (testLoc[0] != startLoc[0]) {
                    if (!board.getSpace(testLoc).isEmpty) {
                        return false;
                    }
                    testLoc[0]--;
                    testLoc[1]++;
                }
            } else if (newMove[0] < startLoc[0] && newMove[1] < startLoc[1]) {
                testLoc = new int[]{newMove[0] + 1, newMove[1] + 1};
                while (testLoc[0] != startLoc[0]) {
                    if (!board.getSpace(testLoc).isEmpty) {
                        return false;
                    }
                    testLoc[0]++;
                    testLoc[1]++;
                }
            } else {
                testLoc = new int[]{newMove[0] + 1, newMove[1] - 1};
                while (testLoc[0] != startLoc[0]) {
                    if (!board.getSpace(testLoc).isEmpty) {
                        return false;
                    }
                    testLoc[0]++;
                    testLoc[1]--;
                }
            }
            return true;
        }
        return false;
    }

    public static Map<String,ArrayList<Piece>> allMoves(Board board, Piece.Team team) {
        Map<String, ArrayList<Piece>> allMoves = new HashMap<String, ArrayList<Piece>>();
        ArrayList<Piece> pieces = team == Piece.Team.Computer ? board.computerPieces : board.humanPieces;
        ArrayList<Piece> piecesCanMove;
        String pieceLocString;
        for (Piece piece : pieces) {
            switch (piece.name) {
                case Pawn:
                    boolean firstMoveFree = false;
                    if(team == Piece.Team.Computer) {
                        if (Move.canMove(board, Piece.Team.Computer, piece.loc, new int[]{piece.loc[0] - 1, piece.loc[1]})) {
                            if(allMoves.containsKey(piece.loc[0] - 1 +""+piece.loc[1])){
                                allMoves.get(piece.loc[0] - 1 +""+piece.loc[1]).add(piece);
                            }else{
                                piecesCanMove = new ArrayList<Piece>();
                                piecesCanMove.add(piece);
                                allMoves.put(piece.loc[0] - 1 +""+piece.loc[1], piecesCanMove);
                            }
                        }
                        if (Move.canMove(board, Piece.Team.Computer, piece.loc, new int[]{piece.loc[0] - 2, piece.loc[1]})) {
                            if(allMoves.containsKey(piece.loc[0] - 2 +""+piece.loc[1])){
                                allMoves.get(piece.loc[0] - 2 +""+piece.loc[1]).add(piece);
                            }else{
                                piecesCanMove = new ArrayList<Piece>();
                                piecesCanMove.add(piece);
                                allMoves.put(piece.loc[0] - 2 +""+piece.loc[1], piecesCanMove);
                            }
                        }
                        if (Move.canMove(board, Piece.Team.Computer, piece.loc, new int[]{piece.loc[0] - 1, piece.loc[1] - 1})) {
                            if(allMoves.containsKey(piece.loc[0] - 1 +""+(piece.loc[1] - 1))){
                                allMoves.get(piece.loc[0] - 1 +""+(piece.loc[1]-1)).add(piece);
                            }else{
                                piecesCanMove = new ArrayList<Piece>();
                                piecesCanMove.add(piece);
                                allMoves.put(piece.loc[0] - 1 +""+(piece.loc[1] - 1), piecesCanMove);
                            }
                        }
                        if (Move.canMove(board, Piece.Team.Computer, piece.loc, new int[]{piece.loc[0] - 1, piece.loc[1] + 1})) {
                            if(allMoves.containsKey(piece.loc[0] - 1 +""+piece.loc[1] + 1)){
                                allMoves.get(piece.loc[0]- 1 +""+piece.loc[1] + 1).add(piece);
                            }else{
                                piecesCanMove = new ArrayList<Piece>();
                                piecesCanMove.add(piece);
                                allMoves.put(piece.loc[0] - 1 +""+piece.loc[1] + 1, piecesCanMove);
                            }
                        }
                    }else{
                        if (Move.canMove(board, Piece.Team.Human, piece.loc, new int[]{piece.loc[0] + 1, piece.loc[1]})) {
                            if(allMoves.containsKey(piece.loc[0] + 1 +""+piece.loc[1])){
                                allMoves.get(piece.loc[0] + 1 +""+piece.loc[1]).add(piece);
                            }else{
                                piecesCanMove = new ArrayList<Piece>();
                                piecesCanMove.add(piece);
                                allMoves.put(piece.loc[0] + 1 +""+piece.loc[1], piecesCanMove);
                            }
                        }
                        if (Move.canMove(board, Piece.Team.Human, piece.loc, new int[]{piece.loc[0] + 2, piece.loc[1]})) {
                            if(allMoves.containsKey(piece.loc[0] + 2 +""+piece.loc[1])){
                                allMoves.get(piece.loc[0] + 2 +""+piece.loc[1]).add(piece);
                            }else{
                                piecesCanMove = new ArrayList<Piece>();
                                piecesCanMove.add(piece);
                                allMoves.put(piece.loc[0] + 2 +""+piece.loc[1], piecesCanMove);
                            }
                        }
                        if (Move.canMove(board, Piece.Team.Human, piece.loc, new int[]{piece.loc[0] + 1, piece.loc[1] - 1})) {
                            if(allMoves.containsKey(piece.loc[0] + 1 +""+(piece.loc[1] - 1))){
                                allMoves.get(piece.loc[0] + 1 +""+(piece.loc[1] - 1)).add(piece);
                            }else{
                                piecesCanMove = new ArrayList<Piece>();
                                piecesCanMove.add(piece);
                                allMoves.put(piece.loc[0] + 1 +""+(piece.loc[1] - 1), piecesCanMove);
                            }
                        }
                        if (Move.canMove(board, Piece.Team.Human, piece.loc, new int[]{piece.loc[0] + 1, piece.loc[1] + 1})) {
                            if(allMoves.containsKey(piece.loc[0] + 1 +""+(piece.loc[1] + 1))){
                                allMoves.get(piece.loc[0] + 1 +""+(piece.loc[1] + 1)).add(piece);
                            }else{
                                piecesCanMove = new ArrayList<Piece>();
                                piecesCanMove.add(piece);
                                allMoves.put(piece.loc[0] + 1 +""+(piece.loc[1] + 1), piecesCanMove);
                            }
                        }
                    }
                    break;

                case Rook:
                    checkRookMoves(board, allMoves, piece);
                    break;
                case Bishop:
                    checkBishopMoves(board, allMoves, piece);
                    break;
                case Knight:
                    if(Move.canMove(board, team, piece.loc, new int[]{piece.loc[0] + 2, piece.loc[1] + 1 })){
                        if(allMoves.containsKey(piece.loc[0] + 2 + ""+(piece.loc[1] + 1))){
                            allMoves.get(piece.loc[0] + 2 + ""+(piece.loc[1] + 1)).add(piece);
                        }else{
                            piecesCanMove = new ArrayList<Piece>();
                            piecesCanMove.add(piece);
                            allMoves.put(piece.loc[0] + 2 + ""+(piece.loc[1] + 1), piecesCanMove);
                        }
                    }
                    if(Move.canMove(board, team, piece.loc, new int[]{piece.loc[0] + 2, piece.loc[1] - 1})){
                        if(allMoves.containsKey(piece.loc[0] + 2 + ""+(piece.loc[1] - 1))){
                            allMoves.get(piece.loc[0] + 2 + ""+(piece.loc[1] - 1)).add(piece);
                        }else{
                            piecesCanMove = new ArrayList<Piece>();
                            piecesCanMove.add(piece);
                            allMoves.put(piece.loc[0] + 2 + ""+(piece.loc[1] - 1), piecesCanMove);
                        }
                    }
                    if(Move.canMove(board, team, piece.loc, new int[]{piece.loc[0] - 2, piece.loc[1] + 1})){
                        if(allMoves.containsKey(piece.loc[0] - 2 + ""+(piece.loc[1] + 1))){
                            allMoves.get(piece.loc[0] - 2 + ""+(piece.loc[1] + 1)).add(piece);
                        }else{
                            piecesCanMove = new ArrayList<Piece>();
                            piecesCanMove.add(piece);
                            allMoves.put(piece.loc[0] - 2 + ""+(piece.loc[1] + 1), piecesCanMove);
                        }
                    }
                    if(Move.canMove(board, team, piece.loc, new int[]{piece.loc[0] - 2, piece.loc[1] - 1})){
                        if(allMoves.containsKey(piece.loc[0] - 2 + ""+(piece.loc[1] - 1))){
                            allMoves.get(piece.loc[0] - 2 + ""+(piece.loc[1] - 1)).add(piece);
                        }else{
                            piecesCanMove = new ArrayList<Piece>();
                            piecesCanMove.add(piece);
                            allMoves.put(piece.loc[0] - 2 + ""+(piece.loc[1] - 1), piecesCanMove);
                        }
                    }

                    if(Move.canMove(board, team, piece.loc, new int[]{piece.loc[0] + 1, piece.loc[1] + 2 })){
                        if(allMoves.containsKey(piece.loc[0] + 1 + ""+(piece.loc[1] + 2))){
                            allMoves.get(piece.loc[0] + 21 + ""+(piece.loc[1] + 2)).add(piece);
                        }else{
                            piecesCanMove = new ArrayList<Piece>();
                            piecesCanMove.add(piece);
                            allMoves.put(piece.loc[0] + 1 + ""+(piece.loc[1] + 2), piecesCanMove);
                        }
                    }
                    if(Move.canMove(board, team, piece.loc, new int[]{piece.loc[0] + 1, piece.loc[1] - 2})){
                        if(allMoves.containsKey(piece.loc[0] + 1 + ""+(piece.loc[1] - 2))){
                            allMoves.get(piece.loc[0] + 1 + ""+(piece.loc[1] - 2)).add(piece);
                        }else{
                            piecesCanMove = new ArrayList<Piece>();
                            piecesCanMove.add(piece);
                            allMoves.put(piece.loc[0] + 1 + ""+(piece.loc[1] - 2), piecesCanMove);
                        }
                    }
                    if(Move.canMove(board, team, piece.loc, new int[]{piece.loc[0] - 1, piece.loc[1] + 2})){
                        if(allMoves.containsKey(piece.loc[0] - 1 + ""+(piece.loc[1] + 2))){
                            allMoves.get(piece.loc[0] - 1 + ""+(piece.loc[1] + 2)).add(piece);
                        }else{
                            piecesCanMove = new ArrayList<Piece>();
                            piecesCanMove.add(piece);
                            allMoves.put(piece.loc[0] - 1 + ""+(piece.loc[1] + 2), piecesCanMove);
                        }
                    }
                    if(Move.canMove(board, team, piece.loc, new int[]{piece.loc[0] - 1, piece.loc[1] - 2})){
                        if(allMoves.containsKey(piece.loc[0] - 1 + ""+(piece.loc[1] - 2))){
                            allMoves.get(piece.loc[0] - 1 + ""+(piece.loc[1] - 2)).add(piece);
                        }else{
                            piecesCanMove = new ArrayList<Piece>();
                            piecesCanMove.add(piece);
                            allMoves.put(piece.loc[0] - 1 + ""+(piece.loc[1] - 2), piecesCanMove);
                        }
                    }
                    break;

                case King:
                    if(Move.canMove(board, team, piece.loc, new int[]{piece.loc[0] + 1, piece.loc[1] + 1})){
                        if(allMoves.containsKey(piece.loc[0] + 1 + ""+(piece.loc[1] + 1))){
                            allMoves.get(piece.loc[0] + 1 + ""+(piece.loc[1] + 1)).add(piece);
                        }else{
                            piecesCanMove = new ArrayList<Piece>();
                            piecesCanMove.add(piece);
                            allMoves.put(piece.loc[0] + 1 + ""+(piece.loc[1] + 1), piecesCanMove);
                        }
                    }
                    if(Move.canMove(board, team, piece.loc, new int[]{piece.loc[0] + 1, piece.loc[1] - 1})){
                        if(allMoves.containsKey(piece.loc[0] + 1 + ""+(piece.loc[1] - 1))){
                            allMoves.get(piece.loc[0] + 1 + ""+(piece.loc[1] - 1)).add(piece);
                        }else{
                            piecesCanMove = new ArrayList<Piece>();
                            piecesCanMove.add(piece);
                            allMoves.put(piece.loc[0] + 1 + ""+(piece.loc[1] - 1), piecesCanMove);
                        }
                    }
                    if(Move.canMove(board, team, piece.loc, new int[]{piece.loc[0] - 1, piece.loc[1] - 1})){
                        if(allMoves.containsKey(piece.loc[0] - 1 + ""+(piece.loc[1] - 1))){
                            allMoves.get(piece.loc[0] - 1 + ""+(piece.loc[1] - 1)).add(piece);
                        }else{
                            piecesCanMove = new ArrayList<Piece>();
                            piecesCanMove.add(piece);
                            allMoves.put(piece.loc[0] - 1 + ""+(piece.loc[1] - 1), piecesCanMove);
                        }
                    }
                    if(Move.canMove(board, team, piece.loc, new int[]{piece.loc[0] - 1, piece.loc[1] + 1})){
                        if(allMoves.containsKey(piece.loc[0] - 1 + ""+(piece.loc[1] + 1))){
                            allMoves.get(piece.loc[0] - 1 + ""+(piece.loc[1] + 1)).add(piece);
                        }else{
                            piecesCanMove = new ArrayList<Piece>();
                            piecesCanMove.add(piece);
                            allMoves.put(piece.loc[0] - 1 + ""+(piece.loc[1] + 1), piecesCanMove);
                        }
                    }

                    if(Move.canMove(board, team, piece.loc, new int[]{piece.loc[0], piece.loc[1] + 1})){
                        if(allMoves.containsKey(piece.loc[0] + ""+(piece.loc[1] + 1))){
                            allMoves.get(piece.loc[0] + ""+(piece.loc[1] + 1)).add(piece);
                        }else{
                            piecesCanMove = new ArrayList<Piece>();
                            piecesCanMove.add(piece);
                            allMoves.put(piece.loc[0] + ""+(piece.loc[1] + 1), piecesCanMove);
                        }
                    }
                    if(Move.canMove(board, team, piece.loc, new int[]{piece.loc[0] + 1, piece.loc[1]})){
                        if(allMoves.containsKey(piece.loc[0] + 1 + ""+(piece.loc[1]))){
                            allMoves.get(piece.loc[0] + 1 + ""+(piece.loc[1])).add(piece);
                        }else{
                            piecesCanMove = new ArrayList<Piece>();
                            piecesCanMove.add(piece);
                            allMoves.put(piece.loc[0] + 1 + ""+(piece.loc[1]), piecesCanMove);
                        }
                    }
                    if(Move.canMove(board, team, piece.loc, new int[]{piece.loc[0] - 1, piece.loc[1]})){
                        if(allMoves.containsKey(piece.loc[0] - 1 + ""+(piece.loc[1]))){
                            allMoves.get(piece.loc[0] - 1 + ""+(piece.loc[1])).add(piece);
                        }else{
                            piecesCanMove = new ArrayList<Piece>();
                            piecesCanMove.add(piece);
                            allMoves.put(piece.loc[0] - 1 + ""+(piece.loc[1]), piecesCanMove);
                        }
                    }
                    if(Move.canMove(board, team, piece.loc, new int[]{piece.loc[0], piece.loc[1] - 1})){
                        if(allMoves.containsKey(piece.loc[0] + ""+(piece.loc[1] - 1))){
                            allMoves.get(piece.loc[0] + ""+(piece.loc[1] - 1)).add(piece);
                        }else{
                            piecesCanMove = new ArrayList<Piece>();
                            piecesCanMove.add(piece);
                            allMoves.put(piece.loc[0] + ""+(piece.loc[1] - 1), piecesCanMove);
                        }
                    }
                    if(Move.canMove(board, team, piece.loc, new int[]{piece.loc[0] + 2, piece.loc[1]})){
                        if(allMoves.containsKey(piece.loc[0] + 2 + ""+(piece.loc[1]))){
                            allMoves.get(piece.loc[0] + 2 + ""+(piece.loc[1])).add(piece);
                        }else{
                            piecesCanMove = new ArrayList<Piece>();
                            piecesCanMove.add(piece);
                            allMoves.put(piece.loc[0] + 2 + ""+(piece.loc[1]), piecesCanMove);
                        }
                    }
                    if(Move.canMove(board, team, piece.loc, new int[]{piece.loc[0] - 2, piece.loc[1]})){
                        if(allMoves.containsKey(piece.loc[0] - 2 + ""+(piece.loc[1]))){
                            allMoves.get(piece.loc[0] - 2 + ""+(piece.loc[1])).add(piece);
                        }else{
                            piecesCanMove = new ArrayList<Piece>();
                            piecesCanMove.add(piece);
                            allMoves.put(piece.loc[0] - 2 + ""+(piece.loc[1]), piecesCanMove);
                        }
                    }
                    break;
                case Queen:
                    checkRookMoves(board, allMoves, piece);
                    checkBishopMoves(board, allMoves, piece);
                    break;
                default:
                    break;
            }
        }
        return allMoves;
    }

    private static void checkRookMoves(Board board, Map<String, ArrayList<Piece>> allMoves, Piece piece) {
        for (int i = piece.loc[0] + 1; i < 8 && Move.canMove(board, piece.team, piece.loc, new int[]{i, piece.loc[1]}); i++) {
            if(allMoves.containsKey(i + ""+piece.loc[1])){
                allMoves.get(i+""+piece.loc[1]).add(piece);
            }else{
                ArrayList<Piece> piecesCanMove = new ArrayList<Piece>();
                piecesCanMove.add(piece);
                allMoves.put((i + "" + piece.loc[1]), piecesCanMove);
            }
            if (!board.getSpace(i, piece.loc[1]).isEmpty && board.getSpace(i, piece.loc[1]).piece.team != piece.team) {
                break;
            }
        }
        for (int i = piece.loc[0] - 1; i >= 0 && Move.canMove(board, piece.team, piece.loc, new int[]{i, piece.loc[1]}); i--) {
            if(allMoves.containsKey(i + ""+piece.loc[1])){
                allMoves.get(i+""+piece.loc[1]).add(piece);
            }else{
                ArrayList<Piece> piecesCanMove = new ArrayList<Piece>();
                piecesCanMove.add(piece);
                allMoves.put((i + "" + piece.loc[1]), piecesCanMove);
            }
            if (!board.getSpace(i, piece.loc[1]).isEmpty && board.getSpace(i, piece.loc[1]).piece.team != piece.team) {
                break;
            }
        }
        for (int i = piece.loc[1] + 1; i < 8 && Move.canMove(board, piece.team, piece.loc, new int[]{piece.loc[0], i}); i++) {
            if(allMoves.containsKey(piece.loc[0] + "" + i)){
                allMoves.get(piece.loc[0] + "" + i).add(piece);
            }else{
                ArrayList<Piece> piecesCanMove = new ArrayList<Piece>();
                piecesCanMove.add(piece);
                allMoves.put((piece.loc[0] + "" + i), piecesCanMove);
            }
            if (!board.getSpace(piece.loc[0], i).isEmpty && board.getSpace(piece.loc[0], i).piece.team != piece.team) {
                break;
            }
        }
        for (int i = piece.loc[1] - 1; i >= 0 && Move.canMove(board, piece.team, piece.loc, new int[]{piece.loc[0], i}); i++) {
            if(allMoves.containsKey(piece.loc[0] + "" + i)){
                allMoves.get(piece.loc[0] + "" + i).add(piece);
            }else{
                ArrayList<Piece> piecesCanMove = new ArrayList<Piece>();
                piecesCanMove.add(piece);
                allMoves.put((piece.loc[0] + "" + i), piecesCanMove);
            }
            if (!board.getSpace(piece.loc[0], i).isEmpty && board.getSpace(piece.loc[0], i).piece.team != piece.team) {
                break;
            }
        }
    }

    private static void checkBishopMoves(Board board, Map<String, ArrayList<Piece>> allMoves, Piece piece) {
        for (int i = 1; piece.loc[0] + i < 8 && piece.loc[1] + i < 8 && Move.canMove(board, piece.team, piece.loc, new int[]{piece.loc[0]+i, piece.loc[1]+i}); i++) {
            if(allMoves.containsKey(piece.loc[0] + i + "" + (piece.loc[1] + i))){
                allMoves.get(piece.loc[0] + i + "" + (piece.loc[1] + i)).add(piece);
            }else{
                ArrayList<Piece> piecesCanMove = new ArrayList<Piece>();
                piecesCanMove.add(piece);
                allMoves.put((piece.loc[0] + i + "" + (piece.loc[1] + i)), piecesCanMove);
            }
            if (!board.getSpace(piece.loc[0] + i, piece.loc[1] + i).isEmpty &&
                    board.getSpace(piece.loc[0] + i, piece.loc[1] + i).piece.team != piece.team) {
                break;
            }
        }
        for (int i = 1; piece.loc[0] - i >=0 && piece.loc[1] - i >=0 && Move.canMove(board, piece.team, piece.loc, new int[]{piece.loc[0]-i, piece.loc[1]-i}); i++) {
            if(allMoves.containsKey(piece.loc[0] - i + "" + (piece.loc[1] - i))){
                allMoves.get(piece.loc[0] - i + "" + (piece.loc[1] - i)).add(piece);
            }else{
                ArrayList<Piece> piecesCanMove = new ArrayList<Piece>();
                piecesCanMove.add(piece);
                allMoves.put((piece.loc[0] - i + "" + (piece.loc[1] - i)), piecesCanMove);
            }
            if (!board.getSpace(piece.loc[0] - i, piece.loc[1] - i).isEmpty &&
                    board.getSpace(piece.loc[0] - i, piece.loc[1] - i).piece.team != piece.team) {
                break;
            }
        }
        for (int i = 1; piece.loc[0] +i < 8 && piece.loc[1] - i>=0 && Move.canMove(board, piece.team, piece.loc, new int[]{piece.loc[0] +i, piece.loc[1]-i}); i++) {
            if(allMoves.containsKey(piece.loc[0] + i + "" + (piece.loc[1] - i))){
                allMoves.get(piece.loc[0] + i + "" + (piece.loc[1] - i)).add(piece);
            }else{
                ArrayList<Piece> piecesCanMove = new ArrayList<Piece>();
                piecesCanMove.add(piece);
                allMoves.put((piece.loc[0] + i + "" + (piece.loc[1] - i)), piecesCanMove);
            }
            if (!board.getSpace(piece.loc[0] + i, piece.loc[1] - i).isEmpty &&
                    board.getSpace(piece.loc[0] + i, piece.loc[1] - i).piece.team != piece.team) {
                break;
            }
        }
        for (int i = 1; piece.loc[0] -i >=0 && piece.loc[1] + i<8 && Move.canMove(board, piece.team, piece.loc, new int[]{piece.loc[0] -i, piece.loc[1]+i}); i++) {
            if(allMoves.containsKey(piece.loc[0] - i + "" + (piece.loc[1] + i))){
                allMoves.get(piece.loc[0] - i + "" + (piece.loc[1] + i)).add(piece);
            }else{
                ArrayList<Piece> piecesCanMove = new ArrayList<Piece>();
                piecesCanMove.add(piece);
                allMoves.put((piece.loc[0] - i + "" + (piece.loc[1] + i)), piecesCanMove);
            }
            if (!board.getSpace(piece.loc[0] - i, piece.loc[1] + i).isEmpty &&
                    board.getSpace(piece.loc[0] - i, piece.loc[1] + i).piece.team != piece.team) {
                break;
            }
        }
    }
}
