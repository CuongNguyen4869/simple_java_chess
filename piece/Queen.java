package piece;

import main.GamePanel;

public class Queen extends Piece {
    public Queen(int color, int col, int row) {
        super(color, col, row);

        if (color == GamePanel.WHITE) {
            image = getImage("/piece/w-queen");
        }
        else {
            image = getImage("/piece/b-queen");
        }
    }

    public boolean canMove(int targetCol, int targetRow) {
        if (isWithinBoard(targetCol, targetRow) == false || isSameSquare(targetCol, targetRow)) {
            return false;
        } 

        if (Math.abs(targetCol - preCol) == Math.abs(targetRow - preRow) ||
                (targetCol == preCol) || (targetRow == preRow)) {
            if (isValidSquare(targetCol, targetRow) && isThereOtherPiecesOnTheWay(targetCol, targetRow) == false) {
                return true;
            }
        }

        return false;
    }
}