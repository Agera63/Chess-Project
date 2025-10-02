package Pieces;

import GameManagement.GameManager;
import GameManagement.PieceManagers;
import Position.Pos;

import java.util.HashMap;

public class Rook extends Piece{

    private static boolean firstWhite = true;
    private static boolean firstBlack = true;
    private boolean canCastle;
    //First string is the position the king moves too, second string is where the rook moves
    private HashMap<String, String> castlePositions;

    /**
     * Creates a Pieces.Rook depending on the piece color.
     * @param color Color of the piece (true = black / false = white)
     */
    public Rook(boolean color){
        super("Rook", 'R', new Pos(0, 0), color);
        canCastle = true;
        //If its a black piece, I need to create it on a different side of board and different icon.
        if (!color){
            this.icon = 'r';
            //Checks if the first rook has been placed
            if(firstBlack){
                this.position = new Pos(7,0);
                firstBlack = false;
            } else {
                this.position = new Pos(7,7);
            }
        } else {
            //Checks if the first rook has been placed
            if(firstWhite){
                firstWhite = false;
            } else {
                this.position = new Pos(0,7);
            }
        }
        GameManager.getGameObjects().add(this);
    }

    /**
     * This constructor is for the SimulationClass. To create temporary pieces and simulate everything
     * @param Rook The piece we want to make a copy of.
     */
    public Rook(Rook Rook) {
        super(Rook.name, Rook.icon, new Pos(Rook.position.num, Rook.position.letter), Rook.color);
        canCastle = Rook.canCastle;
    }

    /**
     * This is used only when a pawn will promote to a rook
     * @param color color of the piece
     * @param p position of the old pawn
     */
    public Rook(boolean color, Pos p){
        super("Rook", 'R', p, color);
        canCastle = false;
        //If its a black piece, I need to create it on a different side of board and different icon.
        if (!color) {
            this.icon = 'r';
        }
        PieceManagers.getBoard()[p.letter][p.num] = this.icon;
        GameManager.getGameObjects().add(this);
        drawPieces();
    }

    /**
     * Returns the canCastle variable
     * @return true, if can castle / false, if can't castle
     */
    public boolean getCanCastle(){
        return canCastle;
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
        canCastle = false;
        PieceManagers.setBoard(temporaryBoard);
    }

    protected char[][] castleMovement(char[][] temporaryBoard, String placeToMove){
        if(castlePositions == null){
            initalizeCastlePositions();
        }

        //We change the place to move from the kings place to the rooks place based on the king movement
        placeToMove = castlePositions.get(Piece.getCastlingMap().get(placeToMove));
        temporaryBoard[this.position.num][this.position.letter] = '\u0000';
        temporaryBoard[Pos.stringToPos(placeToMove).num][Pos.stringToPos(placeToMove).letter] = this.icon;
        this.position = Pos.stringToPos(placeToMove);
        return temporaryBoard;
    }

    @Override
    public void deactivate() {
        if(isActive){
            isActive = false;
        }
    }

    private void initalizeCastlePositions(){
        castlePositions = new HashMap<>();
        castlePositions.put(Piece.getCastlingMap().get("g1"), "f1");
        castlePositions.put(Piece.getCastlingMap().get("c1"), "d1");
        castlePositions.put(Piece.getCastlingMap().get("g8"), "f8");
        castlePositions.put(Piece.getCastlingMap().get("c8"), "d8");
    }
}
