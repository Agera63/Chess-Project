package Pieces;

import GameManagement.GameManager;
import GameManagement.PieceManagers;
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
