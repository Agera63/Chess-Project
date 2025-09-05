package Pieces;

import GameManagement.GameManager;
import GameManagement.PieceManagers;
import Position.Pos;

import java.util.ArrayList;

public abstract class Piece {
    public static ArrayList<Piece> pieceTable = new ArrayList<>();
    public String name;
    public char icon;
    public boolean isActive;
    public boolean color; //true = white and false = black
    public Pos position;

    /**
     * Pieces.Piece constructor. This is everything any piece of the game needs.
     * @param name The name of the piece.
     * @param icon The icon the piece will have.
     * @param position The position of the piece.
     */
    protected Piece(String name, char icon, Pos position, boolean color){
        this.name = name;
        this.icon = icon;
        this.isActive = true;
        this.position = position;
        this.color = color;
        GameManager.getGameObjects().add(this);
        pieceTable.add(this);
    }

    /**
     * Movement that the piece can do.
     */
    public abstract void mouvement(String placeToMove);

    /**
     * Deactivates and later removes the piece later.
     */
    public abstract void deactivate();

    /**
     * Automaticlly creates the chess board
     */
    public static void boardCreation(){
        boolean temp = true;
            for(int j = 0; j < 2; j++) {
                King K = new King(temp);
                Queen q = new Queen(temp);
                for(int i = 0; i < 2; i++){
                    Bishop b = new Bishop(temp);
                    Knight k = new Knight(temp);
                    Rook r = new Rook(temp);
                }
                for(int i = 0; i < 8; i++){
                    Pawn p = new Pawn(temp);
                }
                temp = false;
        }
        drawPieces();
    }

    /**
     * Draws the pieces and sets the board. This does not show the board in the console.
     */
    public static void drawPieces(){
        char[][] temp = PieceManagers.getBoard();
        for(Piece p : pieceTable) {
            temp[p.position.num][p.position.letter] = p.icon;
        }
        PieceManagers.setBoard(temp);
    }

    /**
     * Check if there is a piece in the place to move depending on the condition.
     * @param p Piece that will move. Only used for its color property
     * @param posToMove The position that the piece (p) is trying to move to.
     * @param condition true checks if any piece is in that position
     *                  false checks if piece of different color is in the position
     * @return true if no piece of same color in that slot / false if there is a piece in that position
     */
    public boolean checkPosToMove(Piece p, Pos posToMove, boolean condition){
        for(Piece tempPiece : pieceTable){
            if(condition) {
                if(posToMove.num == tempPiece.position.num && posToMove.letter == tempPiece.position.letter){
                    return false;
                }
            } else {
                if(posToMove.num == tempPiece.position.num && posToMove.letter == tempPiece.position.letter
                        && p.color != tempPiece.color){
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Looks for a Piece on the board via a position
     * @param position the position that I am looking for
     * @return the Piece in the position OR null if nothing is found
     */
    private static Piece findPieceOfPos(Pos position){
        for(Piece p : GameManager.getGameObjects()){
            if(position.num == p.position.num && position.letter == p.position.letter){
                return p;
            }
        }
        return null;
     }

    /**
     * Checks if the move the user is trying to do is valid
     * @return true if valid / false if not valid
     */
    public static boolean validMove(char[] MovementChar){
        Pos finalPosition = Pos.stringToPos(String.valueOf(MovementChar[3]) +
                String.valueOf(MovementChar[4]).toLowerCase());
        Piece PieceToMove = findPieceOfPos(Pos.stringToPos(String.valueOf(MovementChar[0]) +
                String.valueOf(MovementChar[1]).toLowerCase()));

        try{
            if(PieceToMove == null || PieceToMove.color != GameManager.getColor()) {
                return false;
            } else {
                String movementType = Pos.checkMovementDirection(PieceToMove.position, finalPosition);
                if(movementType.equals("vertical") && PieceToMove instanceof Pawn){
                    if(PieceToMove.color){
                        if(PieceToMove.checkPosToMove(PieceToMove, finalPosition, true) &&
                                (Pos.squaresMoved(movementType, PieceToMove.position, finalPosition) == 1 ||
                                        (Pos.squaresMoved(movementType, PieceToMove.position, finalPosition) == 2 &&
                                                PieceToMove.position.num == 1)) &&
                                finalPosition.num - PieceToMove.position.num  > 0){
                            return true;
                        }
                    } else {
                        if(PieceToMove.checkPosToMove(PieceToMove, finalPosition, true) &&
                                (Pos.squaresMoved(movementType, PieceToMove.position, finalPosition) == 1 ||
                                        (Pos.squaresMoved(movementType, PieceToMove.position, finalPosition) == 2 &&
                                                PieceToMove.position.num == 6)) &&
                                PieceToMove.position.num - finalPosition.num > 0) {
                            return true;
                        }
                    }
                } else if (movementType.equals("diagonal") && PieceToMove instanceof Pawn) {
                    if(PieceToMove.color){
                        if(!PieceToMove.checkPosToMove(PieceToMove, finalPosition, false) &&
                                Pos.squaresMoved(movementType, PieceToMove.position, finalPosition) == 1
                                && finalPosition.num - PieceToMove.position.num  > 0
                                && findPieceOfPos(finalPosition) != null){
                            if(findPieceOfPos(finalPosition).color == !PieceToMove.color){
                                return true;
                            }
                        }
                    } else {
                        if(!PieceToMove.checkPosToMove(PieceToMove, finalPosition, false) &&
                                Pos.squaresMoved(movementType, PieceToMove.position, finalPosition) == 1
                                && PieceToMove.position.num - finalPosition.num > 0
                                && findPieceOfPos(finalPosition) != null){
                            if(findPieceOfPos(finalPosition).color == !PieceToMove.color){
                                return true;
                            }
                        }
                    }
                } else if ((movementType.equals("vertical") || movementType.equals("horizontal")) &&
                        PieceToMove instanceof Rook) {
                    if(!PieceToMove.checkPosToMove(PieceToMove, finalPosition, false) &&
                            !PieceToMove.anyPieceBlocking(finalPosition, movementType)){
                        return true;
                    } else if (PieceToMove.checkPosToMove(PieceToMove, finalPosition, true) &&
                            !PieceToMove.anyPieceBlocking(finalPosition, movementType)){
                        return true;
                    }
                } else if ((movementType.equals("vertical") || movementType.equals("horizontal")
                        || movementType.equals("diagonal")) && PieceToMove instanceof Queen) {
                    if(!PieceToMove.checkPosToMove(PieceToMove, finalPosition, false) &&
                            !PieceToMove.anyPieceBlocking(finalPosition, movementType)){
                        return true;
                    } else if (PieceToMove.checkPosToMove(PieceToMove, finalPosition, true) &&
                            !PieceToMove.anyPieceBlocking(finalPosition, movementType)){
                        return true;
                    }
                } else if ((movementType.equals("vertical") || movementType.equals("horizontal") ||
                        movementType.equals("diagonal") && PieceToMove instanceof King)) {
                    if(!PieceToMove.checkPosToMove(PieceToMove, finalPosition, false) &&
                            !PieceToMove.anyPieceBlocking(finalPosition, movementType) &&
                            Pos.squaresMoved(movementType, PieceToMove.position, finalPosition) == 1){
                        return true;
                    } else if (PieceToMove.checkPosToMove(PieceToMove, finalPosition, true) &&
                            !PieceToMove.anyPieceBlocking(finalPosition, movementType) &&
                            Pos.squaresMoved(movementType, PieceToMove.position, finalPosition) == 1){
                        return true;
                    }
                } else if (movementType.equals("knight") && PieceToMove instanceof Knight) {
                    //if something is there, check color then it can be valid
                    if(!PieceToMove.checkPosToMove(PieceToMove, finalPosition, false) &&
                            findPieceOfPos(finalPosition) != null &&
                            findPieceOfPos(finalPosition).color == !PieceToMove.color){
                        return true;
                    }
                    //if something is not there, its valid
                    else if (PieceToMove.checkPosToMove(PieceToMove, finalPosition, true)){
                        return true;
                    }
                } else if (movementType.equals("diagonal") && PieceToMove instanceof Bishop) {
                    if(!PieceToMove.checkPosToMove(PieceToMove, finalPosition, false) &&
                            !PieceToMove.anyPieceBlocking(finalPosition, movementType)){
                        return true;
                    } else if (PieceToMove.checkPosToMove(PieceToMove, finalPosition, true) &&
                            !PieceToMove.anyPieceBlocking(finalPosition, movementType)){
                        return true;
                    }
                }
                return false;
            }
        } catch (Exception e){
            return false;
        }
    }

    /**
     * Checks to see if there are any pieces that could block the move.
     * @param finalPos The final position the pieces needs to go to
     * @param movementType the type of movement that will be made
     * @return true if something is blocking the move / false if nothing is blocking the move
     */
    private boolean anyPieceBlocking(Pos finalPos, String movementType){
        int slotsToCheck = Pos.squaresMoved(movementType, this.position, finalPos);
        for(int i = 1; i <= slotsToCheck; i++){
            try {
                if(movementType.equals("vertical")) {
                    //checks if we are going to add or substract for the position
                    if(this.position.num < finalPos.num){
                        if(PieceManagers.getBoard()[this.position.num + i][this.position.letter] != '\u0000'){
                            if(finalPos.posToString().equals(new Pos(this.position.num + i, this.position.letter + i).posToString()) &&
                                    findPieceOfPos(new Pos(this.position.num + i, this.position.letter)).color == this.color){
                                return true;
                            }
                        }
                    } else {
                        if(PieceManagers.getBoard()[this.position.num - i][this.position.letter] != '\u0000'){
                            if(finalPos.posToString().equals(new Pos(this.position.num + i, this.position.letter + i).posToString()) &&
                                    findPieceOfPos(new Pos(this.position.num + i, this.position.letter)).color == this.color){
                                return true;
                            }
                        }
                    }
                } else if (movementType.equals("horizontal")) {
                    if(this.position.letter < finalPos.letter){
                        if(PieceManagers.getBoard()[this.position.num][this.position.letter + i] != '\u0000'){
                            if(finalPos.posToString().equals(new Pos(this.position.num + i, this.position.letter + i).posToString()) &&
                                    findPieceOfPos(new Pos(this.position.num + i, this.position.letter)).color == this.color){
                                return true;
                            }
                        }
                    } else {
                        if(PieceManagers.getBoard()[this.position.num][this.position.letter - i] != '\u0000'){
                            if(finalPos.posToString().equals(new Pos(this.position.num + i, this.position.letter + i).posToString()) &&
                                    findPieceOfPos(new Pos(this.position.num + i, this.position.letter)).color == this.color){
                                return true;
                            }
                        }
                    }
                } else if (movementType.equals("diagonal")) {
                    //Up right
                    if(this.position.num < finalPos.num && this.position.letter < finalPos.letter){
                        if(PieceManagers.getBoard()[this.position.num + i][this.position.letter + i] != '\u0000'){
                            if(finalPos.posToString().equals(new Pos(this.position.num + i, this.position.letter + i).posToString())){
                                if (findPieceOfPos(new Pos(this.position.num + i, this.position.letter + i)).color == this.color) {
                                    return true;
                                }
                            } else {
                                return true;
                            }
                        }
                        //Down Right
                    } else if(this.position.num > finalPos.num && this.position.letter < finalPos.letter){
                        if(PieceManagers.getBoard()[this.position.num - i][this.position.letter + i] != '\u0000'){
                            if(finalPos.posToString().equals(new Pos(this.position.num - i, this.position.letter + i).posToString())){
                                if (findPieceOfPos(new Pos(this.position.num - i, this.position.letter + i)).color == this.color) {
                                    return true;
                                }
                            } else {
                                return true;
                            }
                        }
                        //Up Left
                    } else if(this.position.num < finalPos.num && this.position.letter > finalPos.letter) {
                        if(PieceManagers.getBoard()[this.position.num + i][this.position.letter - i] != '\u0000'){
                            if(finalPos.posToString().equals(new Pos(this.position.num + i, this.position.letter - i).posToString())){
                                if (findPieceOfPos(new Pos(this.position.num + i, this.position.letter - i)).color == this.color) {
                                    return true;
                                }
                            } else {
                                return true;
                            }
                        }
                        //Down Left
                    } else if (this.position.num > finalPos.num && this.position.letter > finalPos.letter) {
                        if(PieceManagers.getBoard()[this.position.num - i][this.position.letter - i] != '\u0000'){
                            if(finalPos.posToString().equals(new Pos(this.position.num - i, this.position.letter - i).posToString())){
                                if (findPieceOfPos(new Pos(this.position.num - i, this.position.letter - i)).color == this.color) {
                                    return true;
                                }
                            } else {
                                return true;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                //If any exception is caught, we think the move is invalid (to look into)
                return true;
            }
        }
        return false;
    }
}
