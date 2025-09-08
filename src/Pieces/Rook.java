package Pieces;

import GameManagement.GameManager;
import GameManagement.PieceManagers;
import Position.Pos;

public class Rook extends Piece{

    private static boolean firstWhite = true;
    private static boolean firstBlack = true;
    private boolean canCastle;

    /**
     * Creates a Pieces.Rook depending on the piece color.
     * @param color Color of the piece (true = black / false = white)
     */
    public Rook(boolean color){
        super("Rook", 'R', new Pos(0, 0), color);
        canCastle = true;
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

    /**
     * This is used only when a pawn will promote to a rook
     * @param color color of the piece
     * @param p position of the old pawn
     */
    public Rook(boolean color, Pos p){
        super("Rook", 'R', p, color);
        canCastle = false;
        //If its a black piece, I need to create it on a different side of board and different icon.
        if (!color) {
            this.icon = 'r';
        }
        PieceManagers.getBoard()[p.letter][p.num] = this.icon;
        drawPieces();
    }

    /**
     * Returns the canCastle variable
     * @return true, if can castle / false, if can't castle
     */
    public boolean getCanCastle(){
        return canCastle;
    }

    /**
     * Movement that the piece can do.
     */
    @Override
    public void mouvement(String placeToMove) {
        char[][] temporaryBoard = PieceManagers.getBoard();

        if(!this.checkPosToMove(this, Pos.stringToPos(placeToMove), true)){
            //if we are in this condition, it means that there is a piece of the opposit color that will be removed.
            for(Piece p : GameManager.getGameObjects()){
                String PStringPosition = p.position.posToString();
                if(placeToMove.equals(PStringPosition)){
                    p.deactivate();
                    temporaryBoard[this.position.num][this.position.letter] = '\u0000';
                    temporaryBoard[Pos.stringToPos(placeToMove).num][Pos.stringToPos(placeToMove).letter] = this.icon;
                    this.position = Pos.stringToPos(placeToMove);
                    break;
                }
            }
        } else {
            temporaryBoard[this.position.num][this.position.letter] = '\u0000';
            temporaryBoard[Pos.stringToPos(placeToMove).num][Pos.stringToPos(placeToMove).letter] = this.icon;
            this.position = Pos.stringToPos(placeToMove);
        }
        PieceManagers.setBoard(temporaryBoard);
    }

    @Override
    public void deactivate() {
        if(isActive){
            isActive = false;
        }
    }
}
