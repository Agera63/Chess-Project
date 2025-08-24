package Pieces;

import Position.Pos;

public class Rook extends Piece{

    private static boolean firstWhite = true;
    private static boolean firstBlack = true;

    /**
     * Creates a Pieces.Rook depending on the piece color.
     * @param color Color of the piece (true = black / false = white)
     */
    public Rook(boolean color){
        super("Rook", 'R', new Pos(0, 0), color);
        //If its a black piece, I need to create it on a different side of board and different icon.
        if (!color){
            this.icon = 'r';
            //Checks if the first rook has been placed
            if(firstBlack){
                this.position = new Pos(7,0);
                firstBlack = false;
            } else {
                this.position = new Pos(7,7);
            }
        } else {
            //Checks if the first rook has been placed
            if(firstWhite){
                firstWhite = false;
            } else {
                this.position = new Pos(0,7);
            }
        }
    }

    public Rook(boolean color, Pos p){
        super("Rook", 'R', p, color);
        //If its a black piece, I need to create it on a different side of board and different icon.
        if (!color) {
            this.icon = 'r';
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
