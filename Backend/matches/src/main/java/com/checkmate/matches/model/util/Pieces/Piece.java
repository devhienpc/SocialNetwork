// Nicholas Donahue 2023
package com.checkmate.matches.model.util.Pieces;

import com.checkmate.matches.model.util.Game.*;


public class Piece {
    // Fields to store the color of the piece and its type
    protected boolean white; // True for white pieces, false for black
    protected String PieceType;

    // Method to check if the piece is white
    public boolean isWhite() {
        return white; // Returns true if the piece is white, false otherwise
    }

    // Method to check if a move is legal for this piece
    public boolean isLegal(Move move, Game game) {
        // Checks if the piece's color matches the current turn in the game
        if (white != game.isWhiteTurn())
            return false; // Move is illegal if it's not this piece's turn

        // Checks if the piece at the destination is of the same color
        Piece captured = game.getPiece(move.getRow1(), move.getCol1());
        return captured == null || captured.white != this.white; // Move is illegal if it tries to capture a piece of the same color
// If none of the above conditions are met, the move is considered legal
    }

    // Method to get the type of the piece
    public String getPieceType() {
        return PieceType; // Returns the type of the piece
    }

    public String getPieceNotation() {
        String pType = getPieceType();
        if (pType.equals("Knight")) {
            pType = "N";
        } else {
            pType = pType.substring(0, 1);
        }

        if (isWhite()) {
            return pType.substring(0, 1);
        } else {
            return pType.substring(0, 1).toLowerCase();
        }
    }

    // Constructor to initialize a piece with its color and type
    public Piece(boolean white, String Type) {
        this.white = white; // Sets the color of the piece
        PieceType = Type; // Sets the type of the piece
    }

    // Static method to create a piece of a specific type and color
    public static Piece createPiece(String Type, boolean White) {
        switch (Type) {
            // Creates and returns an instance of the specified piece type
            case ("Pawn"):
                return new Pawn(White);
            case ("Rook"):
                return new Rook(White);
            case ("Bishop"):
                return new Bishop(White);
            case ("Knight"):
                return new Knight(White);
            case ("King"):
                return new King(White);
            case ("Queen"):
                return new Queen(White);
            default:
                return null; // Returns null if the type is not recognized
        }
    }
}
