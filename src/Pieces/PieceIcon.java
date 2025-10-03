package Pieces;

public enum PieceIcon {
    KING('♔', '♚'),
    QUEEN('♕', '♛'),
    ROOK('♖', '♜'),
    BISHOP('♗', '♝'),
    KNIGHT('♘', '♞'),
    PAWN('♙', '♟');


    public char white;
    public char black;

    PieceIcon(char black, char white){
        this.white = white;
        this.black = black;
    }

    public static char transaltePieceIcon(char letterIcon){
        switch (letterIcon) {
            case 'B' : return BISHOP.white;
            case 'b' : return BISHOP.black;

            case 'K' : return KING.white;
            case 'k' : return KING.black;

            case 'P' : return PAWN.white;
            case 'p' : return PAWN.black;

            case 'R' : return ROOK.white;
            case 'r' : return ROOK.black;

            case 'N' : return KNIGHT.white;
            case 'n' : return KNIGHT.black;

            case 'Q' : return QUEEN.white;
            case 'q' : return QUEEN.black;
            default : return ' ';
        }
    }
}
