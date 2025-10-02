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
        GameManager.getGameObjects().add(this);
    }

    /**
     * This constructor is for the SimulationClass. To create temporary pieces and simulate everything
     * @param King The piece we want to make a copy of.
     */
    public King(King King) {
        super(King.name, King.icon, new Pos(King.position.num, King.position.letter), King.color);
        canCastle = King.canCastle;
    }

    /**
     * Movement that the piece can do.
     */
    @Override
    public void mouvement(String placeToMove){
        char[][] temporaryBoard = PieceManagers.getBoard();

        //Checks if we want to caslte the king
        if(placeToMove.equals("g1") || placeToMove.equals("c1") ||
                placeToMove.equals("g8") || placeToMove.equals("c8")) {
            Rook r = (Rook) Piece.findPieceOfPos(Pos.stringToPos(Piece.getCastlingMap().get(placeToMove)));
            if(PieceManagers.canCastle(r, this)){
                temporaryBoard[this.position.num][this.position.letter] = '\u0000';
                temporaryBoard[Pos.stringToPos(placeToMove).num][Pos.stringToPos(placeToMove).letter] = this.icon;
                this.position = Pos.stringToPos(placeToMove);
                temporaryBoard = r.castleMovement(temporaryBoard, placeToMove);
            }
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

    /**
     * Checks if the king is currently in check
     * @return true if in check / false if not in check
     */
    public boolean isChecked(){
        //Get King position (DONE)
        String kingPosStr = this.position.posToString();

        //First for is number position, second for is letter position
        for(int number = 0; number < 8; number++){
            for(int letter = 0; letter < 8; letter++){
                if(findPieceOfPos(new Pos(number, letter)) != null){
                    char[] movementChart = ((new Pos(number, letter).posToString()) + "-" + kingPosStr).toCharArray();

                    Pos finalPosition = Pos.stringToPos(String.valueOf(movementChart[3]) +
                            String.valueOf(movementChart[4]).toLowerCase());
                    Piece PieceToMove = findPieceOfPos(Pos.stringToPos(String.valueOf(movementChart[0]) +
                            String.valueOf(movementChart[1]).toLowerCase()));

                    //Check if valid move
                    if(Piece.checkPieceMovement(PieceToMove, finalPosition) && this.color != PieceToMove.color){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Finds the white king
     * @return the white king
     */
    public static King findWhiteKing(){
        for(Piece p : GameManager.getGameObjects()) {
            if(p instanceof King && p.color) {
                return (King) p;
            }
        }
        return null;
    }

    /**
     * Finds the black king
     * @return the black king
     */
    public static King findBlackKing(){
        for(Piece p : GameManager.getGameObjects()) {
            if(p instanceof King && !p.color) {
                return (King) p;
            }
        }
        return null;
    }
}
