package GameManagement;

import Pieces.*;

public class PieceManagers {
    private static char[][] Board = new char[8][8];
    public static char[][] Boardcolors = fillColorBoard();

    /**
     * Getter for the board with the pieces
     * @return the chess board
     */
    public static char[][] getBoard() {
        return Board;
    }

    /**
     * Setter for the board
     * @param board the new board to be set
     */
    public static void setBoard(char[][] board){
        Board = board;
    }

    /**
     * Fills the basic colors of a chess baord
     * @return char[][] with the colors only
     */
    private static char[][] fillColorBoard() {
        return new char[][]{{'▒','█','▒','█','▒','█','▒','█'},
                            {'█','▒','█','▒','█','▒','█','▒'},
                            {'▒','█','▒','█','▒','█','▒','█'},
                            {'█','▒','█','▒','█','▒','█','▒'},
                            {'▒','█','▒','█','▒','█','▒','█'},
                            {'█','▒','█','▒','█','▒','█','▒'},
                            {'▒','█','▒','█','▒','█','▒','█'},
                            {'█','▒','█','▒','█','▒','█','▒'}};
    }

    /**
     * This is called to update all the pieces which has been moved.
     * @param MovementChar has the begining and the
     */
    public static void Update(char[] MovementChar){
        String PieceToMove = String.valueOf(MovementChar[0]) + String.valueOf(MovementChar[1]).toLowerCase();
        String PositionToMove = String.valueOf(MovementChar[3]) + String.valueOf(MovementChar[4]).toLowerCase();

        for (Piece p : GameManager.getGameObjects()){
            if(p.position.posToString().equals(PieceToMove.toLowerCase())){
                p.mouvement(PositionToMove.toLowerCase());
                if(MovementChar.length == 6 && p instanceof Pawn){
                    if((p.position.num == 0 && !GameManager.getColor()) || (p.position.num == 7 && GameManager.getColor())){
                        ((Pawn) p).promotion(MovementChar[5]);
                    }
                }
                break;
            }
        }
        //checks for deactivated pieces
        for (Piece p : GameManager.getGameObjects()) {
            if(!p.isActive){
                GameManager.getGameObjects().remove(p);
                /*DO NOT REMOVE BREAK!!!
                If you remove the break, the array list would of been altered and then it will thrown an error */
                break;
            }
        }
        drawBoardWithIcons();
    }

    /**
     * Draws the board in the console.
     */
    public static void drawBoardWithIcons(){
        //Draws the board once the move has been updated.
        int temp; //prints the number on the side of the chess board
        if(GameManager.getColor()) {
            temp = 8;
            System.out.println("   A  B  C  D  E  F  G  H");
            for (int i = Board[0].length - 1; i >= 0; i--) {
                System.out.print(temp + "  ");
                for (int y = 0; y < Board[0].length; y++) {
                    if(Board[i][y] == '\u0000'){
                        System.out.print(PieceManagers.Boardcolors[i][y] + "  ");
                    } else {
                        System.out.print(PieceIcon.transaltePieceIcon(Board[i][y]) + "  ");
                    }
                }
                System.out.print(temp + "  ");
                temp--;

                System.out.println();
            }
            System.out.println("   A  B  C  D  E  F  G  H");
        } else {
            temp = 1;
            System.out.println("   A  B  C  D  E  F  G  H");
            for (int i = 0; i < Board.length; i++) {
                System.out.print(temp + "  ");
                for (int y = 0; y < Board.length; y++) {
                    if(Board[i][y] == '\u0000'){
                        System.out.print(PieceManagers.Boardcolors[i][y] + "  ");
                    } else {
                        System.out.print(PieceIcon.transaltePieceIcon(Board[i][y]) + "  ");
                    }
                }
                System.out.print(temp + "  ");
                temp++;

                System.out.println();
            }
            System.out.println("   A  B  C  D  E  F  G  H");
        }
    }

    /**
     * Checking to see if we can castle the rook and the king
     * @param r The rook we are checking
     * @param k The King we are checking
     * @return
     */
    public static boolean canCastle(Rook r, King k) {
        return k.getCanCastle() && r.getCanCastle() && k.color == r.color;
    }

    /**
     * Checks that the structure to make sure the game won't crash
     * @param PieceToMove enrty that was typed by the user
     * @return true if invalid / false if its valid structure
     */
    public static boolean CheckMovementStructure(char[] PieceToMove){
        //Makes sure the structure and information submitted will not crash the game.
        if((PieceToMove.length == 5 || PieceToMove.length == 6) && PieceToMove[2] == '-'){
            switch (PieceToMove[0]){
                case 'a': case 'b': case 'c':
                case 'd': case 'e': case 'f':
                case 'g': case 'h':
                    switch (PieceToMove[1]){
                        case '1' : case '2' : case '3' :
                        case '4' : case '5' : case '6' :
                        case '7': case '8' :
                            switch (PieceToMove[3]){
                                case 'a': case 'b': case 'c':
                                case 'd': case 'e': case 'f':
                                case 'g': case 'h':
                                    switch (PieceToMove[4]){
                                        case '1' : case '2' : case '3' :
                                        case '4' : case '5' : case '6' :
                                        case '7': case '8' :
                                            //false makes the loop stop
                                            return false;
                                        default:return true;
                                    }
                                default: return true;
                            }
                        default: return true;
                    }
                default: return true;
            }
        }
        return true;
    }
}
