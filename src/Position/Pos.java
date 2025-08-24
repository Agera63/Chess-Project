package Position;

public class Pos {
    public int num;
    public int letter;

    public Pos(int num, int letter){
        this.num = num;
        this.letter = letter;
    }

    /**
     * Allows for Pos objects to convert a number to a letter.
     * IMPORTANT : can only use with Pos object.
     * @return the letter corresponding to the number on the board.
     */
    public char numToLetter(){
        char temp = ' ';
        switch (letter){
            case 0 : temp = 'a'; break;
            case 1 : temp = 'b'; break;
            case 2 : temp = 'c'; break;
            case 3 : temp = 'd'; break;
            case 4 : temp = 'e'; break;
            case 5 : temp = 'f'; break;
            case 6 : temp = 'g'; break;
            case 7 : temp = 'h'; break;
        }
        return temp;
    }

    /**
     * Getter for String version of the position
     * @return position in string
     */
    public String posToString(){
        return String.valueOf(numToLetter()) + String.valueOf(num + 1);
    }

    /**
     * Take a String like e4 and turns it into a new Pos object
     * @param pos The position we want to convert
     * @return New Pos object
     */
    public static Pos stringToPos(String pos){
        int temp = 8;
        //switches the letter to the corresponding number.
        switch (pos.charAt(0)) {
            case 'a': temp =  0; break;
            case 'b': temp =  1; break;
            case 'c': temp =  2; break;
            case 'd': temp =  3; break;
            case 'e': temp =  4; break;
            case 'f': temp =  5; break;
            case 'g': temp =  6; break;
            case 'h': temp =  7; break;
        }
        return new Pos(Integer.parseInt(String.valueOf(pos.charAt(1))) - 1, temp);
    }

    /**
     * Checks the direction of the movement and returns the direction.
     * @param start First position.
     * @param finish Final position.
     * @return String of movement type.
     */
    public static String checkMovementDirection(Pos start, Pos finish){
        //calculates if its a L movement (knight)
        boolean knightMovement =
                (start.letter == finish.letter + 2 && start.num == finish.num - 1) ||
                (start.letter == finish.letter + 2 && start.num == finish.num + 1) ||
                (start.letter == finish.letter + 1 && start.num == finish.num - 2) ||
                (start.letter == finish.letter + 1 && start.num == finish.num + 2) ||
                (start.letter == finish.letter - 2 && start.num == finish.num - 1) ||
                (start.letter == finish.letter - 2 && start.num == finish.num + 1) ||
                (start.letter == finish.letter - 1 && start.num == finish.num - 2) ||
                (start.letter == finish.letter - 1 && start.num == finish.num + 2);
        //calculates if its a diagonal movement
        boolean diagonalMovement = Math.abs(finish.letter - start.letter) == Math.abs(finish.num - start.num);

        if(start.num != finish.num && start.letter == finish.letter){
            return "vertical";
        } else if (start.num == finish.num && start.letter != finish.letter) {
            return "horizontal";
        } else if (knightMovement){
            return "knight";
        } else if (diagonalMovement) {
            return "diagonal";
        } else {
            return null;
        }
    }

    /**
     * Checks how many squares the piece is moving
     * @param movementType what type of movement (vertical, horizontal, horizontal)
     * @param start initial position
     * @param finish final position
     * @return number of squares it moved
     */
    public static int squaresMoved(String movementType, Pos start, Pos finish){
        int counter = 0;
        if (movementType.equals("vertical") || movementType.equals("diagonal")) {
            counter = Math.abs(finish.num - start.num);
        } else if (movementType.equals("horizontal")){
            counter = Math.abs(finish.letter - start.letter);
        }
        return counter;
    }
}
