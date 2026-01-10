// Nicholas Donahue 2023
package com.checkmate.matches.model.util.Pieces;


import com.checkmate.matches.model.util.Game.*;

public class Bishop extends Queen {

    // Constructor for Bishop, calling the constructor of the superclass (Queen)
    public Bishop(boolean white) {
        super(white, "Bishop"); // Setting the color and type of the piece
    }

    // Private method to check if there are pieces on the diagonal path
    private boolean PiecesOnDiagonalPath(Move move, Game game) {
        int rowDiff = move.getRow1() - move.getRow0();
        int colDiff = move.getCol1() - move.getCol0();

        // Check each square along the diagonal for the presence of other pieces
        for (int i = 1; i < Math.abs(rowDiff); i++) {
            int n = i * (rowDiff > 0 ? 1 : -1);
            if (game.getPiece(move.getRow0() + n, move.getCol0() + n) != null)
                return true; // Return true if a piece is found
        }

        return false; // Return false if the path is clear
    }

    // Overridden method to check if a move is legal specifically for a bishop
    @Override
    public boolean isLegal(Move move, Game game) {
        // First, it calls the isLegal method from the superclass to check basic move legality
        if (!super.isLegal(move, game))
            return false;

        // Specific rules for bishop movement
        int rowDiff = move.getRow1() - move.getRow0();
        int colDiff = move.getCol1() - move.getCol0();

        // Check if the move is along a diagonal
        if (Math.abs(rowDiff) != Math.abs(colDiff))
            return false; // Return false if the move is not diagonal

        // Check if there are pieces on the diagonal path
        if (PiecesOnDiagonalPath(move, game))
            return false; // Return false if the path is not clear

        return true; // Return true if the move is valid for a bishop
    }

    // Method to return a string representation of the Bishop
    @Override
    public String toString() {
        // Unicode characters for the white and black bishop
        return white ? "\u2657" : "\u265D";
    }
}
