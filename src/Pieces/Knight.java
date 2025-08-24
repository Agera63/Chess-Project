package Pieces;

import Position.Pos;

public class Knight extends Piece{
    private static boolean firstWhite = true;
    private static boolean firstBlack = true;

    /**
     * Creates a Pieces.Knight depending on the piece color.
     * @param color Color of the piece (true = black / false = white)
     */
    public Knight(boolean color){
        super("Knight", 'N', new Pos(0, 1), color);
        //If its a black piece, I need to create it on a different side of board and different icon.
        if (!color){
            this.icon = 'n';
            //Checks if the first Pieces.Knight has been placed
            if(firstBlack){
                this.position = new Pos(7,1);
                firstBlack = false;
            } else {
                this.position = new Pos(7,6);
            }
        } else {
            //Checks if the first Pieces.Knight has been placed
            if(firstWhite){
                firstWhite = false;
            } else {
                this.position = new Pos(0,6);
            }
        }
    }

    public Knight(boolean color, Pos p){
        super("Knight", 'N', p, color);
        //If its a black piece, I need to create it on a different side of board and different icon.
        if (!color) {
            this.icon = 'n';
        }
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
