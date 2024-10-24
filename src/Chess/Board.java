package Chess;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Board {
    Space[][] spaces = new Space[8][8];
    Piece.Team turn = Piece.Team.Human;
    int humanScore = 0;
    int computerScore = 0;
    ArrayList<Piece.PieceType> humanCaptured = new ArrayList<Piece.PieceType>();
    ArrayList<Piece.PieceType> computerCaptured = new ArrayList<Piece.PieceType>();
    ArrayList<Piece> computerPieces = new ArrayList<Piece>();
    ArrayList<Piece> humanPieces = new ArrayList<Piece>();
    int[][] castledRook;

    public static class Space {

        enum Color {Brown, Blue};
        Color color;
        boolean isEmpty;
        int[] loc;
        Piece piece;

        public Space(Color color, boolean isEmpty, int[] loc, Piece piece) {
            this.color = color;
            this.isEmpty = isEmpty;
            this.loc = loc;
            this.piece = piece;
        }

        public Space copySpace() {
            if(this.piece == null) {
                return new Space(this.color, this.isEmpty, new int[]{this.loc[0], this.loc[1]}, null);
            }
            return new Space(this.color, this.isEmpty, new int[]{this.loc[0], this.loc[1]}, this.piece.copyPiece());
        }
    }

    public Board() {
        Piece currentPiece;
        Space.Color color;
        Piece.Team team;
        Piece.PieceType pieceType;
        int val;
        int[] loc;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                loc = new int[]{i, j};
                color = (j + i + 2) % 2 == 0 ? Space.Color.Blue : Space.Color.Brown;
                if (i == 1 || i == 6) {
                    team = i == 1 ? Piece.Team.Human : Piece.Team.Computer;
                    currentPiece = new Piece(Piece.PieceType.Pawn, team, loc, 1);
                    spaces[i][j] = new Space(color, false, loc, currentPiece);
                    if(i == 1){
                        this.humanPieces.add(currentPiece);
                    }else{
                        this.computerPieces.add(currentPiece);
                    }
                } else if (i == 0 || i == 7) {
                    team = i == 0 ? Piece.Team.Human : Piece.Team.Computer;
                    switch (j) {
                        case 0, 7:
                            pieceType = Piece.PieceType.Rook;
                            val = 4;
                            break;
                        case 1, 6:
                            pieceType = Piece.PieceType.Knight;
                            val = 3;
                            break;
                        case 2, 5:
                            pieceType = Piece.PieceType.Bishop;
                            val = 3;
                            break;
                        case 3:
                            pieceType = Piece.PieceType.Queen;
                            val = 8;
                            break;
                        case 4:
                            pieceType = Piece.PieceType.King;
                            val = 10;
                            break;
                        default:
                            pieceType = null;
                            val = -1;
                            break;
                    }
                    currentPiece = new Piece(pieceType, team, new int[]{i, j}, val);
                    spaces[i][j] = new Space(color, false, loc, currentPiece);
                    if(i == 0){
                        this.humanPieces.add(currentPiece);
                    }else{
                        this.computerPieces.add(currentPiece);
                    }
                } else {
                    spaces[i][j] = new Space(color, true, new int[]{i, j}, null);
                }
            }
        }
    }

    public Board(Space[][] spaces) {
        this.spaces = spaces;
    }

    public Board copyBoard() {
        Space[][] copySpaces = new Space[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                copySpaces[i][j] = this.spaces[i][j].copySpace();
            }
        }
        Board newBoard = new Board(copySpaces);
        for(Piece humanPiece: this.humanPieces){
            newBoard.humanPieces.add(humanPiece.copyPiece());
        }
        for(Piece compPiece: this.computerPieces){
            newBoard.computerPieces.add(compPiece.copyPiece());
        }
        newBoard.humanCaptured.addAll(this.humanCaptured);
        newBoard.computerCaptured.addAll(this.computerCaptured);
        newBoard.turn = this.turn;
        newBoard.humanScore = this.humanScore;
        newBoard.computerScore = this.computerScore;
        return newBoard;
    }

    public Space getSpace(int[] space) {
        return spaces[space[0]][space[1]];
    }

    public Space getSpace(int i, int j){
        return spaces[i][j];
    }

    public void removePiece(int[] pieceLoc){
        Space sp = this.getSpace(pieceLoc);
        this.computerPieces.remove(sp.piece);
        sp.piece = null;
        sp.isEmpty = true;
    }

    public void printBoard() {
        for (int i = 7; i >= 0; i--) {
            for (int j = 0; j < 8; j++) {
                if (!this.spaces[i][j].isEmpty) {
                    switch (this.spaces[i][j].piece.name) {
                        case King:
                            System.out.print("K");
                            break;
                        case Queen:
                            System.out.print("Q");
                            break;
                        case Bishop:
                            System.out.print("B");
                            break;
                        case Rook:
                            System.out.print("R");
                            break;
                        case Knight:
                            System.out.print("N");
                            break;
                        case Pawn:
                            System.out.print("P");
                            break;
                        default:
                            break;
                    }
                } else {
                    System.out.print("-");
                }
                System.out.print(" ");
            }
            System.out.println();
        }
        System.out.println();
        System.out.println("Human Score: "+this.humanScore);
        System.out.println("Computer Score: "+this.computerScore);

    }
}
