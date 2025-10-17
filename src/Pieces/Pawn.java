package Pieces;

import GameManagement.GameManager;
import GameManagement.PieceManagers;
import Position.Pos;

import java.util.Scanner;

public class Pawn extends Piece{
    private static int whitePos = 0;
    private static int blackPos = 0;
    public boolean moves2Squares = false;
    /**
     * Creates a Pieces.Pawn depending on the piece color.
     * @param color Color of the piece. (true = black / false = white)
     */
    public Pawn(boolean color) {
        super("Pawn", 'P', new Pos(0,0), color);
        //Makes sur the pieces have the correct position
        if(color){
            this.position = pawnPosition(color);
        } else {
            this.icon = 'p';
            this.position = pawnPosition(color);
        }
        GameManager.getGameObjects().add(this);
    }

    /**
     * This constructor is for the SimulationClass. To create temporary pieces and simulate everything
     * @param Pawn The piece we want to make a copy of.
     */
    public Pawn(Pawn Pawn) {
        super(Pawn.name, Pawn.icon, new Pos(Pawn.position.num, Pawn.position.letter), Pawn.color);
    }

    @Override
    public void mouvement(String placeToMove) {
        String movementType = Pos.checkMovementDirection(this.position, Pos.stringToPos(placeToMove));
        char[][] temporaryBoard = PieceManagers.getBoard();

        if(!this.checkPosToMove(this, Pos.stringToPos(placeToMove), true) && movementType.equals("diagonal")){
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

    /**
     * This handles pawn promotions.
     * @param pieceToPromote The first letter of the piece that the pawn will become.
     */
    public void promotion(char pieceToPromote){
        if(pieceToPromote == ' '){
            Scanner sc = new Scanner(System.in);
            boolean temp = true;
            do{
                System.out.println("Select what the Pawn will promote to by entering the letter in the [] : " +
                        "\n [Q]ueen, [B]ishop, K[n]ight, [R]ook");
                char input = sc.next().toLowerCase().charAt(0);
                switch (input){
                    case 'q' : new Queen(this.color, this.position); temp = false; break;
                    case 'b' : new Bishop(this.color, this.position); temp = false; break;
                    case 'n' : new Knight(this.color, this.position); temp = false; break;
                    case 'r' : new Rook(this.color, this.position); temp = false; break;
                    default: break;
                }

            } while(temp);
        } else {
            switch (pieceToPromote){
                case 'q' : new Queen(this.color, this.position); break;
                case 'b' : new Bishop(this.color, this.position); break;
                case 'n' : new Knight(this.color, this.position); break;
                case 'r' : new Rook(this.color, this.position); break;
                default: break;
            }
        }
        this.deactivate();
    }

    /**
     * Makes sure the positions of the pawns are correctly applied
     * @param c the color of the piece
     * @return the position of the piece
     */
    private Pos pawnPosition(boolean c){
        if(c){
            return new Pos(1, whitePos++);
        } else {
            return new Pos(6, blackPos++);
        }
    }

    /**
     * Simulates the pawn promotion for simulation class
     * @param pieceToPromote The first letter of the piece that the pawn will become.
     */
    public Piece promotionSimulation(char pieceToPromote){
        switch (pieceToPromote){
            case 'q' : return new Queen(this.color, this.position);
            case 'b' : return new Bishop(this.color, this.position);
            case 'n' : return new Knight(this.color, this.position);
            case 'r' : return new Rook(this.color, this.position);
            default : return null;
        }
    }
}
