package piece;

import main.GamePanel;

public class Rook extends Piece {
    public Rook(int color, int col, int row) {
        super(color, col, row);

        if (color == GamePanel.WHITE) {
            image = getImage("/piece/w-rook");
        }
        else {
            image = getImage("/piece/b-rook");
        }
    }

    public boolean canMove(int targetCol, int targetRow) {
        if (isWithinBoard(targetCol, targetRow) == false && isSameSquare(targetCol, targetRow)) {
            return false;
        } 

        if ((targetCol == preCol) || (targetRow == preRow)) {
            if (isValidSquare(targetCol, targetRow) && isThereOtherPiecesOnTheWay(targetCol, targetRow) == false) {
                return true;
            }
        }

        return false;
    }
}