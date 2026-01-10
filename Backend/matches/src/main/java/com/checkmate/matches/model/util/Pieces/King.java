// Nicholas Donahue 2023
package com.checkmate.matches.model.util.Pieces;

import com.checkmate.matches.model.util.Game.*;


public class King extends Piece {
    // Constructor for King, calling the constructor of the superclass (Piece)
    public King(boolean white) {
        super(white, "King"); // Setting the color and type of the piece
    }

    // Overridden method to check if a move is legal specifically for a king
    @Override
    public boolean isLegal(Move move, Game game) {
        // First, it calls the isLegal method from the superclass to check basic move legality
        if (!super.isLegal(move, game))
            return false;

        // Specific rules for king movement
        int rowDiff = move.getRow1() - move.getRow0(); // Calculating row difference
        int colDiff = move.getCol1() - move.getCol0(); // Calculating column difference

        // The king can move only one square in any direction
        if (Math.abs(rowDiff) > 1 || Math.abs(colDiff) > 1)
            return false; // Return false if the move is more than one square away

        return true; // Return true if the move is valid for a king
    }

    // Method to return a string representation of the King
    @Override
    public String toString() {
        // Unicode characters for the white and black king
        return white ? "\u2654" : "\u265A";
    }
}
