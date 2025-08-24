package Pieces;

import Position.Pos;

public class Bishop extends Piece{
    private static boolean firstWhite = true;
    private static boolean firstBlack = true;

    /**
     * Creates a Pieces.Bishop depending on the piece color.
     * @param color Color of the piece (true = black / false = white)
     */
    public Bishop(boolean color){
        super("Bishop", 'B', new Pos(0, 2), color);
        //If its a black piece, I need to create it on a different side of board and different icon.
        if (!color){
            this.icon = 'b';
            //Checks if the first Pieces.Bishop has been placed
            if(firstBlack){
                this.position = new Pos(7,2);
                firstBlack = false;
            } else {
                this.position = new Pos(7,5);
            }
        } else {
            //Checks if the first Pieces.Bishop has been placed
            if(firstWhite){
                firstWhite = false;
            } else {
                this.position = new Pos(0,5);
            }
        }
    }

    public Bishop(boolean color, Pos p){
        super("Bishop", 'B', p, color);
        //If its a black piece, I need to create it on a different side of board and different icon.
        if (!color) {
            this.icon = 'b';
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
