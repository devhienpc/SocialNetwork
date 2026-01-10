// Nicholas Donahue 2023
package com.checkmate.matches.model.util.Pieces;

import com.checkmate.matches.model.util.Game.*;

public class Queen extends Piece {
    // Constructor for Queen, calling the constructor of the superclass (Piece)
    public Queen(boolean white) {
        super(white, "Queen"); // Setting the color and type of the piece
    }

    // Overloaded constructor to allow specifying the piece type, which can be useful for subclasses
    public Queen(boolean white, String Type) {
        super(white, Type);
    }

    // Overridden method to check if a move is legal specifically for a queen
    @Override
    public boolean isLegal(Move move, Game game) {
        // First, it calls the isLegal method from the superclass to check basic move legality
        if (!super.isLegal(move, game))
            return false;

        // Specific rules for queen movement
        int rowDiff = move.getRow1() - move.getRow0();
        int colDiff = move.getCol1() - move.getCol0();

        // Horizontal movement
        if (rowDiff == 0) {
            // Loop to check if there are any pieces on the horizontal path
            for (int i = 1; i < Math.abs(colDiff); i++)
                if ((colDiff > 0 && game.getPiece(move.getRow0(), move.getCol0() + i) != null) // Right
                        || (colDiff < 0 && game.getPiece(move.getRow0(), move.getCol1() + i) != null)) // Left
                    return false;
            return true;
        }

        // Vertical movement
        if (colDiff == 0) {
            // Loop to check if there are any pieces on the vertical path
            for (int i = 1; i < Math.abs(rowDiff); i++)
                if ((rowDiff > 0 && game.getPiece(move.getRow0() + i, move.getCol0()) != null) // Down
                        || (rowDiff < 0 && game.getPiece(move.getRow1() + i, move.getCol0()) != null)) // Up
                    return false;
            return true;
        }

        // Diagonal movement
        if (Math.abs(rowDiff) == Math.abs(colDiff)) {
            // Loop to check if there are any pieces on the diagonal path
            for (int i = 1; i < Math.abs(rowDiff); i++)
                if ((rowDiff > 0 && colDiff > 0 && game.getPiece(move.getRow0() + i, move.getCol0() + i) != null) // Down-Right
                        || (rowDiff > 0 && colDiff < 0 && game.getPiece(move.getRow0() + i, move.getCol1() + i) != null) // Down-Left
                        || (rowDiff < 0 && colDiff > 0 && game.getPiece(move.getRow1() + i, move.getCol0() + i) != null) // Up-Right
                        || (rowDiff < 0 && colDiff < 0 && game.getPiece(move.getRow1() + i, move.getCol1() + i) != null)) // Up-Left
                    return false;
            return true;
        }

        return false; // If none of the movement patterns match, the move is illegal
    }

    // Method to return a string representation of the Queen
    @Override
    public String toString() {
        // Unicode characters for the white and black queen
        return white ? "\u2655" : "\u265B";
    }
}
