package com.chess.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the chess game state and logic.
 */
public class Game {
    private Board board;
    private Color currentTurn;
    private List<Move> moveHistory;
    private GameState state;

    public enum GameState {
        PLAYING,
        CHECK,
        CHECKMATE,
        STALEMATE,
        DRAW
    }

    public Game() {
        board = new Board();
        board.initializeStandardPosition();
        currentTurn = Color.WHITE;
        moveHistory = new ArrayList<>();
        state = GameState.PLAYING;
    }

    public Board getBoard() {
        return board;
    }

    public Color getCurrentTurn() {
        return currentTurn;
    }

    public GameState getState() {
        return state;
    }

    public List<Move> getMoveHistory() {
        return new ArrayList<>(moveHistory);
    }

    /**
     * Attempts to make a move from one position to another.
     * Returns true if the move was successful.
     */
    public boolean makeMove(Position from, Position to) {
        return makeMove(from, to, null);
    }

    /**
     * Attempts to make a move with optional pawn promotion.
     */
    public boolean makeMove(Position from, Position to, PieceType promotionType) {
        if (state == GameState.CHECKMATE || state == GameState.STALEMATE || state == GameState.DRAW) {
            return false;
        }

        Piece piece = board.getPieceAt(from);
        if (piece == null || piece.getColor() != currentTurn) {
            return false;
        }

        List<Position> legalMoves = board.getLegalMoves(from);
        if (!legalMoves.contains(to)) {
            return false;
        }

        Piece capturedPiece = board.getPieceAt(to);
        boolean isCastling = false;
        boolean isEnPassant = false;

        // Handle en passant capture
        if (piece instanceof Pawn && to.equals(board.getEnPassantTarget())) {
            isEnPassant = true;
            int captureRow = (currentTurn == Color.WHITE) ? to.getRow() + 1 : to.getRow() - 1;
            Position capturePos = new Position(captureRow, to.getCol());
            capturedPiece = board.getPieceAt(capturePos);
            board.setPieceAt(capturePos, null);
        }

        // Handle castling
        if (piece instanceof King && Math.abs(to.getCol() - from.getCol()) == 2) {
            isCastling = true;
            int rookFromCol = (to.getCol() > from.getCol()) ? 7 : 0;
            int rookToCol = (to.getCol() > from.getCol()) ? 5 : 3;
            Position rookFrom = new Position(from.getRow(), rookFromCol);
            Position rookTo = new Position(from.getRow(), rookToCol);
            Piece rook = board.getPieceAt(rookFrom);
            board.setPieceAt(rookFrom, null);
            board.setPieceAt(rookTo, rook);
            rook.setMoved(true);
        }

        // Set en passant target for pawn double move
        if (piece instanceof Pawn && Math.abs(to.getRow() - from.getRow()) == 2) {
            int enPassantRow = (from.getRow() + to.getRow()) / 2;
            board.setEnPassantTarget(new Position(enPassantRow, from.getCol()));
        } else {
            board.setEnPassantTarget(null);
        }

        // Move the piece
        board.setPieceAt(from, null);
        
        // Handle pawn promotion
        if (piece instanceof Pawn && (to.getRow() == 0 || to.getRow() == 7)) {
            if (promotionType == null) {
                promotionType = PieceType.QUEEN;
            }
            piece = createPromotedPiece(promotionType, piece.getColor());
        }
        
        board.setPieceAt(to, piece);
        piece.setMoved(true);

        // Record the move
        Move move = new Move(from, to, piece, capturedPiece, isCastling, isEnPassant, promotionType);
        moveHistory.add(move);

        // Switch turns
        currentTurn = currentTurn.opposite();

        // Update game state
        updateGameState();

        return true;
    }

    private Piece createPromotedPiece(PieceType type, Color color) {
        switch (type) {
            case QUEEN:
                return new Queen(color);
            case ROOK:
                return new Rook(color);
            case BISHOP:
                return new Bishop(color);
            case KNIGHT:
                return new Knight(color);
            default:
                return new Queen(color);
        }
    }

    private void updateGameState() {
        boolean inCheck = board.isInCheck(currentTurn);
        boolean hasLegalMoves = board.hasLegalMoves(currentTurn);

        if (inCheck) {
            if (hasLegalMoves) {
                state = GameState.CHECK;
            } else {
                state = GameState.CHECKMATE;
            }
        } else {
            if (hasLegalMoves) {
                state = GameState.PLAYING;
            } else {
                state = GameState.STALEMATE;
            }
        }
    }

    /**
     * Resets the game to the initial state.
     */
    public void reset() {
        board = new Board();
        board.initializeStandardPosition();
        currentTurn = Color.WHITE;
        moveHistory.clear();
        state = GameState.PLAYING;
    }

    /**
     * Returns a message describing the current game state.
     */
    public String getStateMessage() {
        switch (state) {
            case PLAYING:
                return currentTurn + "'s turn";
            case CHECK:
                return currentTurn + " is in check!";
            case CHECKMATE:
                return currentTurn.opposite() + " wins by checkmate!";
            case STALEMATE:
                return "Stalemate! The game is a draw.";
            case DRAW:
                return "The game is a draw.";
            default:
                return "";
        }
    }
}
