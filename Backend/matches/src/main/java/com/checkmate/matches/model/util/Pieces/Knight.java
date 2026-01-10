// Nicholas Donahue 2023
package com.checkmate.matches.model.util.Pieces;

import com.checkmate.matches.model.util.Game.*;


public class Knight extends Piece {
    // Constructor for Knight, calling the constructor of the superclass (Piece)
    public Knight(boolean white) {
        super(white, "Knight"); // Setting the color and type of the piece
    }

    // Overridden method to check if a move is legal specifically for a knight
    @Override
    public boolean isLegal(Move move, Game game) {
        // First, it calls the isLegal method from the superclass to check basic move legality
        if (!super.isLegal(move, game))
            return false;

        // Specific rules for knight movement
        int rowDiff = move.getRow1() - move.getRow0(); // Calculating row difference
        int colDiff = move.getCol1() - move.getCol0(); // Calculating column difference

        // Check for the L-shaped move of a knight (2 squares in one direction and 1 in the other)
        if ((Math.abs(rowDiff) == 2 && Math.abs(colDiff) != 1) ||
                (Math.abs(rowDiff) == 1 && Math.abs(colDiff) != 2)) {
            return false; // Return false if the move doesn't match the L-shaped pattern
        }

        return true; // Return true if the move is valid for a knight
    }

    // Method to return a string representation of the Knight
    @Override
    public String toString() {
        // Unicode characters for the white and black knight
        return white ? "\u2658" : "\u265E";
    }
}
