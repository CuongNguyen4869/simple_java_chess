package piece;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import main.Board;
import main.GamePanel;
import main.Type;

public class Piece {

    public Type type;
    public BufferedImage image;
    public int x, y;
    public int col, row, preCol, preRow;
    public int color;
    public Piece hittingP;
    public boolean moved = false, twoStepped = false;

    public Piece(int color, int col, int row) {
        this.color = color;
        this.col = col;
        this.row = row;
        x = getX(col);
        y = getY(row);
        preCol = col;
        preRow = row;
    }

    public BufferedImage getImage(String imagePath) {
        BufferedImage image = null;
        System.out.println(imagePath + ".png");
        try {
            // need to improve the reading path
            image = ImageIO.read(getClass().getResourceAsStream("../res/" + imagePath + ".png"));
        } catch(IOException e) {
            e.printStackTrace();
            
        }
        
        return image;
    }

    public int getX(int col) {
        return col * Board.SQUARE_SIZE;
    }

    public int getY(int row) {
        return row * Board.SQUARE_SIZE;
    }

    public int getCol(int x) {
        return (x + Board.HALF_SQUARE_SIZE) / Board.SQUARE_SIZE;
    }

    public int getRow(int y) {
        return (y + Board.HALF_SQUARE_SIZE) / Board.SQUARE_SIZE;
    }

    public int getIndex() {
        for (int index = 0; index < GamePanel.simPieces.size(); index++) {
            if (GamePanel.simPieces.get(index) == this) {
                return index;
            }
        }
        return -1;
    }

    public void updatePosition() {
        if (type == Type.PAWN) {
            if (Math.abs(row - preRow) == 2) {
                twoStepped = true;
            }
        }

        x = getX(col);
        y = getY(row);
        preCol = getCol(x);
        preRow = getRow(y);
        moved = true;
    }
    
    public void resetPosition() {
        col = preCol;
        row = preRow;
        x = getX(col);
        y = getY(row);
    }

    public boolean canMove(int targetCol, int targetRow) {
        return false;
    }

    public boolean doesGuard(int targetCol, int targetRow) {
        return false;
    }
    
    public boolean isWithinBoard(int targetCol, int targetRow) {
        if (targetCol >= 0 && targetCol <= 7 && targetRow >= 0 && targetRow <= 7) {
            return true;
        }
        return false;
    }

    public boolean isSameSquare(int targetCol, int targetRow) {
        if (targetCol == preCol && targetRow == preRow) {
            return true;
        }
        return false;
    }

    public boolean isThereOtherPiecesOnTheWay(int targetCol, int targetRow) {
        
        if (targetRow == preRow){
            // moving left
            for (int c = preCol - 1; c > targetCol; c--) {
                for (Piece piece : GamePanel.simPieces) {
                    if (piece.col == c && piece.row == targetRow && piece !=  this) {
                        // hittingP = piece;
                        return true;
                    }
                }
            }

            // moving right
            for (int c = preCol + 1; c < targetCol; c++) {
                for (Piece piece : GamePanel.simPieces) {
                    if (piece.col == c && piece.row == targetRow && piece !=  this) {
                        // hittingP = piece;
                        return true;
                    }
                }
            }
        }

        if (targetCol == preCol) {
            // moving up
            for (int r = preRow - 1; r > targetRow; r--){
                for (Piece piece : GamePanel.simPieces) {
                    if (piece.row == r && piece.col == targetCol) {
                        return true;
                    }
                }
            }
            // moving down
            for (int r = preRow + 1; r < targetRow; r++){
                for (Piece piece : GamePanel.simPieces) {
                    if (piece.row == r && piece.col == targetCol) {
                        return true;
                    }
                }
            }
        }

        if (Math.abs(targetCol - preCol) == Math.abs(targetRow - preRow)) {
            // moving up left
            for (int c = preCol - 1, r = preRow - 1; c > targetCol && r > targetRow; c--, r--) {
                for (Piece piece : GamePanel.simPieces) {
                    if (piece.row == r && piece.col == c) {
                        return true;
                    }
                }
            }
            // moving up right
            for (int c = preCol + 1, r = preRow - 1; c < targetCol && r > targetRow; c++, r--) {
                for (Piece piece : GamePanel.simPieces) {
                    if (piece.row == r && piece.col == c) {
                        return true;
                    }
                }
            }
            // moving down left
            for (int c = preCol - 1, r = preRow + 1; c > targetCol && r < targetRow; c--, r++) {
                for (Piece piece : GamePanel.simPieces) {
                    if (piece.row == r && piece.col == c) {
                        return true;
                    }
                }
            }
            // moving down right
            for (int c = preCol + 1, r = preRow + 1; c < targetCol && r < targetRow; c++, r++) {
                for (Piece piece : GamePanel.simPieces) {
                    if (piece.row == r && piece.col == c) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public Piece getHittingP(int targetCol, int targetRow) {
        for (Piece piece : GamePanel.simPieces) {
            // System.out.println("col " + piece.col + " row " + piece.row);
            if (piece.col == targetCol && piece.row == targetRow && piece !=  this) {
                return piece;
            }
        }
        return null;
    }

    public boolean isValidSquare(int targetCol, int targetRow) {
        
        hittingP = getHittingP(targetCol, targetRow);
        
        if (hittingP == null) {
            return true;
        } else {
            if (hittingP.color != this.color) {
                // hittingP = null;
                return true;
            } else {
                hittingP = null;                
            }
        }
        return false;
    }

    public void draw(Graphics2D g2) {
        g2.drawImage(image, x, y, Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
    }
}
