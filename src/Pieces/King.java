package Pieces;

import GameManagement.GameManager;
import GameManagement.PieceManagers;
import Position.Pos;

import java.util.HashMap;

public class King extends Piece{

    private boolean canCastle;

    /**
     * Creates a Pieces.King depending on the piece color.
     * @param color Color of the piece (true = black / false = white)
     */
    public King(boolean color){
        super("King", 'K', new Pos(0,4), color);
        canCastle = true;
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
        char[][] temporaryBoard = PieceManagers.getBoard();
        Rook r = (Rook) Piece.findPieceOfPos(Pos.stringToPos(Piece.getCastlingMap().get(placeToMove)));

        //Checks if we want to caslte the king
        if((placeToMove.equals("g1") || placeToMove.equals("c1") ||
                placeToMove.equals("g8") || placeToMove.equals("c8")) && PieceManagers.canCastle(r, this)) {
            temporaryBoard[this.position.num][this.position.letter] = '\u0000';
            temporaryBoard[Pos.stringToPos(placeToMove).num][Pos.stringToPos(placeToMove).letter] = this.icon;
            this.position = Pos.stringToPos(placeToMove);
            temporaryBoard = r.castleMovement(temporaryBoard, placeToMove);
        } else {
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
            canCastle = false;
        }
        PieceManagers.setBoard(temporaryBoard);
    }

    /**
     * Returns the canCastle variable
     * @return true, if can castle / false, if can't castle
     */
    public boolean getCanCastle(){
        return canCastle;
    }

    @Override
    public void deactivate() {
        if(isActive){
            isActive = false;
        }
    }
}
