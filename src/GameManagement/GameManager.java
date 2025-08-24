package GameManagement;

import Pieces.Piece;

import java.util.ArrayList;

public class GameManager {

    private static ArrayList<Piece> GameObjects = new ArrayList<>();
    public static String StockFishPath;
    private static boolean color; //ture = white | false = black

    public GameManager(String StockFishPath) {
        this.StockFishPath = StockFishPath;
    }

    /**
     * Getter for the gameobjects
     * @return gameObjects arraylist
     */
    public static ArrayList<Piece> getGameObjects(){
        return GameObjects;
    }

    /**
     * Getter for the color paramater
     * @return boolean for the color (true = white / false = black)
     */
    public static boolean getColor(){return color;}

    /**
     * Sets a boolean for the player color during the game
     * @param temp boolean of the color that will be set (true = white / false = black)
     */
    public static void setColor(boolean temp){color = temp;}

    /**
     * NOTES TO UNDERSTAND: The turns are assigned using a boolean. Starts with true will always change itself at the end.
     * Checks if its the users turn with color boolean paramater.
     * @param turn boolean of the current turn
     * @return true = if its user turn / false = its stockfish turn
     */
    public static boolean isUserTurn(boolean turn){
        //If white(true) turn and color is black(false), the code will return black(false) move.
        if(turn == color){
            return true;
        } else {
            return false;
        }
    }
}
