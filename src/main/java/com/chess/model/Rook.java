package com.chess.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the Rook piece.
 */
public class Rook extends Piece {

    public Rook(Color color) {
        super(color, PieceType.ROOK);
    }

    @Override
    public List<Position> getValidMoves(Position from, Board board) {
        List<Position> moves = new ArrayList<>();
        int[][] directions = {
            {-1, 0}, {1, 0}, {0, -1}, {0, 1}
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
