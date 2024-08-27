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
        if (isWithinBoard(targetCol, targetRow) == false /* && isSameSquare(targetCol, targetRow) */) {
            return false;
        }

        int moveValue;
        if (color == GamePanel.WHITE) {
            moveValue = -1;
        } else {
            moveValue = 1;
        }

        hittingP = getHittingP(targetCol, targetRow);

        // 1 square movement
        if (targetCol == preCol && targetRow == preRow + moveValue && hittingP == null) {
            return true;
        }

        // 2 square movement
        if (targetCol == preCol && targetRow == preRow + moveValue * 2 && hittingP == null && moved == false && 
                isThereOtherPiecesOnTheWay(targetCol, targetRow) == false) {
            return true;
        }

        // capture pieces
        if (Math.abs(targetCol - preCol) == 1 && targetRow == preRow + moveValue 
                && hittingP != null && hittingP.color != color) {
            return true;
        }

        /*
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
        */

        return false;
    }
}
