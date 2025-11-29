package com.chess.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the Pawn piece.
 */
public class Pawn extends Piece {

    public Pawn(Color color) {
        super(color, PieceType.PAWN);
    }

    @Override
    public List<Position> getValidMoves(Position from, Board board) {
        List<Position> moves = new ArrayList<>();
        int direction = (color == Color.WHITE) ? -1 : 1;
        int startRow = (color == Color.WHITE) ? 6 : 1;

        // Forward move
        Position oneStep = from.offset(direction, 0);
        if (oneStep.isValid() && board.getPieceAt(oneStep) == null) {
            moves.add(oneStep);

            // Double step from starting position
            if (from.getRow() == startRow) {
                Position twoSteps = from.offset(direction * 2, 0);
                if (board.getPieceAt(twoSteps) == null) {
                    moves.add(twoSteps);
                }
            }
        }

        // Diagonal captures
        for (int colOffset : new int[]{-1, 1}) {
            Position capture = from.offset(direction, colOffset);
            if (capture.isValid()) {
                Piece targetPiece = board.getPieceAt(capture);
                if (targetPiece != null && targetPiece.getColor() != color) {
                    moves.add(capture);
                }

                // En passant
                Position enPassantTarget = board.getEnPassantTarget();
                if (enPassantTarget != null && capture.equals(enPassantTarget)) {
                    moves.add(capture);
                }
            }
        }

        return moves;
    }
}
