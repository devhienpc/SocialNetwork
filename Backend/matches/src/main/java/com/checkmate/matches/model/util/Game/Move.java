// Nicholas Donahue 2023
package com.checkmate.matches.model.util.Game;

import com.checkmate.matches.model.util.Pieces.Piece;
public class Move {
    // Variables to store the start and end positions of the move
    private int row0, col0; // Starting position
    private int row1, col1; // Ending position
    private String move; // String representation of the move

    // Variables to store information about the moved and captured pieces
    private boolean movedPieceWasPawn = false;
    private String movedPieceType;
    private boolean movedPieceWhite;
    private String capturedPieceType;
    private boolean capturedPieceWhite;

    // Constructor for the Move class
    public Move(String move) {
        assert (move.length() == 4); // Asserting that the move string is of correct length
        this.move = move;
        // Parsing the move string to get row and column indices
        row0 = 8 - move.charAt(1) + '0';
        row1 = 8 - move.charAt(3) + '0';
        col0 = move.charAt(0) - 'a';
        col1 = move.charAt(2) - 'a';

        // Initializing piece-related variables
        movedPieceType = null;
        movedPieceWhite = false;
        capturedPieceType = null;
        capturedPieceWhite = false;
    }

    // Getters for start and end positions
    public int getRow0() { return row0; }
    public int getCol0() { return col0; }
    public int getRow1() { return row1; }
    public int getCol1() { return col1; }

    // Method to set the moved piece
    public void setMovedPiece(Piece thePiece) {
        this.movedPieceType = thePiece.getPieceType();
        this.movedPieceWhite = thePiece.isWhite();
    }

    // Method to set the captured piece
    public void setCapturedPiece(Piece thePiece) {
        this.capturedPieceType = thePiece.getPieceType();
        this.capturedPieceWhite = thePiece.isWhite();
    }

    // Method to mark that the moved piece was a pawn
    public void setWasPawn() { movedPieceWasPawn = true; }
    public boolean wasPawn() { return movedPieceWasPawn; }

    // Method to check if a piece was captured
    public boolean didCapturePiece() {
        return capturedPieceType != null;
    }

    // Method to retrieve the moved piece
    public Piece getMovedPiece() {
        return Piece.createPiece(movedPieceType, movedPieceWhite);
    }

    // Method to retrieve the captured piece, if any
    public Piece getCapturedPiece() {
        if (capturedPieceType != null) {
            return Piece.createPiece(capturedPieceType, capturedPieceWhite);
        }
        return null;
    }

    // Method to return a string representation of the move
    @Override
    public String toString() {
        return move;
    }
}
