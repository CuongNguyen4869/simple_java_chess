package main;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;

import javax.swing.JPanel;

import piece.*;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

public class GamePanel extends JPanel implements Runnable{
    public static final int WIDTH = 1100;
    public static final int HEIGHT = 800;
    final int FPS = 60;
    Thread gameThread;
    Board board = new Board();
    Mouse mouse = new Mouse();

    // COLOR
    public static final int WHITE = 0;
    public static final int BLACK = 1;
    int currentColor = WHITE;

    // BOOLEANS
    boolean canMove;
    boolean validSquare;
    boolean promotion;

    //PIECES
    public static ArrayList<Piece> pieces = new ArrayList<>();      //backup for reset position
    public static ArrayList<Piece> simPieces = new ArrayList<>();   //current position
    ArrayList<Piece> promoPieces = new ArrayList<>();

    Piece activeP;
    public static Piece castlingP;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.black);
        addMouseListener(mouse);
        addMouseMotionListener(mouse);

        setPieces();
        copyPieces(pieces, simPieces);
    }

    public void launchGame() {
        gameThread = new Thread(this);
        gameThread.start(); // calls run()
    }
    
    public void setPieces() {
        //WHITE
        pieces.add(new Pawn(WHITE, 0, 6));
        pieces.add(new Pawn(WHITE, 1, 6));
        pieces.add(new Pawn(WHITE, 2, 6));
        pieces.add(new Pawn(WHITE, 3, 6));
        pieces.add(new Pawn(WHITE, 4, 6));
        pieces.add(new Pawn(WHITE, 5, 6));
        pieces.add(new Pawn(WHITE, 6, 6));
        pieces.add(new Pawn(WHITE, 7, 6));
        pieces.add(new Rook(WHITE, 0, 7));        
        pieces.add(new Rook(WHITE, 7, 7));
        pieces.add(new Knight(WHITE, 1, 7));       
        pieces.add(new Knight(WHITE, 6, 7));
        pieces.add(new Bishop(WHITE, 2, 7));       
        pieces.add(new Bishop(WHITE, 5, 7));
        pieces.add(new Queen(WHITE, 3, 7));
        pieces.add(new King(WHITE, 4, 7));      
        
        //BLACK
        pieces.add(new Pawn(BLACK, 0, 1));
        pieces.add(new Pawn(BLACK, 1, 1));
        pieces.add(new Pawn(BLACK, 2, 1));
        pieces.add(new Pawn(BLACK, 3, 1));
        pieces.add(new Pawn(BLACK, 4, 1));
        pieces.add(new Pawn(BLACK, 5, 1));
        pieces.add(new Pawn(BLACK, 6, 1));
        pieces.add(new Pawn(BLACK, 7, 1));
        pieces.add(new Rook(BLACK, 0, 0));        
        pieces.add(new Rook(BLACK, 7, 0));
        pieces.add(new Knight(BLACK, 1, 0));       
        pieces.add(new Knight(BLACK, 6, 0));
        pieces.add(new Bishop(BLACK, 2, 0));       
        pieces.add(new Bishop(BLACK, 5, 0));
        pieces.add(new Queen(BLACK, 3, 0));
        pieces.add(new King(BLACK, 4, 0));
    }

    private void copyPieces(ArrayList<Piece> source, ArrayList<Piece> target) {
        target.clear();
        for (int i = 0; i < source.size(); i++) {
            target.add(source.get(i));
        }
    }

    private boolean isIllegal() {
        Piece king = null;
        
        for (Piece piece : simPieces) {
            if (piece.type == Type.KING && piece.color == currentColor) {
                king = piece;
                break;
            }
        }

        for (Piece piece : simPieces) {
            if (piece.color != currentColor && piece.canMove(king.col, king.row)) {
                return true;
            }
        }
        return false;
    }

    private void checkCastling() {
        if (castlingP != null) {
            if (castlingP.col == 0) {
                castlingP.col = 3;
            } else {
                castlingP.col = 5;
            }
            castlingP.x = castlingP.getX(castlingP.col);
        }
    }
    
    private void changePlayer() {
        if (currentColor == WHITE) {
            currentColor = BLACK;

            // Reset two stepped for en passant
            for (Piece piece : pieces) {
                if (piece.color == BLACK) {
                    piece.twoStepped = false;
                }
            }

        } else {
            currentColor = WHITE;

            // Reset two stepped for en passant
            for (Piece piece : pieces) {
                if (piece.color == WHITE) {
                    piece.twoStepped = false;
                }
            }
        }
        activeP = null;
    }

    private void promoting() {
        if (mouse.pressed) {
            for (Piece piece : promoPieces) {
                if (piece.col == mouse.x/Board.SQUARE_SIZE && piece.row == mouse.y/Board.SQUARE_SIZE) {
                    switch (piece.type) {
                    case ROOK:
                        simPieces.add(new Rook(currentColor, activeP.col, activeP.row));
                        break;
                    case KNIGHT:
                        simPieces.add(new Knight(currentColor, activeP.col, activeP.row));
                        break;
                    case BISHOP:
                        simPieces.add(new Bishop(currentColor, activeP.col, activeP.row));
                        break;
                    case QUEEN:
                        simPieces.add(new Queen(currentColor, activeP.col, activeP.row));
                        break;
                    default: break;
                    }

                    simPieces.remove(activeP.getIndex());
                    copyPieces(simPieces, pieces);
                    activeP = null;
                    promotion = false;
                    changePlayer();
                }
            }
        }
    }
    
    private void update() {
        
        if (promotion) {
            promoting();
            return;
        }
        
        // MOUSE PRESSED
        if (mouse.pressed) {
            // If activeP == null, check if you can pick up a piece
            if (activeP == null) {
                for (Piece piece : simPieces) {
                    if (piece.color == currentColor &&
                            piece.col == mouse.x / Board.SQUARE_SIZE &&
                            piece.row == mouse.y / Board.SQUARE_SIZE) {
                        
                        activeP = piece;
                    }
                }
            }
            // If the player pick up an ally piece simulate the move
            else {
                simulate();
            }
        }

        // MOUSE RELEASED
        if (mouse.pressed == false) {
            if (activeP != null) {
                if (validSquare) {
                    copyPieces(simPieces, pieces);
                    activeP.updatePosition();

                    if (castlingP != null) {
                        castlingP.updatePosition();
                    }

                    if (activeP.hittingP != null) {
                        simPieces.remove(activeP.hittingP.getIndex());
                    }

                    if (canPromote()) {
                        promotion = true;
                    } else {
                        changePlayer();
                    }
                } else {
                    copyPieces(simPieces, pieces);
                    activeP.resetPosition();
                    activeP = null;  
                }                               
            }
        }
    }

    private void simulate() {

        try {
            Thread.sleep(0, 1);
        } catch (InterruptedException e) {
            System.out.println("Thread was interrupted");
        }
        
        canMove = false;
        validSquare = false;

        // Restore the removed piece during the simulation
        copyPieces(simPieces, pieces);

        // Reset the castling piece's position
        if (castlingP != null) {
            castlingP.col = castlingP.preCol;
            castlingP.x = castlingP.getX(castlingP.col);
            castlingP = null;
        }
        
        // Held piece, update position
        activeP.x = mouse.x - Board.HALF_SQUARE_SIZE;
        activeP.y = mouse.y - Board.HALF_SQUARE_SIZE;
        activeP.col = activeP.getCol(activeP.x);
        activeP.row = activeP.getRow(activeP.y);

        if (activeP.canMove(activeP.col, activeP.row)) {
            canMove = true;

            checkCastling();

            if (isIllegal() == false) {
                validSquare = true;
            }

            // if (activeP.hittingP != null) {
            //     simPieces.remove(activeP.hittingP.getIndex());
            // }
        }
    }

    private boolean canPromote() {
        if (activeP.type == Type.PAWN) {
            if (currentColor == WHITE && activeP.row == 0 || currentColor == BLACK && activeP.row == 7) {
                promoPieces.clear();
                promoPieces.add(new Rook(currentColor, 9, 2));
                promoPieces.add(new Knight(currentColor, 9, 3));
                promoPieces.add(new Bishop(currentColor, 9, 4));
                promoPieces.add(new Queen(currentColor, 9, 5));
                return true;
            }
        }
        return false;
    }
    
    public void paintComponent(Graphics g) {    // is called by repaint()
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        //  DRAW BOARD
        board.draw(g2);

        // DRAW PIECES
        for (Piece p : simPieces) {
            p.draw(g2);
        }

        // Chess panel to move into
        if (activeP != null) {
            if (canMove) {
                if (isIllegal()) {
                    g2.setColor(Color.gray);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
                    g2.fillRect(activeP.col * Board.SQUARE_SIZE, activeP.row * Board.SQUARE_SIZE,
                        Board.SQUARE_SIZE, Board.SQUARE_SIZE);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                    activeP.draw(g2);
                } else {
                    g2.setColor(Color.white);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
                    g2.fillRect(activeP.col * Board.SQUARE_SIZE, activeP.row * Board.SQUARE_SIZE,
                            Board.SQUARE_SIZE, Board.SQUARE_SIZE);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                    activeP.draw(g2);
                }                
            }           
        }

        // Turn message
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setFont(new Font("Book Antique", Font.PLAIN, 40));
        g2.setColor(Color.white);

        if (promotion) {
            g2.drawString("Promote to:", 840, 150);
            for (Piece piece : promoPieces) {
                g2.drawImage(piece.image, piece.getX(piece.col), piece.getY(piece.row),
                    Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
            }
        } else {
            if (currentColor == WHITE) {
                g2.drawString("White's turn", 840, 550);
            } else {
                g2.drawString("Black's turn", 840, 250);
            }
        }     
    }

    @Override
    public void run() {     // is called by thread start()
        double drawInterval = 1000000000/FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;

            if (delta >= 1) {
                update();
                repaint();      // calls paintComponent()
                delta--;
            }
        }
    }
}
