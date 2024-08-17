package piece;

import main.GamePanel;

public class Pawn extends Piece {
    public Pawn(int color, int col, int row) {
        super(color, col, row);

        if (color == GamePanel.WHITE) {
            image = getImage("/piece/w-pawn");
        }
        else {
            image = getImage("/piece/b-pawn");
        }
    }

    public boolean canMove(int targetCol, int targetRow) {
        if (isWithinBoard(targetCol, targetRow) == false) {
            return false;
        } 

        {
            if (color == GamePanel.WHITE) {
                if (targetCol == preCol && (preRow - targetRow == 1 || (preRow == 6 && preRow - targetRow == 2))) {
                    return true;
                }
            } else {
                if (targetCol == preCol && (targetRow - preRow == 1 || (preRow == 1 && targetRow - preRow == 2))) {
                    return true;
                }
            }

        }

        return false;
    }
}
