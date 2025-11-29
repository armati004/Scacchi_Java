package com.chess.gui;

import com.chess.model.Game;
import com.chess.model.Move;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Main window for the chess application.
 */
public class ChessFrame extends JFrame implements BoardPanel.GameStatusListener {
    private final Game game;
    private final BoardPanel boardPanel;
    private final JLabel statusLabel;
    private JTextArea moveHistoryArea;

    public ChessFrame() {
        super("Scacchi Java - Chess");
        
        game = new Game();
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);

        // Create menu bar
        JMenuBar menuBar = createMenuBar();
        setJMenuBar(menuBar);

        // Create main panel with board
        boardPanel = new BoardPanel(game);
        boardPanel.setStatusListener(this);

        // Create side panel
        JPanel sidePanel = createSidePanel();

        // Create status panel
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusLabel = new JLabel(game.getStateMessage());
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        statusPanel.add(statusLabel);

        // Add components
        add(boardPanel, BorderLayout.CENTER);
        add(sidePanel, BorderLayout.EAST);
        add(statusPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // Game menu
        JMenu gameMenu = new JMenu("Game");
        
        JMenuItem newGameItem = new JMenuItem("New Game");
        newGameItem.addActionListener(e -> startNewGame());
        gameMenu.add(newGameItem);

        gameMenu.addSeparator();

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        gameMenu.add(exitItem);

        menuBar.add(gameMenu);

        // Help menu
        JMenu helpMenu = new JMenu("Help");
        
        JMenuItem rulesItem = new JMenuItem("Rules");
        rulesItem.addActionListener(e -> showRules());
        helpMenu.add(rulesItem);

        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAbout());
        helpMenu.add(aboutItem);

        menuBar.add(helpMenu);

        return menuBar;
    }

    private JPanel createSidePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(200, 640));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title
        JLabel titleLabel = new JLabel("Move History");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Move history area
        moveHistoryArea = new JTextArea();
        moveHistoryArea.setEditable(false);
        moveHistoryArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(moveHistoryArea);
        scrollPane.setPreferredSize(new Dimension(180, 400));
        panel.add(scrollPane);

        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Buttons
        JButton newGameButton = new JButton("New Game");
        newGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        newGameButton.addActionListener(e -> startNewGame());
        panel.add(newGameButton);

        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private void startNewGame() {
        int result = JOptionPane.showConfirmDialog(
            this,
            "Start a new game?",
            "New Game",
            JOptionPane.YES_NO_OPTION
        );
        
        if (result == JOptionPane.YES_OPTION) {
            boardPanel.resetBoard();
            statusLabel.setText(game.getStateMessage());
        }
    }

    private void showRules() {
        String rules = "Chess Rules:\n\n" +
            "• White moves first, then players alternate turns\n" +
            "• Each piece has specific movement rules:\n" +
            "  - King: One square in any direction\n" +
            "  - Queen: Any number of squares horizontally, vertically, or diagonally\n" +
            "  - Rook: Any number of squares horizontally or vertically\n" +
            "  - Bishop: Any number of squares diagonally\n" +
            "  - Knight: L-shape (2+1 squares), can jump over pieces\n" +
            "  - Pawn: Forward one square (or two from start), captures diagonally\n\n" +
            "• Special moves:\n" +
            "  - Castling: King moves two squares toward a rook\n" +
            "  - En passant: Pawn captures passing pawn\n" +
            "  - Promotion: Pawn reaching the opposite end becomes another piece\n\n" +
            "• Check: King is under attack\n" +
            "• Checkmate: King is in check with no escape - game over!\n" +
            "• Stalemate: No legal moves but not in check - draw!";
        
        JOptionPane.showMessageDialog(
            this,
            rules,
            "Chess Rules",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void showAbout() {
        JOptionPane.showMessageDialog(
            this,
            "Scacchi Java\n\nA chess application with graphical interface\n\nVersion 1.0",
            "About",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    @Override
    public void onStatusChanged(String status) {
        statusLabel.setText(status);
        updateMoveHistory();
    }

    @Override
    public void onGameOver(String result) {
        JOptionPane.showMessageDialog(
            this,
            result,
            "Game Over",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void updateMoveHistory() {
        List<Move> moves = game.getMoveHistory();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < moves.size(); i += 2) {
            int moveNumber = (i / 2) + 1;
            sb.append(moveNumber).append(". ");
            sb.append(moves.get(i).toString());
            if (i + 1 < moves.size()) {
                sb.append(" ").append(moves.get(i + 1).toString());
            }
            sb.append("\n");
        }
        moveHistoryArea.setText(sb.toString());
    }
}
