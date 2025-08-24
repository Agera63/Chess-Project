package Pieces;

import Position.Pos;

public class King extends Piece{

    /**
     * Creates a Pieces.King depending on the piece color.
     * @param color Color of the piece (true = black / false = white)
     */
    public King(boolean color){
        super("King", 'K', new Pos(0,4), color);
        //If its a black piece, I need to create it on a different side of board and different icon.
        if (!color){
            this.icon = 'k';
            this.position = new Pos(7,4);
        }
    }

    /**
     * Movement that the piece can do.
     */
    @Override
    public void mouvement(String placeToMove){

    }

    @Override
    public void deactivate() {
        if(isActive){
            isActive = false;
        }
    }
}
