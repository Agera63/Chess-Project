package Pieces;

public enum pieceIcon {
    KING('♔', '♚'),
    QUEEN('♕', '♛'),
    ROOK('♖', '♜'),
    BISHOP('♗', '♝'),
    KNIGHT('♘', '♞'),
    PAWN('♙', '♟');


    public char white;
    public char black;

    pieceIcon(char black, char white){
        this.white = white;
        this.black = black;
    }
}
