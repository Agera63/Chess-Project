package ChessClient.Code;

import GameManagement.GameManager;
import java.io.*;

public class StockFishChessClient {

    static final String STOCKFISH_PATH = GameManager.StockFishPath;

    //Call this method to get the best moves from StockFish
    public static String getBestMoveFromBoard(char[][] board) throws IOException {
        // 1. Convert 2D board array to FEN
        String fen = boardToFen(board);

        // 2. Start Stockfish process
        ProcessBuilder processBuilder = new ProcessBuilder(STOCKFISH_PATH);
        processBuilder.redirectErrorStream(true); // Merge stderr with stdout
        Process process = processBuilder.start();

        BufferedWriter writer = null;
        BufferedReader reader = null;
        String bestMove = null;

        try {
            writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            // 3. Initialize UCI protocol
            writer.write("uci\n");
            writer.flush();
            waitForReady(reader);

            // 4. Set position and search for best move
            writer.write("position fen " + fen + "\n");
            writer.flush();

            writer.write("go depth 15\n");
            writer.flush();

            // 5. Read response
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("bestmove")) {
                    String[] parts = line.split(" ");
                    if (parts.length > 1 && !parts[1].equals("(none)")) {
                        bestMove = parts[1];
                    }
                    break;
                }
            }

        } catch (IOException e) {
            System.err.println("Error communicating with Stockfish: " + e.getMessage());
            throw e;
        } finally {
            // Clean up resources
            try {
                if (writer != null) {
                    writer.write("quit\n");
                    writer.flush();
                    writer.close();
                }
            } catch (IOException e) {
                // Ignore cleanup errors
            }

            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                // Ignore cleanup errors
            }

            // Wait for process to terminate gracefully
            try {
                process.waitFor();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            process.destroyForcibly();
        }

        // Format the move with a hyphen
        if (bestMove != null && bestMove.length() >= 4) {
            String fromSquare = bestMove.substring(0, 2);
            String toSquare = bestMove.substring(2, 4);
            return fromSquare + "-" + toSquare;
        }

        return "No move found";
    }

    private static String boardToFen(char[][] board) {
        StringBuilder fen = new StringBuilder();
        for (int row = 0; row < 8; row++) {
            int emptyCount = 0;
            for (int col = 0; col < 8; col++) {
                char piece = board[row][col];
                if (piece == '\0' || piece == ' ') {
                    emptyCount++;
                } else {
                    if (emptyCount > 0) {
                        fen.append(emptyCount);
                        emptyCount = 0;
                    }
                    fen.append(piece);
                }
            }
            if (emptyCount > 0) fen.append(emptyCount);
            if (row < 7) fen.append('/');
        }

        // FEN requires additional fields: side to move, castling, en passant, halfmove, fullmove
        fen.append(" w - - 0 1");
        return fen.toString();
    }

    private static void waitForReady(BufferedReader reader) throws IOException {
        // First wait for uciok
        String line;
        boolean uciReceived = false;

        while ((line = reader.readLine()) != null) {
            if (line.equals("uciok")) {
                uciReceived = true;
                break;
            }
            // Optional: You can print debug info
            // System.out.println("[Stockfish UCI]: " + line);
        }

        if (!uciReceived) {
            throw new IOException("Failed to initialize UCI protocol with Stockfish");
        }
    }

}