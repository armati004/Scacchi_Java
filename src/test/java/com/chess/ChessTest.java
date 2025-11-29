package com.chess;

import com.chess.model.*;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import java.util.List;

/**
 * Unit tests for the chess game logic.
 */
public class ChessTest {
    private Game game;
    private Board board;

    @Before
    public void setUp() {
        game = new Game();
        board = game.getBoard();
    }

    @Test
    public void testInitialBoardSetup() {
        // Test white pieces
        assertTrue(board.getPieceAt(new Position(7, 0)) instanceof Rook);
        assertTrue(board.getPieceAt(new Position(7, 1)) instanceof Knight);
        assertTrue(board.getPieceAt(new Position(7, 2)) instanceof Bishop);
        assertTrue(board.getPieceAt(new Position(7, 3)) instanceof Queen);
        assertTrue(board.getPieceAt(new Position(7, 4)) instanceof King);
        assertTrue(board.getPieceAt(new Position(7, 5)) instanceof Bishop);
        assertTrue(board.getPieceAt(new Position(7, 6)) instanceof Knight);
        assertTrue(board.getPieceAt(new Position(7, 7)) instanceof Rook);
        
        for (int col = 0; col < 8; col++) {
            assertTrue(board.getPieceAt(new Position(6, col)) instanceof Pawn);
            assertEquals(Color.WHITE, board.getPieceAt(new Position(6, col)).getColor());
        }

        // Test black pieces
        assertTrue(board.getPieceAt(new Position(0, 0)) instanceof Rook);
        assertTrue(board.getPieceAt(new Position(0, 4)) instanceof King);
        
        for (int col = 0; col < 8; col++) {
            assertTrue(board.getPieceAt(new Position(1, col)) instanceof Pawn);
            assertEquals(Color.BLACK, board.getPieceAt(new Position(1, col)).getColor());
        }
    }

    @Test
    public void testPawnMove() {
        // White pawn single step
        Position from = new Position(6, 4);
        Position to = new Position(5, 4);
        assertTrue(game.makeMove(from, to));
        assertNull(board.getPieceAt(from));
        assertTrue(board.getPieceAt(to) instanceof Pawn);
    }

    @Test
    public void testPawnDoubleMove() {
        Position from = new Position(6, 4);
        Position to = new Position(4, 4);
        assertTrue(game.makeMove(from, to));
        assertTrue(board.getPieceAt(to) instanceof Pawn);
    }

    @Test
    public void testKnightMove() {
        // White knight can move
        Position from = new Position(7, 1);
        Position to = new Position(5, 2);
        assertTrue(game.makeMove(from, to));
        assertTrue(board.getPieceAt(to) instanceof Knight);
    }

    @Test
    public void testIllegalMoveBlockedPawn() {
        // Try to move a blocked pawn
        Position from = new Position(6, 0);
        Position to = new Position(4, 0);
        assertTrue(game.makeMove(from, to));
        
        // Black moves
        game.makeMove(new Position(1, 0), new Position(3, 0));
        
        // White pawn is now blocked
        List<Position> moves = board.getLegalMoves(new Position(4, 0));
        assertFalse(moves.contains(new Position(3, 0)));
    }

    @Test
    public void testTurnOrder() {
        assertEquals(Color.WHITE, game.getCurrentTurn());
        game.makeMove(new Position(6, 4), new Position(4, 4));
        assertEquals(Color.BLACK, game.getCurrentTurn());
    }

    @Test
    public void testCannotMoveOpponentPiece() {
        // Try to move black piece on white's turn
        Position from = new Position(1, 4);
        Position to = new Position(3, 4);
        assertFalse(game.makeMove(from, to));
        assertEquals(Color.WHITE, game.getCurrentTurn());
    }

    @Test
    public void testPositionConversion() {
        Position pos = Position.fromString("e4");
        assertEquals(4, pos.getRow());
        assertEquals(4, pos.getCol());
        assertEquals("e4", pos.toString());
    }

    @Test
    public void testFindKing() {
        Position whiteKing = board.findKing(Color.WHITE);
        assertEquals(7, whiteKing.getRow());
        assertEquals(4, whiteKing.getCol());
        
        Position blackKing = board.findKing(Color.BLACK);
        assertEquals(0, blackKing.getRow());
        assertEquals(4, blackKing.getCol());
    }

    @Test
    public void testReset() {
        game.makeMove(new Position(6, 4), new Position(4, 4));
        game.reset();
        
        assertEquals(Color.WHITE, game.getCurrentTurn());
        assertEquals(Game.GameState.PLAYING, game.getState());
        assertTrue(game.getMoveHistory().isEmpty());
        assertTrue(game.getBoard().getPieceAt(new Position(6, 4)) instanceof Pawn);
    }

    @Test
    public void testColorOpposite() {
        assertEquals(Color.BLACK, Color.WHITE.opposite());
        assertEquals(Color.WHITE, Color.BLACK.opposite());
    }

    @Test
    public void testPieceTypeSymbol() {
        assertEquals("K", PieceType.KING.getSymbol());
        assertEquals("Q", PieceType.QUEEN.getSymbol());
        assertEquals("R", PieceType.ROOK.getSymbol());
        assertEquals("B", PieceType.BISHOP.getSymbol());
        assertEquals("N", PieceType.KNIGHT.getSymbol());
        assertEquals("P", PieceType.PAWN.getSymbol());
    }
}
