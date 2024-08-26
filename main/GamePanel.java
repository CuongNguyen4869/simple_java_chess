package main;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.swing.JPanel;

import piece.*;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;

public class GamePanel extends JPanel implements Runnable{
    public static final int WIDTH = 800;
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

    //PIECES
    public static ArrayList<Piece> pieces = new ArrayList<>();      //backup for reset position
    public static ArrayList<Piece> simPieces = new ArrayList<>();   //current position?
    Piece activeP;

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

    private void update() {
        
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
                    if (activeP.hittingP != null) {
                        simPieces.remove(activeP.hittingP.getIndex());
                    }
                } else {
                    copyPieces(simPieces, pieces);
                    activeP.resetPosition();
                }
                activeP = null;                                
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
        
        // Held piece, update position
        activeP.x = mouse.x - Board.HALF_SQUARE_SIZE;
        activeP.y = mouse.y - Board.HALF_SQUARE_SIZE;
        activeP.col = activeP.getCol(activeP.x);
        activeP.row = activeP.getRow(activeP.y);

        // System.out.println("mouse.x " + mouse.x +
        //                     " mouse.y " + mouse.y +
        //                     " activeP.x " + activeP.x +
        //                     " activeP.y " + activeP.y +
        //                     " activeP.col " + activeP.col +
        //                     " activeP.row " + activeP.row);  

        if (activeP.canMove(activeP.col, activeP.row)) {
            canMove = true;
            validSquare = true;

            // if (activeP.hittingP != null) {
            //     simPieces.remove(activeP.hittingP.getIndex());
            // }
        }
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
                g2.setColor(Color.white);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
                g2.fillRect(activeP.col * Board.SQUARE_SIZE, activeP.row * Board.SQUARE_SIZE,
                        Board.SQUARE_SIZE, Board.SQUARE_SIZE);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                activeP.draw(g2);
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
