package Pieces;

import GameManagement.PieceManagers;
import Position.Pos;

public class Queen extends Piece{

    /**
     * Creates a Pieces.King depending on the piece color.
     * @param color Color of the piece (true = black / false = white)
     */
    public Queen(boolean color){
        super("Queen", 'Q', new Pos(0,3), color);
        //If its a black piece, I need to create it on a different side of board and different icon.
        if (!color) {
            this.icon = 'q';
            this.position = new Pos(7, 3);
        }
    }

    /**
     * This contructor is only used to replace a promoting pawn
     * @param color color of the piece
     * @param p position of the old pawn
     */
    public Queen (boolean color, Pos p){
        super("Queen", 'Q', p, color);
        //If its a black piece, I need to create it on a different side of board and different icon.
        if (!color) {
            this.icon = 'q';
        }
        PieceManagers.getBoard()[p.letter][p.num] = this.icon;
        drawPieces();
    }

    /**
     * Movement that the piece can do.
     */
    @Override
    public void mouvement(String placeToMove) {

    }

    @Override
    public void deactivate() {
        if(isActive){
            isActive = false;
        }
    }
}
