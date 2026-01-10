// Nicholas Donahue 2023
package com.checkmate.matches.model.util.Pieces;

import com.checkmate.matches.model.util.Game.*;


public class Rook extends Queen {
    // Constructor for Rook, calling the constructor of the superclass (Queen)
    public Rook(boolean white) {
        super(white, "Rook"); // Setting the color and type of the piece
    }

    // Private method to check if there are pieces on the vertical path
    private boolean PiecesOnVerticalPath(Move move, Game game) {
        int rowDiff = move.getRow1() - move.getRow0();

        // Loop to check each square along the vertical path
        for (int i = 1; i < Math.abs(rowDiff); i++) {
            int n = i * (rowDiff > 0 ? 1 : -1);
            if (game.getPiece(move.getRow0() + n, move.getCol0()) != null)
                return true; // Return true if a piece is found
        }

        return false; // Return false if the path is clear
    }

    // Private method to check if there are pieces on the horizontal path
    private boolean PiecesOnHorizontalPath(Move move, Game game) {
        int colDiff = move.getCol1() - move.getCol0();

        // Loop to check each square along the horizontal path
        for (int i = 1; i < Math.abs(colDiff); i++) {
            int n = i * (colDiff > 0 ? 1 : -1);
            if (game.getPiece(move.getRow0(), move.getCol0() + n) != null)
                return true; // Return true if a piece is found
        }

        return false; // Return false if the path is clear
    }

    // Overridden method to check if a move is legal specifically for a rook
    @Override
    public boolean isLegal(Move move, Game game) {
        if (!super.isLegal(move, game))
            return false;

        // Specific rules for rook movement
        int rowDiff = move.getRow1() - move.getRow0();
        int colDiff = move.getCol1() - move.getCol0();

        // Check for vertical or horizontal movement and if the path is clear
        if ((rowDiff != 0 && colDiff != 0) || // If not moving straight
                (rowDiff > 0 && PiecesOnVerticalPath(move, game)) || // Vertical path check
                (colDiff > 0 && PiecesOnHorizontalPath(move, game))) // Horizontal path check
            return false;

        return true; // Return true if the move is valid for a rook
    }

    // Method to return a string representation of the Rook
    @Override
    public String toString() {
        // Unicode characters for the white and black rook
        return white ? "\u2656" : "\u265C";
    }
}
