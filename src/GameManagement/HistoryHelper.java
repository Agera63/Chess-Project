package GameManagement;

import java.io.*;

public class HistoryHelper {
    private FileOutputStream moveHistoryFile;
    private PrintStream moveHistoryOutput;

    public HistoryHelper(String path) {
        try {
            // --- Locate the base folder (where StockFishPath.txt should be) ---
            File baseFolder = getBaseFolder();

            if (baseFolder == null) {
                throw new FileNotFoundException("Base folder for StockFishPath.txt not found.");
            }

            // --- Create MoveHistory.txt in that same folder ---
            File moveHistory = new File(baseFolder, path);
            moveHistoryFile = new FileOutputStream(moveHistory, false); // changed to false to overwrite
            moveHistoryOutput = new PrintStream(moveHistoryFile, true); // auto-flush

            moveHistoryOutput.println("=== New Game Session ===");
        } catch (Exception e) {
            System.err.println("Error initializing MoveHistory.txt: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --- Adds a move to the history file ---
    public void addMove(char[] charArrayMove) {
        if (moveHistoryOutput == null) return; // if file couldn't open
        String move = formatMove(charArrayMove);
        moveHistoryOutput.println(move);
    }

    // --- Closes both streams safely ---
    public void closeFile() {
        try {
            if (moveHistoryOutput != null) moveHistoryOutput.close();
            if (moveHistoryFile != null) moveHistoryFile.close();
        } catch (Exception e) {
            System.err.println("Error while closing MoveHistory.txt: " + e.getMessage());
        }
    }

    // --- Converts a char array move into a String ---
    private String formatMove(char[] charArrayMove) {
        return new String(charArrayMove);
    }

    // --- Finds the folder containing StockFishPath.txt ---
    private File getBaseFolder() {
        try {
            File externalFile = new File("StockFishPath.txt");
            if (externalFile.exists()) {
                return externalFile.getParentFile() != null ? externalFile.getParentFile() : new File(".");
            }

            // If not found next to JAR, check inside resources
            InputStream internal = HistoryHelper.class.getResourceAsStream("/StockFishPath.txt");
            if (internal != null) {
                // Running from inside JAR → get the folder where the JAR is located
                String jarPath = new File(HistoryHelper.class
                        .getProtectionDomain()
                        .getCodeSource()
                        .getLocation()
                        .toURI())
                        .getParent();
                return new File(jarPath);
            }
        } catch (Exception e) {
            System.err.println("Error locating base folder: " + e.getMessage());
        }
        return null;
    }
}