package GameManagement;

import Pieces.*;
import Position.Pos;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * This class is only used to simulate a move. The main usage of this class is to check if the
 * next move the player will make will remove the check or will keep it. Only method that can
 * be called is kingSim().
 */
public class SimulationClass {
    private static char[][] BoardCopy;
    private static ArrayList<Piece> GOCopy;
    private static King KingToMove;
    private static Pos originalKingPos; // Track original position

    /**
     * Basic constructor taking current board and objects in the game
     */
    private SimulationClass(King k) {
        BoardCopy = deepCopyBoard(PieceManagers.getBoard());
        GOCopy = deepCopyPieces();
        KingToMove = findKing(k);
        originalKingPos = new Pos(k.position.num, k.position.letter); // Store original position
    }

    /**
     * Simulates if the move that has been done will remove the kings check.
     * @return true if removes check / false if king is still in check
     */
    public static boolean kingSim(Piece pieceToMove, Pos finalPos, King k) {
        SimulationClass sc = new SimulationClass(k);

        //recreate the char[] for movement
        char[] movementCharSim = (pieceToMove.position.posToString() + "-" + finalPos.posToString()).toCharArray();

        // Find the piece in the simulation that corresponds to pieceToMove
        Piece simPiece = sc.findPieceOfPosSim(pieceToMove.position);

        //Check if move is valid
        if(simPiece != null && sc.checkPieceMovementSim(simPiece, finalPos)){
            // Check if this is a castling move
            if(simPiece instanceof King && ((King) simPiece).getCanCastle() && simPiece.color
                && (finalPos.posToString().equals("g1")) || finalPos.posToString().equals("c1") && canCastleSafely(k, finalPos)) {
                sc.UpdateSim(movementCharSim);
            } else if (simPiece instanceof King && ((King) simPiece).getCanCastle() && !simPiece.color
                    && (finalPos.posToString().equals("g8")) || finalPos.posToString().equals("c8") && canCastleSafely(k, finalPos)) {
                sc.UpdateSim(movementCharSim);
            } else {
                sc.UpdateSim(movementCharSim);
            }
        }

        //Final check to see if the move can be done while removing the check
        return !sc.isKingCheckedSim(sc.KingToMove);
    }

    /**
     * Will create a simulation that checks if the move entered will check the king.
     * @param pieceToMove The king that will move
     * @param finalPos the final position the king will go to
     * @return true if it will check / false if it won't check
     */
    public static boolean willMoveCheckKing(Piece pieceToMove, Pos finalPos){
        SimulationClass sc = new SimulationClass((King) pieceToMove);
        //recreate the char[] for movement
        char[] movementCharSim = (pieceToMove.position.posToString() + "-" + finalPos.posToString()).toCharArray();

        // Find the piece in the simulation
        Piece simPiece = sc.findPieceOfPosSim(pieceToMove.position);

        //Check if move is valid
        if(simPiece != null && sc.checkPieceMovementSim(simPiece, finalPos)){
            sc.UpdateSim(movementCharSim);
        }

        //Checks if the move made will check the king.
        return !sc.isKingCheckedSim(sc.KingToMove);
    }

    /**
     * Checks if castling will result in the king being in check
     * @param king The king attempting to castle
     * @param targetPos The target position for castling (g1, c1, g8, or c8)
     * @return true if the castle is safe (king not in check) / false if king would be in check
     */
    public static boolean canCastleSafely(King king, Pos targetPos) {
        // Create a simulation
        SimulationClass sim = new SimulationClass(king);

        // Get the simulated king
        Piece simKing = sim.findPieceOfPosSim(king.position);

        if(!(simKing instanceof King)) {
            return false;
        }

        String targetStr = targetPos.posToString();
        Rook simRook = null;
        Pos rookTargetPos = null;

        // Determine which rook to castle with and where it goes
        if(targetStr.equals("g1")) {
            // Kingside castle white
            simRook = (Rook) sim.findPieceOfPosSim(Pos.stringToPos("h1"));
            rookTargetPos = Pos.stringToPos("f1");
        } else if(targetStr.equals("c1")) {
            // Queenside castle white
            simRook = (Rook) sim.findPieceOfPosSim(Pos.stringToPos("a1"));
            rookTargetPos = Pos.stringToPos("d1");
        } else if(targetStr.equals("g8")) {
            // Kingside castle black
            simRook = (Rook) sim.findPieceOfPosSim(Pos.stringToPos("h8"));
            rookTargetPos = Pos.stringToPos("f8");
        } else if(targetStr.equals("c8")) {
            // Queenside castle black
            simRook = (Rook) sim.findPieceOfPosSim(Pos.stringToPos("a8"));
            rookTargetPos = Pos.stringToPos("d8");
        }

        if(simRook == null || rookTargetPos == null) {
            return false;
        }

        // Move the king
        sim.movementSim(targetPos, (King) simKing);

        // Move the rook
        sim.movementSim(rookTargetPos, simRook);

        // Check if king is in check after castling
        return !sim.isKingCheckedSim((King) simKing);
    }

    protected static boolean isCheckMate(King k) {
        // Create a fresh simulation to check the initial state
        SimulationClass checkSim = new SimulationClass(k);

        // First, verify the king is actually in check
        if(!checkSim.isKingCheckedSim(checkSim.KingToMove)) {
            return false; // Can't be checkmate if not in check
        }

        // Try all possible moves for all pieces of the king's color
        for(Piece p : GameManager.getGameObjects()) {
            if(p.color == k.color && p.isActive) {
                // Try moving this piece to every square on the board
                for(int num = 0; num < 8; num++) {
                    for(int letter = 0; letter < 8; letter++) {
                        Pos targetPos = new Pos(num, letter);

                        // Create a fresh simulation for each move test
                        SimulationClass testSim = new SimulationClass(k);
                        Piece simPiece = testSim.findPieceOfPosSim(p.position);

                        if(simPiece != null && testSim.checkPieceMovementSim(simPiece, targetPos)) {
                            // Simulate the move
                            char[] moveChar = (p.position.posToString() + "-" + targetPos.posToString()).toCharArray();
                            testSim.UpdateSim(moveChar);

                            // Check if king is still in check after this move
                            if(!testSim.isKingCheckedSim(testSim.KingToMove)) {
                                return false; // Found a move that escapes check
                            }
                        }
                    }
                }
            }
        }

        return true; // No moves escape check - checkmate
    }

    /**
     * Find the king in the simulation via the king given
     * @param K the king to find
     * @return king in the simulation
     */
    private static King findKing(King K) {
        for (Piece p : GOCopy) {
            if(p.position.letter == K.position.letter && p.position.num == K.position.num && p instanceof King){
                return (King) p;
            }
        }
        return null;
    }

    /**
     * Simulates an update like in the "Main"
     * @param MovementChar recreation of the movementChar
     */
    private void UpdateSim(char[] MovementChar){
        String PieceToMove = String.valueOf(MovementChar[0]) + String.valueOf(MovementChar[1]).toLowerCase();
        String PositionToMove = String.valueOf(MovementChar[3]) + String.valueOf(MovementChar[4]).toLowerCase();

        for (Piece p : GOCopy){
            if(p.position.posToString().equals(PieceToMove.toLowerCase())){
                movementSim(Pos.stringToPos(PositionToMove.toLowerCase()), p);
                if(MovementChar.length == 6 && p instanceof Pawn){
                    if((p.position.num == 0 && !GameManager.getColor()) || (p.position.num == 7 && GameManager.getColor())){
                        boolean temp = true;
                        do {
                            Scanner sc = new Scanner(System.in);
                            switch (MovementChar[5]) {
                                case 'n' : Knight n = new Knight ((Knight) ((Pawn) p).promotionSimulation(MovementChar[5]));
                                    temp = false; GOCopy.add(n); break;
                                case 'q' : Queen q = new Queen ((Queen) ((Pawn) p).promotionSimulation(MovementChar[5]));
                                    temp = false; GOCopy.add(q); break;
                                case 'r' : Rook r = new Rook((Rook) ((Pawn) p).promotionSimulation(MovementChar[5]));
                                    temp = false; GOCopy.add(r); break;
                                case 'b' : Bishop b = new Bishop((Bishop) ((Pawn) p).promotionSimulation(MovementChar[5]));
                                    temp = false; GOCopy.add(b); break;
                                default:
                                    System.out.println("Select what the Pawn will promote to by entering the letter in the [] : " +
                                            "\n [Q]ueen, [B]ishop, K[n]ight, [R]ook");
                                    MovementChar[5] = sc.nextLine().toLowerCase().charAt(0);
                            }
                        } while (temp);
                        p.deactivate();
                    }
                }
                break;
            }
        }
        //checks for deactivated pieces
        for (int i = GOCopy.size() - 1; i >= 0; i--) {
            if(!GOCopy.get(i).isActive){
                GOCopy.remove(i);
            }
        }
    }

    /**
     * Simulates a movement with the tools in this class
     * @param targetPos location to move the piece and simulate
     * @param PieceToMove the piece we need to move
     */
    private void movementSim(Pos targetPos, Piece PieceToMove) {
        // Check if there's a piece at the target position to capture
        Piece targetPiece = findPieceOfPosSim(targetPos);
        if(targetPiece != null && targetPiece.color != PieceToMove.color){
            targetPiece.deactivate();
        }

        // Update board and piece position
        BoardCopy[PieceToMove.position.num][PieceToMove.position.letter] = '\u0000';
        BoardCopy[targetPos.num][targetPos.letter] = PieceToMove.icon;
        PieceToMove.position = targetPos;
    }

    /**
     * After the simulations, uses the resources in this class to check if the king is still checked
     * @param k the king to verify if checked
     * @return true = checked / false = not checked
     */
    private boolean isKingCheckedSim(King k){
        if(BoardCopy == null && GOCopy == null){
            SimulationClass sc = new SimulationClass(k);
        }

        //Get King position
        String kingPosStr = k.position.posToString();

        //Check all positions on the board
        for(int number = 0; number < 8; number++){
            for(int letter = 0; letter < 8; letter++){
                Piece attackingPiece = findPieceOfPosSim(new Pos(number, letter));

                if(attackingPiece != null && attackingPiece.color != k.color){
                    Pos kingPos = Pos.stringToPos(kingPosStr);

                    //Check if this enemy piece can attack the king
                    if(checkPieceMovementSim(attackingPiece, kingPos)){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Check, in the simulation, if there is a piece in the place to move depending on the condition.
     * @param p Piece that will move. Only used for its color property
     * @param posToMove The position that the piece (p) is trying to move to.
     * @param condition true checks if any piece is in that position
     *                  false checks if piece of different color is in the position
     * @return true if position is valid / false if blocked
     */
    private boolean checkPosToMoveSim(Piece p, Pos posToMove, boolean condition){
        for(Piece tempPiece : GOCopy) {
            if(posToMove.num == tempPiece.position.num && posToMove.letter == tempPiece.position.letter){
                if(condition) {
                    // Any piece blocks
                    return false;
                } else {
                    // Only different color blocks (for captures)
                    if(p.color != tempPiece.color){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Finds a piece in our GOCopy and returns it
     * @param position position of the piece to find
     * @return the piece at that position
     */
    private Piece findPieceOfPosSim(Pos position){
        for(Piece p : GOCopy){
            if(position.num == p.position.num && position.letter == p.position.letter){
                return p;
            }
        }
        return null;
    }

    /**
     * Checks if, in the simulation, the movement for a piece is legal
     * @param PieceToMove Piece to move
     * @param finalPosition position the piece will go to
     * @return true if move is legal / false if move is illegal
     */
    private boolean checkPieceMovementSim(Piece PieceToMove, Pos finalPosition) {
        try{
            String movementType = Pos.checkMovementDirection(PieceToMove.position, finalPosition);

            if(movementType.equals("vertical") && PieceToMove instanceof Pawn){
                if(PieceToMove.color){
                    if(checkPosToMoveSim(PieceToMove, finalPosition, true) &&
                            (Pos.squaresMoved(movementType, PieceToMove.position, finalPosition) == 1 ||
                                    (Pos.squaresMoved(movementType, PieceToMove.position, finalPosition) == 2 &&
                                            PieceToMove.position.num == 1)) &&
                            finalPosition.num - PieceToMove.position.num  > 0){
                        return true;
                    }
                } else {
                    if(checkPosToMoveSim(PieceToMove, finalPosition, true) &&
                            (Pos.squaresMoved(movementType, PieceToMove.position, finalPosition) == 1 ||
                                    (Pos.squaresMoved(movementType, PieceToMove.position, finalPosition) == 2 &&
                                            PieceToMove.position.num == 6)) &&
                            PieceToMove.position.num - finalPosition.num > 0) {
                        return true;
                    }
                }
            } else if (movementType.equals("diagonal") && PieceToMove instanceof Pawn) {
                if(PieceToMove.color){
                    if(!checkPosToMoveSim(PieceToMove, finalPosition, false) &&
                            Pos.squaresMoved(movementType, PieceToMove.position, finalPosition) == 1
                            && finalPosition.num - PieceToMove.position.num  > 0
                            && findPieceOfPosSim(finalPosition) != null){
                        if(findPieceOfPosSim(finalPosition).color != PieceToMove.color){
                            return true;
                        }
                    }
                } else {
                    if(!checkPosToMoveSim(PieceToMove, finalPosition, false) &&
                            Pos.squaresMoved(movementType, PieceToMove.position, finalPosition) == 1
                            && PieceToMove.position.num - finalPosition.num > 0
                            && findPieceOfPosSim(finalPosition) != null){
                        if(findPieceOfPosSim(finalPosition).color != PieceToMove.color){
                            return true;
                        }
                    }
                }
            } else if ((movementType.equals("vertical") || movementType.equals("horizontal")) &&
                    PieceToMove instanceof Rook) {
                if(!checkPosToMoveSim(PieceToMove, finalPosition, false) &&
                        !anyPieceBlockingSim(PieceToMove,finalPosition, movementType)){
                    return true;
                } else if (checkPosToMoveSim(PieceToMove, finalPosition, true) &&
                        !anyPieceBlockingSim(PieceToMove,finalPosition, movementType)){
                    return true;
                }
            } else if ((movementType.equals("vertical") || movementType.equals("horizontal")
                    || movementType.equals("diagonal")) && PieceToMove instanceof Queen) {
                if(!checkPosToMoveSim(PieceToMove, finalPosition, false) &&
                        !anyPieceBlockingSim(PieceToMove,finalPosition, movementType)){
                    return true;
                } else if (checkPosToMoveSim(PieceToMove, finalPosition, true) &&
                        !anyPieceBlockingSim(PieceToMove,finalPosition, movementType)){
                    return true;
                }
            } else if ((movementType.equals("vertical") || movementType.equals("horizontal") ||
                    movementType.equals("diagonal")) && PieceToMove instanceof King) {
                //We first check if the king wants to castle with a rook
                String strFinalPos = finalPosition.posToString();
                if(movementType.equals("horizontal") && (strFinalPos.equals("g1") || strFinalPos.equals("c1") ||
                        strFinalPos.equals("g8") || strFinalPos.equals("c8"))){
                    if(Piece.getCastlingMap() == null){
                        Piece.initalizeCastleMap();
                    }
                    Rook r = (Rook) findPieceOfPosSim(Pos.stringToPos(Piece.getCastlingMap().get(strFinalPos)));
                    if(r != null && PieceManagers.canCastle(r, (King) PieceToMove)){
                        if(strFinalPos.equals("g1") &&
                                !anyPieceBlockingSim(PieceToMove, Pos.stringToPos("g1"), movementType)){
                            return true;
                        } else if(strFinalPos.equals("c1") &&
                                !anyPieceBlockingSim(PieceToMove, Pos.stringToPos("b1"), movementType)){
                            return true;
                        } else if(strFinalPos.equals("g8") &&
                                !anyPieceBlockingSim(PieceToMove, Pos.stringToPos("g8"), movementType)){
                            return true;
                        } else if(strFinalPos.equals("c8") &&
                                !anyPieceBlockingSim(PieceToMove, Pos.stringToPos("b8"), movementType)){
                            return true;
                        }
                    }
                }

                if(!checkPosToMoveSim(PieceToMove, finalPosition, false) &&
                        !anyPieceBlockingSim(PieceToMove,finalPosition, movementType) &&
                        Pos.squaresMoved(movementType, PieceToMove.position, finalPosition) == 1){
                    return true;
                } else if (checkPosToMoveSim(PieceToMove, finalPosition, true) &&
                        !anyPieceBlockingSim(PieceToMove,finalPosition, movementType) &&
                        Pos.squaresMoved(movementType, PieceToMove.position, finalPosition) == 1){
                    return true;
                }
            } else if (movementType.equals("knight") && PieceToMove instanceof Knight) {
                //if something is there, check color then it can be valid
                Piece targetPiece = findPieceOfPosSim(finalPosition);
                if(targetPiece != null){
                    if(targetPiece.color != PieceToMove.color){
                        return true;
                    }
                } else {
                    return true;
                }
            } else if (movementType.equals("diagonal") && PieceToMove instanceof Bishop) {
                if(!checkPosToMoveSim(PieceToMove, finalPosition, false) &&
                        !anyPieceBlockingSim(PieceToMove,finalPosition, movementType)){
                    return true;
                } else if (checkPosToMoveSim(PieceToMove, finalPosition, true) &&
                        !anyPieceBlockingSim(PieceToMove,finalPosition, movementType)){
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Checks to see if, in the simulation, there are any pieces that could block the move.
     * @param finalPos The final position the pieces needs to go to
     * @param movementType the type of movement that will be made
     * @return true if something is blocking the move / false if nothing is blocking the move
     */
    private boolean anyPieceBlockingSim(Piece pm, Pos finalPos, String movementType){
        Piece pieceToMove = pm;
        int slotsToCheck = Pos.squaresMoved(movementType, pieceToMove.position, finalPos);

        for(int i = 1; i < slotsToCheck; i++){ // Start at 1 to skip starting position, end before final
            try {
                if(movementType.equals("vertical")) {
                    if(pieceToMove.position.num < finalPos.num){
                        if(BoardCopy[pieceToMove.position.num + i][pieceToMove.position.letter] != '\u0000'){
                            return true;
                        }
                    } else {
                        if(BoardCopy[pieceToMove.position.num - i][pieceToMove.position.letter] != '\u0000'){
                            return true;
                        }
                    }
                } else if (movementType.equals("horizontal")) {
                    if(pieceToMove.position.letter < finalPos.letter){
                        if(BoardCopy[pieceToMove.position.num][pieceToMove.position.letter + i] != '\u0000'){
                            return true;
                        }
                    } else {
                        if(BoardCopy[pieceToMove.position.num][pieceToMove.position.letter - i] != '\u0000'){
                            return true;
                        }
                    }
                } else if (movementType.equals("diagonal")) {
                    //Up right
                    if(pieceToMove.position.num < finalPos.num && pieceToMove.position.letter < finalPos.letter){
                        if(BoardCopy[pieceToMove.position.num + i][pieceToMove.position.letter + i] != '\u0000'){
                            return true;
                        }
                    }
                    //Down Right
                    else if(pieceToMove.position.num > finalPos.num && pieceToMove.position.letter < finalPos.letter){
                        if(BoardCopy[pieceToMove.position.num - i][pieceToMove.position.letter + i] != '\u0000'){
                            return true;
                        }
                    }
                    //Up Left
                    else if(pieceToMove.position.num < finalPos.num && pieceToMove.position.letter > finalPos.letter) {
                        if(BoardCopy[pieceToMove.position.num + i][pieceToMove.position.letter - i] != '\u0000'){
                            return true;
                        }
                    }
                    //Down Left
                    else if (pieceToMove.position.num > finalPos.num && pieceToMove.position.letter > finalPos.letter) {
                        if(BoardCopy[pieceToMove.position.num - i][pieceToMove.position.letter - i] != '\u0000'){
                            return true;
                        }
                    }
                }
            } catch (Exception e) {
                return true;
            }
        }
        return false;
    }

    /**
     * Copies the char map
     * @param original original char map
     * @return copy of the original char map
     */
    private static char[][] deepCopyBoard(char[][] original) {
        char[][] copy = new char[original.length][];
        for (int i = 0; i < original.length; i++) {
            copy[i] = original[i].clone();
        }
        return copy;
    }

    /**
     * Copies all the pieces into a new array list
     * @return the copy of the arraylist
     */
    private static ArrayList<Piece> deepCopyPieces() {
        ArrayList<Piece> temp = new ArrayList<>();
        for (Piece piece : GameManager.getGameObjects()) {
            if(piece instanceof King){
                temp.add(new King((King) piece));
            } else if(piece instanceof Queen){
                temp.add(new Queen((Queen) piece));
            } else if (piece instanceof Bishop){
                temp.add(new Bishop((Bishop) piece));
            } else if (piece instanceof Rook){
                temp.add(new Rook((Rook) piece));
            } else if (piece instanceof Pawn){
                temp.add(new Pawn((Pawn) piece));
            } else if (piece instanceof Knight){
                temp.add(new Knight((Knight) piece));
            }
        }
        return temp;
    }
}