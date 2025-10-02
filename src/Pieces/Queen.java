package Pieces;

import GameManagement.GameManager;
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
        GameManager.getGameObjects().add(this);
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
        GameManager.getGameObjects().add(this);
        drawPieces();
    }

    /**
     * This constructor is for the SimulationClass. To create temporary pieces and simulate everything
     * @param Queen The piece we want to make a copy of.
     */
    public Queen(Queen Queen) {
        super(Queen.name, Queen.icon, new Pos(Queen.position.num, Queen.position.letter), Queen.color);
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
