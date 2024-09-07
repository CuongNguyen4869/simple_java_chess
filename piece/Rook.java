package piece;

import main.GamePanel;
import main.Type;

public class Rook extends Piece {
    public Rook(int color, int col, int row) {
        super(color, col, row);

        type = Type.ROOK;

        if (color == GamePanel.WHITE) {
            image = getImage("/piece/w-rook");
        }
        else {
            image = getImage("/piece/b-rook");
        }
    }

    public boolean canMove(int targetCol, int targetRow) {
        if (isWithinBoard(targetCol, targetRow) == false || isSameSquare(targetCol, targetRow)) {
            return false;
        } 

        if ((targetCol == preCol) || (targetRow == preRow)) {
            if (isValidSquare(targetCol, targetRow) && isThereOtherPiecesOnTheWay(targetCol, targetRow) == false) {
                return true;
            }
        }

        return false;
    }

    public boolean doesGuard(int targetCol, int targetRow) {
        if (isWithinBoard(targetCol, targetRow) == false || isSameSquare(targetCol, targetRow)) {
            return false;
        } 

        if ((targetCol == preCol) || (targetRow == preRow)) {
            if (isThereOtherPiecesOnTheWay(targetCol, targetRow) == false) {
                return true;
            }
        }

        return false;
    }
}