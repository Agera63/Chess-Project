package GameManagement;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class HistoryHelper {
    private FileOutputStream moveHistoryFile;
    private PrintStream moveHistoryOutput;
    public HistoryHelper(String file) {
         moveHistoryFile = ouvrirFichier(file);
         moveHistoryOutput = new PrintStream(moveHistoryFile);
         moveHistoryOutput.println("History of all the users move : ");
    }
    public void addMove(char[] charArrayMove) {
        String move = formatMove(charArrayMove);
        moveHistoryOutput.append(move + "\n");
    }

    public void closeFile(){
        try{
            moveHistoryFile.close();
            moveHistoryOutput.close();
        } catch (Exception e) {
            System.out.println("Error while closing file.");
        }
    }

    private String formatMove(char[] charArrayMove) {
        String moveStr = "";
        for(char c : charArrayMove) {
            moveStr += c;
        }
        return moveStr;
    }

    private FileOutputStream ouvrirFichier(String file){
        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream(file);
        } catch (Exception e) {
            System.out.println("\"MoveHistory.txt\" was not correctly initalized, this games history will not be saved.)");
        }
        return fout;
    }
}
