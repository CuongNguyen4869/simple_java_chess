package piece;

import main.GamePanel;
import main.Type;

public class Bishop extends Piece {
    public Bishop(int color, int col, int row) {
        super(color, col, row);
        
        type = Type.BISHOP;

        if (color == GamePanel.WHITE) {
            image = getImage("/piece/w-bishop");
        }
        else {
            image = getImage("/piece/b-bishop");
        }
    }

    public boolean canMove(int targetCol, int targetRow) {
        if (isWithinBoard(targetCol, targetRow) == false || isSameSquare(targetCol, targetRow)) {
            return false;
        } 

        if (Math.abs(targetCol - preCol) == Math.abs(targetRow - preRow) ) {
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

        if (Math.abs(targetCol - preCol) == Math.abs(targetRow - preRow) ) {
            if (isThereOtherPiecesOnTheWay(targetCol, targetRow) == false) {
                return true;
            }
        }

        return false;
    }
}