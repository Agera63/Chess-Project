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
     * Draws the pieces and sets the board.
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
     *                  false checks if piece of same color is in the position
     * @return true if no piece of same color in that slot / false if there is a piece in that position
     */
    public boolean checkPosToMove(Piece p, Pos posToMove, boolean condition){
        for(Piece tempPiece : pieceTable){
            if(condition) {
                if(posToMove.num - 1 == tempPiece.position.num && posToMove.letter - 1 == tempPiece.position.letter){
                    return false;
                }
            } else {
                if(posToMove.num - 1 == tempPiece.position.num && posToMove.letter - 1 == tempPiece.position.letter
                        && p.color == tempPiece.color){
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
    public static Piece findPieceOfPos(Pos position){
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
        Pos finalPosition = Pos.stringToPos(String.valueOf(MovementChar[3]) + String.valueOf(MovementChar[4]).toLowerCase());
        Piece PieceToMove = findPieceOfPos(Pos.stringToPos(String.valueOf(MovementChar[0]) + String.valueOf(MovementChar[1]).toLowerCase()));

        //the piece can not be null or it will crash the game
        if(PieceToMove == null) {
            return false;
        }

        String movementType = Pos.checkMovementDirection(PieceToMove.position, finalPosition);
        //Logic for all the white pieces
        if(PieceToMove.color){
            if(movementType.equals("vertical") && PieceToMove instanceof Pawn){
                if(PieceToMove.checkPosToMove(PieceToMove, finalPosition, true) &&
                        Pos.squaresMoved(movementType, PieceToMove.position, finalPosition) == 1
                        && finalPosition.num - PieceToMove.position.num  > 0){
                    return true;
                }
            } else if (movementType.equals("diagonal") && PieceToMove instanceof Pawn) {
                boolean temp = !PieceToMove.checkPosToMove(PieceToMove, finalPosition, false);
                if(temp &&
                        Pos.squaresMoved(movementType, PieceToMove.position, finalPosition) == 1
                        && finalPosition.num - PieceToMove.position.num  > 0
                        && findPieceOfPos(finalPosition) != null){
                    if(findPieceOfPos(finalPosition).color == !PieceToMove.color){
                        return true;
                    }
                }
            } else if (movementType.equals("vertical") && PieceToMove instanceof Rook) {

            } else if (movementType.equals("horizontal") && PieceToMove instanceof Rook) {

            } else if (movementType.equals("vertical") && PieceToMove instanceof Queen) {

            } else if (movementType.equals("horizontal") && PieceToMove instanceof Queen) {

            } else if (movementType.equals("diagonal") && PieceToMove instanceof Queen) {

            } else if (movementType.equals("vertical") && PieceToMove instanceof King) {

            } else if (movementType.equals("horizontal") && PieceToMove instanceof King) {

            } else if (movementType.equals("diagonal") && PieceToMove instanceof King) {

            } else if (movementType.equals("knight") && PieceToMove instanceof Knight){

            }
        } else {
            if(movementType.equals("vertical") && PieceToMove instanceof Pawn){

            } else if (movementType.equals("diagonal") && PieceToMove instanceof Pawn) {

            } else if (movementType.equals("vertical") && PieceToMove instanceof Rook) {

            } else if (movementType.equals("horizontal") && PieceToMove instanceof Rook) {

            } else if (movementType.equals("vertical") && PieceToMove instanceof Queen) {

            } else if (movementType.equals("horizontal") && PieceToMove instanceof Queen) {

            } else if (movementType.equals("diagonal") && PieceToMove instanceof Queen) {

            } else if (movementType.equals("vertical") && PieceToMove instanceof King) {

            } else if (movementType.equals("horizontal") && PieceToMove instanceof King) {

            } else if (movementType.equals("diagonal") && PieceToMove instanceof King) {

            } else if (movementType.equals("knight") && PieceToMove instanceof Knight){

            }
        }
        return false;
    }
}
