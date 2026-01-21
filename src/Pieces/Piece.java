package Pieces;

import GameManagement.GameManager;
import GameManagement.PieceManagers;
import GameManagement.SimulationClass;
import Position.Pos;

import java.util.HashMap;

/**
 * Parts of this class have been modified and simplified by AI
 */
public abstract class Piece {

    public String name;
    public char icon;
    public boolean isActive;
    public boolean color; //true = white and false = black
    public Pos position;
    private static HashMap<String, String> castlingMap;

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
        for(Piece p : GameManager.getGameObjects()) {
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
        for(Piece tempPiece : GameManager.getGameObjects()) {
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
    protected static Piece findPieceOfPos(Pos position){
        for(Piece p : GameManager.getGameObjects()){
            if(position.num == p.position.num && position.letter == p.position.letter){
                return p;
            }
        }
        return null;
    }

    /**
     * Checks if the move the user is trying to do is valid (checks for checks too)
     * This is the TOP-LEVEL validation used during actual gameplay
     * @return true if valid / false if not valid
     */
    public static boolean validMove(char[] MovementChar){
        Pos finalPosition = Pos.stringToPos(String.valueOf(MovementChar[3]) +
                String.valueOf(MovementChar[4]).toLowerCase());
        Piece PieceToMove = findPieceOfPos(Pos.stringToPos(String.valueOf(MovementChar[0]) +
                String.valueOf(MovementChar[1]).toLowerCase()));

        //find both kings
        King whiteKing = King.findWhiteKing();
        King blackKing = King.findBlackKing();

        try{
            if(PieceToMove == null || PieceToMove.color != GameManager.getColor()) {
                return false;
            }

            // Use the unified validation method
            return isValidMoveWithCheckValidation(PieceToMove, finalPosition);
        } catch (Exception e){
            return false;
        }
    }

    /**
     * NEW: Unified method to check if a move is valid AND doesn't leave king in check
     * This is used by both gameplay and checkmate detection
     * @param pieceToMove The piece to move
     * @param finalPosition The target position
     * @return true if move is legal and doesn't leave king in check
     */
    public static boolean isValidMoveWithCheckValidation(Piece pieceToMove, Pos finalPosition) {
        // First check if the basic move is legal
        if(!checkPieceMovement(pieceToMove, finalPosition)) {
            return false;
        }

        // Find the king of the moving piece's color
        King kingToCheck = pieceToMove.color ? King.findWhiteKing() : King.findBlackKing();

        if(kingToCheck == null) {
            return false;
        }

        // Check if king is currently in check
        boolean kingChecked = kingToCheck.isChecked();

        if(kingChecked) {
            // King is in check - move must remove the check
            return SimulationClass.kingSim(pieceToMove, finalPosition, kingToCheck);
        } else {
            // King is not in check - move must not put king in check
            if(pieceToMove instanceof King) {
                // If moving the king, make sure destination is safe
                return SimulationClass.willMoveCheckKing(pieceToMove, finalPosition);
            } else {
                // If moving another piece, make sure it doesn't expose the king
                return SimulationClass.willMoveCheckKing(pieceToMove, finalPosition);
            }
        }
    }

    /**
     * Initializes the castleing map.
     */
    public static void initalizeCastleMap(){
        if(castlingMap == null){
            castlingMap = new HashMap<>();
            //Example : if king moves to g1, rook to move is at h1
            castlingMap.put("g1", "h1");
            castlingMap.put("c1", "a1");
            castlingMap.put("g8", "h8");
            castlingMap.put("c8", "a8");
        }
    }

    /**
     * Returns the castleing map and makes sure its not empty
     * @return Castleingmap
     */
    public static HashMap<String, String > getCastlingMap(){
        if(castlingMap == null){
            initalizeCastleMap();
        }
        return castlingMap;
    }

    /**
     * Helper method to check if a square is under attack by the opposite color
     * @param square The square to check
     * @param friendlyColor The color of the piece we're protecting (true = white, false = black)
     * @return true if square is under attack / false if safe
     */
    private static boolean isSquareUnderAttack(Pos square, boolean friendlyColor) {
        // Check all enemy pieces to see if they can attack this square
        for(Piece enemyPiece : GameManager.getGameObjects()) {
            if(enemyPiece.color != friendlyColor && enemyPiece.isActive) {
                // Check if this enemy piece can move to the square
                // We use basic movement rules (no check validation to avoid recursion)
                String movementType = Pos.checkMovementDirection(enemyPiece.position, square);

                if(movementType == null) {
                    continue;
                }

                // Check based on piece type
                if(enemyPiece instanceof Pawn) {
                    // Pawns attack diagonally
                    if(movementType.equals("diagonal") &&
                            Pos.squaresMoved(movementType, enemyPiece.position, square) == 1) {
                        if(enemyPiece.color) { // white pawn
                            if(square.num - enemyPiece.position.num > 0) {
                                return true;
                            }
                        } else { // black pawn
                            if(enemyPiece.position.num - square.num > 0) {
                                return true;
                            }
                        }
                    }
                } else if(enemyPiece instanceof Knight) {
                    if(movementType.equals("knight")) {
                        return true;
                    }
                } else if(enemyPiece instanceof Bishop) {
                    if(movementType.equals("diagonal") &&
                            !enemyPiece.anyPieceBlocking(square, movementType)) {
                        return true;
                    }
                } else if(enemyPiece instanceof Rook) {
                    if((movementType.equals("vertical") || movementType.equals("horizontal")) &&
                            !enemyPiece.anyPieceBlocking(square, movementType)) {
                        return true;
                    }
                } else if(enemyPiece instanceof Queen) {
                    if((movementType.equals("vertical") || movementType.equals("horizontal") ||
                            movementType.equals("diagonal")) &&
                            !enemyPiece.anyPieceBlocking(square, movementType)) {
                        return true;
                    }
                } else if(enemyPiece instanceof King) {
                    // King attacks one square in any direction
                    if(Pos.squaresMoved(movementType, enemyPiece.position, square) == 1) {
                        return true;
                    }
                }
            }
        }
        return false;
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
                            if(finalPos.posToString().equals(new Pos(this.position.num + i, this.position.letter).posToString()) &&
                                    findPieceOfPos(new Pos(this.position.num + i, this.position.letter)).color == this.color){
                                return true;
                            }
                        }
                    } else {
                        if(PieceManagers.getBoard()[this.position.num - i][this.position.letter] != '\u0000'){
                            if(finalPos.posToString().equals(new Pos(this.position.num - i, this.position.letter).posToString()) &&
                                    findPieceOfPos(new Pos(this.position.num - i, this.position.letter)).color == this.color){
                                return true;
                            }
                        }
                    }
                } else if (movementType.equals("horizontal")) {
                    if(this.position.letter < finalPos.letter){
                        if(PieceManagers.getBoard()[this.position.num][this.position.letter + i] != '\u0000'){
                            if(finalPos.posToString().equals(new Pos(this.position.num, this.position.letter + i).posToString()) &&
                                    findPieceOfPos(new Pos(this.position.num, this.position.letter + i)).color == this.color){
                                return true;
                            }
                        }
                    } else {
                        if(PieceManagers.getBoard()[this.position.num][this.position.letter - i] != '\u0000'){
                            if(finalPos.posToString().equals(new Pos(this.position.num, this.position.letter - i).posToString()) &&
                                    findPieceOfPos(new Pos(this.position.num, this.position.letter - i)).color == this.color){
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

    /**
     * Checks if the movement for a piece is legal (BASIC RULES ONLY - no check validation)
     * This is the foundation that other validation builds on
     * @param PieceToMove Piece to move
     * @param finalPosition position the piece will go to
     * @return true if move follows piece movement rules / false if illegal
     */
    public static boolean checkPieceMovement(Piece PieceToMove, Pos finalPosition) {
        try{
            //start
            String movementType = Pos.checkMovementDirection(PieceToMove.position, finalPosition);
            if(movementType.equals("vertical") && PieceToMove instanceof Pawn){
                if(PieceToMove.color){
                    if(PieceToMove.checkPosToMove(PieceToMove, finalPosition, true) &&
                            (Pos.squaresMoved(movementType, PieceToMove.position, finalPosition) == 1) &&
                            finalPosition.num - PieceToMove.position.num  > 0){
                        ((Pawn) PieceToMove).moves2Squares = false;
                        return true;
                    } else if(PieceToMove.checkPosToMove(PieceToMove, finalPosition, true) &&
                            ((Pos.squaresMoved(movementType, PieceToMove.position, finalPosition) == 2 &&
                                    PieceToMove.position.num == 1)) &&
                            finalPosition.num - PieceToMove.position.num  > 0) {
                        ((Pawn) PieceToMove).moves2Squares = true;
                        return true;
                    }
                } else {
                    if(PieceToMove.checkPosToMove(PieceToMove, finalPosition, true) &&
                            (Pos.squaresMoved(movementType, PieceToMove.position, finalPosition) == 1) &&
                            PieceToMove.position.num - finalPosition.num > 0) {
                        ((Pawn) PieceToMove).moves2Squares = false;
                        return true;
                    } else if (PieceToMove.checkPosToMove(PieceToMove, finalPosition, true) &&
                            ((Pos.squaresMoved(movementType, PieceToMove.position, finalPosition) == 2 &&
                                    PieceToMove.position.num == 6)) &&
                            PieceToMove.position.num - finalPosition.num > 0) {
                        ((Pawn) PieceToMove).moves2Squares = true;
                        return true;
                    }
                }
            } else if (movementType.equals("diagonal") && PieceToMove instanceof Pawn) {
                if(PieceToMove.color){
                    //white capture
                    if(!PieceToMove.checkPosToMove(PieceToMove, finalPosition, false) &&
                            Pos.squaresMoved(movementType, PieceToMove.position, finalPosition) == 1
                            && finalPosition.num - PieceToMove.position.num  > 0
                            && findPieceOfPos(finalPosition) != null){
                        if(findPieceOfPos(finalPosition).color == !PieceToMove.color){
                            ((Pawn) PieceToMove).moves2Squares = false;
                            return true;
                        }
                    }
                    //white en passant
                    else if(PieceToMove.checkPosToMove(PieceToMove, finalPosition, true) &&
                            Pos.squaresMoved(movementType, PieceToMove.position, finalPosition) == 1
                            && finalPosition.num - PieceToMove.position.num  > 0
                            && findPieceOfPos(finalPosition) == null){
                        Piece temppiece = findPieceOfPos(new Pos(finalPosition.num - 1 , finalPosition.letter));
                        if(temppiece.color != PieceToMove.color && temppiece instanceof Pawn &&
                                ((Pawn) temppiece).moves2Squares && temppiece.position.num == 4){
                            ((Pawn) temppiece).moves2Squares = false;
                            return true;
                        }
                    }

                } else {
                    //black capture
                    if(!PieceToMove.checkPosToMove(PieceToMove, finalPosition, false) &&
                            Pos.squaresMoved(movementType, PieceToMove.position, finalPosition) == 1
                            && PieceToMove.position.num - finalPosition.num > 0
                            && findPieceOfPos(finalPosition) != null){
                        if(findPieceOfPos(finalPosition).color == !PieceToMove.color){
                            ((Pawn) PieceToMove).moves2Squares = false;
                            return true;
                        }
                    }
                    //black en passant
                    else if(!PieceToMove.checkPosToMove(PieceToMove, finalPosition, true) &&
                            Pos.squaresMoved(movementType, PieceToMove.position, finalPosition) == 1
                            && PieceToMove.position.num - finalPosition.num > 0
                            && findPieceOfPos(finalPosition) == null){
                        Piece temppiece = findPieceOfPos(new Pos(finalPosition.num - 1 , finalPosition.letter));
                        if(temppiece.color != PieceToMove.color && temppiece instanceof Pawn &&
                                ((Pawn) temppiece).moves2Squares && temppiece.position.num == 3){
                            ((Pawn) temppiece).moves2Squares = false;
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
                //We first check if the king wants to castle with a rook
                String strFinalPos = finalPosition.posToString();
                if(movementType.equals("horizontal") && (strFinalPos.equals("g1") || strFinalPos.equals("c1") ||
                        strFinalPos.equals("g8") || strFinalPos.equals("c8"))){
                    if(castlingMap == null){
                        initalizeCastleMap();
                    }

                    King king = (King) PieceToMove;

                    // CRITICAL: Cannot castle if king is in check
                    if(king.isChecked()) {
                        return false;
                    }

                    Rook r = (Rook) findPieceOfPos(Pos.stringToPos(castlingMap.get(strFinalPos)));
                    if(PieceManagers.canCastle(r, king)){
                        // Check if path is clear AND king doesn't pass through check
                        if(strFinalPos.equals("g1") &&
                                !PieceToMove.anyPieceBlocking(Pos.stringToPos("g1"), movementType) &&
                                !isSquareUnderAttack(new Pos(0, 5), king.color) && // f1
                                !isSquareUnderAttack(new Pos(0, 6), king.color)) {  // g1
                            return true;
                        } else if(strFinalPos.equals("c1") &&
                                !PieceToMove.anyPieceBlocking(Pos.stringToPos("b1"), movementType) &&
                                !isSquareUnderAttack(new Pos(0, 3), king.color) && // d1
                                !isSquareUnderAttack(new Pos(0, 2), king.color)) {  // c1
                            return true;
                        } else if(strFinalPos.equals("g8") &&
                                !PieceToMove.anyPieceBlocking(Pos.stringToPos("g8"), movementType) &&
                                !isSquareUnderAttack(new Pos(7, 5), king.color) && // f8
                                !isSquareUnderAttack(new Pos(7, 6), king.color)) {  // g8
                            return true;
                        } else if(strFinalPos.equals("c8") &&
                                !PieceToMove.anyPieceBlocking(Pos.stringToPos("b8"), movementType) &&
                                !isSquareUnderAttack(new Pos(7, 3), king.color) && // d8
                                !isSquareUnderAttack(new Pos(7, 2), king.color)) {  // c8
                            return true;
                        }
                    }
                }
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
            //end
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
