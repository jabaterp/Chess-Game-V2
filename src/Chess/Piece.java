package Chess;

public class Piece {

    public static enum Team{Computer, Human};
    public static enum PieceType{King, Queen, Rook, Knight, Bishop, Pawn};

    PieceType name;
    Team team;
    int[] loc;
    int val;
    boolean moved;

    public Piece(PieceType name, Team team, int[] loc, int val){
        this.name = name;
        this.team = team;
        this.loc = loc;
        this.val = val;
    }

    public Piece copyPiece(){
        return new Piece(this.name, this.team, new int[]{this.loc[0], this.loc[1]}, this.val);
    }


}
