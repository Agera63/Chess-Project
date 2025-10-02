package ChessClient.Code;

import GameManagement.GameManager;
import java.io.*;

public class StockFishChessClient {
    private static final String STOCKFISH_PATH = GameManager.StockFishPath;
    private static final int SEARCH_DEPTH = 15;
    private static final String[] UCI_INIT_COMMANDS = {"uci"};

    // Reuse process for better performance (optional enhancement)
    private static Process stockfishProcess;
    private static BufferedWriter processWriter;
    private static BufferedReader processReader;

    /**
     * Get the best move from Stockfish for the current board position
     * @param board 8x8 char array representing the chess board
     * @return Best move in format "e2-e4" or "No move found"
     */
    public static String getBestMoveFromBoard(char[][] board) throws IOException {
        String fen = boardToFen(board);

        try {
            initializeStockfishProcess();
            return getBestMoveFromFen(fen);
        } finally {
            cleanupProcess();
        }
    }

    private static void initializeStockfishProcess() throws IOException {
        ProcessBuilder pb = new ProcessBuilder(STOCKFISH_PATH);
        pb.redirectErrorStream(true);
        stockfishProcess = pb.start();

        processWriter = new BufferedWriter(new OutputStreamWriter(stockfishProcess.getOutputStream()));
        processReader = new BufferedReader(new InputStreamReader(stockfishProcess.getInputStream()));

        // Initialize UCI protocol
        sendCommand("uci");
        waitForUciOk();
    }

    private static String getBestMoveFromFen(String fen) throws IOException {
        sendCommand("position fen " + fen);
        sendCommand("go depth " + SEARCH_DEPTH);

        String line;
        while ((line = processReader.readLine()) != null) {
            if (line.startsWith("bestmove")) {
                String[] parts = line.split(" ");
                if (parts.length > 1 && !parts[1].equals("(none)")) {
                    return formatMove(parts[1]);
                }
                break;
            }
        }
        return "No move found";
    }

    private static void sendCommand(String command) throws IOException {
        processWriter.write(command + "\n");
        processWriter.flush();
    }

    private static String formatMove(String move) {
        if (move != null && move.length() >= 4) {
            System.out.println("Stockfish plays " + move.substring(0, 2) + " to " + move.substring(2, 4) + ".");
            return move.substring(0, 2) + "-" + move.substring(2, 4);
        }
        return "No move found";
    }

    private static String boardToFen(char[][] board) {
        StringBuilder fen = new StringBuilder(80); // Pre-allocate capacity

        // Convert board to FEN notation (rank 8 to rank 1)
        for (int row = 7; row >= 0; row--) {
            appendRankToFen(fen, board[row]);
            if (row > 0) fen.append('/');
        }

        // Add FEN metadata: active color, castling, en passant, halfmove, fullmove
        boolean stockfishIsWhite = !GameManager.getColor(); // Stockfish plays opposite of player
        fen.append(' ').append(stockfishIsWhite ? 'w' : 'b').append(" - - 0 1");

        return fen.toString();
    }

    private static void appendRankToFen(StringBuilder fen, char[] rank) {
        int emptyCount = 0;

        for (char piece : rank) {
            if (piece == '\0') {
                emptyCount++;
            } else {
                if (emptyCount > 0) {
                    fen.append(emptyCount);
                    emptyCount = 0;
                }
                fen.append(piece);
            }
        }

        if (emptyCount > 0) {
            fen.append(emptyCount);
        }
    }

    private static void waitForUciOk() throws IOException {
        String line;
        while ((line = processReader.readLine()) != null) {
            if ("uciok".equals(line)) {
                return;
            }
        }
        throw new IOException("Failed to initialize UCI protocol with Stockfish");
    }

    private static void cleanupProcess() {
        try {
            if (processWriter != null) {
                processWriter.write("quit\n");
                processWriter.flush();
                processWriter.close();
            }
        } catch (IOException ignored) {}

        try {
            if (processReader != null) {
                processReader.close();
            }
        } catch (IOException ignored) {}

        if (stockfishProcess != null) {
            try {
                if (!stockfishProcess.waitFor(1, java.util.concurrent.TimeUnit.SECONDS)) {
                    stockfishProcess.destroyForcibly();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                stockfishProcess.destroyForcibly();
            }
        }
    }
}