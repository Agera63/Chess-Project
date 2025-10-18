package GameManagement;

import Pieces.King;
import Pieces.Piece;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class GameManager {

    private static ArrayList<Piece> GameObjects = new ArrayList<>();
    public static String StockFishPath;
    private static boolean color; //ture = white | false = black
    private static boolean whoWon;

    public GameManager() {
        this.StockFishPath = setStockFishPath();
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

    /**
     * Reads the "StockFishPath.txt" file to get the StockFish path
     */
    private static String setStockFishPath() {
        Scanner lecteurFichier = null;
        StringBuilder stockFishPath = new StringBuilder();

        try {
            // Try to load the file from the same directory as the JAR
            File file = new File("StockFishPath.txt");
            InputStream input;

            if (file.exists()) {
                // File found next to the JAR
                input = new FileInputStream(file);
            } else {
                // Try to load it from resources inside the JAR
                input = GameManager.class.getResourceAsStream("/StockFishPath.txt");
                if (input == null) {
                    throw new FileNotFoundException("StockFishPath.txt not found (neither external nor in resources).");
                }
            }

            lecteurFichier = new Scanner(new InputStreamReader(input));

            if (lecteurFichier.hasNextLine()) {
                char[] ligne = lecteurFichier.nextLine().toCharArray();
                for (char c : ligne) {
                    if (c != '"') {
                        stockFishPath.append(c);
                    }
                }
            } else {
                throw new IOException("StockFishPath.txt is empty.");
            }

        } catch (Exception e) {
            System.err.println("Error reading StockFishPath.txt: " + e.getMessage());
            e.printStackTrace();
            return null; // You could return a default path here if you want
        } finally {
            if (lecteurFichier != null) {
                lecteurFichier.close();
            }
        }

        return stockFishPath.toString();
    }


    public static boolean isGameOVer(){
        if(SimulationClass.isCheckMate(King.findWhiteKing())){
            whoWon = true;
            return true;
        } else if(SimulationClass.isCheckMate(King.findBlackKing())) {
            whoWon = false;
            return true;
        }
        return false;
    }

    public static void whoWon(){
        System.out.println((whoWon ? "Black" : "White") + " won!");
    }

    private static FileInputStream openFileReader(String nomFichier) {
        FileInputStream fichier = null;
        try {
            fichier = new FileInputStream(nomFichier);
        } catch (FileNotFoundException ex) {
            System.out.println("Error reading \"StockFishPath.txt\" file.");
        }
        return fichier;
    }

    private static void closeFile(Closeable fichier) {
        try {
            fichier.close();
        } catch (IOException ex) {
            System.out.println("Error closing \"StockFishPath.txt\" file.");
        }
    }
}
