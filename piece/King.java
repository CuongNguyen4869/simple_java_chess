package piece;

import main.GamePanel;
import main.Type;

public class King extends Piece {
    public King(int color, int col, int row) {
        super(color, col, row);

        type = Type.KING;

        if (color == GamePanel.WHITE) {
            image = getImage("/piece/w-king");
        }
        else {
            image = getImage("/piece/b-king");
        }
    }

    public boolean canMove(int targetCol, int targetRow) {

        if (isWithinBoard(targetCol, targetRow) == false) {
            return false;
        }
        //if (Math.abs(targetCol - preCol) <= 1 && Math.abs(targetRow - preRow) <= 1) {

        // Normal movement
        if (Math.abs(targetCol - preCol) + Math.abs(targetRow - preRow) == 1 ||
                Math.abs(targetCol - preCol) * Math.abs(targetRow - preRow) == 1) {
            if (isValidSquare(targetCol, targetRow)) {
                return true;
            }           
        }

        // Castling
        if (moved == false) {
            // Right castling
            if (targetCol == preCol + 2 && targetRow == preRow && 
                    isThereOtherPiecesOnTheWay(targetCol + 1, targetRow) == false) {
                for (Piece piece : GamePanel.simPieces) {
                    if (piece.col == preCol + 3 && piece.row == preRow && piece.moved == false) {
                        GamePanel.castlingP = piece;
                        return true;
                    }
                }
            }

            // Left castling
            if (targetCol == preCol - 2 && targetRow == preRow && 
                    isThereOtherPiecesOnTheWay(targetCol - 2, targetRow) == false) {
                for (Piece piece : GamePanel.simPieces) {
                    if (piece.col == preCol - 4 && piece.row == preRow && piece.moved == false) {
                        GamePanel.castlingP = piece;
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean doesGuard(int targetCol, int targetRow) {
        if (isWithinBoard(targetCol, targetRow) == false) {
            return false;
        }
        //if (Math.abs(targetCol - preCol) <= 1 && Math.abs(targetRow - preRow) <= 1) {

        // Normal movement
        if (Math.abs(targetCol - preCol) + Math.abs(targetRow - preRow) == 1 ||
                Math.abs(targetCol - preCol) * Math.abs(targetRow - preRow) == 1) {
            return true;         
        }

        return false;
    }
}