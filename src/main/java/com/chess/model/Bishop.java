package com.chess.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the Bishop piece.
 */
public class Bishop extends Piece {

    public Bishop(Color color) {
        super(color, PieceType.BISHOP);
    }

    @Override
    public List<Position> getValidMoves(Position from, Board board) {
        List<Position> moves = new ArrayList<>();
        int[][] directions = {
            {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
        };

        for (int[] dir : directions) {
            for (int i = 1; i < 8; i++) {
                Position to = from.offset(dir[0] * i, dir[1] * i);
                if (!to.isValid()) break;
                
                Piece targetPiece = board.getPieceAt(to);
                if (targetPiece == null) {
                    moves.add(to);
                } else {
                    if (targetPiece.getColor() != color) {
                        moves.add(to);
                    }
                    break;
                }
            }
        }

        return moves;
    }
}
