import ChessClient.Code.StockFishChessClient;
import GameManagement.GameManager;
import GameManagement.PieceManagers;
import Pieces.Piece;

import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

import static java.lang.Thread.sleep;

public class Main {


    /* TO DO LIST IN ORDER:
    * CURRENTLY WORKING ON : winCondition
    * ask ChatGPT about special interactions with Stockfish
    * When your done with checking if queen in check, fix rule where you can't castle while in check AND can not castle
    * into a check
    *
    * PIECE COMPLETION :
    * PAWN (ALMOST DONE) en passant
    * BISHOP (DONE)
    * ROOK (DONE)
    * KNIGHT (DONE)
    * KING (DONE)
    * QUEEN (DONE)
    * */

    //Changes the amount of seconds between
    final private static int timeBetweenMoves = 1;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        //Before anything, do not forget to set the StockFish path.
        GameManager GM = new GameManager("C:\\Engines\\stockfish\\stockfish-windows-x86-64.exe");

        //Picks color of the player.
        System.out.println("What color would you like to be? \n[W]hite, [B]lack, [R]andom?");
        char colorChoice = sc.next().toUpperCase().charAt(0);
        switch (colorChoice) {
            case 'W' : GameManager.setColor(true); break;
            case 'B' : GameManager.setColor(false); break;
            case 'R' :
            default:
                Random rng = new Random();
                int colorChooser = rng.nextInt(2);
                if(colorChooser == 0){
                    GameManager.setColor(true);
                } else {
                    GameManager.setColor(false);
                }
            break;
        }

        //Creates all the pieces of the game.
        Piece.boardCreation();
        System.out.println("Capital letters are white, lowercase letters are black.");

        //Allows Stockfish to make his first move.
        if(GameManager.getColor()){
            PieceManagers.drawBoard();
        }

        boolean winCondition = false; //While false, the game will not end. Once true, game is over.
        boolean turn = true; //ture = white turn | false = black turn

        char[] PieceToMove; //Makes sure that the structure for the movement is followed before sending it
        do{
            if(turn){
                System.out.println("White's turn");
            } else {
                System.out.println("Black's move");
            }

            //Checks if its players turn or StockFish Consoles turn.
            if(GameManager.isUserTurn(turn)){
                do{
                    System.out.println("Please mention the piece and then where its to be moved. Example : e4-e5");
                    PieceToMove = sc.next().toLowerCase().toCharArray();
                } while(PieceManagers.CheckMovementStructure(PieceToMove) || !Piece.validMove(PieceToMove));
            } else {
                try {
                    PieceToMove = StockFishChessClient.getBestMoveFromBoard(PieceManagers.getBoard()).toCharArray();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            PieceManagers.Update(PieceToMove);
            System.out.println("----------------------------------");

            //Lest the user see the board and think out his next move;
            pause(timeBetweenMoves);

            //Makes sure the turn changes and the next person can play
            turn = !turn;
        }while(!winCondition);
    }

    private static void pause(int seconds){
        try {
            //needs to be in milliseconds, so *1000
            sleep(seconds * 1000);
        }
        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}